package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.DelimitedPeriod;

/**
 * Created by padhyad on 6/15/2016.
 */
@Mapper
public abstract class DelimitedPeriodMapper extends BaseMapper {

    public static DelimitedPeriodMapper INSTANCE = Mappers.getMapper(DelimitedPeriodMapper.class);

    @Mappings({
            @Mapping(target = "startDate", expression = "java(convertToDate(delimitedPeriod.getStartDateTime()))"),
            @Mapping(target = "endDate", expression = "java(convertToDate(delimitedPeriod.getEndDateTime()))"),
            @Mapping(target = "duration", expression = "java(getMeasure(delimitedPeriod.getDurationMeasure()))"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)")
    })
    public abstract DelimitedPeriodEntity mapToDelimitedPeriodEntity(DelimitedPeriod delimitedPeriod, FishingActivityEntity fishingActivityEntity, @MappingTarget DelimitedPeriodEntity delimitedPeriodEntity);

    @Mappings({
            @Mapping(target = "startDate", expression = "java(convertToDate(delimitedPeriod.getStartDateTime()))"),
            @Mapping(target = "endDate", expression = "java(convertToDate(delimitedPeriod.getEndDateTime()))"),
            @Mapping(target = "duration", expression = "java(getMeasure(delimitedPeriod.getDurationMeasure()))"),
            @Mapping(target = "fishingTrip", expression = "java(fishingTripEntity)")
    })
    public abstract DelimitedPeriodEntity mapToDelimitedPeriodEntity(DelimitedPeriod delimitedPeriod, FishingTripEntity fishingTripEntity, @MappingTarget DelimitedPeriodEntity delimitedPeriodEntity);

}
