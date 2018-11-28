/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Stopwatch;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.ers.fa.utils.UsmUtils;
import eu.europa.ec.fisheries.ers.service.*;
import eu.europa.ec.fisheries.ers.service.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.ers.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityHistoryDtoElement;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityDepartureViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.ActivityViewEnum;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.ActivityViewMapperFactory;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;


/**
 * Created by sanera on 29/06/2016.
 */
@Stateless
@Local(ActivityService.class)
@Transactional
@Slf4j
public class ActivityServiceBean extends BaseActivityBean implements ActivityService {

    private FaReportDocumentDao faReportDocumentDao;

    private FishingActivityDao fishingActivityDao;

    @EJB
    private SpatialModuleService spatialModule;

    @EJB
    private AssetModuleService assetsServiceBean;

    @EJB
    private FishingTripService fishingTripServiceBean;

    @EJB
    private MdrModuleService mdrModuleService;

    @PostConstruct
    public void init() {
        initEntityManager();
        fishingActivityDao = new FishingActivityDao(getEntityManager());
        faReportDocumentDao = new FaReportDocumentDao(getEntityManager());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FaReportCorrectionDTO> getFaReportHistory(String refReportId, String refSchemeId) {
        FaReportDocumentEntity faReport = faReportDocumentDao.findFaReportByIdAndScheme(refReportId, refSchemeId);
        List<FaReportDocumentEntity> historyOfFaReport = faReportDocumentDao.getHistoryOfFaReport(faReport, new ArrayList<FaReportDocumentEntity>());
        List<FaReportCorrectionDTO> faReportCorrectionDTOs = FaReportDocumentMapper.INSTANCE.mapToFaReportCorrectionDtoList(historyOfFaReport);
        log.info("Sort collection by date before sending");
        Collections.sort(faReportCorrectionDTOs);
        return faReportCorrectionDTOs;
    }

    @Override
    public FilterFishingActivityReportResultDTO getFishingActivityListByQuery(FishingActivityQuery query, List<Dataset> datasets) throws ServiceException {
        List<FishingActivityEntity> activityList;
        log.debug("[INFO] FishingActivityQuery received : {}", query);
        // Get the VesselTransportMeans guids from Assets if one of the Vessel related filters (VESSEL, VESSEL_GROUP) has been issued.
        // Returning true means that the query didn't produce results.
        if (checkAndEnrichIfVesselFiltersArePresent(query)) {
            return createResultDTO(null, 0);
        }
        // Check if any filters are present. If not, We need to return all fishing activity data
        String areaWkt = getRestrictedAreaGeom(datasets);
        log.debug("Geometry for the user received from USM : " + areaWkt);
        if (areaWkt != null && areaWkt.length() > 0) {
            Map<SearchFilter, String> mapSearch = query.getSearchCriteriaMap();
            if (mapSearch == null) {
                mapSearch = new EnumMap<>(SearchFilter.class);
                query.setSearchCriteriaMap(mapSearch);
            }
            mapSearch.put(SearchFilter.AREA_GEOM, areaWkt);
        }
        separateSingleVsMultipleFilters(query);
        activityList = fishingActivityDao.getFishingActivityListByQuery(query);
        int totalCountOfRecords = getRecordsCountForFilterFishingActivityReports(query);
        log.debug("Total count of records: {} ", totalCountOfRecords);
        return createResultDTO(activityList, totalCountOfRecords);
    }

    /**
     * Checks if one of the VESSEL filters is issued.
     * If true then queries the ASSETS module for guids related to these filters.
     * If assets answers with some guids then puts those guids in searchCriteriaMapMultipleValues of
     * FishingActivityQuery and returns false.
     * <p>
     * In every other case it returns true, which means that the filters were present but,
     * there were no matches in ASSET module.
     *
     * @param query
     * @return
     * @throws ServiceException
     */
    @Override
    public boolean checkAndEnrichIfVesselFiltersArePresent(FishingActivityQuery query) throws ServiceException {
        Map<SearchFilter, String> searchCriteriaMap = query.getSearchCriteriaMap();
        Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues = query.getSearchCriteriaMapMultipleValues();
        List<String> guidsFromAssets;
        if (searchCriteriaMap == null)
            return false;
        String vesselSearchStr = searchCriteriaMap.get(SearchFilter.VESSEL);
        String vesselGroupSearchStr = searchCriteriaMap.get(SearchFilter.VESSEL_GROUP);
        if (StringUtils.isNotEmpty(vesselSearchStr) || StringUtils.isNotEmpty(vesselGroupSearchStr)) {
            guidsFromAssets = assetsServiceBean.getAssetGuids(vesselSearchStr, vesselGroupSearchStr);
            if (CollectionUtils.isEmpty(guidsFromAssets)) {
                return true;
            }
            searchCriteriaMap.remove(SearchFilter.VESSEL);
            searchCriteriaMap.remove(SearchFilter.VESSEL_GROUP);
            if (searchCriteriaMapMultipleValues == null) {
                searchCriteriaMapMultipleValues = new EnumMap<>(SearchFilter.class);
            }
            searchCriteriaMapMultipleValues.put(SearchFilter.VESSEL_GUIDS, guidsFromAssets);
        }
        return false;
    }

    /**
     * Gets a FishingActivityEntity with a certain activityId and maps it to a FishingActivityViewDTO representation layer POJO.
     *
     * @param activityId
     * @param datasets
     * @return
     * @throws ServiceException
     */
    @Override
    public FishingActivityViewDTO getFishingActivityForView(Integer activityId, String tripId, Integer reportId, List<Dataset> datasets, ActivityViewEnum view, boolean withHistory) throws ServiceException {
        Geometry geom = getRestrictedAreaGeometry(datasets);
        FishingActivityEntity activityEntityFound = fishingActivityDao.getFishingActivityById(activityId, geom);
        if (activityEntityFound == null){
            throw new ServiceException("Could not find FishingActivityEntity for the given id:" + activityId);
        }
        log.debug("FishingActivityEntity fetched from database with id:" + activityEntityFound.getId());
        FishingActivityViewDTO fishingActivityViewDTO = ActivityViewMapperFactory.getMapperForView(view).mapFaEntityToFaDto(activityEntityFound);
        if (reportId != null && (activityEntityFound.getFaReportDocument().getId() !=  reportId)) { // Means we're fetching a correcter/deleter report - which is not directly related to the activity -!
            FaReportDocumentEntity corrOrDeletionReport = faReportDocumentDao.findEntityById(FaReportDocumentEntity.class, reportId);
            ReportDocumentDto repDocDTO = ActivityDepartureViewMapper.INSTANCE.getReportDocsFromEntity(corrOrDeletionReport);
            fishingActivityViewDTO.setReportDetails(repDocDTO);
        }
        fishingActivityViewDTO.setTripDetails(fishingTripServiceBean.getTripWidgetDto(activityEntityFound, tripId));
        log.debug("fishingActivityView generated after mapping is :" + fishingActivityViewDTO);
        addPortDescriptions(fishingActivityViewDTO, "LOCATION");
        if(withHistory){
            Stopwatch stopwatch = Stopwatch.createStarted();
            fishingActivityViewDTO.setHistory(getActivityHistory(activityEntityFound));
            stopwatch.stop();
            log.info("It took [ {} ] seconds to get the history.", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        log.debug("fishingActivityView generated after mapping is :" + fishingActivityViewDTO);
        return fishingActivityViewDTO;
    }

    @Override
    public GetFishingActivitiesForTripResponse getFaAndTripIdsFromTripIds(List<FishingActivityForTripIds> faAndTripIds) {
        GetFishingActivitiesForTripResponse response = new GetFishingActivitiesForTripResponse();
        List<FaIdsListWithTripIdMap> responseList = new ArrayList<>();
        response.setFaWithIdentifiers(responseList);
        for (FishingActivityForTripIds faTripId : faAndTripIds) {
            List<FishingActivityEntity> fishingActivies = fishingActivityDao.getFishingActivityForTrip(faTripId.getTripId(), faTripId.getTripSchemeId(),
                    faTripId.getFishActTypeCode(), faTripId.getFluxRepDocPurposeCodes());
            for (FishingActivityEntity faEntity : fishingActivies) {
                addToIdsList(responseList, faEntity);
            }
        }
        return response;
    }

    private void addToIdsList(List<FaIdsListWithTripIdMap> responseList, FishingActivityEntity faEntity) {
        Set<FishingTripIdentifierEntity> fishingTripIdentifiers = faEntity.getFishingTrips().iterator().next().getFishingTripIdentifiers();
        List<FishingActivityWithIdentifiers> faIdentifiers = mapToActivityIdsAndType(faEntity.getFishingActivityIdentifiers(), faEntity.getTypeCode());
        for (FishingTripIdentifierEntity tripIdentifEntity : fishingTripIdentifiers) {
            FaIdsListWithTripIdMap existingActWithIdentifiers = getElementWithTripId(responseList, tripIdentifEntity.getTripId());
            if (null != existingActWithIdentifiers) {
                existingActWithIdentifiers.getFaIdentifierLists().addAll(faIdentifiers);
            } else {
                responseList.add(new FaIdsListWithTripIdMap(tripIdentifEntity.getTripId(), tripIdentifEntity.getTripSchemeId(), faIdentifiers));
            }
        }
    }

    private FaIdsListWithTripIdMap getElementWithTripId(List<FaIdsListWithTripIdMap> responseList, String tripId) {
        FaIdsListWithTripIdMap mapToReturn = null;
        for (FaIdsListWithTripIdMap respMap : responseList) {
            if (tripId.equals(respMap.getTripId())) {
                mapToReturn = respMap;
                break;
            }
        }
        return mapToReturn;
    }


    @NotNull
    private FilterFishingActivityReportResultDTO createResultDTO(List<FishingActivityEntity> activityList, int totalCountOfRecords) {
        if (CollectionUtils.isEmpty(activityList)) {
            log.debug("Could not find FishingActivity entities matching search criteria");
            activityList = Collections.emptyList();
        }
        // Prepare DTO to return to Frontend
        log.debug("Fishing Activity Report resultset size : " + Integer.toString(activityList.size()));
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO = new FilterFishingActivityReportResultDTO();
        filterFishingActivityReportResultDTO.setResultList(mapToFishingActivityReportDTOList(activityList));
        filterFishingActivityReportResultDTO.setTotalCountOfRecords(totalCountOfRecords);
        return filterFishingActivityReportResultDTO;
    }

    // Improve this part later on
    private void separateSingleVsMultipleFilters(FishingActivityQuery query) throws ServiceException {
        Map<SearchFilter, List<String>> searchMapWithMultipleValues = query.getSearchCriteriaMapMultipleValues();
        if (searchMapWithMultipleValues == null || searchMapWithMultipleValues.size() == 0 || searchMapWithMultipleValues.get(SearchFilter.PURPOSE) == null)
            throw new ServiceException("No purpose code provided for the Fishing activity filters! At least one needed!");
        Map<SearchFilter, String> searchMap = query.getSearchCriteriaMap();
        if (searchMap == null)
            return;
        validateInputFilters(searchMapWithMultipleValues);
        Set<SearchFilter> filtersWhichSupportMultipleValues = FilterMap.getFiltersWhichSupportMultipleValues();
        Iterator<Map.Entry<SearchFilter, String>> searchMapIterator = searchMap.entrySet().iterator();
        while (searchMapIterator.hasNext()) {
            Map.Entry<SearchFilter, String> e = searchMapIterator.next();
            SearchFilter key = e.getKey();
            String value = e.getValue();
            if (value == null)
                throw new ServiceException("Null value present for the key:" + key + " Please provide correct input.");

            if (filtersWhichSupportMultipleValues.contains(key)) {
                List<String> values = new ArrayList<>();
                values.add(value);
                searchMapWithMultipleValues.put(key, values);
                searchMapIterator.remove();
            }
        }
        query.setSearchCriteriaMapMultipleValues(searchMapWithMultipleValues);
        query.setSearchCriteriaMap(searchMap);
    }

    private void validateInputFilters(Map<SearchFilter, List<String>> searchMapWithMultipleValues) throws ServiceException {
        if (MapUtils.isNotEmpty(searchMapWithMultipleValues)) {
            for (Map.Entry<SearchFilter, List<String>> e : searchMapWithMultipleValues.entrySet()) {
                SearchFilter key = e.getKey();
                List<String> values = e.getValue();
                if (values.contains(null) || values.contains("")) {
                    throw new ServiceException("Null value present for the key:" + key + " Please provide correct input.");
                }
            }
        }
    }

    /*
     * Query to calculate total number of result set
     */
    private Integer getRecordsCountForFilterFishingActivityReports(FishingActivityQuery query) throws ServiceException {
        return fishingActivityDao.getCountForFishingActivityListByQuery(query);
    }

    private String getRestrictedAreaGeom(List<Dataset> datasets) throws ServiceException {
        if (CollectionUtils.isEmpty(datasets)) {
            return null;
        }
        List<AreaIdentifierType> areaIdentifierTypes = UsmUtils.convertDataSetToAreaId(datasets);
        return spatialModule.getFilteredAreaGeom(areaIdentifierTypes);
    }

    private List<FishingActivityReportDTO> mapToFishingActivityReportDTOList(List<FishingActivityEntity> activityList) {
        List<FishingActivityReportDTO> activityReportDTOList = new ArrayList<>();
        for (FishingActivityEntity entity : activityList) {
            FishingActivityReportDTO fishingActivityReportDTO = FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTO(entity);
            // Switch the report ids if this activity was canceled or deleted (needed from FE to display correctly)
            if(fishingActivityReportDTO.getCancelingReportID() != 0){
                fishingActivityReportDTO.setFaReportID(fishingActivityReportDTO.getCancelingReportID());
            } else if(fishingActivityReportDTO.getDeletingReportID() != 0){
                fishingActivityReportDTO.setFaReportID(fishingActivityReportDTO.getDeletingReportID());
            }
            // If it is a correction then we need to switch back the purpose code to the one of the original report.
            String correctionPurposeCode = FaReportStatusType.UPDATED.getPurposeCode().toString();
            if(correctionPurposeCode.equals(entity.getFaReportDocument().getFluxReportDocument().getPurposeCode())){
                fishingActivityReportDTO.setPurposeCode(correctionPurposeCode);
            }

            // In case of a deleted or cancelled correction
            FaReportStatusType status = FaReportStatusType.valueOf(entity.getFaReportDocument().getStatus());
            if(FaReportStatusType.CANCELED.equals(status) || FaReportStatusType.DELETED.equals(status)){
                fishingActivityReportDTO.setPurposeCode(status.getPurposeCode().toString());
            }
            activityReportDTOList.add(fishingActivityReportDTO);
        }
        return activityReportDTOList;
    }

    private Geometry getRestrictedAreaGeometry(List<Dataset> datasets) throws ServiceException {
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

    private List<FishingActivityWithIdentifiers> mapToActivityIdsAndType(Set<FishingActivityIdentifierEntity> fishingActivityIdentifiers, String typeCode) {
        List<FishingActivityWithIdentifiers> actIDList = new ArrayList<>();
        if (CollectionUtils.isEmpty(fishingActivityIdentifiers)) {
            return actIDList;
        }
        for (FishingActivityIdentifierEntity tripIdent : fishingActivityIdentifiers) {
            actIDList.add(new FishingActivityWithIdentifiers(tripIdent.getFaIdentifierId(), tripIdent.getFaIdentifierSchemeId(), typeCode));
        }
        return actIDList;
    }

    private void addPortDescriptions(FishingActivityViewDTO fishingActivityViewDTO, String fluxLocationIdSchemeId) {
        if (fishingActivityViewDTO == null || StringUtils.isBlank(fluxLocationIdSchemeId)) {
            return;
        }
        final String ACRONYM = "LOCATION";
        String filter;
        final List<String> columnsList = new ArrayList<String>(Arrays.asList("code"));
        Integer nrOfResults = 1;
        if (CollectionUtils.isNotEmpty(fishingActivityViewDTO.getLocations())) {
            for (FluxLocationDto fluxLocationDto : fishingActivityViewDTO.getLocations()) {
                if (fluxLocationIdSchemeId.equals(fluxLocationDto.getFluxLocationIdentifierSchemeId())) {
                    try {
                        filter = fluxLocationDto.getFluxLocationIdentifier();
                        List<String> codeDescriptions = mdrModuleService.getAcronymFromMdr(ACRONYM, filter, columnsList, nrOfResults, "description").get("description");
                        String codeDescription = codeDescriptions.get(0);
                        fluxLocationDto.setPortDescription(codeDescription);
                    } catch (ServiceException e) {
                        log.error("Error while trying to set port description on FluxLocationDto.", e);
                    } catch (IndexOutOfBoundsException iobe) {
                        log.error("Error while trying to set port description on FluxLocationDto! Description for code: " + fluxLocationDto.getTypeCode() +
                                " doesn't exist", iobe);
                    }
                }
            }
        }
    }

    /**
     *  Return the full history of this activity.
     *
     * @param fishingActivity
     * @return
     */
    private List<ActivityHistoryDtoElement> getActivityHistory(FishingActivityEntity fishingActivity) {
        if (fishingActivity == null || fishingActivity.getFaReportDocument() == null) {
            log.error("fishingActivity or fishingActivityTime ");
            return new ArrayList<>();
        }
        return mapFromReportsToActivityHistory(faReportDocumentDao.getHistoryOfFaReport(fishingActivity.getFaReportDocument(), new ArrayList<>()), fishingActivity.getTypeCode());
    }

    private List<ActivityHistoryDtoElement> mapFromReportsToActivityHistory(List<FaReportDocumentEntity> fullHistoryReportsList, String activityType) {
        List<ActivityHistoryDtoElement> dtoElements = new ArrayList<>();
        if(CollectionUtils.isEmpty(fullHistoryReportsList)){
            return dtoElements;
        }
        for (FaReportDocumentEntity faRep : fullHistoryReportsList) {
            Integer purposeCode = Integer.valueOf(faRep.getFluxReportDocument().getPurposeCode());
            List<FishingActivityEntity> fishingActivities = faRep.getFishingActivities();
            List<Integer> acticityIds = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(fishingActivities)){ // In case of deletion or cancellation report this list is empty..
                for (FishingActivityEntity fishingActivity : fishingActivities) {
                    if(StringUtils.equals(fishingActivity.getTypeCode(), activityType)){
                        acticityIds.add(fishingActivity.getId());
                    }
                }
            } else if(purposeCode == 1 || purposeCode == 3){ // in deletion/cancellation cases we need to populate the activityIds from the report this one deleted
                String referenceId = faRep.getFluxReportDocument().getReferenceId();
                for (FaReportDocumentEntity faFromHistory : fullHistoryReportsList) {
                    if(referenceId.equalsIgnoreCase(faFromHistory.getFluxReportDocument().getFluxReportIdentifiers().iterator().next().getFluxReportIdentifierId())){
                        for (FishingActivityEntity fishingActivity : faFromHistory.getFishingActivities()) {
                            acticityIds.add(fishingActivity.getId());
                        }
                    }
                }

            }
            dtoElements.add(new ActivityHistoryDtoElement(faRep.getId(), faRep.getAcceptedDatetime(), purposeCode, acticityIds));
        }
        Collections.sort(dtoElements);
        return dtoElements;
    }

    public int getPreviousFishingActivity(int fishingActivityId) {
        log.info(" Retrieve fishing activity from db:" + fishingActivityId);
        FishingActivityEntity activityEntity = fishingActivityDao.getFishingActivityById(fishingActivityId, null);
        log.info(" activityEntity received from db Id:" + activityEntity.getId() + " typeCode: " + activityEntity.getTypeCode() + " Date:" + DateUtils.parseUTCDateToString(activityEntity.getCalculatedStartTime()));
        return fishingActivityDao.getPreviousFishingActivityId(activityEntity.getId(), activityEntity.getTypeCode(), activityEntity.getCalculatedStartTime());
    }


    public int getNextFishingActivity(int fishingActivityId) {
        log.info(" Retrieve fishing activity from db:" + fishingActivityId);
        FishingActivityEntity activityEntity = fishingActivityDao.getFishingActivityById(fishingActivityId, null);
        log.info(" activityEntity received from db Id:" + activityEntity.getId() + " typeCode: " + activityEntity.getTypeCode() + " Date:" + DateUtils.parseUTCDateToString(activityEntity.getCalculatedStartTime()));
        return fishingActivityDao.getNextFishingActivityId(activityEntity.getId(), activityEntity.getTypeCode(), activityEntity.getCalculatedStartTime());
    }
}