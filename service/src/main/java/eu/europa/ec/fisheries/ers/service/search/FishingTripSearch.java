package eu.europa.ec.fisheries.ers.service.search;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
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
         List<FishingTripId> fishingTripIdsWithoutGeom = new ArrayList<>();  // List of unique fishing Trip ids without geometry information

        Map<FishingTripId,List<Geometry>> uniqueTripIdWithGeometry = new HashMap<>(); // Stores unique Fishing tripIds and Geometries associated with its FA Report

        processFishingTripsToCollectUniqueTrips(fishingTripList,uniqueTripIdWithGeometry,fishingActivityLists,fishingTripIdsWithoutGeom);
        mapFishingTripIdsToGeomWkt(uniqueTripIdWithGeometry,fishingTripIdLists);
        addFishingTripIdsWithoutGeomToResponseList(fishingTripIdLists,fishingTripIdsWithoutGeom);

        // populate response object
        FishingTripResponse response = new FishingTripResponse();
        response.setFishingActivityLists(fishingActivityLists);
        response.setFishingTripIdLists(fishingTripIdLists);
        return response;
    }


    // Add list of fishing trip ids without geometry to master list
    private void addFishingTripIdsWithoutGeomToResponseList(List<FishingTripIdWithGeometry> fishingTripIdLists,List<FishingTripId> fishingTripIdsWithoutGeom){
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
            FishingTripIdWithGeometry fishingTripIdWithGeometry = new FishingTripIdWithGeometry();

            Geometry geometry =GeometryUtils.createMultipoint(uniqueTripIdWithGeometry.get(fishingTripId));
            if(geometry ==null)
                throw new ServiceException("Exception while trying to create Multipoint geometry with list of geometries.");

            fishingTripIdLists.add(FishingTripIdWithGeometryMapper.INSTANCE.mapToFishingTripIdWithGeometry(fishingTripId,GeometryMapper.INSTANCE.geometryToWkt(geometry).getValue()));
        }

    }


    /*
       Process FishingTripEntities to identify Unique FishingTrips.
       Every FishingTripEntity has List of FishingTripIdentifiers. Every FishingTrip can ideally have maximum 2 Identifiers in the list.
       Every Identifier in the list uniquely defines separate trip.

       This method identifies unique FishingTripIdentifiers and collect Geometries for those trips.
     */
    private void processFishingTripsToCollectUniqueTrips(List<FishingTripEntity> fishingTripList,Map<FishingTripId,List<Geometry>> uniqueTripIdWithGeometry,List<FishingActivitySummary> fishingActivityLists,List<FishingTripId> fishingTripIdsWithoutGeom){

        for(FishingTripEntity entity:fishingTripList){
            Set<FishingTripIdentifierEntity> fishingTripIdList= entity.getFishingTripIdentifiers();
            if(fishingTripIdList ==null)
                continue;

            // Identify unique FishingTrips and collect different geometries for that fishing trip.
            for(FishingTripIdentifierEntity id:fishingTripIdList){

                FishingTripId tripIdObj=new FishingTripId(id.getTripId(),id.getTripSchemeId());
                try {
                    Geometry geometry = id.getFishingTrip().getFishingActivity().getFaReportDocument().getGeom();
                    List<Geometry> geomList = uniqueTripIdWithGeometry.get(tripIdObj);
                    if (geomList == null || geomList.isEmpty()) {
                        geomList = new ArrayList<>();
                    }
                    geomList.add(geometry);
                    uniqueTripIdWithGeometry.put(tripIdObj, geomList);
                }catch(Exception e){
                    LOG.error("Error occurred while trying to find Geometry for FishingTrip. Put tripID into separateList");
                    fishingTripIdsWithoutGeom.add(tripIdObj);
                }
            }
            // Collect Fishing Activity data
            fishingActivityLists.add(FishingActivityMapper.INSTANCE.mapToFishingActivitySummary(entity.getFishingActivity()));

        }
    }

}
