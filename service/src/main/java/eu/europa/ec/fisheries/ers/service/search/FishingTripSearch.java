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

package eu.europa.ec.fisheries.ers.service.search;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.utils.ActivityConfigurationProperties;
import eu.europa.ec.fisheries.ers.fa.utils.GeometryUtils;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingTripIdWithGeometryMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivitySummary;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static eu.europa.ec.fisheries.ers.service.search.FilterMap.DELIMITED_PERIOD_TABLE_ALIAS;
import static eu.europa.ec.fisheries.ers.service.search.FilterMap.populateFilterMappings;

/**
 * Created by sanera on 16/11/2016.
 */
public class FishingTripSearch extends SearchQueryBuilder{
   // public static final FishingTripSearch INSTANCE = new FishingTripSearch();

    private static final Logger LOG = LoggerFactory.getLogger(FishingActivitySearch.class);
    private static final String FISHING_TRIP_JOIN = "SELECT DISTINCT ft from FishingTripEntity ft LEFT JOIN FETCH ft.fishingActivity a LEFT JOIN FETCH a.faReportDocument fa ";

    public   StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {
        LOG.debug("Start building SQL depending upon Filter Criterias");
        StringBuilder sql = new StringBuilder();

        DELIMITED_PERIOD_TABLE_ALIAS = " ft.delimitedPeriods dp ";
        populateFilterMappings();

        sql.append(FISHING_TRIP_JOIN); // Common Join for all filters

        // Create join part of SQL query
         createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
         createWherePartForQuery(sql, query);  // Add Where part associated with Filters
      //  SearchQueryBuilder.createSortPartForQuery(sql, query); // Add Order by clause for only requested Sort field
        LOG.info("sql :" + sql);

        return sql;
    }

    // Build Where part of the query based on Filter criterias
    public  StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Where part of Query");

        sql.append(" where ");

        createWherePartForQueryForFilters(sql,query);

       LOG.debug("Generated Query After Where :" + sql);
        return sql;
    }


    /*
       Create FishingTrip resopnse as expected by Execute report Filter Fishing trips functionality
     */
    public FishingTripResponse buildFishingTripSearchRespose(List<FishingTripEntity> fishingTripList) throws ServiceException {
         if(fishingTripList ==null || fishingTripList.isEmpty())
             return new FishingTripResponse();


         List<FishingTripIdWithGeometry> fishingTripIdLists = new ArrayList<>(); // List of unique fishing trip ids with geometry
         List<FishingActivitySummary> fishingActivityLists= new ArrayList<>(); // List of FishingActivities with details required by response
         Set<FishingTripId> fishingTripIdsWithoutGeom = new HashSet<>();  // List of unique fishing Trip ids without geometry information

        Map<FishingTripId,List<Geometry>> uniqueTripIdWithGeometry = new HashMap<>(); // Stores unique Fishing tripIds and Geometries associated with its FA Report


        processFishingTripsToCollectUniqueTrips(fishingTripList,uniqueTripIdWithGeometry,fishingActivityLists,fishingTripIdsWithoutGeom); // process data to find out unique FishingTrip with their Geometries
        checkThresholdForFishingTripList(uniqueTripIdWithGeometry); // Check if the size of unique Fishing trips is withing threshold specified
        mapFishingTripIdsToGeomWkt(uniqueTripIdWithGeometry,fishingTripIdLists); // Convert list of Geometries to WKT
        addFishingTripIdsWithoutGeomToResponseList(fishingTripIdLists,fishingTripIdsWithoutGeom); // There could be some fishing trips without geometries, consider those trips as well

        // populate response object
        FishingTripResponse response = new FishingTripResponse();
        response.setFishingActivityLists(fishingActivityLists);
        response.setFishingTripIdLists(fishingTripIdLists);
        return response;
    }

    // Check if the size of unique Fishing trips is withing threshold specified
    private void checkThresholdForFishingTripList(Map<FishingTripId,List<Geometry>> uniqueTripIdWithGeometry) throws ServiceException {

        String treshold_trips= ActivityConfigurationProperties.getValue(ActivityConfigurationProperties.LIMIT_FISHING_TRIPS);
        if(treshold_trips !=null) {
            int threshold = Integer.parseInt(treshold_trips);
            LOG.info("fishing trip threshold value:"+threshold);
            if (uniqueTripIdWithGeometry.size() > threshold)
                throw new ServiceException("Fishing Trips found for matching criteria exceed threshold value. Please restrict resultset by modifying filters");

            LOG.info("fishing trip list size is within threshold value:"+uniqueTripIdWithGeometry.size());
        }

    }

    // Add list of fishing trip ids without geometry to master list
    private void addFishingTripIdsWithoutGeomToResponseList(List<FishingTripIdWithGeometry> fishingTripIdLists,Set<FishingTripId> fishingTripIdsWithoutGeom){
       for(FishingTripId fishingTripId: fishingTripIdsWithoutGeom){
           fishingTripIdLists.add(FishingTripIdWithGeometryMapper.INSTANCE.mapToFishingTripIdWithGeometry(fishingTripId,null));

       }
    }

    /*
       For Every Fishing trip, combine all geometries into one and convert it into Wkt
     */
    private void mapFishingTripIdsToGeomWkt(Map<FishingTripId,List<Geometry>> uniqueTripIdWithGeometry,List<FishingTripIdWithGeometry> fishingTripIdLists) throws ServiceException {

        Set<FishingTripId>  tripIdSet= uniqueTripIdWithGeometry.keySet();
        for(FishingTripId fishingTripId: tripIdSet){

            Geometry geometry =GeometryUtils.createMultipoint(uniqueTripIdWithGeometry.get(fishingTripId));
            if(geometry ==null)
                fishingTripIdLists.add(FishingTripIdWithGeometryMapper.INSTANCE.mapToFishingTripIdWithGeometry(fishingTripId,null));
            else
                fishingTripIdLists.add(FishingTripIdWithGeometryMapper.INSTANCE.mapToFishingTripIdWithGeometry(fishingTripId,GeometryMapper.INSTANCE.geometryToWkt(geometry).getValue()));
        }

    }


    /*
       Process FishingTripEntities to identify Unique FishingTrips.
       Every FishingTripEntity has List of FishingTripIdentifiers. Every FishingTrip can ideally have maximum 2 Identifiers in the list.
       Every Identifier in the list uniquely defines separate trip.

       This method identifies unique FishingTripIdentifiers and collect Geometries for those trips.
     */
    private void processFishingTripsToCollectUniqueTrips(List<FishingTripEntity> fishingTripList,Map<FishingTripId,List<Geometry>> uniqueTripIdWithGeometry,List<FishingActivitySummary> fishingActivityLists,Set<FishingTripId> fishingTripIdsWithoutGeom){
        Set<Integer> uniqueFishingActivityIdList=new HashSet<>();
        for(FishingTripEntity entity:fishingTripList){

            LOG.info("FishingTripEntity:"+entity +" FishingActivityEntity:"+entity.getFishingActivity());
            Set<FishingTripIdentifierEntity> fishingTripIdList= entity.getFishingTripIdentifiers();
            if(fishingTripIdList ==null)
                continue;

            // Identify unique FishingTrips and collect different geometries for that fishing trip.
            for(FishingTripIdentifierEntity id:fishingTripIdList){

                FishingTripId tripIdObj=new FishingTripId(id.getTripId(),id.getTripSchemeId());
                try {

                    List<Geometry> geomList = uniqueTripIdWithGeometry.get(tripIdObj);
                    if (geomList == null || geomList.isEmpty()) {
                        geomList = new ArrayList<>();
                    }
                    Geometry geometry = id.getFishingTrip().getFishingActivity().getFaReportDocument().getGeom();
                    if(geometry!=null) {
                        geomList.add(geometry);

                    }

                    uniqueTripIdWithGeometry.put(tripIdObj, geomList);
                }catch(Exception e){
                    LOG.error("Error occurred while trying to find Geometry for FishingTrip. Put tripID into separateList");
                    fishingTripIdsWithoutGeom.add(tripIdObj);
                }
            }

            FishingActivityEntity fishingActivityEntity= entity.getFishingActivity();
            if(fishingActivityEntity !=null && uniqueFishingActivityIdList.add(fishingActivityEntity.getId()) == true )
                    fishingActivityLists.add(FishingActivityMapper.INSTANCE.mapToFishingActivitySummary(entity.getFishingActivity()));// Collect Fishing Activity data

        }
    }

}
