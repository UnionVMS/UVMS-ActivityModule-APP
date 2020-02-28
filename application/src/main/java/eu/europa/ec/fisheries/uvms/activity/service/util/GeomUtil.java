package eu.europa.ec.fisheries.uvms.activity.service.util;

import org.apache.commons.collections.CollectionUtils;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.linearref.LengthIndexedLine;

import java.util.ArrayList;
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

    public static Geometry calculateIntersectingPoint(LengthIndexedLine lengthIndexedLine, Double index) {
        Coordinate coordinate = lengthIndexedLine.extractPoint(index);
        return createPoint(coordinate);
    }

    public static LengthIndexedLine createLengthIndexedLine(String wkt1, String wkt2) throws ParseException {
        Geometry lineString = GeomUtil.createLineString(wkt1, wkt2);
        return new LengthIndexedLine(lineString);
    }

    public static Geometry createLineString(String wkt1, String wkt2) throws ParseException {
        LineString line;

        Geometry point1 = new WKTReader().read(wkt1);
        Geometry point2 = new WKTReader().read(wkt2);
        GeometryFactory geometryFactory = new GeometryFactory();
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(point1.getCoordinate());
        coordinates.add(point2.getCoordinate());
        line = geometryFactory.createLineString(coordinates.toArray(new Coordinate[0]));
        line.setSRID(DEFAULT_EPSG_SRID);

        return line;
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
