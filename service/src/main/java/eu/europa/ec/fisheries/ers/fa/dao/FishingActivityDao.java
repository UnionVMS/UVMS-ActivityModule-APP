/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries  European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.dao;


import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.ers.service.search.*;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

/**
 * Created by padhyad on 5/3/2016.
 */

public class FishingActivityDao extends AbstractDAO<FishingActivityEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(FishingActivityDao.class);
    private  static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static  final String FISHING_ACTIVITY_LIST_ALL_DATA="SELECT DISTINCT a  from FishingActivityEntity a JOIN FETCH a.faReportDocument fa where fa.status = '"+ FaReportStatusEnum.NEW.getStatus() +"' order by fa.acceptedDatetime asc ";

    private EntityManager em;

    public FishingActivityDao(EntityManager em) {
        this.em = em;
    }


    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<FishingActivityEntity> getFishingActivityList(){
        return getFishingActivityList(null);
    }



    public List<FishingActivityEntity> getFishingActivityListForFishingTrip(String fishingTripId) throws ServiceException {
        if(fishingTripId == null || fishingTripId.length() == 0)
            throw new ServiceException("fishing Trip Id is null or empty. ");
        Query query = getEntityManager().createNamedQuery(FishingActivityEntity.ACTIVITY_FOR_FISHING_TRIP);

        query.setParameter("fishingTripId", fishingTripId);
        return query.getResultList();
    }

    public List<FishingActivityEntity> getFishingActivityList(Pagination pagination)  {
        LOG.info("There are no Filters present to filter Fishing Activity Data. so, fetch all the Fishing Activity Records");
        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(FISHING_ACTIVITY_LIST_ALL_DATA, FishingActivityEntity.class);
        if(pagination!=null) {
            typedQuery.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            typedQuery.setMaxResults(pagination.getListSize());
        }

        return typedQuery.getResultList();
    }

    public Integer getCountForFishingActivityList()  {
        LOG.info("Get Total Count for Fishing Activities When no filter criteria is present");
        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(FISHING_ACTIVITY_LIST_ALL_DATA, FishingActivityEntity.class);
        return typedQuery.getResultList().size();
    }


    public Integer getCountForFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        LOG.info("Get Total Count for Fishing Activities When filter criteria is present");
        StringBuilder sqlToGetActivityListCount =SearchQueryBuilder.createSQL(query);

        Query countQuery= getTypedQueryForFishingActivityFilter(sqlToGetActivityListCount,query);

        return countQuery.getResultList().size();
    }


    // Set typed values for Dynamically generated Query
    public  Query getTypedQueryForFishingActivityFilter(StringBuilder sql, FishingActivityQuery query){
        LOG.debug("Set Typed Parameters to Query");
        Map<Filters,String> mappings =  FilterMap.getFilterQueryParameterMappings();
        Query typedQuery = em.createQuery(sql.toString());
        Map<Filters,String> searchCriteriaMap = query.getSearchCriteriaMap();

        // Assign values to created SQL Query
        for (Map.Entry<Filters,String> entry : searchCriteriaMap.entrySet()){

            Filters key =  entry.getKey();
            String value=  entry.getValue();
            //For WeightMeasure there is no mapping present, In that case
            if(mappings.get(key) ==null)
                continue;

            switch (key) {
                case PERIOD_START:
                    typedQuery.setParameter(mappings.get(key), DateUtils.parseToUTCDate(value,FORMAT));
                    break;
                case PERIOD_END:
                    typedQuery.setParameter(mappings.get(key), DateUtils.parseToUTCDate(value,FORMAT));
                    break;
                case QUNTITY_MIN:
                    typedQuery.setParameter(mappings.get(key), SearchQueryBuilder.normalizeWeightValue(value,searchCriteriaMap.get(Filters.WEIGHT_MEASURE)));
                    break;
                case QUNTITY_MAX:
                    typedQuery.setParameter(mappings.get(key), SearchQueryBuilder.normalizeWeightValue(value,searchCriteriaMap.get(Filters.WEIGHT_MEASURE)));
                    break;
                case MASTER:
                    typedQuery.setParameter(mappings.get(key), value.toUpperCase());
                    break;
                case FA_REPORT_ID:
                    typedQuery.setParameter(mappings.get(key), Integer.parseInt(value));
                    break;
                default:
                    typedQuery.setParameter(mappings.get(key), value);
                    break;
            }

        }
        return typedQuery;
    }


    /*
     Get all the Fishing Activities which match Filter criterias mentioned in the Input. Also, provide the sorted data based on what user has requested.
     Provide paginated data if user has asked for it
     */
    public List<FishingActivityEntity> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        LOG.info("Get Fishing Activity Report list by Query.");

        // Create Query dynamically based on Dilter and Sort criteria
        StringBuilder sqlToGetActivityList =SearchQueryBuilder.createSQL(query);

        // Apply real values to Query built
        Query listQuery= getTypedQueryForFishingActivityFilter(sqlToGetActivityList,query);

        Pagination pagination= query.getPagination();
        if(pagination!=null) {
            listQuery.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            listQuery.setMaxResults(pagination.getListSize());
        }

        return listQuery.getResultList();
    }










}