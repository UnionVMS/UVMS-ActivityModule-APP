/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.GearDto;
import eu.europa.ec.fisheries.ers.service.util.CustomBigDecimal;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearCharacteristic;

@Mapper(imports = BaseMapper.class,
        uses = CustomBigDecimal.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class GearCharacteristicsMapper extends BaseMapper {

    public static final GearCharacteristicsMapper INSTANCE = Mappers.getMapper(GearCharacteristicsMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "description", expression = "java(BaseMapper.getTextFromList(gearCharacteristic.getDescriptions()))"),
            @Mapping(target = "descLanguageId", expression = "java(BaseMapper.getLanguageIdFromList(gearCharacteristic.getDescriptions()))"),
            @Mapping(target = "valueMeasure", source = "valueMeasure.value"),
            @Mapping(target = "valueMeasureUnitCode", source = "valueMeasure.unitCode"),
            @Mapping(target = "valueDateTime", source = "valueDateTime.dateTime"),
            @Mapping(target = "valueIndicator", source = "valueIndicator.indicatorString.value"),
            @Mapping(target = "valueCode", source = "valueCode.value"),
            @Mapping(target = "valueText", source = "value.value"),
            @Mapping(target = "valueQuantity", source = "valueQuantity.value"),
            @Mapping(target = "valueQuantityCode", source = "valueQuantity.unitCode"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "calculatedValueMeasure", ignore = true),
            @Mapping(target = "calculatedValueQuantity", ignore = true),
            @Mapping(target = "fishingGear", ignore = true)
    })
    public abstract GearCharacteristicEntity mapToGearCharacteristicEntity(GearCharacteristic gearCharacteristic);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "descriptions", ignore = true),
            @Mapping(target = "specifiedFLUXLocations", ignore = true),
    })
    public abstract GearCharacteristic mapToGearCharacteristic(GearCharacteristicEntity gearCharacteristic);

    public GearDto mapGearDtoToFishingGearEntity(FishingGearEntity fishingGearEntity) {
        GearDto gearDto = new GearDto();
        gearDto.setType(fishingGearEntity.getTypeCode());
        fillRoleAndCharacteristics(gearDto, fishingGearEntity);
        return gearDto;
    }
}