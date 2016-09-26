/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.ActivityConstants;

import eu.europa.ec.fisheries.ers.message.producer.ActivityMessageProducer;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.mapper.*;

import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.Pagination;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.PaginationDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.ContactPersonDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FluxLocationDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.*;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetFault;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;


/**
 * Created by sanera on 29/06/2016.
 */
@Stateless
@Local(ActivityService.class)
@Transactional
@Slf4j
public class ActivityServiceBean implements ActivityService {

    static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private FaReportDocumentDao faReportDocumentDao;
    private FishingActivityDao fishingActivityDao;
    private FishingTripDao fishingTripDao;
    private FishingTripIdentifierDao fishingTripIdentifierDao;

    @EJB
    private ActivityMessageProducer activityProducer;

    @EJB
    private AssetsMessageConsumerBean activityConsumer;

    @PostConstruct
    public void init() {
        fishingActivityDao = new FishingActivityDao(em);
        faReportDocumentDao = new FaReportDocumentDao(em);
        fishingTripDao = new FishingTripDao(em);
        fishingTripIdentifierDao = new FishingTripIdentifierDao(em);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FaReportCorrectionDTO> getFaReportCorrections(String refReportId, String refSchemeId) throws ServiceException {
        List<FaReportDocumentEntity> faReportDocumentEntities = getReferencedFaReportDocuments(refReportId, refSchemeId);
        List<FaReportCorrectionDTO> faReportCorrectionDTOs = FaReportDocumentMapper.INSTANCE.mapToFaReportCorrectionDtoList(faReportDocumentEntities);
        log.info("Sort collection by date before sending");
        Collections.sort(faReportCorrectionDTOs);
        return faReportCorrectionDTOs;
    }

    private List<FaReportDocumentEntity> getReferencedFaReportDocuments(String refReportId, String refSchemeId) throws ServiceException {
        if (refReportId == null || refSchemeId == null) {
            return Collections.emptyList();
        }
        log.info("Find reference fishing activity report for : " + refReportId + " scheme Id : " + refReportId);
        List<FaReportDocumentEntity> allFaReportDocuments = new ArrayList<>();
        FaReportDocumentEntity faReportDocumentEntity = faReportDocumentDao.findFaReportByIdAndScheme(refReportId, refSchemeId);
        if (faReportDocumentEntity != null) {
            allFaReportDocuments.add(faReportDocumentEntity);
            allFaReportDocuments.addAll(getReferencedFaReportDocuments(faReportDocumentEntity.getFluxReportDocument().getReferenceId(),
                    faReportDocumentEntity.getFluxReportDocument().getReferenceSchemeId()));
        }
        return allFaReportDocuments;
    }

    @Override
    public FilterFishingActivityReportResultDTO getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        List<FishingActivityEntity> activityList;
        boolean isSearchFiltersPresent = true;
        int totalPages=0;
      
        if (query.getSearchCriteriaMap() == null || query.getSearchCriteriaMap().isEmpty())
            isSearchFiltersPresent=false;

        if(isSearchFiltersPresent) {
           activityList = fishingActivityDao.getFishingActivityListByQuery(query);
        } else
            activityList = fishingActivityDao.getFishingActivityList(query.getPagination()); // If search criteria is not present, return all the fishing Activity data

        // Execute query to count all the resultset only if TotalPages value is 0. After first search frontend should send totalPages count in subsequent calls
        Pagination pagination= query.getPagination();
        if(pagination!=null && pagination.getTotalPages()==0 ){
            totalPages= getTotalPagesCountForFilterFishingActivityReports(query);
        }


        if (activityList == null || activityList.isEmpty()) {
            log.info("Could not find FishingActivity entities matching search criteria");
            activityList= Collections.emptyList();
        }

        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO = new FilterFishingActivityReportResultDTO();
        filterFishingActivityReportResultDTO.setResultList(FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTOList(activityList));
        filterFishingActivityReportResultDTO.setPagination(new PaginationDTO(totalPages));

        return filterFishingActivityReportResultDTO;
    }

    // Query to calculate total number of resultset
    private Integer getTotalPagesCountForFilterFishingActivityReports(FishingActivityQuery query) throws ServiceException {

        if (query.getSearchCriteriaMap() == null || query.getSearchCriteriaMap().isEmpty()){
            return fishingActivityDao.getCountForFishingActivityList();
        }else{
            return fishingActivityDao.getCountForFishingActivityListByQuery(query);
        }
    }

    // Get data for Fishing Trip summary view
    @Override
    public FishingTripSummaryViewDTO getFishingTripSummary(String fishingTripId) throws ServiceException {
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO = new FishingTripSummaryViewDTO();

        // Messages count box for Fishing Trip Summary view
        MessageCountDTO messagesCount = new MessageCountDTO();

        List<ReportDTO> reportDTOList = new ArrayList<>();

        // get short summary of Fishing Trip
        Map<String, FishingActivityTypeDTO> summary = new HashMap<>();
        // All Activity Reports and related data  for Fishing Trip
        getFishingActivityReportAndRelatedDataForFishingTrip(fishingTripId, reportDTOList, summary, messagesCount);
        fishingTripSummaryViewDTO.setActivityReports(reportDTOList);
        // fishingTripSummaryViewDTO.setFishingTripSummaryDTO(fishingTripSummaryDTO);
        fishingTripSummaryViewDTO.setSummary(summary);

        // Fishing trip Id for the Fishing Trip summary view
        fishingTripSummaryViewDTO.setFishingTripId(fishingTripId);

        // Vessel Details for specified Fishing Trip
        fishingTripSummaryViewDTO.setVesselDetails(getVesselDetailsForFishingTrip(fishingTripId));
        fishingTripSummaryViewDTO.setMessagesCount(messagesCount);

        return fishingTripSummaryViewDTO;
    }

    @Override
    public VesselDetailsTripDTO getVesselDetailsForFishingTrip(String fishingTripId) {

        VesselDetailsTripDTO vesselDetailsTripDTO = new VesselDetailsTripDTO();
        try {
            FishingTripEntity fishingTrip = fishingTripDao.fetchVesselTransportDetailsForFishingTrip(fishingTripId);

            if (fishingTrip == null || fishingTrip.getFishingActivity() == null || fishingTrip.getFishingActivity().getFaReportDocument() == null
                    || fishingTrip.getFishingActivity().getFaReportDocument().getVesselTransportMeans() == null) {
                return vesselDetailsTripDTO;
            }

            VesselTransportMeansEntity vesselTransportMeansEntity = fishingTrip.getFishingActivity().getFaReportDocument().getVesselTransportMeans();

            // Fill the name and vesselIdentifier Details.
            vesselDetailsTripDTO.setName(vesselTransportMeansEntity.getName());
            Set<VesselIdentifierEntity> vesselIdentifiers = vesselTransportMeansEntity.getVesselIdentifiers();
            if (vesselIdentifiers != null) {
                for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
                    setVesselIdentifierDetails(vesselIdentifier, vesselDetailsTripDTO);
                }
            }

            // Fill the flagState.
            RegistrationEventEntity registrationEventEntity = vesselTransportMeansEntity.getRegistrationEvent();
            if (registrationEventEntity != null && registrationEventEntity.getRegistrationLocation() != null)
                vesselDetailsTripDTO.setFlagState(registrationEventEntity.getRegistrationLocation().getLocationCountryId());

            // Fill the contactPersons List and check if is captain.
            Set<ContactPartyEntity> contactParties         = vesselTransportMeansEntity.getContactParty();
            Set<ContactPersonDetailsDTO> contactPersonsListDTO = vesselDetailsTripDTO.getContactPersons();
            if(CollectionUtils.isNotEmpty(contactParties)){
                for (ContactPartyEntity contactParty : contactParties) {
                    ContactPersonDetailsDTO contactPersDTO           = ContactPersonMapper.INSTANCE.mapToContactPersonDetailsDTO(contactParty.getContactPerson());
                    Set<StructuredAddressEntity> structuredAddresses = contactParty.getStructuredAddresses();
                    contactPersDTO.setAdresses(StructuredAddressMapper.INSTANCE.mapToAddressDetailsDTOList(structuredAddresses));
                    checkAndSetIsCaptain(contactPersDTO, contactParty);
                    contactPersonsListDTO.add(contactPersDTO);
                }
                vesselDetailsTripDTO.setContactPersons(contactPersonsListDTO);
            }

            // If some data are missing from the current DTOs then will make a call to
            // ASSETS module with the data we already have to enrich it.
            enrichWithAssetsModuleDataIfNeeded(vesselDetailsTripDTO);

        } catch (Exception e) {
            log.error("Error while trying to get Vessel Details.", e);
        }

        return vesselDetailsTripDTO;
    }

    /**
     * Checks if the ContactPartyEntity has the captain Role and assigns it to ContactPersonDetailsDTO.isCaptain.
     *
     * @param contactPersDTO
     * @param contactParty
     */
    private void checkAndSetIsCaptain(ContactPersonDetailsDTO contactPersDTO, ContactPartyEntity contactParty) {
        Set<ContactPartyRoleEntity> contactPartyRoles = contactParty.getContactPartyRole();
        if(CollectionUtils.isNotEmpty(contactPartyRoles)){
            for(ContactPartyRoleEntity roleEntity : contactPartyRoles){
                contactPersDTO.setCaptain(StringUtils.equalsIgnoreCase(roleEntity.getRoleCode(), "MASTER"));
            }
        }
    }

    /**
     * Enriches the VesselDetailsTripDTO with data got from Assets module.
     *
     * @param vesselDetailsTripDTO
     */

    private void enrichWithAssetsModuleDataIfNeeded(VesselDetailsTripDTO vesselDetailsTripDTO) {
        if(someVesselDetailsAreMissing(vesselDetailsTripDTO)){
            String response = null;
            TextMessage message = null;
            try {
                // Create request object;
                String assetsRequest = AssetsRequestMapper.mapToAssetsRequest(vesselDetailsTripDTO);
                // Send message to Assets module and get response;
                String messageID = activityProducer.sendAssetsModuleSynchronousMessage(assetsRequest);
                message          = activityConsumer.getMessage(messageID, TextMessage.class);
                response         = message.getText();
            } catch (Exception e){
                log.error("Error while trying to send message to Assets module.", e);
            }
            if(isFaultMessage(message)){
                log.error("The Asset module responded with a fault message related to Vessel Details Enrichment: ",response);
                return;
            }
            if(StringUtils.isNotEmpty(response)){
                try {
                    ListAssetResponse listResp = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
                    AssetsRequestMapper.mapAssetsResponseToVesselDetailsTripDTO(listResp, vesselDetailsTripDTO);
                } catch (ModelMarshallException e) {
                    log.error("Error while trying to unmarshall response from Asset Module regarding VesselDetailsTripDTO enrichment",e);
                }
            }

        }
    }

    /**
     * Checks if the related message is a Fault message from Assets module;
     *
     * @param response
     * @return true/false
     */
    private boolean isFaultMessage(TextMessage response) {
        try {
            AssetFault fault = JAXBMarshaller.unmarshallTextMessage(response, AssetFault.class);
            int code = fault.getCode();
            return true;
        } catch (ModelMarshallException e) {
            return false;
        }
    }

    /**
     * Checks if some vessel details are missing
     *
     * @param vesselDetailsTripDTO
     * @return
     */
    private boolean someVesselDetailsAreMissing(VesselDetailsTripDTO vesselDetailsTripDTO) {
        return StringUtils.isEmpty(vesselDetailsTripDTO.getCfr())
                || StringUtils.isEmpty(vesselDetailsTripDTO.getExMark())
                || StringUtils.isEmpty(vesselDetailsTripDTO.getUvi())
                || StringUtils.isEmpty(vesselDetailsTripDTO.getGfcm())
                || StringUtils.isEmpty(vesselDetailsTripDTO.getIccat())
                || StringUtils.isEmpty(vesselDetailsTripDTO.getIrcs())
                || StringUtils.isEmpty(vesselDetailsTripDTO.getName())
                || StringUtils.isEmpty(vesselDetailsTripDTO.getFlagState());
    }

    private void setVesselIdentifierDetails(VesselIdentifierEntity vesselIdentifier, VesselDetailsTripDTO vesselDetailsTripDTO) {
        String fieldName = vesselIdentifier.getVesselIdentifierSchemeId().toUpperCase();
        String fieldValue = vesselIdentifier.getVesselIdentifierId();
        switch (fieldName) {
            case "EXT_MARK":
                vesselDetailsTripDTO.setExMark(fieldValue);
                break;
            case "IRCS":
                vesselDetailsTripDTO.setIrcs(fieldValue);
                break;
            case "CFR":
                vesselDetailsTripDTO.setCfr(fieldValue);
                break;
            case "UVI":
                vesselDetailsTripDTO.setUvi(fieldValue);
                break;
            case "ICCAT":
                vesselDetailsTripDTO.setIccat(fieldValue);
                break;
            case "GFCM":
                vesselDetailsTripDTO.setGfcm(fieldValue);
                break;
            default:
                log.error("VesselIdentifierSchemeId not found in the ActivityServiceBean.setVesselIdentifierDetails(..) method!");
                break;
        }
    }


    @Override
    public void getFishingActivityReportAndRelatedDataForFishingTrip(String fishingTripId, List<ReportDTO> reportDTOList, Map<String, FishingActivityTypeDTO> summary, MessageCountDTO messagesCount) throws ServiceException {

        List<FishingActivityEntity> fishingActivityList;
        int reportsCnt = 0;
        int declarations = 0;
        int notifications = 0;
        int corrections = 0;
        int fishingOperations = 0;


        try {
            fishingActivityList = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId, null);
        } catch (Exception e) {
            log.error("Error while trying to get Fishing Activity reports for fishing trip with Id:" + fishingTripId, e);
            return;
        }

        if (fishingActivityList == null || fishingActivityList.isEmpty())
            return;

        if (reportDTOList == null)
            reportDTOList = new ArrayList<>();

        for (FishingActivityEntity activityEntity : fishingActivityList) {

            ReportDTO reportDTO = FishingActivityMapper.INSTANCE.mapToReportDTO(activityEntity);

            if (ActivityConstants.DECLARATION.equalsIgnoreCase(reportDTO.getFaReportDocumentType())) {
                declarations++;
            } else if (ActivityConstants.NOTIFICATION.equalsIgnoreCase(reportDTO.getFaReportDocumentType())) {
                notifications++;
            }

            if (reportDTO.isCorrection())
                corrections++;

            if (reportDTO.getUniqueFAReportId() != null)
                reportsCnt++;

            String activityType = reportDTO.getActivityType();
            if (ActivityConstants.FISHING_OPERATION.equalsIgnoreCase(activityType))
                fishingOperations++;

            // FA Report should be of type Declaration. And Fishing Activity type should be Either Departure,Arrival or Landing
            if (ActivityConstants.DECLARATION.equalsIgnoreCase(reportDTO.getFaReportDocumentType())) {
                //createFishingSummaryDTO(reportDTO,fishingTripSummaryDTO);
                populateSummaryMap(reportDTO, summary);
            }
            reportDTOList.add(reportDTO);
        }// end of for loop


        messagesCount.setNoOfCorrections(corrections);
        messagesCount.setNoOfDeclarations(declarations);
        messagesCount.setNoOfFishingOperations(fishingOperations);
        messagesCount.setNoOfNotifications(notifications);
        messagesCount.setNoOfReports(reportsCnt);
    }

    private void populateSummaryMap(ReportDTO reportDTO, Map<String, FishingActivityTypeDTO> summary) {
        if (ActivityConstants.DEPARTURE.equalsIgnoreCase(reportDTO.getActivityType()) || ActivityConstants.ARRIVAL.equalsIgnoreCase(reportDTO.getActivityType()) || ActivityConstants.LANDING.equalsIgnoreCase(reportDTO.getActivityType())) {
            Date occurence = reportDTO.getOccurence();
            List<FluxLocationDetailsDTO> fluxLocations = reportDTO.getFluxLocations();
            FishingActivityTypeDTO fishingActivityTypeDTO = summary.get(reportDTO.getActivityType());

            if ((fishingActivityTypeDTO == null || (reportDTO.isCorrection() && fishingActivityTypeDTO.getDate() != null && occurence != null && occurence.compareTo(fishingActivityTypeDTO.getDate()) > 0))) {
                fishingActivityTypeDTO = new FishingActivityTypeDTO();
                fishingActivityTypeDTO.setDate(occurence);
                fishingActivityTypeDTO.setLocations(fluxLocations);
                summary.put(reportDTO.getActivityType(), fishingActivityTypeDTO);
            }
        }
    }


}