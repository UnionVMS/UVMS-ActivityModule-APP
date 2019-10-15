/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.ers.service.dto.FluxCharacteristicsDto;
import eu.europa.ec.fisheries.ers.service.util.CustomBigDecimal;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;

@Mapper(imports = BaseMapper.class, uses = CustomBigDecimal.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FluxCharacteristicsMapper {

    FluxCharacteristicsMapper INSTANCE = Mappers.getMapper(FluxCharacteristicsMapper.class);

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
    FluxCharacteristicEntity mapToFluxCharEntity(FLUXCharacteristic fluxCharacteristic);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "descriptions", ignore = true),
            @Mapping(target = "values", ignore = true),
            @Mapping(target = "specifiedFLUXLocations", ignore = true),
            @Mapping(target = "relatedFLAPDocuments", ignore = true)
    })
    FLUXCharacteristic mapToFLUXCharacteristic(FluxCharacteristicEntity fluxCharacteristicEntity);

    FluxCharacteristicsDto mapToFluxCharacteristicsDto(FluxCharacteristicEntity fluxCharacteristicEntity);

}