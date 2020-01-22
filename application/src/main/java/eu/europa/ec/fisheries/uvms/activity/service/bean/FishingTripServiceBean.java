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

package eu.europa.ec.fisheries.uvms.activity.service.bean;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.VesselTransportMeansDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselStorageCharacteristicsEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.UsmUtils;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivitySummary;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.service.ActivityService;
import eu.europa.ec.fisheries.uvms.activity.service.AssetModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.FishingTripService;
import eu.europa.ec.fisheries.uvms.activity.service.MdrModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.SpatialModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchEvolutionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.FishingActivityTypeDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.FishingTripSummaryViewDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.MessageCountDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.ReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripIdDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripOverviewDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripWidgetDto;
import eu.europa.ec.fisheries.uvms.activity.service.facatch.evolution.CatchEvolutionProgressProcessor;
import eu.europa.ec.fisheries.uvms.activity.service.facatch.evolution.TripCatchEvolutionProgressRegistry;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.BaseMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FaCatchMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FishingTripIdWithGeometryMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FishingTripToGeoJsonMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.VesselStorageCharacteristicsMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.VesselTransportMeansMapper;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.service.search.SortKey;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static eu.europa.ec.fisheries.uvms.activity.service.util.GeomUtil.DEFAULT_EPSG_SRID;

@Stateless
@Local(FishingTripService.class)
@Transactional
@Slf4j
public class FishingTripServiceBean extends BaseActivityBean implements FishingTripService {

    private static final String DECLARATION = "Declaration";
    private static final String NOTIFICATION = "Notification";

    @EJB
    private SpatialModuleService spatialModule;

    @EJB
    private ActivityService activityServiceBean;

    @EJB
    private AssetModuleService assetModuleService;

    @EJB
    private MdrModuleService mdrModuleService;

    private FaReportDocumentDao faReportDocumentDao;
    private FishingActivityDao fishingActivityDao;
    private VesselTransportMeansDao vesselTransportMeansDao;
    private FishingTripDao fishingTripDao;
    private FaCatchDao faCatchDao;

    private static final CatchEvolutionProgressProcessor catchEvolutionProgressProcessor =
            new CatchEvolutionProgressProcessor(new TripCatchEvolutionProgressRegistry());

    @PostConstruct
    public void init() {
        fishingActivityDao = new FishingActivityDao(entityManager);
        faReportDocumentDao = new FaReportDocumentDao(entityManager);
        faCatchDao = new FaCatchDao(entityManager);
        fishingTripDao = new FishingTripDao(entityManager);
        vesselTransportMeansDao = new VesselTransportMeansDao(entityManager);
    }

    @Override
    public VesselDetailsDTO getVesselDetailsForFishingTrip(final String fishingTripId) {
        if (fishingTripId == null) {
            throw new IllegalArgumentException("PARAMETER CANNOT BE NULL");
        }
        VesselTransportMeansEntity latestVesselByTripId = vesselTransportMeansDao.findLatestVesselByTripId(fishingTripId);
        if (latestVesselByTripId != null) {
            FishingActivityEntity parent = latestVesselByTripId.getFishingActivity();
            return getVesselDetailsDTO(latestVesselByTripId, parent);
        }

        return null;
    }

    private VesselDetailsDTO getVesselDetailsDTO(VesselTransportMeansEntity vesselTransportMeansEntity, FishingActivityEntity fishingActivityEntity) {
        VesselDetailsDTO detailsDTO;
        detailsDTO = VesselTransportMeansMapper.INSTANCE.map(vesselTransportMeansEntity);

        getMdrCodesEnrichWithAssetsModuleDataIfNeeded(detailsDTO);

        if (fishingActivityEntity != null) {
            VesselStorageCharacteristicsEntity sourceVesselCharId = fishingActivityEntity.getSourceVesselCharId();
            if (detailsDTO != null) {
                detailsDTO.setStorageDto(VesselStorageCharacteristicsMapper.INSTANCE.mapToStorageDto(sourceVesselCharId));
            }
        }
        return detailsDTO;
    }

    //To process MDR code list and compare with  database:vesselTransportMeansDao and then enrich with asset module
    private void getMdrCodesEnrichWithAssetsModuleDataIfNeeded(VesselDetailsDTO vesselDetailsDTO) {
        final String ACRONYM = "FLUX_VESSEL_ID_TYPE";
        final String filter = "*";
        final List<String> columnsList = new ArrayList<>();
        Integer nrOfResults = 1000;
        if (vesselDetailsDTO == null) {
            return;
        }
        List<String> codeList;
        codeList = mdrModuleService.getAcronymFromMdr(ACRONYM, filter, columnsList, nrOfResults, "code").get("code");
        Set<AssetIdentifierDto> vesselIdentifiers = vesselDetailsDTO.getVesselIdentifiers();
        if (vesselIdentifiers == null || codeList == null) {
            return;
        }
        AssetQuery assetQuery = new AssetQuery();
        for (AssetIdentifierDto assetIdentifierDto : vesselIdentifiers) {
            if (codeList.contains(assetIdentifierDto.getIdentifierSchemeId().name())) {
                final List<String> idValueAsList = Arrays.asList(assetIdentifierDto.getFaIdentifierId());
                switch (assetIdentifierDto.getIdentifierSchemeId()) {
                    case CFR:
                        assetQuery.setCfr(idValueAsList);
                        break;
                    case EXT_MARK:
                        assetQuery.setExternalMarking(idValueAsList);
                        break;
                    case GFCM:
                        assetQuery.setGfcm(idValueAsList);
                        break;
                    case ICCAT:
                        assetQuery.setIccat(idValueAsList);
                        break;
                    case IRCS:
                        assetQuery.setIrcs(idValueAsList);
                        break;
                    case UVI:
                        assetQuery.setUvi(idValueAsList);
                        break;
                    default:
                }
            }
        }
        List<AssetDTO> assetList = assetModuleService.getAssets(assetQuery);
        if (!CollectionUtils.isEmpty(assetList)) {
            vesselDetailsDTO.enrichVesselIdentifiersFromAsset(assetList.get(0));
        }
    }

    @Override
    public FishingTripSummaryViewDTO getFishingTripSummaryAndReports(String fishingTripId, List<Dataset> datasets) throws ServiceException {
        List<ReportDTO> reportDTOList = new ArrayList<>();
        Geometry multipolygon = getRestrictedAreaGeom(datasets);
        Map<String, FishingActivityTypeDTO> summary = populateFishingActivityReportListAndFishingTripSummary(fishingTripId, reportDTOList, multipolygon, false);
        return populateFishingTripSummary(fishingTripId, reportDTOList, summary);
    }

    private Geometry getRestrictedAreaGeom(List<Dataset> datasets) throws ServiceException {
        if (CollectionUtils.isEmpty(datasets)) {
            return null;
        }
        try {
            List<AreaIdentifierType> areaIdentifierTypes = UsmUtils.convertDataSetToAreaId(datasets);
            String areaWkt = spatialModule.getFilteredAreaGeom(areaIdentifierTypes);
            Geometry geometry = new WKTReader().read(areaWkt);
            geometry.setSRID(DEFAULT_EPSG_SRID);
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

    /**
     * @param fishingTripId     - Fishing trip summary will be collected for this tripID
     * @param reportDTOList     - This DTO will have details about Fishing Activities.Method will process and populate data into this list     *
     * @param multipolygon      - Activities only in this area would be selected
     * @param isOnlyTripSummary - This method you could reuse to only get Fishing trip summary as well
     * @throws ServiceException
     */
    private Map<String, FishingActivityTypeDTO> populateFishingActivityReportListAndFishingTripSummary(String fishingTripId, List<ReportDTO> reportDTOList,
                                                                                                      Geometry multipolygon, boolean isOnlyTripSummary) throws ServiceException {
        List<FishingActivityEntity> fishingActivityList = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId, multipolygon);
        fishingActivityList.addAll(getReportsThatWereCancelledOrDeleted(fishingActivityList));
        Map<String, FishingActivityTypeDTO> tripSummary = new HashMap<>();
        for (FishingActivityEntity activityEntity : Utils.safeIterable(fishingActivityList)) {
            if (!isOnlyTripSummary) {
                ReportDTO reportDTO = FishingActivityMapper.INSTANCE.mapToReportDTO(activityEntity);
                reportDTOList.add(reportDTO);
            }
            if (activityEntity != null && activityEntity.getFaReportDocument() != null && DECLARATION.equalsIgnoreCase(activityEntity.getFaReportDocument().getTypeCode())) {
                // FA Report should be of type Declaration. And Fishing Activity type should be Either Departure,Arrival or Landing
                populateFishingTripSummary(activityEntity, tripSummary);
            }
        }
        return tripSummary;
    }

    private List<FishingActivityEntity> getReportsThatWereCancelledOrDeleted(List<FishingActivityEntity> fishingActivityList) {
        Map<FaReportDocumentEntity, FishingActivityEntity> responseMap = new HashMap<>();
        List<Integer> cancelledByDeletedByList = new ArrayList<>();
        List<FishingActivityEntity> cancelledOrDeletedActivities = new ArrayList<>();
        if (CollectionUtils.isEmpty(fishingActivityList)) {
            return new ArrayList<>();
        }
        for (FishingActivityEntity fishingActivityEntity : fishingActivityList) {
            if (fishingActivityEntity.getCanceledBy() != null) {
                cancelledByDeletedByList.add(fishingActivityEntity.getCanceledBy());
                cancelledOrDeletedActivities.add(fishingActivityEntity);
            }
            if (fishingActivityEntity.getDeletedBy() != null) {
                cancelledByDeletedByList.add(fishingActivityEntity.getDeletedBy());
                cancelledOrDeletedActivities.add(fishingActivityEntity);
            }
        }
        if (CollectionUtils.isNotEmpty(cancelledByDeletedByList)) {
            List<FaReportDocumentEntity> reportsByIdsList = faReportDocumentDao.findReportsByIdsList(cancelledByDeletedByList);
            if (CollectionUtils.isNotEmpty(reportsByIdsList)) {
                mapFishActToFaReportCounterpart(responseMap, cancelledOrDeletedActivities, reportsByIdsList);
            }
        }
        return mapToFishingActivitiesList(responseMap);
    }

    private List<FishingActivityEntity> mapToFishingActivitiesList(Map<FaReportDocumentEntity, FishingActivityEntity> fishActFaRepMap) {
        List<FishingActivityEntity> fishingActivityEntities = new ArrayList<>();
        if (MapUtils.isEmpty(fishActFaRepMap)) {
            return fishingActivityEntities;
        }
        for (Map.Entry<FaReportDocumentEntity, FishingActivityEntity> entry : fishActFaRepMap.entrySet()) {
            FaReportDocumentEntity faRep = entry.getKey();
            FishingActivityEntity fishAct = entry.getValue();
            FishingActivityEntity cloneActivity = SerializationUtils.clone(fishAct);
            cloneActivity.setFaReportDocument(faRep);
            cloneActivity.setFishingGears(fishAct.getFishingGears());
            cloneActivity.setFaCatchs(fishAct.getFaCatchs());
            cloneActivity.setAllRelatedFishingActivities(fishAct.getAllRelatedFishingActivities());
            cloneActivity.setFisheryTypeCodeListId(fishAct.getFisheryTypeCodeListId());
            cloneActivity.setFluxCharacteristics(fishAct.getFluxCharacteristics());
            fishingActivityEntities.add(cloneActivity);
        }
        return fishingActivityEntities;
    }

    private void mapFishActToFaReportCounterpart(Map<FaReportDocumentEntity, FishingActivityEntity> responseMap, List<FishingActivityEntity> cancelledOrDeletedActivities, List<FaReportDocumentEntity> reportsByIdsList) {
        for (FishingActivityEntity fishingActivityEntity : cancelledOrDeletedActivities) {
            Integer canceledBy = fishingActivityEntity.getCanceledBy();
            Integer deletedBy = fishingActivityEntity.getDeletedBy();
            for (FaReportDocumentEntity faReportDocumentEntity : reportsByIdsList) {
                if (Objects.equals(canceledBy, faReportDocumentEntity.getId()) ||
                    Objects.equals(deletedBy, faReportDocumentEntity.getId())) {
                    responseMap.put(faReportDocumentEntity, fishingActivityEntity);
                }
            }
        }
    }

    private void populateFishingTripSummary(FishingActivityEntity activityEntity, Map<String, FishingActivityTypeDTO> summary) {
        String activityTypeCode = activityEntity.getTypeCode();
        if (FishingActivityTypeEnum.DEPARTURE.toString().equalsIgnoreCase(activityTypeCode)
                || FishingActivityTypeEnum.ARRIVAL.toString().equalsIgnoreCase(activityTypeCode)
                || FishingActivityTypeEnum.LANDING.toString().equalsIgnoreCase(activityTypeCode)) {
            Instant occurrence = activityEntity.getOccurence();
            boolean isCorrection = BaseMapper.getCorrection(activityEntity);
            FishingActivityTypeDTO fishingActivityTypeDTO = summary.get(activityTypeCode);
            if (occurrence != null && (
                    (fishingActivityTypeDTO == null)
                            || (isCorrection
                            && fishingActivityTypeDTO.getDate() != null
                            && occurrence.compareTo(fishingActivityTypeDTO.getDate().toInstant()) > 0))) {
                fishingActivityTypeDTO = new FishingActivityTypeDTO();
                fishingActivityTypeDTO.setDate(Date.from(occurrence));
                summary.put(activityTypeCode, fishingActivityTypeDTO);
            }
        }
    }

    /**
     * Gets the counters for the trip with this tripId.
     *
     * @param tripId
     */
    @Override
    public MessageCountDTO getMessageCountersForTripId(String tripId) {
        List<FaReportDocumentEntity> reports = faReportDocumentDao.loadReports(tripId, "N");
        if (CollectionUtils.isNotEmpty(reports)) {
            List<FaReportDocumentEntity> canceledDeletedReports = faReportDocumentDao.loadCanceledAndDeletedReports(reports);
            if (CollectionUtils.isNotEmpty(canceledDeletedReports)) {
                reports.addAll(canceledDeletedReports);
            }
        }
        return createMessageCounter(reports);
    }

    /**
     * Populates the MessageCounter adding to the right counter 1 unit depending on the type of report (typeCode, purposeCode, size()).
     *
     * @param faReportDocumentList
     */
    private MessageCountDTO createMessageCounter(List<FaReportDocumentEntity> faReportDocumentList) {

        MessageCountDTO messagesCounter = new MessageCountDTO();
        if (CollectionUtils.isEmpty(faReportDocumentList)) {
            return messagesCounter;
        }

        // Reports total
        messagesCounter.setNoOfReports(faReportDocumentList.size());

        for (FaReportDocumentEntity faReport : faReportDocumentList) {
            String faDocumentType = faReport.getTypeCode();
            FaReportStatusType status = FaReportStatusType.valueOf(faReport.getStatus());

            // Declarations / Notifications
            if (DECLARATION.equalsIgnoreCase(faDocumentType)) {
                messagesCounter.setNoOfDeclarations(messagesCounter.getNoOfDeclarations() + 1);
            } else if (NOTIFICATION.equalsIgnoreCase(faDocumentType)) {
                messagesCounter.setNoOfNotifications(messagesCounter.getNoOfNotifications() + 1);
            }

            // Fishing operations
            Set<FishingActivityEntity> faEntitiyList = faReport.getFishingActivities();
            for (FishingActivityEntity faEntity : Utils.safeIterable(faEntitiyList)) {
                if (FishingActivityTypeEnum.FISHING_OPERATION.toString().equalsIgnoreCase(faEntity.getTypeCode())) {
                    messagesCounter.setNoOfFishingOperations(messagesCounter.getNoOfFishingOperations() + 1);
                }
            }

            // PurposeCode : Deletions / Cancellations / Corrections
            if (FaReportStatusType.DELETED.equals(status)) {
                messagesCounter.setNoOfDeletions(messagesCounter.getNoOfDeletions() + 1);
            } else if (FaReportStatusType.CANCELED.equals(status)) {
                messagesCounter.setNoOfCancellations(messagesCounter.getNoOfCancellations() + 1);
            } else if (FaReportStatusType.UPDATED.equals(status)) {
                messagesCounter.setNoOfCorrections(messagesCounter.getNoOfCorrections() + 1);
            }
        }
        return messagesCounter;
    }

    /**
     * Retrieves all the catches for the given fishing trip;
     *
     * @param fishingTripId
     */
    @Override
    public Map<String, CatchSummaryListDTO> retrieveFaCatchesForFishingTrip(String fishingTripId) {
        return FaCatchMapper.INSTANCE.mapCatchesToSummaryDTO(faCatchDao.findFaCatchesByFishingTrip(fishingTripId));
    }

    /**
     * Retrieve GEO data for fishing trip Map for tripID
     *
     * @param tripId
     */
    @Override
    public ObjectNode getTripMapDetailsForTripId(String tripId) {
        log.info("Get GEO data for Fishing Trip for tripId:" + tripId);
        List<FaReportDocumentEntity> faReportDocumentEntityList = faReportDocumentDao.loadReports(tripId, "Y");
        List<Geometry> geoList = new ArrayList<>();
        for (FaReportDocumentEntity entity : faReportDocumentEntityList) {
            if (entity.getGeom() != null)
                geoList.add(entity.getGeom());
        }
        return FishingTripToGeoJsonMapper.toJson(geoList);
    }

    /**
     * This method filters fishing Trips for Activity tab
     */
    @Override
    public FishingTripResponse filterFishingTrips(FishingActivityQuery query) throws ServiceException {
        log.info("getFishingTripResponse For Filter");
        if ((MapUtils.isEmpty(query.getSearchCriteriaMap()) && MapUtils.isEmpty(query.getSearchCriteriaMapMultipleValues()))
                || activityServiceBean.checkAndEnrichIfVesselFiltersArePresent(query)) {
            return new FishingTripResponse();
        }
        Set<FishingTripId> fishingTripIds = fishingTripDao.getFishingTripIdsForMatchingFilterCriteria(query);
        Integer totalCountOfRecords = fishingTripDao.getCountOfFishingTripsForMatchingFilterCriteria(query);
        log.debug("Total count of records: {} ", totalCountOfRecords);
        FishingTripResponse fishingTripResponse = buildFishingTripSearchRespose(fishingTripIds);
        fishingTripResponse.setTotalCountOfRecords(BigInteger.valueOf(totalCountOfRecords));
        return fishingTripResponse;
    }


    /**
     * This method builds FishingTripSerachReponse objectc for FishingTripIds passed to the method
     * collectFishingActivities : If the value is TRUE, all fishing Activities for every fishing Trip would be sent in the response.
     * If the value is FALSE, No fishing activities would be sent in the response.
     */
    protected FishingTripResponse buildFishingTripSearchRespose(Set<FishingTripId> fishingTripIds) throws ServiceException {
        if (fishingTripIds == null || fishingTripIds.isEmpty()) {
            return new FishingTripResponse();
        }
        List<FishingActivitySummary> fishingActivitySummaries = new ArrayList<>();

        List<FishingTripIdWithGeometry> fishingTripIdLists = new ArrayList<>();
        for (FishingTripId fishingTripId : fishingTripIds) {

            FishingActivityQuery query = new FishingActivityQuery();
            Map<SearchFilter, String> searchCriteriaMap = new EnumMap<>(SearchFilter.class);
            searchCriteriaMap.put(SearchFilter.TRIP_ID, fishingTripId.getTripId());
            searchCriteriaMap.put(SearchFilter.FISHING_TRIP_SCHEME_ID, fishingTripId.getSchemeID());
            query.setSearchCriteriaMap(searchCriteriaMap);
            SortKey sortKey = new SortKey();
            sortKey.setSortBy(SearchFilter.PERIOD_START); // this is important to find out first and last fishing activity for the Fishing Trip
            sortKey.setReversed(false);
            query.setSorting(sortKey);
            List<FishingActivityEntity> fishingActivityEntityList = fishingActivityDao.getFishingActivityListByQuery(query);

            FishingTripIdWithGeometry fishingTripIdWithGeometry = FishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(fishingTripId, fishingActivityEntityList);
            fishingTripIdLists.add(fishingTripIdWithGeometry);
        }

        // populate response object
        FishingTripResponse response = new FishingTripResponse();
        response.setFishingActivityLists(fishingActivitySummaries);
        response.setFishingTripIdLists(fishingTripIdLists);
        return response;
    }

    private TripWidgetDto getTripWidgetDto(FishingActivityEntity activityEntity, String tripId) {
        if (activityEntity == null) {
            return null;
        }
        TripWidgetDto tripWidgetDto = new TripWidgetDto();
        try {
            if (tripId != null) {
                log.debug("Trip Id found for Fishing Activity. Get TripWidget information for tripID :" + tripId);
                TripOverviewDto tripOverviewDto = getTripOverviewDto(activityEntity, tripId);
                List<TripOverviewDto> tripOverviewDtoList = new ArrayList<>();
                tripOverviewDtoList.add(tripOverviewDto);
                tripWidgetDto.setTrips(tripOverviewDtoList);
                if (activityEntity.getFaReportDocument() != null && CollectionUtils.isNotEmpty(activityEntity.getFaReportDocument().getVesselTransportMeans())) {
                    Set<VesselTransportMeansEntity> vesselTransportMeansEntities = activityEntity.getFaReportDocument().getVesselTransportMeans();
                    for (VesselTransportMeansEntity vesselTransportMeansEntity : vesselTransportMeansEntities) {
                        if (vesselTransportMeansEntity.getFishingActivity() == null) {
                            tripWidgetDto.setVesselDetails(getVesselDetailsDTO(vesselTransportMeansEntity, activityEntity));
                            break;
                        }
                    }
                }
                log.debug("tripWidgetDto set for tripID :" + tripId);
            } else {
                log.debug("TripId is not received for the screen. Try to get TripSummary information for all the tripIds specified for FishingActivity:" + activityEntity.getId());
                return createTripWidgetDtoWithFishingActivity(activityEntity);
            }
        } catch (ServiceException e) {
            log.error("Error while creating TripWidgetDto.", e);
        }
        return tripWidgetDto;
    }

    @Override
    public CatchEvolutionDTO retrieveCatchEvolutionForFishingTrip(String fishingTripId) throws ServiceException {
        CatchEvolutionDTO catchEvolution = new CatchEvolutionDTO();
        List<FishingActivityEntity> fishingActivities = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId, null);
        FishingActivityEntity activityEntity = fishingActivities.isEmpty() ? null : fishingActivities.get(0);
        catchEvolution.setTripDetails(getTripWidgetDto(activityEntity, fishingTripId));
        catchEvolution.setCatchEvolutionProgress(prepareCatchEvolutionProgress(fishingActivities));

        return catchEvolution;
    }

    private List<CatchEvolutionProgressDTO> prepareCatchEvolutionProgress(List<FishingActivityEntity> fishingActivities) {
        List<CatchEvolutionProgressDTO> catchEvolutionProgress = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(fishingActivities)) {
            catchEvolutionProgress = catchEvolutionProgressProcessor.process(fishingActivities);
        }

        return catchEvolutionProgress;
    }

    private TripWidgetDto createTripWidgetDtoWithFishingActivity(FishingActivityEntity activityEntity) throws ServiceException {
        TripWidgetDto tripWidgetDto = new TripWidgetDto();
        List<TripOverviewDto> tripOverviewDtoList = new ArrayList<>();
        tripOverviewDtoList.add(getTripOverviewDto(activityEntity, activityEntity.getFishingTrip().getFishingTripKey().getTripId()));
        tripWidgetDto.setTrips(tripOverviewDtoList);
        //As per new requirement, vessel should always be the one associated with fishing Activity in the trip widget
        if (activityEntity.getFaReportDocument() != null && CollectionUtils.isNotEmpty(activityEntity.getFaReportDocument().getVesselTransportMeans())) {
            Set<VesselTransportMeansEntity> vesselTransportMeansEntities = activityEntity.getFaReportDocument().getVesselTransportMeans();
            for (VesselTransportMeansEntity vesselTransportMeansEntity : vesselTransportMeansEntities) {
                if (vesselTransportMeansEntity.getFishingActivity() == null) {
                    tripWidgetDto.setVesselDetails(getVesselDetailsDTO(vesselTransportMeansEntity, activityEntity));
                    break;
                }
            }

        }
        return tripWidgetDto;
    }

    private TripOverviewDto getTripOverviewDto(FishingActivityEntity activityEntity, String tripId) throws ServiceException {
        Map<String, FishingActivityTypeDTO> typeDTOMap = populateFishingActivityReportListAndFishingTripSummary(tripId, null, null, true);
        TripOverviewDto tripOverviewDto = new TripOverviewDto();
        TripIdDto tripIdDto = new TripIdDto();
        tripIdDto.setId(tripId);
        tripIdDto.setSchemeId(activityEntity.getFishingTrip().getFishingTripKey().getTripSchemeId());
        List<TripIdDto> tripIdList = new ArrayList<>();
        tripIdList.add(tripIdDto);
        tripOverviewDto.setTripId(tripIdList);
        tripOverviewDto.setTypeCode(activityEntity.getFishingTrip().getTypeCode());
        populateTripOverviewDto(typeDTOMap, tripOverviewDto);
        return tripOverviewDto;
    }

    private void populateTripOverviewDto(Map<String, FishingActivityTypeDTO> typeDTOMap, TripOverviewDto tripOverviewDto) {
        for (Map.Entry<String, FishingActivityTypeDTO> entry : typeDTOMap.entrySet()) {
            String key = entry.getKey();
            FishingActivityTypeDTO fishingActivityTypeDTO = entry.getValue();
            switch (FishingActivityTypeEnum.valueOf(key)) {
                case DEPARTURE:
                    tripOverviewDto.setDepartureTime(fishingActivityTypeDTO.getDate());
                    break;
                case ARRIVAL:
                    tripOverviewDto.setArrivalTime(fishingActivityTypeDTO.getDate());
                    break;
                case LANDING:
                    tripOverviewDto.setLandingTime(fishingActivityTypeDTO.getDate());
                    break;
                default:
                    log.debug("Fishing Activity type found is :" + key);
                    break;
            }
        }
    }
}
