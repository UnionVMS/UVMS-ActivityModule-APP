/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.service.ActivityService;
import eu.europa.ec.fisheries.uvms.activity.service.AssetModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FishingActivityUtilsMapper;
import eu.europa.ec.fisheries.uvms.activity.service.search.FilterMap;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Stateless
@Local(ActivityService.class)
@Transactional
@Slf4j
public class ActivityServiceBean extends BaseActivityBean implements ActivityService {

    private FaReportDocumentDao faReportDocumentDao;

    private FishingActivityDao fishingActivityDao;

    @Inject
    FaReportDocumentMapper faReportDocumentMapper;

    @Inject
    FishingActivityUtilsMapper fishingActivityUtilsMapper;

    @EJB
    private AssetModuleService assetsServiceBean;

    @PostConstruct
    public void init() {
        fishingActivityDao = new FishingActivityDao(entityManager);
        faReportDocumentDao = new FaReportDocumentDao(entityManager);
    }

    @Override
    public List<FaReportCorrectionDTO> getFaReportHistory(String refReportId, String refSchemeId) {
        FaReportDocumentEntity faReport = faReportDocumentDao.findFaReportByIdAndScheme(refReportId, refSchemeId);
        if (faReport == null) {
            return new ArrayList<>();
        }

        List<FaReportDocumentEntity> historyOfFaReport = faReportDocumentDao.getHistoryOfFaReport(faReport);
        List<FaReportCorrectionDTO> faReportCorrectionDTOs = faReportDocumentMapper.mapToFaReportCorrectionDtoList(historyOfFaReport);
        Collections.sort(faReportCorrectionDTOs);
        return faReportCorrectionDTOs;
    }

    @Override
    public FilterFishingActivityReportResultDTO getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        log.debug("FishingActivityQuery received: {}", query);

        // Get the VesselTransportMeans GUIDs from Assets if one of the Vessel related filters (VESSEL, VESSEL_GROUP) has been issued.
        // Returning true means that the query didn't produce results.
        if (checkAndEnrichIfVesselFiltersArePresent(query)) {
            return createResultDTO(null, 0);
        }
        // TODO: Ok, this needs som testing.
        separateSingleVsMultipleFilters(query);

        List<FishingActivityEntity> activityList = fishingActivityDao.getFishingActivityListByQuery(query);
        int totalCountOfRecords = getRecordsCountForFilterFishingActivityReports(query);
        log.debug("Total count of records: {} ", totalCountOfRecords);
        return createResultDTO(activityList, totalCountOfRecords);
    }

    /**
     * Checks if the VESSEL filter is issued.
     * If true then queries the ASSETS module for guids related to this filter.
     * If assets answers with some guids then puts those guids in searchCriteriaMapMultipleValues of
     * FishingActivityQuery and returns false.
     * <p>
     * In every other case it returns true, which means that the filter were present but,
     * there were no matches in ASSET module.
     *
     * @param query
     * @throws ServiceException
     */
    @Override
    public boolean checkAndEnrichIfVesselFiltersArePresent(FishingActivityQuery query) throws ServiceException {
        Map<SearchFilter, String> searchCriteriaMap = query.getSearchCriteriaMap();
        Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues = query.getSearchCriteriaMapMultipleValues();
        if (searchCriteriaMap == null) {
            return false;
        }

        String vesselSearchStr = searchCriteriaMap.get(SearchFilter.VESSEL);
        // Support for SearchFilter.VESSEL_GROUP has been removed, since the query towards Asset for it
        // has changed considerably (now needs defining AssetFilter). Add when there is a need.
        if (StringUtils.isNotEmpty(vesselSearchStr)) {
            List<String> guidsFromAssets = assetsServiceBean.getAssetGuids(vesselSearchStr);
            if (CollectionUtils.isEmpty(guidsFromAssets)) {
                return true;
            }
            searchCriteriaMap.remove(SearchFilter.VESSEL);
            if (searchCriteriaMapMultipleValues == null) {
                searchCriteriaMapMultipleValues = new EnumMap<>(SearchFilter.class);
            }
            searchCriteriaMapMultipleValues.put(SearchFilter.VESSEL_GUIDS, guidsFromAssets);
        }
        return false;
    }

    @NotNull
    private FilterFishingActivityReportResultDTO createResultDTO(List<FishingActivityEntity> activityList, int totalCountOfRecords) {
        if (CollectionUtils.isEmpty(activityList)) {
            log.debug("Could not find FishingActivity entities matching search criteria");
            activityList = Collections.emptyList();
        }

        // Prepare DTO to return to Frontend
        log.debug("Fishing Activity Report resultset size: {}", activityList.size());

        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO = new FilterFishingActivityReportResultDTO();
        filterFishingActivityReportResultDTO.setResultList(mapToFishingActivityReportDTOList(activityList));
        filterFishingActivityReportResultDTO.setTotalCountOfRecords(totalCountOfRecords);
        return filterFishingActivityReportResultDTO;
    }

    private void separateSingleVsMultipleFilters(FishingActivityQuery query) throws ServiceException {
        Map<SearchFilter, List<String>> searchMapWithMultipleValues = query.getSearchCriteriaMapMultipleValues();
        if (searchMapWithMultipleValues == null || searchMapWithMultipleValues.size() == 0 || searchMapWithMultipleValues.get(SearchFilter.PURPOSE) == null) {
            throw new ServiceException("No purpose code provided for the Fishing activity filters! At least one needed!");
        }

        Map<SearchFilter, String> searchMap = query.getSearchCriteriaMap();
        if (searchMap == null) {
            return;
        }

        validateInputFilters(searchMapWithMultipleValues);

        Set<SearchFilter> filtersWhichSupportMultipleValues = FilterMap.getFiltersWhichSupportMultipleValues();
        Iterator<Map.Entry<SearchFilter, String>> searchMapIterator = searchMap.entrySet().iterator();
        while (searchMapIterator.hasNext()) {
            Map.Entry<SearchFilter, String> e = searchMapIterator.next();
            SearchFilter key = e.getKey();
            String value = e.getValue();

            if (value == null) {
                throw new ServiceException("Null value present for the key:" + key + " Please provide correct input.");
            }

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

    private List<FishingActivityReportDTO> mapToFishingActivityReportDTOList(List<FishingActivityEntity> activityList) {
        List<FishingActivityReportDTO> activityReportDTOList = new ArrayList<>();

        for (FishingActivityEntity entity : activityList) {
            FishingActivityReportDTO fishingActivityReportDTO = fishingActivityUtilsMapper.mapToFishingActivityReportDTO(entity);
            // Switch the report ids if this activity was canceled or deleted (needed from FE to display correctly)
            if (fishingActivityReportDTO.getCancelingReportID() != 0) {
                fishingActivityReportDTO.setFaReportID(fishingActivityReportDTO.getCancelingReportID());
            } else if (fishingActivityReportDTO.getDeletingReportID() != 0) {
                fishingActivityReportDTO.setFaReportID(fishingActivityReportDTO.getDeletingReportID());
            }
            // If it is a correction then we need to switch back the purpose code to the one of the original report.
            String correctionPurposeCode = FaReportStatusType.UPDATED.getPurposeCode().toString();
            if (correctionPurposeCode.equals(entity.getFaReportDocument().getFluxReportDocument_PurposeCode())) {
                fishingActivityReportDTO.setPurposeCode(correctionPurposeCode);
            }

            // In case of a deleted or cancelled correction
            FaReportStatusType status = FaReportStatusType.valueOf(entity.getFaReportDocument().getStatus());
            if (FaReportStatusType.CANCELED.equals(status) || FaReportStatusType.DELETED.equals(status)) {
                fishingActivityReportDTO.setPurposeCode(status.getPurposeCode().toString());
            }
            activityReportDTOList.add(fishingActivityReportDTO);
        }
        return activityReportDTOList;
    }

    public int getPreviousFishingActivity(int fishingActivityId) {
        FishingTripEntity fishingTrip = getFishingTripForActivity(fishingActivityId);
        if (fishingTrip == null) {
            return -1;
        }

        Set<FishingActivityEntity> fishingActivities = fishingTrip.getFishingActivities();
        List<FishingActivityEntity> fishingActivityEntityList = new ArrayList<>(fishingActivities);
        fishingActivityEntityList.sort(Comparator.comparing(FishingActivityEntity::getOccurence).reversed());

        return getNextActivityInList(fishingActivityId, fishingActivityEntityList);
    }

    public int getNextFishingActivity(int fishingActivityId) {
        FishingTripEntity fishingTrip = getFishingTripForActivity(fishingActivityId);
        if (fishingTrip == null) {
            return -1;
        }

        Set<FishingActivityEntity> fishingActivities = fishingTrip.getFishingActivities();
        List<FishingActivityEntity> fishingActivityEntityList = new ArrayList<>(fishingActivities);
        fishingActivityEntityList.sort(Comparator.comparing(FishingActivityEntity::getOccurence));

        return getNextActivityInList(fishingActivityId, fishingActivityEntityList);
    }

    private FishingTripEntity getFishingTripForActivity(int fishingActivityId) {
        FishingActivityEntity fishingActivity = getFishingActivity(fishingActivityId);
        if (fishingActivity == null) {
            log.warn("Could not find Fishing Activity for ID: {}", fishingActivityId);
            return null;
        }
        FishingTripEntity fishingTrip = fishingActivity.getFishingTrip();
        if (fishingTrip == null) {
            log.warn("Could not find Fishing Trip on Activity with ID: {}", fishingActivityId);
        }
        return fishingTrip;
    }

    private int getNextActivityInList(int fishingActivityId, List<FishingActivityEntity> fishingActivityEntityList) {
        int index = -1;
        for (int i = 0; i < fishingActivityEntityList.size(); i++) {
            FishingActivityEntity fishingActivityEntity = fishingActivityEntityList.get(i);
            if (fishingActivityEntity.getId() == fishingActivityId) {
                index = i;
                break;
            }
        }

        if (index > -1 && index < fishingActivityEntityList.size() - 1) {
            return fishingActivityEntityList.get(index + 1).getId();
        }
        return index;
    }

    private FishingActivityEntity getFishingActivity(int fishingActivityId) {
        log.info("Retrieve fishing activity from database: {}", fishingActivityId);
        FishingActivityEntity activityEntity = fishingActivityDao.getFishingActivityById(fishingActivityId, null);
        if (activityEntity == null) {
            return null;
        }
        log.info("FishingActivityEntity loaded from database. ID: {} TypeCode: {} Date: {}", activityEntity.getId(), activityEntity.getTypeCode(), DateUtils.dateToHumanReadableString(activityEntity.getCalculatedStartTime()));
        return activityEntity;
    }
}
