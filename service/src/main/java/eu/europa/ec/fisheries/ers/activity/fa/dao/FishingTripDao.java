/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.activity.fa.dao;

import eu.europa.ec.fisheries.ers.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.FluxPartyIdentifierEntity;
import eu.europa.ec.fisheries.ers.activity.service.search.FishingTripId;
import eu.europa.ec.fisheries.ers.activity.service.search.builder.FishingTripIdSearchBuilder;
import eu.europa.ec.fisheries.ers.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.commons.service.dao.AbstractDAO;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sanera on 23/08/2016.
 */
@Slf4j
public class FishingTripDao extends AbstractDAO<FishingTripEntity> {

    private EntityManager em;

    public FishingTripDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<FishingActivityEntity> getFishingActivitiesForFishingTripId(String fishingTripId){
        String sql = "SELECT DISTINCT a from FishingActivityEntity a JOIN a.fishingTrips fishingTrips" +
                "  JOIN fishingTrips.fishingTripIdentifiers fi" +
                "  where fi.tripId =:fishingTripId order by a.calculatedStartTime ASC";
        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(sql, FishingActivityEntity.class);
        typedQuery.setParameter("fishingTripId", fishingTripId);
        return typedQuery.getResultList();
    }

    public String getOwnerFluxPartyFromTripId(String fishingTripId){
        TypedQuery query = getEntityManager().createNamedQuery(FluxPartyIdentifierEntity.MESSAGE_OWNER_FROM_TRIP_ID, FluxPartyIdentifierEntity.class);
        query.setParameter("fishingTripId", fishingTripId).setMaxResults(1).getResultList();
        List<FluxPartyIdentifierEntity> resultList = query.getResultList();
        return CollectionUtils.isNotEmpty(resultList) ? resultList.get(0).getFluxPartyIdentifierId() : StringUtils.EMPTY;
    }

    /**
     * Get all the Fishing Trip entities for matching Filters
     *
     * @param query FishingActivityQuery
     * @throws ServiceException
     */
    public Set<FishingTripId> getFishingTripIdsForMatchingFilterCriteria(FishingActivityQuery query) throws ServiceException {

        Query listQuery = getQueryForFilterFishingTripIds(query);
        PaginationDto pagination = query.getPagination();
        if (pagination != null && pagination.getOffset() != null) {
            listQuery.setFirstResult(pagination.getOffset());
            listQuery.setMaxResults(pagination.getPageSize());
        }
        else {
            listQuery.setMaxResults(100);
        }

        List<Object[]> resultList = listQuery.getResultList();

        if (CollectionUtils.isEmpty(resultList))
            return Collections.emptySet();
        Set<FishingTripId> fishingTripIds = new HashSet<>();
        for(Object[] objArr :resultList){
            try {
                if (objArr !=null && objArr.length ==2){
                    fishingTripIds.add(new FishingTripId((String)objArr[0], (String)objArr[1]) );
                }
            } catch (Exception e) {
                log.error("Could not map sql selection to FishingTripId object", e);
            }
        }
        return fishingTripIds;
    }

    /**
     * Get total number of records for matching criteria without considering pagination
     *
     * @param queryDto
     * @throws ServiceException
     */
    public Integer getCountOfFishingTripsForMatchingFilterCriteria(FishingActivityQuery queryDto) throws ServiceException {
        FishingTripIdSearchBuilder search = new FishingTripIdSearchBuilder();
        StringBuilder sqlToGetActivityList = search.createCountSQL(queryDto);
        log.debug("SQL:" + sqlToGetActivityList);
        Query typedQuery = em.createQuery(sqlToGetActivityList.toString());
        Query query = search.fillInValuesForTypedQuery(queryDto, typedQuery);
        Long nrOfFa = (Long) query.getResultList().get(0);
        return nrOfFa.intValue();

    }

    private Query getQueryForFilterFishingTripIds(FishingActivityQuery query) throws ServiceException {
        FishingTripIdSearchBuilder search = new FishingTripIdSearchBuilder();
        StringBuilder sqlToGetActivityList = search.createSQL(query);
        log.debug("SQL:" + sqlToGetActivityList);
        TypedQuery<Object[]> typedQuery = em.createQuery(sqlToGetActivityList.toString(),Object[].class);
        return search.fillInValuesForTypedQuery(query, typedQuery);
    }

}
