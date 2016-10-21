/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.bean;

import com.google.common.collect.ImmutableMap;
import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.dao.*;
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.ActivityConstants;
import eu.europa.ec.fisheries.ers.fa.utils.GeometryUtils;
import eu.europa.ec.fisheries.ers.fa.utils.UsmUtils;
import eu.europa.ec.fisheries.ers.message.producer.ActivityMessageProducer;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.SpatialModuleService;
import eu.europa.ec.fisheries.ers.service.mapper.AssetsRequestMapper;
import eu.europa.ec.fisheries.ers.service.mapper.ContactPersonMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.mapper.StructuredAddressMapper;
import eu.europa.ec.fisheries.ers.service.mapper.*;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.ContactPersonDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.*;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetFault;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.CatchSummaryListDTO;

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
 * Created by padhyad on 9/22/2016.
 */
@Stateless
@Local(FishingTripService.class)
@Transactional
@Slf4j
public class FishingTripServiceBean implements FishingTripService {

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    @EJB
    private ActivityMessageProducer activityProducer;

    @EJB
    private AssetsMessageConsumerBean activityConsumer;

    @EJB
    private SpatialModuleService spatialModule;

    private FaReportDocumentDao faReportDocumentDao;
    private FishingActivityDao fishingActivityDao;
    private VesselIdentifiersDao vesselIdentifiersDao;
    private FishingTripIdentifierDao fishingTripIdentifierDao;
    private FaCatchDao faCatchDao;

    private static final String PREVIOUS = "PREVIOUS";
    private static final String NEXT = "NEXT";

    @PostConstruct
    public void init() {
        fishingTripIdentifierDao = new FishingTripIdentifierDao(em);
        vesselIdentifiersDao     = new VesselIdentifiersDao(em);
        fishingActivityDao       = new FishingActivityDao(em);
        faReportDocumentDao      = new FaReportDocumentDao(em);
        faCatchDao               = new FaCatchDao(em);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public CronologyTripDTO getCronologyOfFishingTrip(String tripId, Integer count) {
        List<VesselIdentifierEntity> latestVesselIdentifiers = vesselIdentifiersDao.getLatestVesselIdByTrip(tripId); // Find the latest Vessel for the Trip for finding the trip of that vessel
        CronologyTripDTO cronologyTripDTO = new CronologyTripDTO();
        cronologyTripDTO.setCurrentTrip(getCurrentTrip(latestVesselIdentifiers));
        cronologyTripDTO.setSelectedTrip(tripId);

        List<String> previousTrips = new ArrayList<>(getPreviousTrips(tripId, count, latestVesselIdentifiers));
        List<String> nextTrips = new ArrayList<>(getNextTrips(tripId, count, latestVesselIdentifiers));

        Map<String, Integer> countMap = calculateTripCounts(count, previousTrips.size(), nextTrips.size());
        log.info("Number of previous record to find : " + countMap.get(PREVIOUS));
        log.info("Number of next record to find : " + countMap.get(NEXT));

        cronologyTripDTO.setPreviousTrips(previousTrips.subList(previousTrips.size() - countMap.get(PREVIOUS), previousTrips.size()));
        cronologyTripDTO.setNextTrips(nextTrips.subList(0, countMap.get(NEXT)));

        return cronologyTripDTO;
    }

    private Map<String, Integer> calculateTripCounts(Integer count, Integer previousTripCount, Integer nextTripCount) {

        Integer previous = 0;
        Integer next = 0;

        if (count == 0) {
            log.info("All the previous and next result will be returned");
            previous = previousTripCount;
            next = nextTripCount;
        } else if (count != 1) {
            log.info("Count the number of previous and next result based on count received");
            count = count - 1;
            if (count % 2 == 0) {
                previous = count/2;
                next = count/2;
            } else if (count % 2 == 1) {
                previous = count/2 + 1;
                next = count/2;
            }
            if (previous > previousTripCount) {
                previous = previousTripCount;
                next = count - previous;
            }
            if (next > nextTripCount) {
                next = nextTripCount;
                previous = count - next;
                if (previous > previousTripCount) {
                    previous = previousTripCount;
                }
            }
        }
        return ImmutableMap.<String, Integer> builder().put(PREVIOUS, previous).put(NEXT, next).build();
    }

    private String getCurrentTrip(List<VesselIdentifierEntity> vesselIdentifiers) {
        String currentTrip = null;
        if (vesselIdentifiers != null && !vesselIdentifiers.isEmpty()) {
            for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
                FishingTripIdentifierEntity identifierEntity = fishingTripIdentifierDao.getCurrentTrip(vesselIdentifier.getVesselIdentifierId(),
                        vesselIdentifier.getVesselIdentifierSchemeId());
                currentTrip = identifierEntity != null ? identifierEntity.getTripId() : null;
                break;
            }
        }
        log.info("Current Trip : " + currentTrip);
        return currentTrip;
    }

    private Set<String> getPreviousTrips(String tripId, Integer limit, List<VesselIdentifierEntity> vesselIdentifiers) {
        Set<String> tripIds = new LinkedHashSet<>();
        if (vesselIdentifiers != null && !vesselIdentifiers.isEmpty()) {
            for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
                List<FishingTripIdentifierEntity> identifierEntities = fishingTripIdentifierDao.getPreviousTrips(vesselIdentifier.getVesselIdentifierId(),
                        vesselIdentifier.getVesselIdentifierSchemeId(), tripId, limit);
                for (FishingTripIdentifierEntity identifiers : identifierEntities) {
                    tripIds.add(identifiers.getTripId());
                }
            }
        }
        log.info("Previous Trips : " + tripIds);
        return tripIds;
    }

    private Set<String> getNextTrips(String tripId, Integer limit, List<VesselIdentifierEntity> vesselIdentifiers) {
        Set<String> tripIds = new LinkedHashSet<>();
        if (vesselIdentifiers != null && !vesselIdentifiers.isEmpty()) {
            for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
                List<FishingTripIdentifierEntity> identifierEntities = fishingTripIdentifierDao.getNextTrips(vesselIdentifier.getVesselIdentifierId(),
                        vesselIdentifier.getVesselIdentifierSchemeId(), tripId, limit);
                for (FishingTripIdentifierEntity identifiers : identifierEntities) {
                    tripIds.add(identifiers.getTripId());
                }
            }
        }
        log.info("Next Trips : " + tripIds);
        return tripIds;
    }



    @Override
    public VesselDetailsTripDTO getVesselDetailsForFishingTrip(String fishingTripId) {

        VesselDetailsTripDTO vesselDetailsTripDTO = new VesselDetailsTripDTO();
        try {

            List<VesselIdentifierEntity> latestVesselIdentifiers = vesselIdentifiersDao.getLatestVesselIdByTrip(fishingTripId);
            if(CollectionUtils.isEmpty(latestVesselIdentifiers)
                    || latestVesselIdentifiers.get(0) == null
                    || latestVesselIdentifiers.get(0).getVesselTransportMeans() == null){
                return vesselDetailsTripDTO;
            }

            VesselTransportMeansEntity vesselTransportMeansEntity  = latestVesselIdentifiers.get(0).getVesselTransportMeans();

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
            Set<ContactPartyEntity> contactParties             = vesselTransportMeansEntity.getContactParty();
            Set<ContactPersonDetailsDTO> contactPersonsListDTO = vesselDetailsTripDTO.getContactPersons();
            if(CollectionUtils.isNotEmpty(contactParties)){
                for (ContactPartyEntity contactParty : contactParties) {
                    ContactPersonDetailsDTO contactPersDTO           = ContactPersonMapper.INSTANCE.mapToContactPersonDetailsWithRolesDTO(contactParty.getContactPerson(), contactParty.getContactPartyRole());
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
                log.debug("The original VesselDetailsTripDTO that the request for enrichment was made for : ",vesselDetailsTripDTO.toString());
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
            JAXBMarshaller.unmarshallTextMessage(response, AssetFault.class);
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

    /**
     *
     *
     * @param vesselIdentifier
     * @param vesselDetailsTripDTO
     */
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


    // Get data for Fishing Trip summary view
    @Override
    public FishingTripSummaryViewDTO getFishingTripSummaryAndReports(String fishingTripId, List<Dataset> datasets) throws ServiceException {

        List<ReportDTO> reportDTOList = new ArrayList<>();
        // get short summary of Fishing Trip
        Map<String, FishingActivityTypeDTO> summary = new HashMap<>();
        // All Activity Reports and related data  for Fishing Trip
        Geometry multipolygon = getRestrictedAreaGeom(datasets);
        populateFishingActivityReportListAndSummary(fishingTripId, reportDTOList, summary, multipolygon);
        return  populateFishingTripSummary(fishingTripId, reportDTOList, summary);
    }

    private Geometry getRestrictedAreaGeom(List<Dataset> datasets) throws ServiceException {
        if (datasets == null || datasets.isEmpty()) {
            return null;
        }
        List<AreaIdentifierType> areaIdentifierTypes =  UsmUtils.convertDataSetToAreaId(datasets);
        String areaWkt = spatialModule.getFilteredAreaGeom(areaIdentifierTypes);
        return GeometryUtils.wktToGeom(areaWkt);
    }

    /**
     * Populates and return a FishingTripSummaryViewDTO with the inputParameters values.
     *
     * @param  fishingTripId
     * @param  reportDTOList
     * @param  summary
     * @return fishingTripSummaryViewDTO
     */
    private FishingTripSummaryViewDTO populateFishingTripSummary(String fishingTripId, List<ReportDTO> reportDTOList, Map<String, FishingActivityTypeDTO> summary) {
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO = new FishingTripSummaryViewDTO();
        fishingTripSummaryViewDTO.setActivityReports(reportDTOList);
        fishingTripSummaryViewDTO.setSummary(summary);

        // Fishing trip Id for the Fishing Trip summary view
        fishingTripSummaryViewDTO.setFishingTripId(fishingTripId);

        return fishingTripSummaryViewDTO;
    }



    private void populateFishingActivityReportListAndSummary(String fishingTripId, List<ReportDTO> reportDTOList,
                                                             Map<String, FishingActivityTypeDTO> summary,
                                                             Geometry multipolygon) throws ServiceException {
        List<FishingActivityEntity> fishingActivityList;
        try {
            fishingActivityList = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId);
        } catch (Exception e) {
            log.error("Error while trying to get Fishing Activity reports for fishing trip with Id:" + fishingTripId, e);
            return;
        }

        if (CollectionUtils.isEmpty(fishingActivityList)){
            return;
        }

        for (FishingActivityEntity activityEntity : fishingActivityList) {
            ReportDTO reportDTO = null;
            if (multipolygon != null) {
                if (activityEntity.getGeom().intersects(multipolygon)) {
                    reportDTO = FishingActivityMapper.INSTANCE.mapToReportDTO(activityEntity);
                }
            } else {
                reportDTO = FishingActivityMapper.INSTANCE.mapToReportDTO(activityEntity);
            }

            if (reportDTO != null && ActivityConstants.DECLARATION.equalsIgnoreCase(reportDTO.getFaReportDocumentType())) {
                // FA Report should be of type Declaration. And Fishing Activity type should be Either Departure,Arrival or Landing
                populateSummaryMap(reportDTO, summary);
            }
            reportDTOList.add(reportDTO);
        }
    }


    private void populateSummaryMap(ReportDTO reportDTO, Map<String, FishingActivityTypeDTO> summary) {
        if (ActivityConstants.DEPARTURE.equalsIgnoreCase(reportDTO.getActivityType())
                || ActivityConstants.ARRIVAL.equalsIgnoreCase(reportDTO.getActivityType())
                || ActivityConstants.LANDING.equalsIgnoreCase(reportDTO.getActivityType())) {
            Date occurrence = reportDTO.getOccurence();
            List<String> fluxLocations = reportDTO.getLocations();
            FishingActivityTypeDTO fishingActivityTypeDTO = summary.get(reportDTO.getActivityType());
            if (fishingActivityTypeDTO == null
                    || (reportDTO.isCorrection()
                    && fishingActivityTypeDTO.getDate() != null
                    && occurrence != null
                    && occurrence.compareTo(fishingActivityTypeDTO.getDate()) > 0)) {
                fishingActivityTypeDTO = new FishingActivityTypeDTO();
                fishingActivityTypeDTO.setDate(occurrence);
                fishingActivityTypeDTO.setLocations(fluxLocations);
                summary.put(reportDTO.getActivityType(), fishingActivityTypeDTO);
            }
        }
    }

    /**
     * Gets the counters for the trip with this tripId.
     *
     * @param tripId
     * @return
     */
    @Override
    public MessageCountDTO getMessageCountersForTripId(String tripId){
        return createMessageCounter(faReportDocumentDao.getFaReportDocumentsForTrip(tripId));
    }

    /**
     * Populates the MessageCounter adding to the right counter 1 unit depending on the type of report (typeCode, purposeCode, size()).
     *
     * @param faReportDocumentList
     */
    public MessageCountDTO createMessageCounter(List<FaReportDocumentEntity> faReportDocumentList){

        MessageCountDTO messagesCounter = new MessageCountDTO();
        if(CollectionUtils.isEmpty(faReportDocumentList)){
            return messagesCounter;
        }

        // Reports total
        messagesCounter.setNoOfReports(faReportDocumentList.size());

        for(FaReportDocumentEntity faReport : faReportDocumentList){
            String faDocumentType = faReport.getTypeCode();
            String purposeCode    = faReport.getFluxReportDocument().getPurpose();

            // Declarations / Notifications
            if (ActivityConstants.DECLARATION.equalsIgnoreCase(faDocumentType)) {
                messagesCounter.setNoOfDeclarations(messagesCounter.getNoOfDeclarations()+1);
            } else if (ActivityConstants.NOTIFICATION.equalsIgnoreCase(faDocumentType)) {
                messagesCounter.setNoOfNotifications(messagesCounter.getNoOfNotifications()+1);
            }

            // Fishing operations
            Set<FishingActivityEntity> faEntitiyList = faReport.getFishingActivities();
            if(CollectionUtils.isNotEmpty(faEntitiyList)){
                for(FishingActivityEntity faEntity : faEntitiyList){
                    if (ActivityConstants.FISHING_OPERATION.equalsIgnoreCase(faEntity.getTypeCode())){
                        messagesCounter.setNoOfFishingOperations(messagesCounter.getNoOfFishingOperations()+1);
                    }
                }
            }

            // PurposeCode : Deletions / Cancellations / Corrections
            if (ActivityConstants.DELETE.equalsIgnoreCase(purposeCode)){
                messagesCounter.setNoOfDeletions(messagesCounter.getNoOfDeletions()+1);
            } else if (ActivityConstants.CANCELLATION.equalsIgnoreCase(purposeCode)){
                messagesCounter.setNoOfCancellations(messagesCounter.getNoOfCancellations()+1);
            } else if (ActivityConstants.CORRECTION.equalsIgnoreCase(purposeCode)) {
                messagesCounter.setNoOfCorrections(messagesCounter.getNoOfCorrections() + 1);
            }
        }

        return messagesCounter;
    }

    /**
     * Retrieves all the catches for the given fishing trip;
     *
     * @param fishingTripId
     * @return
     */
    public Map<String, CatchSummaryListDTO> retrieveFaCatchesForFishingTrip(String fishingTripId){
        return FaCatchMapper.INSTANCE.mapCatchesToSummaryDTO(faCatchDao.findFaCatchesByFishingTrip(fishingTripId));
    }


}
