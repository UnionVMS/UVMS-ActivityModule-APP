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
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.VesselPositionEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselGeographicalCoordinate;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselPositionEvent;

/**
 * Created by sanera on 17/03/2017.
 */
@Mapper()
public abstract class VesselPositionEventMapper extends BaseMapper  {
    public static final VesselPositionEventMapper INSTANCE = Mappers.getMapper(VesselPositionEventMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(vesselPositionEvent.getTypeCode()))"),
            @Mapping(target = "obtainedOccurrenceDateTime", expression = "java(convertToDate(vesselPositionEvent.getObtainedOccurrenceDateTime()))"),
            @Mapping(target = "speedValueMeasure", expression = "java(getMeasure(vesselPositionEvent.getSpeedValueMeasure()))"),
            @Mapping(target = "courseValueMeasure", expression = "java(getMeasure(vesselPositionEvent.getCourseValueMeasure()))"),
            @Mapping(target = "latitude", expression = "java(getLatitude(vesselPositionEvent.getSpecifiedVesselGeographicalCoordinate()))"),
            @Mapping(target = "altitude", expression = "java(getAltitude(vesselPositionEvent.getSpecifiedVesselGeographicalCoordinate()))"),
            @Mapping(target = "longitude", expression = "java(getLongitude(vesselPositionEvent.getSpecifiedVesselGeographicalCoordinate()))"),
            @Mapping(target = "activityTypeCode", expression = "java(getCodeType(vesselPositionEvent.getActivityTypeCode()))"),
            @Mapping(target = "vesselTransportMeans", source = "vesselTransportMeansEntity")
    })

    public abstract VesselPositionEventEntity mapToVesselPositionEventEntity(VesselPositionEvent vesselPositionEvent,VesselTransportMeansEntity vesselTransportMeansEntity);

    protected Double getLatitude(VesselGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getLatitudeMeasure() != null) {
            return coordinate.getLatitudeMeasure().getValue().doubleValue();
        } else {
            return null;
        }
    }


    protected Double getAltitude(VesselGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getLatitudeMeasure() != null) {
            return coordinate.getAltitudeMeasure().getValue().doubleValue();
        } else {
            return null;
        }
    }

    protected Double getLongitude(VesselGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getLatitudeMeasure() != null) {
            return coordinate.getLongitudeMeasure().getValue().doubleValue();
        } else {
            return null;
        }
    }

}
