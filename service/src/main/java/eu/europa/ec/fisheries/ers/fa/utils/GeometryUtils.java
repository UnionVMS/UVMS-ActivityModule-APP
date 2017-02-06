/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.fa.utils;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 10/11/2016.
 */
@Slf4j
public class GeometryUtils {

    private static final int DEFAULT_SRID = 4326;

    private GeometryUtils() { // Static utility class, not supposed to have instances.
        super();
    }

    public static boolean validatePointIntersectsArea(Geometry point, Geometry multipolygon) {
        return point.intersects(multipolygon);
    }

    public static Geometry calculateIntersectingPoint(LengthIndexedLine lengthIndexedLine, Double index) throws ServiceException {
        Coordinate coordinate = lengthIndexedLine.extractPoint(index);
        return GeometryUtils.createPoint(coordinate);
    }

    public static LengthIndexedLine createLengthIndexedLine(String wkt1, String wkt2) throws ServiceException {
        Geometry lineString = createLineString(wkt1, wkt2);
        return new LengthIndexedLine(lineString);
    }

    public static Geometry createPoint(Double longitude, Double latitude) {
        if(null == longitude || null == latitude){
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Geometry point = geometryFactory.createPoint(coordinate);
        point.setSRID(DEFAULT_SRID);
        return point;
    }

    public static Geometry createPoint(Coordinate coordinate) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry point = geometryFactory.createPoint(coordinate);
        point.setSRID(DEFAULT_SRID);
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
        Geometry multiPoint = geometryFactory.createMultiPoint(coordinates.toArray(new Coordinate[coordinates.size()]));
        multiPoint.setSRID(DEFAULT_SRID);
        return multiPoint;
    }

    public static Geometry wktToGeom(String wkt) throws ServiceException {
        try {
            WKTReader reader = new WKTReader();
            Geometry geometry = reader.read(wkt);
            geometry.setSRID(DEFAULT_SRID);
            return geometry;
        } catch (ParseException e) {
            log.error("Parse Exception", e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public static Double getLongitude(Geometry geom) {
        return geom.getCoordinate().x;
    }

    public static Double getLatitude(Geometry geom) {
        return geom.getCoordinate().y;
    }

    public static Double getDistanceBetweenPoint(String wktPoint1, String wktPoint2) throws ServiceException {
        try {
            CoordinateReferenceSystem crsWGS84 = CRS.decode("EPSG:4326");
            GeodeticCalculator calculator = new GeodeticCalculator(crsWGS84);
            Double startX = wktToGeom(wktPoint1).getCoordinate().x;
            Double startY = wktToGeom(wktPoint1).getCoordinate().y;
            Double destX = wktToGeom(wktPoint2).getCoordinate().x;
            Double destY = wktToGeom(wktPoint2).getCoordinate().y;

            calculator.setStartingGeographicPoint(startX, startY);
            calculator.setDestinationGeographicPoint(destX, destY);
            Double orthodomicDistance = calculator.getOrthodromicDistance();
            return orthodomicDistance;
        } catch (FactoryException e) {
            log.error("Transform Exception", e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public static Geometry createLineString(String wkt1, String wkt2) throws ServiceException {
        Geometry point1 = wktToGeom(wkt1);
        Geometry point2 = wktToGeom(wkt2);
        GeometryFactory geometryFactory = new GeometryFactory();
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(point1.getCoordinate());
        coordinates.add(point2.getCoordinate());
        LineString line = geometryFactory.createLineString(coordinates.toArray(new Coordinate[coordinates.size()]));
        line.setSRID(DEFAULT_SRID);
        return line;
    }

    public static LineSegment createLineSegment(String wkt1, String wkt2) throws ServiceException {
        Geometry point1 = wktToGeom(wkt1);
        Geometry point2 = wktToGeom(wkt2);
        return new LineSegment(point1.getCoordinate(), point2.getCoordinate());
    }
}
