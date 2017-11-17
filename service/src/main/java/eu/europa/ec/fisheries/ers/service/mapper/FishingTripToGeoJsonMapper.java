package eu.europa.ec.fisheries.ers.service.mapper;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Geometry;

import com.vividsolutions.jts.geom.MultiPoint;

import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.FeatureToGeoJsonJacksonMapper;
import lombok.extern.slf4j.Slf4j;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.List;

/**
 * Created by sanera on 14/11/2016.
 */
@Slf4j
public class FishingTripToGeoJsonMapper {
    private static final SimpleFeatureType TRIP_FEATURE = build();
    private static final String GEOMETRY = "geometry";
    private static final String TRIP = "TRIP";

    private FishingTripToGeoJsonMapper(){

    }
    //Convert Geometry list into GEO JSON format
    public static ObjectNode toJson(List<Geometry> geoList)  {

        ObjectNode rootNode=null;

        DefaultFeatureCollection trips = new DefaultFeatureCollection(null, build());
        for(Geometry geo :geoList){
            SimpleFeature feature= toFeature(geo);
            trips.add(feature);
        }

      try {
          rootNode = new FeatureToGeoJsonJacksonMapper().convert(trips);
        } catch (IOException e) {
          log.error("Error while trying to convert fishing trips to Geo JSON",e);

        }

        return rootNode;
    }

    // build simple Feature type
    private static SimpleFeatureType build() {
        SimpleFeatureTypeBuilder sb = new SimpleFeatureTypeBuilder();
        sb.setName(TRIP);
        sb.add(GEOMETRY, MultiPoint.class);
        return sb.buildFeatureType();
    }

    // convert Geometry to Feature
    public static SimpleFeature toFeature(Geometry geometry) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TRIP_FEATURE);
        featureBuilder.set(GEOMETRY, geometry);

        SimpleFeature feature = featureBuilder.buildFeature(null);
        feature.setDefaultGeometry(geometry);
        log.debug("SimpleFeature:"+feature.toString());

        return feature;
    }



}
