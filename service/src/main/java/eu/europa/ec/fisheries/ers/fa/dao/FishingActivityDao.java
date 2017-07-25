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
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Slf4j
public class FishingActivityDao extends AbstractDAO<FishingActivityEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(FishingActivityDao.class);

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
        StringBuilder sqlToGetActivityListCount = search.createSQL(query);

        Query countQuery = getTypedQueryForFishingActivityFilter(sqlToGetActivityListCount, query, search);

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

        // Agreed with frontend.
        // Page size : Number of record to be retrieved in one page
        // offSet : The position from where the result should be picked. Starts with 0

        PaginationDto pagination = query.getPagination();
        if (pagination != null) {
            LOG.debug("Pagination information getting applied to Query is: Offset :"+pagination.getOffset() +" PageSize:"+pagination.getPageSize());
            
            listQuery.setFirstResult(pagination.getOffset());
            listQuery.setMaxResults(pagination.getPageSize());
        }

        List resultList = listQuery.getResultList();
        Hibernate.initialize(resultList);

        return resultList;
    }

    /**
     * Returns a FishingActivityEntity with joined tables depending on the view (ARRIVAL, DEPARTURE, etc..).
     *
     * @param activityId
     * @param geom
     * @return
     */
    public FishingActivityEntity getFishingActivityById(Integer activityId, Geometry geom) {
        String s = fillQueryConditions(geom);
        Query typedQuery = getEntityManager().createQuery(s);
        typedQuery.setParameter("fishingActivityId", activityId);
        if(geom != null){
            typedQuery.setParameter("area", geom);
        }
        List<FishingActivityEntity> resultList = typedQuery.getResultList();
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }
        return resultList.get(0);
    }

    public List<FishingActivityEntity> getFishingActivityForTrip(String tripId, String tripSchemeId, String fishActTypeCode, List<String> flPurposeCodes) {
        Query typedQuery = getEntityManager().createNamedQuery(FishingActivityEntity.FIND_FISHING_ACTIVITY_FOR_TRIP);
        typedQuery.setParameter("fishingTripId", tripId);
        typedQuery.setParameter("tripSchemeId", tripSchemeId);
        typedQuery.setParameter("fishActTypeCode", fishActTypeCode);
        typedQuery.setParameter("flPurposeCodes", flPurposeCodes);
        return typedQuery.getResultList();
    }

    private String fillQueryConditions(Geometry geom) {

        StringBuilder sb = new StringBuilder("SELECT DISTINCT a from FishingActivityEntity a ")
                .append("LEFT JOIN FETCH a.faReportDocument fa ")
                .append("LEFT JOIN FETCH a.fluxLocations fl ")
                .append("LEFT JOIN FETCH a.fishingGears fg ")
                .append("LEFT JOIN FETCH a.fluxCharacteristics fc ")
                .append("LEFT JOIN FETCH a.faCatchs fCatch ")
                .append("LEFT JOIN FETCH a.allRelatedFishingActivities relatedActivities ")
                .append("LEFT JOIN FETCH relatedActivities.vesselTransportMeans relvtm ")
                .append("LEFT JOIN FETCH relvtm.vesselIdentifiers relvid ")
                .append("LEFT JOIN FETCH relatedActivities.faCatchs relCatch ")
                .append("LEFT JOIN FETCH fCatch.fluxLocations ")
                .append("LEFT JOIN FETCH fCatch.sizeDistribution ")
                .append("LEFT JOIN FETCH fCatch.fishingGears ")
                .append("LEFT JOIN FETCH fCatch.fluxCharacteristics ")
                .append("LEFT JOIN FETCH fg.fishingGearRole ")
                .append("LEFT JOIN FETCH fg.gearCharacteristics ")
                .append("LEFT JOIN FETCH fa.fluxReportDocument flux ")
                .append("LEFT JOIN FETCH a.fluxCharacteristics fluxChar ")
                .append("LEFT JOIN FETCH fCatch.fluxCharacteristics fluxCharFa ")
                .append("LEFT JOIN FETCH fl.fluxCharacteristic fluxCharFluxLoc ")
                .append("LEFT JOIN FETCH fl.structuredAddresses flAd ")
                .append("LEFT JOIN FETCH a.flapDocuments flapDoc ")
                .append("LEFT JOIN FETCH flux.fluxParty fluxParty ")
                .append("LEFT JOIN FETCH a.fishingActivityIdentifiers faId ")
                .append("LEFT JOIN FETCH flAd.fluxLocation flAdFluxLoc ")
                .append("LEFT JOIN FETCH a.fishingTrips faFiTrips ")
                .append("LEFT JOIN FETCH faFiTrips.fishingTripIdentifiers tripIdentifiers ")
                .append("LEFT JOIN FETCH faFiTrips.faCatch faFiTripsFaCatch ")
                .append("LEFT JOIN FETCH a.gearProblems gearProb ")
                .append("WHERE ");
        if(geom != null){
            sb.append("(intersects(fa.geom, :area) = true ").append("and a.id=:fishingActivityId) ");
        } else {
            sb.append("a.id=:fishingActivityId ");
        }
        return sb.toString();
    }
}