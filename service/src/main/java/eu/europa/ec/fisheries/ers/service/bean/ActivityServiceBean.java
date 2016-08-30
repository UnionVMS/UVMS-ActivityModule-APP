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
import eu.europa.ec.fisheries.ers.service.mapper.*;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.*;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FaReportDocumentDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.ContactParty;

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

    final static String FORMAT = "yyyy-MM-dd HH:mm:ss";
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

    final static Logger LOG = LoggerFactory.getLogger(ActivityServiceBean.class);

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
            LOG.info("Could not find FishingActivity entities matching search criteria");
            return Collections.emptyList();
        }

        List<FishingActivityReportDTO> dtos = FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTOList(activityList);

       return dtos;
    }


    // Get data for Fishing Trip summary view
    @Override
    public FishingTripSummaryViewDTO getFishingTripSummary(String fishingTripId) throws ServiceException {
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO = new FishingTripSummaryViewDTO();

        // get short summary of Fishing Trip
        FishingTripSummaryDTO fishingTripSummaryDTO = new FishingTripSummaryDTO();

        // Messages count box for Fishing Trip Summary view
        MessageCountDTO messagesCount = new MessageCountDTO();

        List<ReportDTO> reportDTOList = new ArrayList<>();
        // All Activity Reports and related data  for Fishing Trip
        getFishingActivityReportAndRelatedDataForFishingTrip(fishingTripId,reportDTOList,fishingTripSummaryDTO,messagesCount);
        fishingTripSummaryViewDTO.setActivityReports(reportDTOList);
        fishingTripSummaryViewDTO.setFishingTripSummaryDTO(fishingTripSummaryDTO);

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
           LOG.error("Error while trying to get current trip Id:",e);
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
            LOG.error("Error while trying to get Cronology for trip :"+tripID,e);
        }

        return cronologyList;
    }

    @Override
    public VesselDetailsTripDTO getVesselDetailsForFishingTrip(String fishingTripId ){

        VesselDetailsTripDTO vesselDetailsTripDTO = new VesselDetailsTripDTO();
        try {
                 FishingTripEntity fishingTrip = fishingTripDao.fetchVesselTransportDetailsForFishingTrip(fishingTripId);

                if(fishingTrip ==null || fishingTrip.getFishingActivity() ==null || fishingTrip.getFishingActivity().getFaReportDocument() == null
                    || fishingTrip.getFishingActivity().getFaReportDocument().getVesselTransportMeans() ==null)
                return vesselDetailsTripDTO;

                VesselTransportMeansEntity vesselTransportMeansEntity = fishingTrip.getFishingActivity().getFaReportDocument().getVesselTransportMeans();

                vesselDetailsTripDTO.setName(vesselTransportMeansEntity.getName());
                Set<VesselIdentifierEntity> vesselIdentifiers = vesselTransportMeansEntity.getVesselIdentifiers();
                for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
                    if ("EXT_MARK".equalsIgnoreCase(vesselIdentifier.getVesselIdentifierSchemeId())) {
                        vesselDetailsTripDTO.setExMark(vesselIdentifier.getVesselIdentifierId());
                    } else if ("IRCS".equalsIgnoreCase(vesselIdentifier.getVesselIdentifierSchemeId())) {
                        vesselDetailsTripDTO.setIrcs(vesselIdentifier.getVesselIdentifierId());
                    } else if ("CFR".equalsIgnoreCase(vesselIdentifier.getVesselIdentifierSchemeId())) {
                        vesselDetailsTripDTO.setCfr(vesselIdentifier.getVesselIdentifierId());
                    }
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
            LOG.error("Error while trying to get Vessel Details.",e);
        }

       return vesselDetailsTripDTO;
    }


    @Override
    public void  getFishingActivityReportAndRelatedDataForFishingTrip(String fishingTripId, List<ReportDTO> reportDTOList, FishingTripSummaryDTO fishingTripSummaryDTO,MessageCountDTO messagesCount) throws ServiceException {

        List<FishingActivityEntity> fishingActivityList;
        int reportsCnt=0;
        int declarations=0;
        int notifications=0;
        int corrections=0;
        int fishingOperations=0;


        try {
            fishingActivityList = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId, null);
        }catch(Exception e){
            LOG.error("Error while trying to get Fishing Activity reports for fishing trip with Id:"+fishingTripId,e);
            return;
        }

        if(fishingActivityList ==null || fishingActivityList.isEmpty())
             return;


        if(reportDTOList ==null)
              reportDTOList=new ArrayList<>();

        for(FishingActivityEntity activityEntity:fishingActivityList){

            ReportDTO reportDTO=new ReportDTO();
            reportDTO.setFishingActivityId(activityEntity.getId());
            String activityType=activityEntity.getTypeCode();
            reportDTO.setActivityType(activityType);

            if(ActivityConstants.FISHING_OPERATION.equalsIgnoreCase(activityType))
                fishingOperations++;

            FaReportDocumentEntity faReportDocumentEntity= activityEntity.getFaReportDocument();

            if(faReportDocumentEntity !=null) {
                reportDTO.setFaReportDocumentType(faReportDocumentEntity.getTypeCode());
                reportDTO.setFaReportAcceptedDateTime(faReportDocumentEntity.getAcceptedDatetime());
                if(ActivityConstants.DECLARATION.equalsIgnoreCase(reportDTO.getFaReportDocumentType())){
                    declarations++;
                }else if(ActivityConstants.NOTIFICATION.equalsIgnoreCase(reportDTO.getFaReportDocumentType())){
                    notifications++;
                }
                reportsCnt++;
                FluxReportDocumentEntity fluxReportDocument =faReportDocumentEntity.getFluxReportDocument();

                if(fluxReportDocument !=null ) {
                    reportDTO.setUniqueReportId(fluxReportDocument.getFluxReportDocumentId());
                    boolean isCorrection =false;
                    if(fluxReportDocument.getReferenceId() !=null && fluxReportDocument.getReferenceId().length()!=0) {
                        isCorrection = true;
                        corrections++;
                    }
                    reportDTO.setCorrection(isCorrection);

                }
            }

            reportDTO.setOccurence(activityEntity.getOccurence());
            reportDTO.setReason(activityEntity.getReasonCode());


            List<DelimitedPeriodDTO> delimitedPeriodDTOEntities=new ArrayList<>();
            for(DelimitedPeriodEntity dp:activityEntity.getDelimitedPeriods()){
                delimitedPeriodDTOEntities.add(DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodDTO(dp));
            }
            reportDTO.setDelimitedPeriod(delimitedPeriodDTOEntities);

            Set<FluxLocationEntity> fluxLocations= activityEntity.getFluxLocations();
            List<FluxLocationDTO> fluxLocationsDTOList=new ArrayList<>();
            for(FluxLocationEntity fluxLoc :fluxLocations ) {
                fluxLocationsDTOList.add(FluxLocationMapper.INSTANCE.mapToFluxLocationDTO(fluxLoc));
            }
            reportDTO.setFluxLocations(fluxLocationsDTOList);


            List<FluxCharacteristicsDTO> fluxCharacteristicsDTOList=new ArrayList<>();
            for(FluxCharacteristicEntity fluxCharacteristic:activityEntity.getFluxCharacteristics()){
                fluxCharacteristicsDTOList.add(FluxCharacteristicsMapper.INSTANCE.mapToFluxCharacteristicsDTO(fluxCharacteristic));
            }
            reportDTO.setFluxCharacteristics(fluxCharacteristicsDTOList);

            List<FishingGearDTO> fishingGearDTOList=new ArrayList<>();
            for(FishingGearEntity fishingGear:activityEntity.getFishingGears()){
               fishingGearDTOList.add(FishingGearMapper.INSTANCE.mapToFishingGearDTO(fishingGear));
            }
            reportDTO.setFishingGears(fishingGearDTOList);

            if(fishingTripSummaryDTO!=null && (ActivityConstants.DECLARATION.equalsIgnoreCase(reportDTO.getFaReportDocumentType()) && ((ActivityConstants.DEPARTURE.equalsIgnoreCase(activityType) || ActivityConstants.ARRIVAL.equalsIgnoreCase(activityType) || ActivityConstants.LANDING.equalsIgnoreCase(activityType)))) ){
                createFishingSummaryDTO(reportDTO,fishingTripSummaryDTO);
            }
            reportDTOList.add(reportDTO);
        }// end of for loop


        messagesCount.setNoOfCorrections(corrections);
        messagesCount.setNoOfDeclarations(declarations);
        messagesCount.setNoOfFishingOperations(fishingOperations);
        messagesCount.setNoOfNotifications(notifications);
        messagesCount.setNoOfReports(reportsCnt);



    }

     private void createFishingSummaryDTO(ReportDTO reportDTO,FishingTripSummaryDTO fishingTripSummaryDTO){
         Date occurence= reportDTO.getOccurence();
         List<FluxLocationDTO> fluxLocations =reportDTO.getFluxLocations();
              switch(reportDTO.getActivityType()){
                  case ActivityConstants.DEPARTURE:  // Check if it is the first time you are assigning the value. If it is the first time, assign whatever value you have for Departure
                                                     if( (fishingTripSummaryDTO.getDepartureDate() ==null && (fishingTripSummaryDTO.getDepartureLocations() == null || fishingTripSummaryDTO.getDepartureLocations().isEmpty()))  || (reportDTO.isCorrection() && fishingTripSummaryDTO.getDepartureDate()!=null &&  occurence.compareTo(fishingTripSummaryDTO.getDepartureDate())>0)){
                                                          fishingTripSummaryDTO.setDepartureDate(occurence);
                                                          fishingTripSummaryDTO.setDepartureLocations(fluxLocations);}
                                                     break;
                  case ActivityConstants.ARRIVAL:  // Check if it is the first time you are assigning the value. If it is the first time, assign whatever value you have for Departure
                                                  if((fishingTripSummaryDTO.getArrivalDate() ==null && (fishingTripSummaryDTO.getArrivalLocations() == null || fishingTripSummaryDTO.getArrivalLocations().isEmpty())) || (reportDTO.isCorrection() && fishingTripSummaryDTO.getArrivalDate()!=null &&  occurence.compareTo(fishingTripSummaryDTO.getArrivalDate())>0)){
                                                      fishingTripSummaryDTO.setArrivalDate(occurence);
                                                      fishingTripSummaryDTO.setArrivalLocations(fluxLocations);}
                                                    break;
                  case ActivityConstants.LANDING:  // Check if it is the first time you are assigning the value. If it is the first time, assign whatever value you have for Departure
                                                  if(fishingTripSummaryDTO.getLandingDate() ==null && (fishingTripSummaryDTO.getLandingLocations() == null || fishingTripSummaryDTO.getLandingLocations().isEmpty())  || (reportDTO.isCorrection() && fishingTripSummaryDTO.getLandingDate()!=null &&  occurence.compareTo(fishingTripSummaryDTO.getLandingDate())>0)){
                                                      fishingTripSummaryDTO.setLandingDate(occurence);
                                                      fishingTripSummaryDTO.setLandingLocations(fluxLocations); }
                                                  break;

              }
     }
}