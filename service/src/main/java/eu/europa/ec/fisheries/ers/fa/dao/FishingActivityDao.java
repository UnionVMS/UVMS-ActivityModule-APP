/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries  European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.dao;


import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.FishingActivitySearch;
import eu.europa.ec.fisheries.ers.service.search.Pagination;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by padhyad on 5/3/2016.
 */

public class FishingActivityDao extends AbstractDAO<FishingActivityEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(FishingActivityDao.class);

    private static  final String FISHING_ACTIVITY_LIST_ALL_DATA="SELECT DISTINCT a  from FishingActivityEntity a LEFT JOIN FETCH a.faReportDocument fa where fa.status = '"+ FaReportStatusEnum.NEW.getStatus() +"' order by fa.acceptedDatetime asc ";

    private EntityManager em;

    public FishingActivityDao(EntityManager em) {
        this.em = em;
    }


    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<FishingActivityEntity> getFishingActivityList() throws ServiceException {
        return getFishingActivityList(null);
    }


    public List<FishingActivityEntity> getFishingActivityListForFishingTrip(String fishingTripId, Geometry multipolgon) throws ServiceException {
        if(fishingTripId == null || fishingTripId.length() == 0)
            throw new ServiceException("fishing Trip Id is null or empty. ");
        Query query = getEntityManager().createNamedQuery(FishingActivityEntity.ACTIVITY_FOR_FISHING_TRIP);

        query.setParameter("fishingTripId", fishingTripId);
        query.setParameter("area", multipolgon);
        return query.getResultList();
    }

    public List<FishingActivityEntity> getFishingActivityList(Pagination pagination) throws ServiceException {
        LOG.info("There are no Filters present to filter Fishing Activity Data. so, fetch all the Fishing Activity Records");
        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(FISHING_ACTIVITY_LIST_ALL_DATA, FishingActivityEntity.class);

        if(pagination!=null) {
            int listSize =pagination.getListSize();
            int pageNumber = pagination.getPage();
            if(listSize ==0 || pageNumber ==0)
                  throw new ServiceException("Error is pagination list size or page number.Please enter valid values. List Size provided: "+listSize + " Page number:"+pageNumber);

            typedQuery.setFirstResult(listSize * (pageNumber - 1));
            typedQuery.setMaxResults(listSize);
        }

        return typedQuery.getResultList();
    }

    public Integer getCountForFishingActivityList()  {
        LOG.info("Get Total Count for Fishing Activities When no filter criteria is present");
        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(FISHING_ACTIVITY_LIST_ALL_DATA, FishingActivityEntity.class);
        return typedQuery.getResultList().size();
    }


    public Integer getCountForFishingActivityListByQuery(FishingActivityQuery query, Geometry multipolygon) throws ServiceException {
        FishingActivitySearch search = new FishingActivitySearch();
        LOG.info("Get Total Count for Fishing Activities When filter criteria is present");
        StringBuilder sqlToGetActivityListCount =search.createSQL(query);

        Query countQuery= getTypedQueryForFishingActivityFilter(sqlToGetActivityListCount, query, multipolygon,search);

        return countQuery.getResultList().size();
    }


    /**
     * Set typed values for Dynamically generated Query
     */
    private Query getTypedQueryForFishingActivityFilter(StringBuilder sql, FishingActivityQuery query, Geometry multipolygon,FishingActivitySearch search) throws ServiceException {
        LOG.debug("Set Typed Parameters to Query");

        Query typedQuery = em.createQuery(sql.toString());
        return search.getTypedQueryForFishingActivityFilter(sql, query, multipolygon, typedQuery);
    }


    /*
     Get all the Fishing Activities which match Filter criterias mentioned in the Input. Also, provide the sorted data based on what user has requested.
     Provide paginated data if user has asked for it
     */
    public List<FishingActivityEntity> getFishingActivityListByQuery(FishingActivityQuery query, Geometry multipolygon) throws ServiceException {
        LOG.info("Get Fishing Activity Report list by Query.");
        FishingActivitySearch search = new FishingActivitySearch();

        // Create Query dynamically based on filter and Sort criteria
        StringBuilder sqlToGetActivityList =search.createSQL(query);

        // Apply real values to Query built
        Query listQuery= getTypedQueryForFishingActivityFilter(sqlToGetActivityList,query, multipolygon,search);

        PaginationDto pagination= query.getPagination();
        if(pagination!=null) {
            listQuery.setFirstResult(pagination.getPageSize() * (pagination.getOffset() - 1));
            listQuery.setMaxResults(pagination.getPageSize());
        }

        return listQuery.getResultList();
    }
}