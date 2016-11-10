package eu.europa.ec.fisheries.uvms.activity.model.dto;
/*import org.wololo.geojson.GeoJSON;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.jts2geojson.GeoJSONReader;*/

public class GeometryDeserializer{ /*extends JsonDeserializer<Geometry> {

    private static final int DEFAULT_SRID = 4326;

    @Override
    public Geometry deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        GeoJSON geoJSON = GeoJSONFactory.create(jsonParser.readValueAsTree().toString());
        Geometry geom = new GeoJSONReader().read(geoJSON);
        if (geom.getSRID() == 0) {
            geom.setSRID(DEFAULT_SRID);
        }
        return geom;
    }*/
}