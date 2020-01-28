package eu.europa.ec.fisheries.uvms.activity.service.mapper;
/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class FeatureToGeoJsonJacksonMapper {

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String TYPE = "type";
    private static final String FEATURE_COLLECTION = "FeatureCollection";
    private static final String FEATURES = "features";
    private static final String FEATURE = "Feature";
    private static final String GEOMETRY = "geometry";
    private static final String PROPERTIES = "properties";

    public ObjectNode convert(FeatureCollection featureCollection) throws IOException {
        return buildFeatureCollection(featureCollection);
    }

    public ObjectNode convert(SimpleFeature feature) throws IOException {
        return buildFeature(feature);
    }

    @SuppressWarnings("unchecked")
    private ObjectNode buildFeatureCollection(FeatureCollection featureCollection) throws IOException {
        List<ObjectNode> features = new LinkedList<>();
        FeatureIterator simpleFeatureIterator = featureCollection.features();
        while (simpleFeatureIterator.hasNext()) {
            Feature simpleFeature = simpleFeatureIterator.next();
            features.add(buildFeature((SimpleFeature) simpleFeature));
        }

        ObjectNode obj = mapper.createObjectNode();
        obj.put(TYPE, FEATURE_COLLECTION);
        obj.putArray(FEATURES).addAll(features);
        return obj;
    }

    @SuppressWarnings("unchecked")
    private ObjectNode buildFeature(SimpleFeature simpleFeature) throws IOException {
        ObjectNode node = mapper.createObjectNode();
        node.put(TYPE, FEATURE);
        node.set(GEOMETRY, buildGeometry((Geometry) simpleFeature.getDefaultGeometry()));
        node.set(PROPERTIES, buildProperties(simpleFeature));
        return node;

    }

    @SuppressWarnings("unchecked")
    private ObjectNode buildProperties(SimpleFeature simpleFeature) {
        ObjectNode obj = mapper.createObjectNode();
        Collection<Property> properties = simpleFeature.getProperties();
        for (Property property : properties) {
            final Object value = property.getValue();

            if (!property.getName().getLocalPart().equals(GEOMETRY)){
                if (ArrayList.class.equals(property.getType().getBinding())){
                    ArrayNode arrayNode = mapper.createArrayNode();
                    for (Object o : (ArrayList)value){
                        arrayNode.add(o.toString());
                    }
                    obj.putArray(property.getName().toString()).addAll(arrayNode);
                }else if(List.class.equals(property.getType().getBinding())) {
                    ArrayNode arrayNode = mapper.createArrayNode();
                    for (Object o : (ArrayList) value) {
                        arrayNode.add(o.toString());
                    }
                    obj.putArray(property.getName().toString()).addAll(arrayNode);
                }
                else if (Double.class.equals(property.getType().getBinding())) {
                    obj.put(property.getName().toString(), value == null ?
                            0D : (double)value);
                }
                else {
                    obj.put(property.getName().toString(), value == null ?
                            StringUtils.EMPTY : value.toString());
                }
            }
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private JsonNode buildGeometry(Geometry geometry) throws IOException {
        JsonNode jsonNode = null;
        String stringWrapper = new GeometryJSON().toString(geometry);
        if (stringWrapper != null){
            jsonNode = mapper.readTree(stringWrapper);
        }
        return jsonNode;
    }
}
