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
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.UsmUtils;
import eu.europa.ec.fisheries.ers.message.producer.ActivityMessageProducer;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.SpatialModuleService;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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

    @EJB
    private ActivityMessageProducer activityProducer;

    @EJB
    private AssetsMessageConsumerBean activityConsumer;

    @EJB
    private SpatialModuleService spatialModule;

    @PostConstruct
    public void init() {
        fishingActivityDao = new FishingActivityDao(em);
        faReportDocumentDao = new FaReportDocumentDao(em);
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

        // Check if any filters are present. If not, We need to return all fishing activity data

        String areaWkt= getRestrictedAreaGeom(datasets);
        log.debug("Geometry for the user received from USM : "+ areaWkt);
        if(areaWkt!=null && areaWkt.length() > 0){
            Map<SearchFilter, String> mapSearch= query.getSearchCriteriaMap();
            if(mapSearch ==null) {
                mapSearch = new HashMap<>();
                query.setSearchCriteriaMap(mapSearch);
            }
            mapSearch.put(SearchFilter.AREA_GEOM,areaWkt);
        }
        query = separateSingleVsMultipleFilters(query);
        activityList = fishingActivityDao.getFishingActivityListByQuery(query);

        int totalCountOfRecords = getRecordsCountForFilterFishingActivityReports(query);
        log.debug("Total count of records: {} ", totalCountOfRecords);

        if (CollectionUtils.isEmpty(activityList)) {
            log.debug("Could not find FishingActivity entities matching search criteria");
            activityList = Collections.emptyList();
        }

        // Prepare DTO to return to Frontend
        log.debug("Fishing Activity Report resultset size :" + ((activityList == null) ? "list is null" : "" + activityList.size()));
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO = new FilterFishingActivityReportResultDTO();
        filterFishingActivityReportResultDTO.setResultList(mapToFishingActivityReportDTOList(activityList));
        filterFishingActivityReportResultDTO.setTotalCountOfRecords(totalCountOfRecords);

        return filterFishingActivityReportResultDTO;
    }

    // Improve this part later on
    private FishingActivityQuery separateSingleVsMultipleFilters(FishingActivityQuery query) throws ServiceException {
        Map<SearchFilter, List<String>> searchMapWithMultipleValues = query.getSearchCriteriaMapMultipleValues();
        if(searchMapWithMultipleValues ==null)
            throw new ServiceException("No purpose code provided for the Fishing activity filters! At least one needed!");

        Map<SearchFilter, String> searchMap = query.getSearchCriteriaMap();
        if(searchMap == null)
            return query;

        Set<SearchFilter> filtersWhichSupportMultipleValues = FilterMap.getFiltersWhichSupportMultipleValues();

        Iterator<Map.Entry<SearchFilter, String>> it = searchMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<SearchFilter, String> e = it.next();
            SearchFilter key = e.getKey();
            String value = e.getValue();
            if (filtersWhichSupportMultipleValues.contains(key)) {
                List<String> values = new ArrayList<>();
                values.add(value);
                searchMapWithMultipleValues.put(key, values);
                it.remove();
            }
        }

        query.setSearchCriteriaMapMultipleValues(searchMapWithMultipleValues);
        query.setSearchCriteriaMap(searchMap);
        return query;
    }

    /*
     * Query to calculate total number of result set
     */
    private Integer getRecordsCountForFilterFishingActivityReports(FishingActivityQuery query) throws ServiceException {
        log.info(" Get total pages count");
        return fishingActivityDao.getCountForFishingActivityListByQuery(query);
    }

    private String getRestrictedAreaGeom(List<Dataset> datasets) throws ServiceException {
        if (datasets == null || datasets.isEmpty()) {
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
}