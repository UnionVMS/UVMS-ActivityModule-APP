/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;

@Mapper
public abstract class DelimitedPeriodMapper extends BaseMapper {

    public static final DelimitedPeriodMapper INSTANCE = Mappers.getMapper(DelimitedPeriodMapper.class);

    @Mappings({
            @Mapping(target = "startDate", source = "delimitedPeriod.startDateTime.dateTime"),
            @Mapping(target = "endDate", source = "delimitedPeriod.endDateTime.dateTime"),
            @Mapping(target = "duration", source = "delimitedPeriod.durationMeasure.value"),
            @Mapping(target = "durationUnitCode", source = "delimitedPeriod.durationMeasure.unitCode"),
            @Mapping(target = "calculatedDuration", expression = "java(getCalculatedMeasure(delimitedPeriod.getDurationMeasure()))"),
    })
    public abstract DelimitedPeriodEntity mapToDelimitedPeriodEntity(DelimitedPeriod delimitedPeriod);

    @Mappings({
            @Mapping(target = "startDate", source = "delimitedPeriod.startDateTime.dateTime"),
            @Mapping(target = "endDate", source = "delimitedPeriod.endDateTime.dateTime"),
            @Mapping(target = "duration", source = "delimitedPeriod.durationMeasure.value"),
            @Mapping(target = "durationUnitCode", source = "delimitedPeriod.durationMeasure.unitCode"),
            @Mapping(target = "calculatedDuration", expression = "java(getCalculatedMeasure(delimitedPeriod.getDurationMeasure()))"),
            @Mapping(target = "fishingTrip", expression = "java(fishingTripEntity)")
    })
    public abstract DelimitedPeriodEntity mapToDelimitedPeriodEntity(DelimitedPeriod delimitedPeriod, FishingTripEntity fishingTripEntity, @MappingTarget DelimitedPeriodEntity delimitedPeriodEntity);

    public abstract DelimitedPeriodDTO mapToDelimitedPeriodDTO(DelimitedPeriodEntity delimitedPeriodEntity);

}