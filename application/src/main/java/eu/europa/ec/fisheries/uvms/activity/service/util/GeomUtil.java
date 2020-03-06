package eu.europa.ec.fisheries.uvms.activity.service.util;

import org.apache.commons.collections.CollectionUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GeomUtil {

    public static final int DEFAULT_EPSG_SRID = 4326;

    private GeomUtil() {
        // private constructor to prevent instantiation of util class
    }

    public static Point createPoint(Double longitude, Double latitude) {
        if(null == longitude || null == latitude){
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(DEFAULT_EPSG_SRID);
        return point;
    }

    public static Geometry createPoint(Coordinate coordinate) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry point = geometryFactory.createPoint(coordinate);
        point.setSRID(DEFAULT_EPSG_SRID);
        return point;
    }

    public static Geometry createMultipoint(List<Geometry> geometries) {
        if (CollectionUtils.isEmpty(geometries)) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        Set<Coordinate> coordinates = new HashSet<>();
        for (Geometry geom : geometries) {
            coordinates.add(geom.getCoordinate());
        }
        Geometry multiPoint = geometryFactory.createMultiPointFromCoords(coordinates.toArray(new Coordinate[0]));
        multiPoint.setSRID(DEFAULT_EPSG_SRID);
        return multiPoint;
    }
}
