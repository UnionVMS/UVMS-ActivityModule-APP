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
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.UsmUtils;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.SpatialModuleService;
import eu.europa.ec.fisheries.ers.service.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.ers.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.ActivityViewEnum;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.ActivityViewMapperFactory;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.common.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
    public FilterFishingActivityReportResultDTO getFishingActivityListByQuery(FishingActivityQuery query, List<Dataset> datasets) throws ServiceException {

        List<FishingActivityEntity> activityList;
        log.debug("FishingActivityQuery received : {}", query);

        // Get the VesselTransportMeans guids from Assets if one of the Vessel related filters (VESSEL, VESSEL_GROUP) has been issued.
        // Returning true means that the query didn't produce results.
        if (checkAndEnrichIfVesselFiltersArePresent(query)) {
            return createResultDTO(null, 0);
        }

        // Check if any filters are present. If not, We need to return all fishing activity data
        String areaWkt = getRestrictedAreaGeom(datasets);
        log.debug("Geometry for the user received from USM : "+ areaWkt);
        if(areaWkt != null && areaWkt.length() > 0){
            Map<SearchFilter, String> mapSearch = query.getSearchCriteriaMap();
            if(mapSearch == null) {
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
     *
     * In every other case it returns true, which means that the filters were present but,
     * there were no matches in ASSET module.
     *
     * @param query
     * @return
     * @throws ServiceException
     */
    @Override
    public boolean checkAndEnrichIfVesselFiltersArePresent(FishingActivityQuery query) throws ServiceException {
        Map<SearchFilter, String> searchCriteriaMap                     = query.getSearchCriteriaMap();
        Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues = query.getSearchCriteriaMapMultipleValues();
        List<String> guidsFromAssets;
        String vesselSearchStr      = searchCriteriaMap.get(SearchFilter.VESSEL);
        String vesselGroupSearchStr = searchCriteriaMap.get(SearchFilter.VESSEL_GROUP);
        if(StringUtils.isNotEmpty(vesselSearchStr) || StringUtils.isNotEmpty(vesselGroupSearchStr)){
            guidsFromAssets = assetsServiceBean.getAssetGuids(vesselSearchStr, vesselGroupSearchStr);
            if(CollectionUtils.isEmpty(guidsFromAssets)){
                return true;
            }
            searchCriteriaMap.remove(SearchFilter.VESSEL);
            searchCriteriaMap.remove(SearchFilter.VESSEL_GROUP);
            if(searchCriteriaMapMultipleValues == null){
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
    public FishingActivityViewDTO getFishingActivityForView(Integer activityId, String tripId, List<Dataset> datasets, ActivityViewEnum view) throws ServiceException {
        Geometry geom = getRestrictedAreaGeometry(datasets);
        FishingActivityEntity activityEntity = fishingActivityDao.getFishingActivityById(activityId, geom);
        if (activityEntity == null)
            throw new ServiceException("Could not find FishingActivityEntity for the given id:" + activityId);

        log.debug("FishingActivityEntity fetched from database with id:" + activityEntity.getId());
        FishingActivityViewDTO fishingActivityViewDTO = ActivityViewMapperFactory.getMapperForView(view).mapFaEntityToFaDto(activityEntity);
        fishingActivityViewDTO.setTripWidgetDto(fishingTripServiceBean.getTripWidgetDto(activityEntity,tripId));
        log.debug("fishingActivityView generated after mapping is :"+fishingActivityViewDTO);
        return fishingActivityViewDTO;
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
        if(searchMapWithMultipleValues == null)
            throw new ServiceException("No purpose code provided for the Fishing activity filters! At least one needed!");

        Map<SearchFilter, String> searchMap = query.getSearchCriteriaMap();
        if(searchMap == null)
            return;

        Set<SearchFilter> filtersWhichSupportMultipleValues = FilterMap.getFiltersWhichSupportMultipleValues();

        Iterator<Map.Entry<SearchFilter, String>> searchMapIterator = searchMap.entrySet().iterator();
        while (searchMapIterator.hasNext()) {
            Map.Entry<SearchFilter, String> e = searchMapIterator.next();
            SearchFilter key = e.getKey();
            String value = e.getValue();
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

    /*
     * Query to calculate total number of result set
     */
    private Integer getRecordsCountForFilterFishingActivityReports(FishingActivityQuery query) throws ServiceException {
        log.info(" Get total pages count");
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
            activityReportDTOList.add(FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTO(entity));
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


}