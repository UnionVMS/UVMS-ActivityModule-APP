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
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class FishingActivityDao {

    private static final String QUERY_PARAM_FISHING_TRIP_ID = "fishingTripId";
    private static final String QUERY_PARAM_FISHING_ACTIVITY_ID = "fishingActivityId";
    private static final String QUERY_PARAM_AREA = "area";

    private static final Logger LOG = LoggerFactory.getLogger(FishingActivityDao.class);

    private EntityManager em;

    public FishingActivityDao(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * This method will retrieve all the fishingActivities received for the trip order by Activity type and then by FAReport accepted date.
     * so that we know which are corrected activities received.
     * @param fishingTripId
     * @throws ServiceException
     */
    public List<FishingActivityEntity> getFishingActivityListForFishingTrip(String fishingTripId) throws ServiceException {
        if (fishingTripId == null || fishingTripId.length() == 0) {
            throw new ServiceException("fishing Trip Id is null or empty. ");
        }

        String queryName = FishingActivityEntity.FIND_FA_DOCS_BY_TRIP_ID_WITHOUT_GEOM;

        TypedQuery<FishingActivityEntity> typedQuery = getEntityManager().createNamedQuery(queryName, FishingActivityEntity.class);
        typedQuery.setParameter(QUERY_PARAM_FISHING_TRIP_ID, fishingTripId);

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
        if (query.getOffset() != null && query.getPageSize() != null) {
            LOG.debug("Pagination information getting applied to Query is: Offset: {} PageSize. {}", query.getOffset(), query.getPageSize());
            listQuery.setFirstResult(query.getOffset());
            listQuery.setMaxResults(query.getPageSize());
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
                .append("LEFT JOIN FETCH a.locations fl ")
                .append("LEFT JOIN FETCH a.fishingGears fg ")
                .append("LEFT JOIN FETCH a.fluxCharacteristics fc ")
                .append("LEFT JOIN FETCH a.faCatchs fCatch ")
                .append("LEFT JOIN FETCH a.allRelatedFishingActivities relatedActivities ")
                .append("LEFT JOIN FETCH relatedActivities.vesselTransportMeans relvtm ")
                .append("LEFT JOIN FETCH relvtm.vesselIdentifiers relvid ")
                .append("LEFT JOIN FETCH relatedActivities.faCatchs relCatch ")
                .append("LEFT JOIN FETCH fCatch.locations ")
                .append("LEFT JOIN FETCH fCatch.fishingGears ")
                .append("LEFT JOIN FETCH fCatch.fluxCharacteristics ")
                .append("LEFT JOIN FETCH fg.fishingGearRole ")
                .append("LEFT JOIN FETCH fg.gearCharacteristics ")
                .append("LEFT JOIN FETCH a.fluxCharacteristics fluxChar ")
                .append("LEFT JOIN FETCH fCatch.fluxCharacteristics fluxCharFa ")
                .append("LEFT JOIN FETCH a.flapDocuments flapDoc ")
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
