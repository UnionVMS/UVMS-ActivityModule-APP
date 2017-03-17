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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselIdentifiersDao;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.ActivityConstants;
import eu.europa.ec.fisheries.ers.fa.utils.UsmUtils;
import eu.europa.ec.fisheries.ers.message.producer.ActivityMessageProducer;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.SpatialModuleService;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.AddressDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.ContactPersonDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.CronologyTripDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.FishingActivityTypeDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.FishingTripSummaryViewDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.MessageCountDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.ReportDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.VesselDetailsTripDTO;
import eu.europa.ec.fisheries.ers.service.mapper.AssetsRequestMapper;
import eu.europa.ec.fisheries.ers.service.mapper.ContactPersonMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FaCatchMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingTripIdWithGeometryMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingTripToGeoJsonMapper;
import eu.europa.ec.fisheries.ers.service.mapper.StructuredAddressMapper;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.ers.service.search.SortKey;
import eu.europa.ec.fisheries.ers.service.search.builder.FishingTripSearchBuilder;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivitySummary;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.common.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetFault;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by padhyad on 9/22/2016.
 */
@Stateless
@Local(FishingTripService.class)
@Transactional
@Slf4j
public class FishingTripServiceBean implements FishingTripService {

    private static final String PREVIOUS = "PREVIOUsS";
    private static final String NEXT = "NEXT";
    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;
    @EJB
    private ActivityMessageProducer activityProducer;
    @EJB
    private AssetsMessageConsumerBean activityConsumer;
    @EJB
    private SpatialModuleService spatialModule;
    @EJB
    private ActivityService activityServiceBean;
    private FaReportDocumentDao faReportDocumentDao;
    private FishingActivityDao fishingActivityDao;
    private VesselIdentifiersDao vesselIdentifiersDao;
    private FishingTripIdentifierDao fishingTripIdentifierDao;
    private FishingTripDao fishingTripDao;
    private FaCatchDao faCatchDao;

    @PostConstruct
    public void init() {
        fishingTripIdentifierDao = new FishingTripIdentifierDao(em);
        vesselIdentifiersDao = new VesselIdentifiersDao(em);
        fishingActivityDao = new FishingActivityDao(em);
        faReportDocumentDao = new FaReportDocumentDao(em);
        faCatchDao = new FaCatchDao(em);
        fishingTripDao = new FishingTripDao(em);
    }

    /**
     * {@inheritDoc}
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
                previous = count / 2;
                next = count / 2;
            } else if (count % 2 == 1) {
                previous = count / 2 + 1;
                next = count / 2;
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
        return ImmutableMap.<String, Integer>builder().put(PREVIOUS, previous).put(NEXT, next).build();
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

        List<VesselIdentifierEntity> latestVesselIdentifiers = vesselIdentifiersDao.getLatestVesselIdByTrip(fishingTripId);
        if (CollectionUtils.isEmpty(latestVesselIdentifiers)
                || latestVesselIdentifiers.get(0) == null
                || latestVesselIdentifiers.get(0).getVesselTransportMeans() == null) {
            return vesselDetailsTripDTO;
        }

        VesselTransportMeansEntity vesselTransportMeansEntity = latestVesselIdentifiers.get(0).getVesselTransportMeans();

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
        if (registrationEventEntity != null && registrationEventEntity.getRegistrationLocation() != null) {
            vesselDetailsTripDTO.setFlagState(registrationEventEntity.getRegistrationLocation().getLocationCountryId());
        }

        // Fill the contactPersons List and check if is captain.
        Set<ContactPartyEntity> contactParties = vesselTransportMeansEntity.getContactParty();
        Set<ContactPersonDetailsDTO> contactPersonsListDTO = vesselDetailsTripDTO.getContactPersons();
        if (CollectionUtils.isNotEmpty(contactParties)) {
            for (ContactPartyEntity contactParty : contactParties) {
                ContactPersonDetailsDTO contactPersDTO = ContactPersonMapper.INSTANCE.mapToContactPersonDetailsWithRolesDTO(contactParty.getContactPerson(), contactParty.getContactPartyRole());
                Set<StructuredAddressEntity> structuredAddresses = contactParty.getStructuredAddresses();

                Set<AddressDetailsDTO> addressDetailsDTOS = StructuredAddressMapper.INSTANCE.mapToAddressDetailsDTOList(structuredAddresses);
                if (!CollectionUtils.isEmpty(addressDetailsDTOS)) {
                    contactPersDTO.setAdresses(new ArrayList<>(addressDetailsDTOS));
                }
                checkAndSetIsCaptain(contactPersDTO, contactParty);
                contactPersonsListDTO.add(contactPersDTO);
            }
            vesselDetailsTripDTO.setContactPersons(contactPersonsListDTO);
        }

        // If some data are missing from the current DTOs then will make a call to
        // ASSETS module with the data we already have to enrich it.
        enrichWithAssetsModuleDataIfNeeded(vesselDetailsTripDTO);

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
        if (CollectionUtils.isNotEmpty(contactPartyRoles)) {
            for (ContactPartyRoleEntity roleEntity : contactPartyRoles) {
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
        if (someVesselDetailsAreMissing(vesselDetailsTripDTO)) {
            String response = null;
            TextMessage message = null;
            try {
                String assetsRequest = AssetsRequestMapper.mapToAssetsRequest(vesselDetailsTripDTO);

                String messageID = activityProducer.sendAssetsModuleSynchronousMessage(assetsRequest);
                message = activityConsumer.getMessage(messageID, TextMessage.class);
                response = message.getText();
            } catch (Exception e) {
                log.error("Error while trying to send message to Assets module.", e);
            }
            if (isFaultMessage(message)) {
                log.error("The Asset module responded with a fault message related to Vessel Details Enrichment: ", response);
                log.debug("The original VesselDetailsTripDTO that the request for enrichment was made for : ", vesselDetailsTripDTO.toString());
                return;
            }
            if (StringUtils.isNotEmpty(response)) {
                try {
                    ListAssetResponse listResp = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
                    AssetsRequestMapper.mapAssetsResponseToVesselDetailsTripDTO(listResp, vesselDetailsTripDTO);
                } catch (ActivityModelMarshallException e) {
                    log.error("Error while trying to unmarshall response from Asset Module regarding VesselDetailsTripDTO enrichment", e);
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
        } catch (ActivityModelMarshallException e) {
            log.info(e.getMessage(), e);
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
     * @param vesselIdentifier
     * @param vesselDetailsTripDTO
     */
    private void setVesselIdentifierDetails(VesselIdentifierEntity vesselIdentifier, VesselDetailsTripDTO vesselDetailsTripDTO) {
        String fieldName = vesselIdentifier.getVesselIdentifierSchemeId().toUpperCase();
        String fieldValue = vesselIdentifier.getVesselIdentifierId();
        switch (VesselIdentifierSchemeIdEnum.valueOf(fieldName)) {
            case EXT_MARK:
                vesselDetailsTripDTO.setExMark(fieldValue);
                break;
            case IRCS:
                vesselDetailsTripDTO.setIrcs(fieldValue);
                break;
            case CFR:
                vesselDetailsTripDTO.setCfr(fieldValue);
                break;
            case UVI:
                vesselDetailsTripDTO.setUvi(fieldValue);
                break;
            case ICCAT:
                vesselDetailsTripDTO.setIccat(fieldValue);
                break;
            case GFCM:
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
        Map<String, FishingActivityTypeDTO> summary = new HashMap<>(); // get short summary of Fishing Trip
        Geometry multipolygon = getRestrictedAreaGeom(datasets); // All Activity Reports and related data  for Fishing Trip
        populateFishingActivityReportListAndSummary(fishingTripId, reportDTOList, summary, multipolygon);
        return populateFishingTripSummary(fishingTripId, reportDTOList, summary);
    }

    private Geometry getRestrictedAreaGeom(List<Dataset> datasets) throws ServiceException {
        if (datasets == null || datasets.isEmpty()) {
            return null;
        }

        try {
            List<AreaIdentifierType> areaIdentifierTypes = UsmUtils.convertDataSetToAreaId(datasets);
            String areaWkt = spatialModule.getFilteredAreaGeom(areaIdentifierTypes);
            Geometry geometry = GeometryMapper.INSTANCE.wktToGeometry(areaWkt).getValue();
            geometry.setSRID(GeometryUtils.DEFAULT_EPSG_SRID);
            return geometry;
        } catch (ParseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * Populates and return a FishingTripSummaryViewDTO with the inputParameters values.
     *
     * @param fishingTripId
     * @param reportDTOList
     * @param summary
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
        List<FishingActivityEntity> fishingActivityList = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId, multipolygon);
        if (CollectionUtils.isEmpty(fishingActivityList)) {
            return;
        }

        for (FishingActivityEntity activityEntity : fishingActivityList) {
            ReportDTO reportDTO = FishingActivityMapper.INSTANCE.mapToReportDTO(activityEntity);
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
    public MessageCountDTO getMessageCountersForTripId(String tripId) {
        return createMessageCounter(faReportDocumentDao.getFaReportDocumentsForTrip(tripId));
    }

    /**
     * Populates the MessageCounter adding to the right counter 1 unit depending on the type of report (typeCode, purposeCode, size()).
     *
     * @param faReportDocumentList
     */
    public MessageCountDTO createMessageCounter(List<FaReportDocumentEntity> faReportDocumentList) {

        MessageCountDTO messagesCounter = new MessageCountDTO();
        if (CollectionUtils.isEmpty(faReportDocumentList)) {
            return messagesCounter;
        }

        // Reports total
        messagesCounter.setNoOfReports(faReportDocumentList.size());

        for (FaReportDocumentEntity faReport : faReportDocumentList) {
            String faDocumentType = faReport.getTypeCode();
            String purposeCode = faReport.getFluxReportDocument().getPurpose();

            // Declarations / Notifications
            if (ActivityConstants.DECLARATION.equalsIgnoreCase(faDocumentType)) {
                messagesCounter.setNoOfDeclarations(messagesCounter.getNoOfDeclarations() + 1);
            } else if (ActivityConstants.NOTIFICATION.equalsIgnoreCase(faDocumentType)) {
                messagesCounter.setNoOfNotifications(messagesCounter.getNoOfNotifications() + 1);
            }

            // Fishing operations
            Set<FishingActivityEntity> faEntitiyList = faReport.getFishingActivities();
            if (CollectionUtils.isNotEmpty(faEntitiyList)) {
                for (FishingActivityEntity faEntity : faEntitiyList) {
                    if (ActivityConstants.FISHING_OPERATION.equalsIgnoreCase(faEntity.getTypeCode())) {
                        messagesCounter.setNoOfFishingOperations(messagesCounter.getNoOfFishingOperations() + 1);
                    }
                }
            }

            // PurposeCode : Deletions / Cancellations / Corrections
            if (ActivityConstants.DELETE.equalsIgnoreCase(purposeCode)) {
                messagesCounter.setNoOfDeletions(messagesCounter.getNoOfDeletions() + 1);
            } else if (ActivityConstants.CANCELLATION.equalsIgnoreCase(purposeCode)) {
                messagesCounter.setNoOfCancellations(messagesCounter.getNoOfCancellations() + 1);
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
    @Override
    public Map<String, CatchSummaryListDTO> retrieveFaCatchesForFishingTrip(String fishingTripId) {
        return FaCatchMapper.INSTANCE.mapCatchesToSummaryDTO(faCatchDao.findFaCatchesByFishingTrip(fishingTripId));
    }

    /**
     * Retrieve GEO data for fishing trip Map for tripID
     *
     * @param tripId
     * @return
     */
    @Override
    public ObjectNode getTripMapDetailsForTripId(String tripId) {

        log.info("Get GEO data for Fishing Trip for tripId:" + tripId);
        List<FaReportDocumentEntity> faReportDocumentEntityList = faReportDocumentDao.getLatestFaReportDocumentsForTrip(tripId);
        List<Geometry> geoList = new ArrayList<>();
        for (FaReportDocumentEntity entity : faReportDocumentEntityList) {
            if (entity.getGeom() != null)
                geoList.add(entity.getGeom());

        }
        return FishingTripToGeoJsonMapper.toJson(geoList);
    }

    @Override
    public FishingTripResponse getFishingTripIdsForFilter(FishingActivityQuery query) throws ServiceException {
        log.info("getFishingTripResponse For Filter");
        if ((MapUtils.isEmpty(query.getSearchCriteriaMap()) && MapUtils.isEmpty(query.getSearchCriteriaMapMultipleValues()))
                || activityServiceBean.checkAndEnrichIfVesselFiltersArePresent(query)) {
            return new FishingTripResponse();
        }
        List<FishingTripEntity> fishingTripList = fishingTripDao.getFishingTripsForMatchingFilterCriteria(query);
        log.debug("Fishing trips received from db:" + fishingTripList.size());

        // build Fishing trip response from FishingTripEntityList and return
        return buildFishingTripSearchRespose(fishingTripList);
    }

    public FishingTripResponse buildFishingTripSearchRespose(List<FishingTripEntity> fishingTripList) throws ServiceException {
        if (fishingTripList == null || fishingTripList.isEmpty()) {
            return new FishingTripResponse();
        }
        FishingTripSearchBuilder fishingTripSearchBuilder=new FishingTripSearchBuilder();
        //  List<FishingTripIdWithGeometry> fishingTripIdLists = new ArrayList<>(); // List of unique fishing trip ids with geometry
        List<FishingActivitySummary> fishingActivityLists = new ArrayList<>(); // List of FishingActivities with details required by response
        Set<FishingTripId> fishingTripIdsWithoutGeom = new HashSet<>();  // List of unique fishing Trip ids without geometry information

        Map<FishingTripId, List<Geometry>> uniqueTripIdWithGeometry = new HashMap<>(); // Stores unique Fishing tripIds and Geometries associated with its FA Report


        fishingTripSearchBuilder.processFishingTripsToCollectUniqueTrips(fishingTripList, uniqueTripIdWithGeometry, fishingActivityLists, fishingTripIdsWithoutGeom); // process data to find out unique FishingTrip with their Geometries
        fishingTripSearchBuilder.checkThresholdForFishingTripList(uniqueTripIdWithGeometry); // Check if the size of unique Fishing trips is withing threshold specified
        List<FishingTripIdWithGeometry> fishingTripIdLists= getFishingTripIdWithGeometryList(uniqueTripIdWithGeometry); // Convert list of Geometries to WKT
       // fishingTripIdLists.addAll(fishingTripSearchBuilder.addFishingTripIdsWithoutGeomToResponseList(fishingTripIdsWithoutGeom)); // There could be some fishing trips without geometries, consider those trips as well

        // populate response object
        FishingTripResponse response = new FishingTripResponse();
        response.setFishingActivityLists(fishingActivityLists);
        response.setFishingTripIdLists(fishingTripIdLists);
        return response;
    }


    public List<FishingTripIdWithGeometry> getFishingTripIdWithGeometryList(Map<FishingTripId, List<Geometry>> uniqueTripIdWithGeometry) throws ServiceException {
        List<FishingTripIdWithGeometry> fishingTripIdLists = new ArrayList<>();
        Set<FishingTripId> tripIdSet = uniqueTripIdWithGeometry.keySet();
        for (FishingTripId fishingTripId : tripIdSet) {
            Geometry geometry = GeometryUtils.createMultipoint(uniqueTripIdWithGeometry.get(fishingTripId));

            FishingActivityQuery query = new FishingActivityQuery();
            Map<SearchFilter, String> searchCriteriaMap = new EnumMap<>(SearchFilter.class);
            searchCriteriaMap.put(SearchFilter.TRIP_ID,fishingTripId.getTripId());
            query.setSearchCriteriaMap(searchCriteriaMap);
            SortKey sortKey = new SortKey();
            sortKey.setSortBy(SearchFilter.PERIOD_START);
            sortKey.setReversed(false);
            query.setSorting(sortKey);
            List<FishingTripEntity> fishingTripList = fishingTripDao.getFishingTripsForMatchingFilterCriteria(query);

            if (geometry == null) {
                fishingTripIdLists.add(FishingTripIdWithGeometryMapper.INSTANCE.mapToFishingTripIdWithGeometry(fishingTripId, null,fishingTripList));
            }
            else {
                fishingTripIdLists.add(FishingTripIdWithGeometryMapper.INSTANCE.mapToFishingTripIdWithGeometry(fishingTripId, GeometryMapper.INSTANCE.geometryToWkt(geometry).getValue(),fishingTripList));
            }
        }

        return fishingTripIdLists;
    }



}
