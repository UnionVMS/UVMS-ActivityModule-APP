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
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.builder.FishingActivitySearchBuilder;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by padhyad on 5/3/2016.
 */
@Slf4j
public class FishingActivityDao extends AbstractDAO<FishingActivityEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(FishingActivityDao.class);
    private StringBuilder activityViewQuery;

    private EntityManager em;

    public FishingActivityDao(EntityManager em) {
        this.em = em;
    }


    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<FishingActivityEntity> getFishingActivityListForFishingTrip(String fishingTripId, Geometry multipolgon) throws ServiceException {
        if (fishingTripId == null || fishingTripId.length() == 0)
            throw new ServiceException("fishing Trip Id is null or empty. ");

        String queryName = FishingActivityEntity.ACTIVITY_FOR_FISHING_TRIP;
        if (multipolgon == null)
            queryName = FishingActivityEntity.FIND_FA_DOCS_BY_TRIP_ID_WITHOUT_GEOM;

        Query query = getEntityManager().createNamedQuery(queryName);

        query.setParameter("fishingTripId", fishingTripId);

        if (multipolgon != null)
            query.setParameter("area", multipolgon);

        return query.getResultList();
    }

    public Integer getCountForFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        FishingActivitySearchBuilder search = new FishingActivitySearchBuilder();
           LOG.info("Get Total Count for Fishing Activities When filter criteria is present");
           StringBuilder sqlToGetActivityListCount =search.createSQL(query);

           Query countQuery= getTypedQueryForFishingActivityFilter(sqlToGetActivityListCount, query, search);

        return countQuery.getResultList().size();
    }


       /**
        * Set typed values for Dynamically generated Query
        */
    private Query getTypedQueryForFishingActivityFilter(StringBuilder sql, FishingActivityQuery query, FishingActivitySearchBuilder search) throws ServiceException {
        LOG.debug("Set Typed Parameters to Query");

        Query typedQuery = em.createQuery(sql.toString());
        return search.fillInValuesForTypedQuery(query, typedQuery);

    }


    /*
     Get all the Fishing Activities which match Filter criterias mentioned in the Input. Also, provide the sorted data based on what user has requested.
     Provide paginated data if user has asked for it
     */
    public List<FishingActivityEntity> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        LOG.info("Get Fishing Activity Report list by Query.");
        FishingActivitySearchBuilder search = new FishingActivitySearchBuilder();

        // Create Query dynamically based on filter and Sort criteria
        StringBuilder sqlToGetActivityList = search.createSQL(query);

        // Apply real values to Query built
        Query listQuery = getTypedQueryForFishingActivityFilter(sqlToGetActivityList, query, search);

        PaginationDto pagination = query.getPagination();
        if (pagination != null) {
            listQuery.setFirstResult(pagination.getOffset());
            listQuery.setMaxResults(pagination.getPageSize());
        }

        return listQuery.getResultList();
    }

    /**
     * Returns a FishingActivityEntity with joined tables depending on the view (ARRIVAL, DEPARTURE, etc..).
     *
     * @param activityId
     * @param geom
     * @return
     */
    public FishingActivityEntity getFishingActivityById(String activityId, Geometry geom) throws ServiceException {
        fillQueryConditions(geom);
        Query typedQuery = getEntityManager().createQuery(activityViewQuery.toString());
        typedQuery.setParameter("fishingActivityId", Integer.parseInt(activityId));
        if(geom != null){
            typedQuery.setParameter("area", geom);
        }
        List<FishingActivityEntity> resultList = typedQuery.getResultList();
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }
        return resultList.get(0);
    }

    private void fillQueryConditions(Geometry geom) {
        activityViewQuery = new StringBuilder("SELECT DISTINCT a from FishingActivityEntity a ")
  /*              .append("LEFT JOIN FETCH a.faReportDocument fa ")
                .append("LEFT JOIN FETCH a.fluxLocations fl ")
                .append("LEFT JOIN FETCH a.fishingGears fg ")
                .append("LEFT JOIN FETCH a.fluxCharacteristics fc ")
                .append("LEFT JOIN FETCH a.faCatchs fCatch ")
                .append("LEFT JOIN FETCH fg.fishingGearRole ")
                .append("LEFT JOIN FETCH fg.gearCharacteristics ")
                .append("LEFT JOIN FETCH fa.fluxReportDocument flux ")*/
                .append("WHERE ");
        if(geom != null){
            activityViewQuery.append("(intersects(fa.geom, :area) = true ").append("and a.id=:fishingActivityId) ");
        } else {
            activityViewQuery.append("a.id=:fishingActivityId ");
        }
    }

}