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
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.model.dto.DelimitedPeriodDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;

/**
 * Created by padhyad on 6/15/2016.
 */
@Mapper
public abstract class DelimitedPeriodMapper extends BaseMapper {

    public static final DelimitedPeriodMapper INSTANCE = Mappers.getMapper(DelimitedPeriodMapper.class);

    @Mappings({
            @Mapping(target = "startDate", expression = "java(convertToDate(delimitedPeriod.getStartDateTime()))"),
            @Mapping(target = "endDate", expression = "java(convertToDate(delimitedPeriod.getEndDateTime()))"),
            @Mapping(target = "duration", expression = "java(getMeasure(delimitedPeriod.getDurationMeasure()))"),
            @Mapping(target = "durationUnitCode", expression = "java(getMeasureUnitCode(delimitedPeriod.getDurationMeasure()))"),
            @Mapping(target = "calculatedDuration", expression = "java(getCalculatedMeasure(delimitedPeriod.getDurationMeasure()))"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)")
    })
    public abstract DelimitedPeriodEntity mapToDelimitedPeriodEntity(DelimitedPeriod delimitedPeriod, FishingActivityEntity fishingActivityEntity, @MappingTarget DelimitedPeriodEntity delimitedPeriodEntity);

    @Mappings({
            @Mapping(target = "startDate", expression = "java(convertToDate(delimitedPeriod.getStartDateTime()))"),
            @Mapping(target = "endDate", expression = "java(convertToDate(delimitedPeriod.getEndDateTime()))"),
            @Mapping(target = "duration", expression = "java(getMeasure(delimitedPeriod.getDurationMeasure()))"),
            @Mapping(target = "durationUnitCode", expression = "java(getMeasureUnitCode(delimitedPeriod.getDurationMeasure()))"),
            @Mapping(target = "calculatedDuration", expression = "java(getCalculatedMeasure(delimitedPeriod.getDurationMeasure()))"),
            @Mapping(target = "fishingTrip", expression = "java(fishingTripEntity)")
    })
    public abstract DelimitedPeriodEntity mapToDelimitedPeriodEntity(DelimitedPeriod delimitedPeriod, FishingTripEntity fishingTripEntity, @MappingTarget DelimitedPeriodEntity delimitedPeriodEntity);

    @Mappings({
            @Mapping(source = "startDate",target = "startDate"),
            @Mapping(source = "endDate",target = "endDate"),
            @Mapping(source = "duration",target = "duration")
    })
    public abstract DelimitedPeriodDTO mapToDelimitedPeriodDTO(DelimitedPeriodEntity delimitedPeriodEntity);
}