/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.GeometryUtils;
import eu.europa.ec.fisheries.ers.fa.utils.UsmUtils;
import eu.europa.ec.fisheries.ers.message.producer.ActivityMessageProducer;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.SpatialModuleService;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.Pagination;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.PaginationDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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

        int totalPages = 0;
        log.debug("FishingActivityQuery received :" + query);

        // Check if any filters are present. If not, We need to return all fishing activity data

        activityList = fishingActivityDao.getFishingActivityListByQuery(query);
        log.debug("activityList COUNT is: "+activityList.size());

        Geometry multipolygon = getRestrictedAreaGeom(datasets);

        // Execute query to count all the resultset only if TotalPages value is 0. After first search frontend should send totalPages count in subsequent calls
        Pagination pagination= query.getPagination();
        if((pagination!=null && pagination.getTotalPages()==0)  || pagination==null){
            totalPages= getTotalPagesCountForFilterFishingActivityReports(query);
            log.debug("Total number of pages : "+totalPages);
        }

        if (CollectionUtils.isEmpty(activityList)) {
            log.info("Could not find FishingActivity entities matching search criteria");
            activityList = Collections.emptyList();
        }

       // Prepare DTO to return to Frontend
        log.debug("Fishing Activity Report resultset size :" + activityList.size());
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO = new FilterFishingActivityReportResultDTO();
        filterFishingActivityReportResultDTO.setResultList(mapToFishingActivityReportDTOList(activityList, multipolygon));
        filterFishingActivityReportResultDTO.setPagination(new PaginationDTO(totalPages));

        return filterFishingActivityReportResultDTO;
    }


    // Query to calculate total number of result set
    private Integer getTotalPagesCountForFilterFishingActivityReports(FishingActivityQuery query) throws ServiceException {
    log.info(" Get total pages count");
        int countOfRecords ;
        int totalNoOfPages =1;

         countOfRecords=  fishingActivityDao.getCountForFishingActivityListByQuery(query);


        log.info(" countOfRecords:"+countOfRecords);
        Pagination pagination= query.getPagination();
        if(pagination != null){
            int listSize   = pagination.getListSize();
            totalNoOfPages = (countOfRecords+listSize-1)/listSize;
       }

        if(countOfRecords ==0)
            totalNoOfPages =0;

        return totalNoOfPages;
    }

    private Geometry getRestrictedAreaGeom(List<Dataset> datasets) throws ServiceException {
        if (datasets == null || datasets.isEmpty()) {
            return null;
        }
        List<AreaIdentifierType> areaIdentifierTypes =  UsmUtils.convertDataSetToAreaId(datasets);
        String areaWkt = spatialModule.getFilteredAreaGeom(areaIdentifierTypes);
        return GeometryUtils.wktToGeom(areaWkt);
    }

    private List<FishingActivityReportDTO> mapToFishingActivityReportDTOList(List<FishingActivityEntity> activityList, Geometry multipolygon) {
        List<FishingActivityReportDTO> activityReportDTOList = new ArrayList<>();
        for(FishingActivityEntity entity : activityList) {

            if (multipolygon != null) {
                if (entity.getGeom().intersects(multipolygon)) {
                    activityReportDTOList.add(FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTO(entity));
                }
            } else {
                activityReportDTOList.add(FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTO(entity));
            }
        }
        return activityReportDTOList;
    }
}