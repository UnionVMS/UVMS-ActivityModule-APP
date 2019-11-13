/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.service.util.CustomBigDecimal;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FluxCharacteristicsDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;

@Mapper(uses = CustomBigDecimal.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FluxCharacteristicsMapper extends BaseMapper {

    public static FluxCharacteristicsMapper INSTANCE = Mappers.getMapper(FluxCharacteristicsMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "valueMeasure", source = "valueMeasure.value"),
            @Mapping(target = "valueMeasureUnitCode", source = "valueMeasure.unitCode"),
            @Mapping(target = "valueDateTime", source = "valueDateTime.dateTime"),
            @Mapping(target = "valueIndicator", source = "valueIndicator.indicatorString.value"),
            @Mapping(target = "valueCode", source = "valueCode.value"),
            @Mapping(target = "valueText", expression = "java(BaseMapper.getTextFromList(fluxCharacteristic.getValues()))"),
            @Mapping(target = "valueLanguageId", expression = "java(BaseMapper.getLanguageIdFromList(fluxCharacteristic.getValues()))"),
            @Mapping(target = "valueQuantity", source = "valueQuantity.value"),
            @Mapping(target = "valueQuantityCode", source = "valueQuantity.unitCode"),
            @Mapping(target = "description", expression = "java(BaseMapper.getTextFromList(fluxCharacteristic.getDescriptions()))"),
            @Mapping(target = "descriptionLanguageId", expression = "java(BaseMapper.getLanguageIdFromList(fluxCharacteristic.getDescriptions()))"),
            @Mapping(target = "fluxLocation", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "calculatedValueMeasure", ignore = true),
            @Mapping(target = "calculatedValueQuantity", ignore = true),
            @Mapping(target = "faCatch", ignore = true),
            @Mapping(target = "fishingActivity", ignore = true),
            @Mapping(target = "flapDocument", ignore = true)

    })
    public abstract FluxCharacteristicEntity mapToFluxCharEntity(FLUXCharacteristic fluxCharacteristic);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "descriptions", ignore = true),
            @Mapping(target = "values", ignore = true),
            @Mapping(target = "specifiedFLUXLocations", ignore = true),
            @Mapping(target = "relatedFLAPDocuments", ignore = true)
    })
    public abstract FLUXCharacteristic mapToFLUXCharacteristic(FluxCharacteristicEntity fluxCharacteristicEntity);

    public abstract FluxCharacteristicsDto mapToFluxCharacteristicsDto(FluxCharacteristicEntity fluxCharacteristicEntity);

}
