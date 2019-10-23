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

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivitySummary;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselContactPartyType;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by sanera on 16/11/2016.
 */
public class FishingTripIdSearchBuilder extends SearchQueryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FishingTripIdSearchBuilder.class);
    private static final String FISHING_TRIP_JOIN = "SELECT DISTINCT ftripId.tripId , ftripId.tripSchemeId from FishingTripIdentifierEntity ftripId JOIN  ftripId.fishingTrip ft JOIN ft.fishingActivity a LEFT JOIN a.faReportDocument fa ";
    private static final String FISHING_TRIP_COUNT_JOIN = "SELECT COUNT(DISTINCT ftripId) from FishingTripIdentifierEntity ftripId JOIN  ftripId.fishingTrip ft JOIN ft.fishingActivity a LEFT JOIN a.faReportDocument fa ";

    /**
     * For some usecases we need different database column mappings for same filters.
     * We can call method which sets these specific mappings for certain business requirements while calling constructor     *
     */
    public FishingTripIdSearchBuilder() {
        super();
        FilterMap filterMap = FilterMap.createFilterMap();
        filterMap.populateFilterMappingsForFilterFishingTripIds();
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

    public StringBuilder createCountSQL(FishingActivityQuery query) throws ServiceException {
        LOG.debug("Start building SQL depending upon Filter Criterias");
        StringBuilder sql = new StringBuilder();
        sql.append(FISHING_TRIP_COUNT_JOIN); // Common Join for all filters
        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
        createWherePartForQuery(sql, query);  // Add Where part associated with Filters
        LOG.info("sql :" + sql);
        return sql;
    }

    // Build Where part of the query based on Filter criterias
    @Override
    public void createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        createWherePartForQueryForFilters(sql, query);
    }

    /**
     * Process FishingTripEntities to identify Unique FishingTrips.
     * Every FishingTripEntity has List of FishingTripIdentifiers. Every FishingTrip can ideally have maximum 2 Identifiers in the list.
     * Every Identifier in the list uniquely defines separate trip.
     * <p>
     * This method identifies unique FishingTripIdentifiers and collect Geometries for those trips.
     */
    public void processFishingTripsToCollectUniqueTrips(List<FishingTripEntity> fishingTripList, Map<FishingTripId, List<Geometry>> uniqueTripIdWithGeometry,
                                                        List<FishingActivitySummary> fishingActivityLists, Set<FishingTripId> fishingTripIdsWithoutGeom, boolean includeFishingActivities) {
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

            if (includeFishingActivities) {
                FishingActivitySummary fishingActivitySummary = getFishingActivitySummary(uniqueFishingActivityIdList, entity);
                if (fishingActivitySummary != null) {
                    fishingActivityLists.add(fishingActivitySummary);
                }
            }
        }
    }

    private FishingActivitySummary getFishingActivitySummary(Set<Integer> uniqueFishingActivityIdList, FishingTripEntity entity) {
        FishingActivitySummary fishingActivitySummary = null;
        FishingActivityEntity fishingActivityEntity = entity.getFishingActivity();
        if (fishingActivityEntity != null && uniqueFishingActivityIdList.add(fishingActivityEntity.getId())) {
            fishingActivitySummary = FishingActivityMapper.INSTANCE.mapToFishingActivitySummary(entity.getFishingActivity());
            ContactPartyEntity contactParty = getContactParty(fishingActivityEntity);
            if (contactParty != null) {
                VesselContactPartyType vesselContactParty = FishingActivityMapper.INSTANCE.mapToVesselContactParty(contactParty);
                fishingActivitySummary.setVesselContactParty(vesselContactParty);
            }
        }
        return fishingActivitySummary;
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

    private ContactPartyEntity getContactParty(FishingActivityEntity fishingActivity) {
        if ((fishingActivity.getFaReportDocument() != null)
                && (fishingActivity.getFaReportDocument().getVesselTransportMeans() != null)
                && (!fishingActivity.getFaReportDocument().getVesselTransportMeans().isEmpty())
                && (fishingActivity.getFaReportDocument().getVesselTransportMeans().iterator().next().getContactParty() != null)
                && (!fishingActivity.getFaReportDocument().getVesselTransportMeans().iterator().next().getContactParty().isEmpty())) {
            return fishingActivity.getFaReportDocument().getVesselTransportMeans().iterator().next().getContactParty().iterator().next();
        }
        return null;
    }

    protected void appendJoinFetchIfConditionDoesntExist(StringBuilder sql, String valueToFindAndApply) {
        if (sql.indexOf(valueToFindAndApply) == -1) { // Add missing join for required table
            sql.append(JOIN).append(valueToFindAndApply);

        }
    }

    protected  void appendJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(JOIN).append(joinString).append(StringUtils.SPACE);
    }

    protected void appendLeftJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(LEFT).append(JOIN).append(joinString).append(StringUtils.SPACE);
    }
}