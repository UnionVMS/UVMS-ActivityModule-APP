package eu.europa.ec.fisheries.ers.service.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import eu.europa.ec.fisheries.uvms.rest.FeatureToGeoJsonJacksonMapper;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by sanera on 08/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TripMapGeoJsonDTO {

    final static Logger LOG = LoggerFactory.getLogger(TripMapGeoJsonDTO.class);
    private DefaultFeatureCollection trips = new DefaultFeatureCollection(null, build());
  private static final SimpleFeatureType TRIP_FEATURE = build();
    @JsonProperty("geometry")
    private List<Geometry> geometry;
    private static final String GEOMETRY = "geometry";

  private static SimpleFeatureType build() {
    SimpleFeatureTypeBuilder sb = new SimpleFeatureTypeBuilder();
    sb.setName("TRIP");
    sb.add(GEOMETRY, MultiPoint.class);


    return sb.buildFeatureType();
  }

   public SimpleFeature toFeature(Geometry geometry) {
      SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TRIP_FEATURE);
      featureBuilder.set(GEOMETRY, geometry);
       featureBuilder.add(geometry);

       SimpleFeature feature = featureBuilder.buildFeature(""+getId());
       feature.setDefaultGeometry(geometry);
       LOG.info("SimpleFeature:"+feature.toString());

      return feature;
    }

    private int getId(){
        int randomInt=0;
        Random randomGenerator = new Random();
        for (int idx = 1; idx <= 10; ++idx){
             randomInt = randomGenerator.nextInt(100);

        }
        return  randomInt;
    }
    public ObjectNode toJson()  {

        ObjectNode rootNode;

        List<Geometry> geoList=getGeometry();

        for(Geometry geo :geoList){
            trips.add(toFeature(geo));

        }

        ObjectMapper mapper = new ObjectMapper();
        rootNode = mapper.createObjectNode();
        try {
            rootNode.set("trips", new FeatureToGeoJsonJacksonMapper().convert(trips));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootNode;
    }

  public List<Geometry> getGeometry() {
    return geometry;
  }

  public void setGeometry(List<Geometry> geometry) {
    this.geometry = geometry;
  }
}
