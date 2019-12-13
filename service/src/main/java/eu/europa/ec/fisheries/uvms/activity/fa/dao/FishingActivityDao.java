/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries  European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.dao;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.service.search.builder.FishingActivitySearchBuilder;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.commons.service.dao.AbstractDAO;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class FishingActivityDao extends AbstractDAO<FishingActivityEntity> {

    private static final String QUERY_PARAM_FISHING_TRIP_ID = "fishingTripId";
    private static final String QUERY_PARAM_FISHING_ACTIVITY_ID = "fishingActivityId";
    private static final String QUERY_PARAM_ACTIVITY_TYPE_CODE = "activityTypeCode";
    private static final String QUERY_PARAM_ACTIVITY_START_TIME = "activityStartTime";
    private static final String QUERY_PARAM_AREA = "area";

    private static final Logger LOG = LoggerFactory.getLogger(FishingActivityDao.class);

    private EntityManager em;

    public FishingActivityDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * To find out previous Activity date
     * Use referenceId to find our previous FishingActivityReportDocument.
     * Then choose all the fishingActivities from that document with same FishingActivity Type and having date less than or equal to current fishingActivity
     */
    public int getPreviousFishingActivityId(FishingActivityEntity fishingActivityEntity) {
        int previousActivityId = 0;
        if (fishingActivityEntity.getTypeCode() == null || fishingActivityEntity.getCalculatedStartTime() == null) {
            LOG.warn("activityTypeCode OR activityCalculatedStartTime is null.");
            return previousActivityId;
        }

        String queryString = "SELECT a.id from FishingActivityEntity a " +
                "JOIN a.faReportDocument fa " +
                "JOIN fa.fluxReportDocument flux " +
                "JOIN flux.fluxReportIdentifiers fluxIds " +
                "where fluxIds.fluxReportIdentifierId IN " +
                "( select frd.referenceId from " +
                "   FluxReportDocumentEntity frd " +
                "   JOIN frd.faReportDocument fa " +
                "   JOIN  fa.fishingActivities fishingActivities where " +
                "   fishingActivities.id = :fishingActivityId and " +
                "   fishingActivities.typeCode = :activityTypeCode" +
                ") AND " +
                "a.calculatedStartTime <= :activityStartTime " +
                "ORDER BY a.calculatedStartTime desc";

        Query query = getEntityManager().createQuery(queryString);
        query.setParameter(QUERY_PARAM_FISHING_ACTIVITY_ID, fishingActivityEntity.getId());
        query.setParameter(QUERY_PARAM_ACTIVITY_TYPE_CODE, fishingActivityEntity.getTypeCode());
        query.setParameter(QUERY_PARAM_ACTIVITY_START_TIME, fishingActivityEntity.getCalculatedStartTime());

        query.setMaxResults(1); // There could be multiple fishing Activities matching the condition, but we need just one.

        Object result = null;
        try {
            result = query.getSingleResult();
        } catch (NoResultException e) {
            LOG.warn("No next FishingActivity present for: {}", fishingActivityEntity.getId());
        }

        LOG.debug("Previous Fishing Activity: {}", result);
        if (result != null) {
            previousActivityId = (int) result;
        }

        return previousActivityId;
    }


    /**
     *  To find out next fishingActivityId
     *  Take fluxIdentifierId of current fishingActivity. Find  which other faReportDocument referenceId has fluxIdentifierId of current fishingActivity.
     *  Take that faReportDocument , and all the fishingActivities from that document with same FishingActivity Type and having date greater than or equal to current fishingActivity
     *
     */
    public int getNextFishingActivityId(FishingActivityEntity fishingActivityEntity) {
        int nextFishingActivity = 0;
        if (fishingActivityEntity.getTypeCode() == null || fishingActivityEntity.getCalculatedStartTime() == null) {
            LOG.warn("activityTypeCode OR  activityCalculatedStartTime is null.");
            return nextFishingActivity;
        }
        String queryString = "SELECT a.id from FishingActivityEntity a " +
                "JOIN a.faReportDocument fa " +
                "JOIN fa.fluxReportDocument flux " +
                "where flux.referenceId IN " +
                "( select fri.fluxReportIdentifierId from " +
                "   FluxReportIdentifierEntity fri JOIN " +
                "   fri.fluxReportDocument frd JOIN" +
                "   frd.faReportDocument fa JOIN  " +
                "   fa.fishingActivities fishingActivities " +
                "   where fishingActivities.id = :fishingActivityId and " +
                "         fishingActivities.typeCode = :activityTypeCode" +
                ") AND " +
                "a.calculatedStartTime >= :activityStartTime " +
                "ORDER BY a.calculatedStartTime asc";

        Query query = getEntityManager().createQuery(queryString);
        query.setParameter(QUERY_PARAM_FISHING_ACTIVITY_ID, fishingActivityEntity.getId());
        query.setParameter(QUERY_PARAM_ACTIVITY_TYPE_CODE, fishingActivityEntity.getTypeCode());
        query.setParameter(QUERY_PARAM_ACTIVITY_START_TIME, fishingActivityEntity.getCalculatedStartTime());

        query.setMaxResults(1);

        Object result = null;
        try {
            result = query.getSingleResult();
        } catch (NoResultException e) {
            LOG.warn("No next FishingActivity present for: {}", fishingActivityEntity.getId());
        }

        LOG.debug("Next Fishing Activity: {}", result);
        if (result != null) {
            nextFishingActivity = (int) result;
        }
        return nextFishingActivity;
    }

    /**
     * This method will retrieve all the fishingActivities received for the trip order by Activity type and then by FAReport accepted date.
     * so that we know which are corrected activities received.
     * @param fishingTripId
     * @param multipolygon
     * @throws ServiceException
     */
    public List<FishingActivityEntity> getFishingActivityListForFishingTrip(String fishingTripId, Geometry multipolygon) throws ServiceException {
        if (fishingTripId == null || fishingTripId.length() == 0) {
            throw new ServiceException("fishing Trip Id is null or empty. ");
        }

        String queryName = FishingActivityEntity.ACTIVITY_FOR_FISHING_TRIP;
        if (multipolygon == null) {
            queryName = FishingActivityEntity.FIND_FA_DOCS_BY_TRIP_ID_WITHOUT_GEOM;
        }

        TypedQuery<FishingActivityEntity> typedQuery = getEntityManager().createNamedQuery(queryName, FishingActivityEntity.class);
        typedQuery.setParameter(QUERY_PARAM_FISHING_TRIP_ID, fishingTripId);
        if (multipolygon != null) {
            typedQuery.setParameter(QUERY_PARAM_AREA, multipolygon);
        }

        return typedQuery.getResultList();
    }

    public Integer getCountForFishingActivityListByQuery(FishingActivityQuery fishingActivityQuery) throws ServiceException {
        LOG.info("Get Total Count for Fishing Activities When filter criteria is present");
        FishingActivitySearchBuilder search = new FishingActivitySearchBuilder();
        StringBuilder sqlToGetActivityListCount = search.createCountingSql(fishingActivityQuery);
        String replace = sqlToGetActivityListCount.toString().replace("JOIN FETCH", "JOIN");
        Query query = getQueryForFishingActivityFilter(new StringBuilder(replace), fishingActivityQuery, search);
        Long nrOfFa = (Long) query.getResultList().get(0);
        return nrOfFa.intValue();
    }

    /**
     * Set typed values for Dynamically generated Query
     */
    private Query getQueryForFishingActivityFilter(StringBuilder sql, FishingActivityQuery fishingActivityQuery, FishingActivitySearchBuilder search) throws ServiceException {
        LOG.debug("Set Typed Parameters to Query");
        Query query = em.createQuery(sql.toString());
        return search.fillInValuesForQuery(fishingActivityQuery, query);
    }

    /*
     Get all the Fishing Activities which match Filter criterias mentioned in the Input. Also, provide the sorted data based on what user has requested.
     Provide paginated data if user has asked for it
     */
    public List<FishingActivityEntity> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        if (query.getUseStatusInsteadOfPurposeCode() != null && query.getUseStatusInsteadOfPurposeCode()) {
            adaptForStatusInsteadOfPurpose(query);
        }

        LOG.info("Get Fishing Activity Report list by Query.");
        FishingActivitySearchBuilder search = new FishingActivitySearchBuilder();

        // Create Query dynamically based on filter and Sort criteria
        StringBuilder sqlToGetActivityList = search.createSQL(query);

        // Apply real values to Query built
        Query listQuery = getQueryForFishingActivityFilter(sqlToGetActivityList, query, search);

        // Agreed with frontend : Page size : Number of record to be retrieved in one page; offSet : The position from where the result should be picked. Starts with 0
        PaginationDto pagination = query.getPagination();
        if (pagination != null) {
            LOG.debug("Pagination information getting applied to Query is: Offset: {} PageSize. {}", pagination.getOffset(), pagination.getPageSize());
            listQuery.setFirstResult(pagination.getOffset());
            listQuery.setMaxResults(pagination.getPageSize());
        }
        return listQuery.getResultList();
    }

    private void adaptForStatusInsteadOfPurpose(FishingActivityQuery query) {
        Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues = query.getSearchCriteriaMapMultipleValues();
        if (MapUtils.isNotEmpty(searchCriteriaMapMultipleValues)) {
            List<String> purposes = searchCriteriaMapMultipleValues.get(SearchFilter.PURPOSE);
            List<String> statuses = new ArrayList<>();
            for (String purpose : purposes) {
                statuses.add(FaReportStatusType.getFaReportStatusEnum(Integer.parseInt(purpose)).name());
            }
            searchCriteriaMapMultipleValues.remove(SearchFilter.PURPOSE);
            searchCriteriaMapMultipleValues.put(SearchFilter.FA_STATUS, statuses);
        }
    }

    /**
     * Returns a FishingActivityEntity with joined tables depending on the view (ARRIVAL, DEPARTURE, etc..).
     *
     * @param activityId
     * @param geometry
     */
    public FishingActivityEntity getFishingActivityById(Integer activityId, Geometry geometry) {
        TypedQuery<FishingActivityEntity> typedQuery = createFishingActivityEntityQuery(geometry);
        typedQuery.setParameter(QUERY_PARAM_FISHING_ACTIVITY_ID, activityId);

        if (geometry != null) {
            typedQuery.setParameter(QUERY_PARAM_AREA, geometry);
        }

        List<FishingActivityEntity> resultList = typedQuery.getResultList();
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }

        return resultList.get(0);
    }

    private TypedQuery<FishingActivityEntity> createFishingActivityEntityQuery(Geometry geometry) {
        StringBuilder stringBuilder = new StringBuilder("SELECT DISTINCT a from FishingActivityEntity a ")
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
                .append("LEFT JOIN FETCH flAd.fluxLocation flAdFluxLoc ")
                .append("LEFT JOIN FETCH a.fishingTrip faFiTrip ")
                .append("LEFT JOIN FETCH faFiTrip.catchEntities faFiTripsFaCatch ")
                .append("LEFT JOIN FETCH a.gearProblems gearProb ")
                .append("WHERE ");
        if (geometry != null) {
            stringBuilder.append("(intersects(fa.geom, :area) = true ").append("and a.id =: fishingActivityId) ");
        } else {
            stringBuilder.append("a.id =: fishingActivityId ");
        }
        return getEntityManager().createQuery(stringBuilder.toString(), FishingActivityEntity.class);
    }
}
