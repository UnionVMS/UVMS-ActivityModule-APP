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
import eu.europa.ec.fisheries.ers.fa.utils.ActivityType;
import eu.europa.ec.fisheries.ers.service.mapper.*;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.*;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FaReportDocumentDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FluxLocationDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;


import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
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
    public List<FaReportCorrectionDTO> getFaReportCorrections(String selectedFaReportId) throws ServiceException {
        List<FaReportDocumentEntity> faReportDocumentEntities = getReferencedFaReportDocuments(selectedFaReportId);
        List<FaReportCorrectionDTO> faReportCorrectionDTOs = FaReportDocumentMapper.INSTANCE.mapToFaReportCorrectionDtoList(faReportDocumentEntities);
        if (!faReportCorrectionDTOs.isEmpty()) {
            log.info("Sort collection by date if the list is not empty");
            Collections.sort(faReportCorrectionDTOs);
        }
        return faReportCorrectionDTOs;
    }

    private List<FaReportDocumentEntity> getReferencedFaReportDocuments(String referenceId) throws ServiceException {
        if (referenceId == null) {
            return Collections.emptyList();
        }
        log.info("Find reference fishing activity report for  : " + referenceId);
        List<FaReportDocumentEntity> allFaReportDocuments = new ArrayList<>();
        List<FaReportDocumentEntity> faReportDocumentEntities = faReportDocumentDao.findFaReportsByReferenceId(referenceId);
        allFaReportDocuments.addAll(faReportDocumentEntities);
        for (FaReportDocumentEntity faReportDocumentEntity : faReportDocumentEntities) {  //Find all the referenced FA Report recursively
            allFaReportDocuments.addAll(getReferencedFaReportDocuments(faReportDocumentEntity.getFluxReportDocument().getFluxReportDocumentId()));
        }
        return allFaReportDocuments;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public FaReportDocumentDetailsDTO getFaReportDocumentDetails(String faReportDocumentId) throws ServiceException {
        log.info("Find Fa Report document for report Id : " + faReportDocumentId);
        List<FaReportDocumentEntity> faReportDocumentEntities = faReportDocumentDao.findFaReportsByIds(Arrays.asList(faReportDocumentId));
        if (faReportDocumentEntities == null || faReportDocumentEntities.isEmpty()) {
            throw new ServiceException("Report Does not Exist");
        }
        FaReportDocumentEntity faReportDocumentEntity = faReportDocumentEntities.get(0);
        log.info("Map first element from the list to DTO");
        return FaReportDocumentMapper.INSTANCE.mapToFaReportDocumentDetailsDTO(faReportDocumentEntity);
    }

    @Override
    public List<FishingActivityReportDTO> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        List<FishingActivityEntity> activityList;
        if(query.getSearchCriteria() ==null || query.getSearchCriteria().isEmpty()){
            activityList =  fishingActivityDao.getFishingActivityList(query.getPagination());
        }else{
            activityList = fishingActivityDao.getFishingActivityListByQuery(query);
        }


        if(activityList==null || activityList.isEmpty()){
            log.info("Could not find FishingActivity entities matching search criteria");
            return Collections.emptyList();
        }

        return FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTOList(activityList);
    }


    // Get data for Fishing Trip summary view
    @Override
    public FishingTripSummaryViewDTO getFishingTripSummary(String fishingTripId) throws ServiceException {
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO = new FishingTripSummaryViewDTO();

        // Messages count box for Fishing Trip Summary view
        MessageCountDTO messagesCount = new MessageCountDTO();

        List<ReportDTO> reportDTOList = new ArrayList<>();

        // get short summary of Fishing Trip
        Map<String,FishingActivityTypeDTO> summary = new HashMap<>();
        // All Activity Reports and related data  for Fishing Trip
        getFishingActivityReportAndRelatedDataForFishingTrip(fishingTripId,reportDTOList,summary,messagesCount);
        fishingTripSummaryViewDTO.setActivityReports(reportDTOList);
       // fishingTripSummaryViewDTO.setFishingTripSummaryDTO(fishingTripSummaryDTO);
        fishingTripSummaryViewDTO.setSummary(summary);

        // Fishing trip Id for the Fishing Trip summary view
        fishingTripSummaryViewDTO.setFishingTripId(fishingTripId);

        // Vessel Details for specified Fishing Trip
        fishingTripSummaryViewDTO.setVesselDetails(getVesselDetailsForFishingTrip(fishingTripId));
        fishingTripSummaryViewDTO.setMessagesCount(messagesCount);

        // Fishing TripID cronology
        fishingTripSummaryViewDTO.setCronology(getCronologyForTripIds(fishingTripId,2));

        // Current Fishing Trip ID
        fishingTripSummaryViewDTO.setCurrentTripId(getCurrentTripId());
        return fishingTripSummaryViewDTO;
    }

    // Current Fishing Trip ID in the system
    @Override
    public String getCurrentTripId(){
        String currentTripId=null;
       try {
           currentTripId = fishingTripIdentifierDao.getCurrentTrip();
       }catch(Exception e){
           log.error("Error while trying to get current trip Id:",e);
       }
        return currentTripId;
    }

    @Override
    public List<CronologyDTO> getCronologyForTripIds(String tripID,int numberOfTripsBeforeAndAfter){

        List<CronologyDTO> cronologyList =  new ArrayList<>();
        try {
            List<Object[]> tripIdListBefore = fishingTripIdentifierDao.getFishingTripsBefore(tripID, numberOfTripsBeforeAndAfter);

            if(tripIdListBefore!=null && !tripIdListBefore.isEmpty() ) {
                for (int i = tripIdListBefore.size() - 1; i >= 0; i--) {
                    Object[] tripIdAndDate = tripIdListBefore.get(i);
                    cronologyList.add(new CronologyDTO("" + tripIdAndDate[0], tripIdAndDate[1].toString()));
                }
            }

            List<Object[]> tripIdListAfter = fishingTripIdentifierDao.getFishingTripsAfter(tripID, numberOfTripsBeforeAndAfter);
            if(tripIdListAfter!=null) {
                for (Object[] tripIdAndDate : tripIdListAfter) {
                    cronologyList.add(new CronologyDTO("" + tripIdAndDate[0], tripIdAndDate[1].toString()));
                }
            }

        }catch(Exception e){
            log.error("Error while trying to get Cronology for trip :"+tripID,e);
        }

        return cronologyList;
    }

    @Override
    public VesselDetailsTripDTO getVesselDetailsForFishingTrip(String fishingTripId ){

        VesselDetailsTripDTO vesselDetailsTripDTO = new VesselDetailsTripDTO();
        try {
                 FishingTripEntity fishingTrip = fishingTripDao.fetchVesselTransportDetailsForFishingTrip(fishingTripId);

                if(fishingTrip ==null || fishingTrip.getFishingActivity() ==null || fishingTrip.getFishingActivity().getFaReportDocument() == null
                    || fishingTrip.getFishingActivity().getFaReportDocument().getVesselTransportMeans() ==null) {
                    return vesselDetailsTripDTO;
                }

                VesselTransportMeansEntity vesselTransportMeansEntity = fishingTrip.getFishingActivity().getFaReportDocument().getVesselTransportMeans();
                vesselDetailsTripDTO.setName(vesselTransportMeansEntity.getName());
                Set<VesselIdentifierEntity> vesselIdentifiers = vesselTransportMeansEntity.getVesselIdentifiers();
                for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
                    setVesselIdentifierDetails(vesselIdentifier,vesselDetailsTripDTO);
                }
                RegistrationEventEntity registrationEventEntity = vesselTransportMeansEntity.getRegistrationEvent();
                if (registrationEventEntity != null && registrationEventEntity.getRegistrationLocation() != null)
                    vesselDetailsTripDTO.setFlagState(registrationEventEntity.getRegistrationLocation().getLocationCountryId());

                Set<ContactPartyEntity> contactParties = vesselTransportMeansEntity.getContactParty();
                 if(contactParties !=null && !contactParties.isEmpty() ) {
                     ContactPartyEntity contactParty= contactParties.iterator().next();
                     ContactPersonEntity contactPerson = contactParties.iterator().next().getContactPerson();
                     Set<StructuredAddressEntity> structuredAddresses = contactParty.getStructuredAddresses();

                     if (contactPerson != null && structuredAddresses != null && !structuredAddresses.isEmpty()) {
                         vesselDetailsTripDTO.setContactPerson(ContactPersonMapper.INSTANCE.mapToContactPersonDetailsDTO(contactPerson));
                         vesselDetailsTripDTO.setStructuredAddress(StructuredAddressMapper.INSTANCE.mapToAddressDetailsDTO(structuredAddresses.iterator().next()));
                     }
                 }


        }catch(Exception e){
            log.error("Error while trying to get Vessel Details.",e);
        }

       return vesselDetailsTripDTO;
    }

    private void setVesselIdentifierDetails(VesselIdentifierEntity vesselIdentifier,VesselDetailsTripDTO vesselDetailsTripDTO){
        if ("EXT_MARK".equalsIgnoreCase(vesselIdentifier.getVesselIdentifierSchemeId())) {
            vesselDetailsTripDTO.setExMark(vesselIdentifier.getVesselIdentifierId());
        } else if ("IRCS".equalsIgnoreCase(vesselIdentifier.getVesselIdentifierSchemeId())) {
            vesselDetailsTripDTO.setIrcs(vesselIdentifier.getVesselIdentifierId());
        } else if ("CFR".equalsIgnoreCase(vesselIdentifier.getVesselIdentifierSchemeId())) {
            vesselDetailsTripDTO.setCfr(vesselIdentifier.getVesselIdentifierId());
        }
    }


    @Override
    public void  getFishingActivityReportAndRelatedDataForFishingTrip(String fishingTripId, List<ReportDTO> reportDTOList, Map<String,FishingActivityTypeDTO> summary,MessageCountDTO messagesCount) throws ServiceException {

        List<FishingActivityEntity> fishingActivityList;
        int reportsCnt=0;
        int declarations=0;
        int notifications=0;
        int corrections=0;
        int fishingOperations=0;


        try {
            fishingActivityList = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId, null);
        }catch(Exception e){
            log.error("Error while trying to get Fishing Activity reports for fishing trip with Id:"+fishingTripId,e);
            return;
        }

        if(fishingActivityList ==null || fishingActivityList.isEmpty())
             return;

        if(reportDTOList ==null)
              reportDTOList=new ArrayList<>();

        for(FishingActivityEntity activityEntity:fishingActivityList){

            ReportDTO reportDTO= FishingActivityMapper.INSTANCE.mapToReportDTO(activityEntity);

            if(ActivityConstants.DECLARATION.equalsIgnoreCase(reportDTO.getFaReportDocumentType())){
                declarations++;
            }else if(ActivityConstants.NOTIFICATION.equalsIgnoreCase(reportDTO.getFaReportDocumentType())){
                notifications++;
            }

            if(reportDTO.isCorrection())
                corrections++;

            if(reportDTO.getUniqueReportId() !=null)
                reportsCnt++;

            String activityType =reportDTO.getActivityType();
            if(ActivityConstants.FISHING_OPERATION.equalsIgnoreCase(activityType))
                fishingOperations++;

            // FA Report should be of type Declaration. And Fishing Activity type should be Either Departure,Arrival or Landing
            if(ActivityConstants.DECLARATION.equalsIgnoreCase(reportDTO.getFaReportDocumentType()) ){
                //createFishingSummaryDTO(reportDTO,fishingTripSummaryDTO);
                populateSummaryMap(reportDTO,summary);
            }
            reportDTOList.add(reportDTO);
        }// end of for loop


        messagesCount.setNoOfCorrections(corrections);
        messagesCount.setNoOfDeclarations(declarations);
        messagesCount.setNoOfFishingOperations(fishingOperations);
        messagesCount.setNoOfNotifications(notifications);
        messagesCount.setNoOfReports(reportsCnt);
    }

    private void populateSummaryMap(ReportDTO reportDTO,Map<String,FishingActivityTypeDTO> summary){
        if(ActivityConstants.DEPARTURE.equalsIgnoreCase(reportDTO.getActivityType()) || ActivityConstants.ARRIVAL.equalsIgnoreCase(reportDTO.getActivityType()) || ActivityConstants.LANDING.equalsIgnoreCase(reportDTO.getActivityType())) {
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