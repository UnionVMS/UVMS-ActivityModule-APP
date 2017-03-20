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

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselTransportMeansDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.ActivityConstants;
import eu.europa.ec.fisheries.ers.fa.utils.UsmUtils;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.SpatialModuleService;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.CronologyTripDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.FishingActivityTypeDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.FishingTripSummaryViewDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.MessageCountDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.ReportDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.IdentifierDto;
import eu.europa.ec.fisheries.ers.service.mapper.BaseMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FaCatchMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingTripToGeoJsonMapper;
import eu.europa.ec.fisheries.ers.service.mapper.VesselTransportMeansMapper;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.builder.FishingTripSearchBuilder;
import eu.europa.ec.fisheries.uvms.activity.message.producer.AssetProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.common.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteria;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

@Stateless
@Local(FishingTripService.class)
@Transactional
@Slf4j
public class FishingTripServiceBean extends BaseActivityBean implements FishingTripService {

    private static final String PREVIOUS = "PREVIOUS";
    private static final String NEXT = "NEXT";

    @EJB
    private SpatialModuleService spatialModule;

    @EJB
    private ActivityService activityServiceBean;

    @EJB
    private AssetProducerBean assetProducerBean;

    @EJB
    private AssetModuleService assetModuleService;

    private FaReportDocumentDao faReportDocumentDao;
    private FishingActivityDao fishingActivityDao;
    private VesselIdentifierDao vesselIdentifierDao;
    private VesselTransportMeansDao vesselTransportMeansDao;
    private FishingTripIdentifierDao fishingTripIdentifierDao;
    private FishingTripDao fishingTripDao;
    private FaCatchDao faCatchDao;

    @PostConstruct
    public void init() {
        initEntityManager();
        fishingTripIdentifierDao = new FishingTripIdentifierDao(getEntityManager());
        vesselIdentifierDao = new VesselIdentifierDao(getEntityManager());
        fishingActivityDao = new FishingActivityDao(getEntityManager());
        faReportDocumentDao = new FaReportDocumentDao(getEntityManager());
        faCatchDao = new FaCatchDao(getEntityManager());
        fishingTripDao = new FishingTripDao(getEntityManager());
        vesselTransportMeansDao = new VesselTransportMeansDao(em);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronologyTripDTO getCronologyOfFishingTrip(String tripId, Integer count) throws ServiceException {
        List<VesselIdentifierEntity> latestVesselIdentifiers = vesselIdentifierDao.getLatestVesselIdByTrip(tripId); // Find the latest Vessel for the Trip for finding the trip of that vessel
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
            count = count - 1; // FIXME squid:S1226 intoduce a new variable instead of reusing
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
    public VesselDetailsDTO getVesselDetailsForFishingTrip(String fishingTripId) throws ServiceException {

        VesselDetailsDTO result;

        try {

            VesselTransportMeansEntity latestVesselByTripId = vesselTransportMeansDao.findLatestVesselByTripId(fishingTripId);
            result = VesselTransportMeansMapper.INSTANCE.map(latestVesselByTripId);
            enrichWithAssetsModuleDataIfNeeded(result);

        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage(), e);
        }

        return result;
    }

    private void enrichWithAssetsModuleDataIfNeeded(VesselDetailsDTO vesselDetailsDTO) {

        if (vesselDetailsDTO != null && vesselDetailsDTO.hasEmptyIdentifierValues()) {

            try {

                Set<IdentifierDto> vesselIdentifiers = vesselDetailsDTO.getVesselIdentifiers();
                List<AssetListCriteriaPair> assetListCriteriaPairs = BaseMapper.mapToAssetListCriteriaPairList(vesselIdentifiers);
                AssetListCriteria criteria = new AssetListCriteria();
                criteria.getCriterias().addAll(assetListCriteriaPairs);
                AssetListQuery query = new AssetListQuery();
                query.setAssetSearchCriteria(criteria);
                List<Asset> assetList = assetModuleService.getAssetListResponse(query);
                vesselDetailsDTO.enrichIdentifiers(assetList.get(0));

            } catch (ServiceException e) {
                log.error("Error while trying to send message to Assets module.", e);
            }
        }
    }

    @Override
    public FishingTripSummaryViewDTO getFishingTripSummaryAndReports(String fishingTripId, List<Dataset> datasets) throws ServiceException {
        List<ReportDTO> reportDTOList = new ArrayList<>();
        Map<String, FishingActivityTypeDTO> summary = new HashMap<>();
        Geometry multipolygon = getRestrictedAreaGeom(datasets);
        populateFishingActivityReportListAndSummary(fishingTripId, reportDTOList, summary, multipolygon);
        return populateFishingTripSummary(fishingTripId, reportDTOList, summary);
    }

    private Geometry getRestrictedAreaGeom(List<Dataset> datasets) throws ServiceException {
        if (CollectionUtils.isEmpty(datasets)) {
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
            if (isNotEmpty(faEntitiyList)) {
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
        return new FishingTripSearchBuilder().buildFishingTripSearchRespose(fishingTripList);
    }

}
