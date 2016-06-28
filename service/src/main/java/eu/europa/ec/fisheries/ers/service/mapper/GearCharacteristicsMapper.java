package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearCharacteristicEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.GearCharacteristic;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class GearCharacteristicsMapper extends BaseMapper {

    public static GearCharacteristicsMapper INSTANCE = Mappers.getMapper(GearCharacteristicsMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(gearCharacteristic.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(gearCharacteristic.getTypeCode()))"),
            @Mapping(target = "description", expression = "java(getTextFromList(gearCharacteristic.getDescriptions()))"),
            @Mapping(target = "valueMeasure", expression = "java(getMeasure(gearCharacteristic.getValueMeasure()))"),
            @Mapping(target = "valueDateTime", expression = "java(convertToDate(gearCharacteristic.getValueDateTime()))"),
            @Mapping(target = "valueIndicator", expression = "java(getIndicatorType(gearCharacteristic.getValueIndicator()))"),
            @Mapping(target = "valueCode", expression = "java(getCodeType(gearCharacteristic.getValueCode()))"),
            @Mapping(target = "valueText", expression = "java(getTextType(gearCharacteristic.getValue()))"),
            @Mapping(target = "valueQuantity", expression = "java(getQuantity(gearCharacteristic.getValueQuantity()))"),
            @Mapping(target = "fishingGear", expression = "java(fishingGearEntity)")
    })
    public abstract GearCharacteristicEntity mapToGearCharacteristicEntity(GearCharacteristic gearCharacteristic, FishingGearEntity fishingGearEntity, @MappingTarget GearCharacteristicEntity gearCharacteristicEntity);
}
