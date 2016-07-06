/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
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