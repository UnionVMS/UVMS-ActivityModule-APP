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

package eu.europa.ec.fisheries.ers.fa.dao;

import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.builder.FishingTripSearchBuilder;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

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


    public FishingTripEntity fetchVesselTransportDetailsForFishingTrip(String fishingTripId) {
        String sql = "SELECT DISTINCT ft from FishingTripEntity ft JOIN FETCH ft.fishingActivity a" +
                "  JOIN FETCH a.faReportDocument fa" +
                "  JOIN FETCH fa.vesselTransportMeans vt" +
                "  JOIN FETCH vt.contactParty cparty " +
                "  JOIN FETCH cparty.structuredAddresses sa " +
                "  JOIN FETCH cparty.contactPerson cPerson " +
                "  JOIN FETCH ft.fishingTripIdentifiers fi " +
                "  where fi.tripId =:fishingTripId and a is not null";

        TypedQuery<FishingTripEntity> typedQuery = em.createQuery(sql, FishingTripEntity.class);
        typedQuery.setParameter("fishingTripId", fishingTripId);
        typedQuery.setMaxResults(1);

        return typedQuery.getSingleResult();
    }

    /**
     * Get all the Fishing Trip entities for matching Filters
     *
     * @param searchCriteriaMap         Filters with single value
     * @param searchMapWithMultipleVals Filters with multiple values
     * @return List of all FishingTripEntities (Duplicate FishingTrips could be possible in this list)
     * @throws ServiceException
     */
    public List<FishingTripEntity> getFishingTripsForMatchingFilterCriteria(Map<SearchFilter, String> searchCriteriaMap, Map<SearchFilter, List<String>> searchMapWithMultipleVals) throws ServiceException {

        FishingTripSearchBuilder search = new FishingTripSearchBuilder();

        // Prepare FishingActivityQuery as expected by create SQL API
        FishingActivityQuery query = new FishingActivityQuery();
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setSearchCriteriaMapMultipleValues(searchMapWithMultipleVals);
        StringBuilder sqlToGetActivityList = search.createSQL(query); // Create SQL Dynamically based on Filters provided
        log.debug("SQL:" + sqlToGetActivityList);

        Query typedQuery = em.createQuery(sqlToGetActivityList.toString());
        Query listQuery = search.fillInValuesForTypedQuery(query, typedQuery);  // Add values to the Query built
        return listQuery.getResultList();
    }
}
