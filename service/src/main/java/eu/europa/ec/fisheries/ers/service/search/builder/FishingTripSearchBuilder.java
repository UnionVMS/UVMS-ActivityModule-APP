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

package eu.europa.ec.fisheries.ers.service.search.builder;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.utils.ActivityConfigurationProperties;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivitySummary;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import static eu.europa.ec.fisheries.ers.service.search.FilterMap.populateFilterMappingsWithChangedDelimitedPeriodTable;

/**
 * Created by sanera on 16/11/2016.
 */
public class FishingTripSearchBuilder extends SearchQueryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FishingTripSearchBuilder.class);
    private static final String FISHING_TRIP_JOIN = "SELECT DISTINCT ft from FishingTripEntity ft JOIN FETCH ft.fishingTripIdentifiers ftripId LEFT JOIN FETCH ft.fishingActivity a LEFT JOIN FETCH a.faReportDocument fa ";


    /**
     *For some usecases we need different database column mappings for same filters.
     * We can call method which sets these specific mappings for certain business requirements while calling constructor     *
     */
   public FishingTripSearchBuilder(){
        super();
        FilterMap filterMap=FilterMap.createFilterMap();
        filterMap.populateFilterMappingsForFilterFishingTrips();
        setFilterMap(filterMap);
    }

    @Override
    public StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {

        LOG.debug("Start building SQL depending upon Filter Criterias");

        StringBuilder sql = new StringBuilder();


        sql.append(FISHING_TRIP_JOIN); // Common Join for all filters

        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
        createWherePartForQuery(sql, query);  // Add Where part associated with Filters
        LOG.info("sql :" + sql);

        return sql;
    }

    // Build Where part of the query based on Filter criterias
    @Override
    public StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Where part of Query");

        sql.append(" where ");
        createWherePartForQueryForFilters(sql, query);

        LOG.debug("Generated Query After Where :" + sql);
        return sql;
    }


      
    // Check if the size of unique Fishing trips is withing threshold specified
    public void checkThresholdForFishingTripList(Map<FishingTripId, List<Geometry>> uniqueTripIdWithGeometry) throws ServiceException {

        String tresholdTrips = ActivityConfigurationProperties.getValue(ActivityConfigurationProperties.LIMIT_FISHING_TRIPS);
        if (tresholdTrips != null) {
            int threshold = Integer.parseInt(tresholdTrips);
            LOG.info("fishing trip threshold value:" + threshold);
            if (uniqueTripIdWithGeometry.size() > threshold)
                throw new ServiceException("Fishing Trips found for matching criteria exceed threshold value. Please restrict resultset by modifying filters");

            LOG.info("fishing trip list size is within threshold value:" + uniqueTripIdWithGeometry.size());
        }

    }

    /**
     * Process FishingTripEntities to identify Unique FishingTrips.
     * Every FishingTripEntity has List of FishingTripIdentifiers. Every FishingTrip can ideally have maximum 2 Identifiers in the list.
     * Every Identifier in the list uniquely defines separate trip.
     *
     * This method identifies unique FishingTripIdentifiers and collect Geometries for those trips.
     */
    public void processFishingTripsToCollectUniqueTrips(List<FishingTripEntity> fishingTripList, Map<FishingTripId, List<Geometry>> uniqueTripIdWithGeometry, List<FishingActivitySummary> fishingActivityLists, Set<FishingTripId> fishingTripIdsWithoutGeom) {
        Set<Integer> uniqueFishingActivityIdList = new HashSet<>();
        for (FishingTripEntity entity : fishingTripList) {

            Set<FishingTripIdentifierEntity> fishingTripIdList = entity.getFishingTripIdentifiers();
            if (fishingTripIdList == null) {
                continue;
            }

            for (FishingTripIdentifierEntity id : fishingTripIdList) { // Identify unique FishingTrips and collect different geometries for that fishing trip.

                FishingTripId tripIdObj = new FishingTripId(id.getTripId(), id.getTripSchemeId());
                try {
                    uniqueTripIdWithGeometry.put(tripIdObj, fillGeometrylist(uniqueTripIdWithGeometry,
                            id.getFishingTrip().getFishingActivity().getFaReportDocument().getGeom(), tripIdObj));
                } catch (Exception e) {
                    LOG.error("Error occurred while trying to find Geometry for FishingTrip. Put tripID into separateList", e);
                    fishingTripIdsWithoutGeom.add(tripIdObj);
                }
            }

            FishingActivityEntity fishingActivityEntity = entity.getFishingActivity();
            if (fishingActivityEntity != null && uniqueFishingActivityIdList.add(fishingActivityEntity.getId()))
                fishingActivityLists.add(FishingActivityMapper.INSTANCE.mapToFishingActivitySummary(entity.getFishingActivity()));// Collect Fishing Activity data
        }
    }

    @NotNull
    private List<Geometry> fillGeometrylist(Map<FishingTripId, List<Geometry>> uniqueTripIdWithGeometry, Geometry geometry, FishingTripId tripIdObj) {
        List<Geometry> geomList = uniqueTripIdWithGeometry.get(tripIdObj);
        if (CollectionUtils.isEmpty(geomList)) {
            geomList = new ArrayList<>();
        }
        if (geometry != null) {
            geomList.add(geometry);
        }
        return geomList;
    }
}
