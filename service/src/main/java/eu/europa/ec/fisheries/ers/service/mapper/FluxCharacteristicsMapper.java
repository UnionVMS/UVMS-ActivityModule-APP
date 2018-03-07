/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.ers.service.dto.FluxCharacteristicsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;

@Mapper
public abstract class FluxCharacteristicsMapper extends BaseMapper {

    public static final FluxCharacteristicsMapper INSTANCE = Mappers.getMapper(FluxCharacteristicsMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "fluxCharacteristic.typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "fluxCharacteristic.typeCode.listID"),
            @Mapping(target = "valueMeasure", source = "fluxCharacteristic.valueMeasure.value"),
            @Mapping(target = "valueMeasureUnitCode", source = "fluxCharacteristic.valueMeasure.unitCode"),
            @Mapping(target = "calculatedValueMeasure", expression = "java(getCalculatedMeasure(fluxCharacteristic.getValueMeasure()))"),
            @Mapping(target = "valueDateTime", source = "fluxCharacteristic.valueDateTime.dateTime"),
            @Mapping(target = "valueIndicator", source = "fluxCharacteristic.valueIndicator.indicatorString.value"),
            @Mapping(target = "valueCode", source = "fluxCharacteristic.valueCode.value"),
            @Mapping(target = "valueText", expression = "java(getTextFromList(fluxCharacteristic.getValues()))"),
            @Mapping(target = "valueLanguageId", expression = "java(getLanguageIdFromList(fluxCharacteristic.getValues()))"),
            @Mapping(target = "valueQuantity", source = "fluxCharacteristic.valueQuantity.value"),
            @Mapping(target = "valueQuantityCode", source = "fluxCharacteristic.valueQuantity.unitCode"),
            @Mapping(target = "calculatedValueQuantity", expression = "java(getCalculatedQuantity(fluxCharacteristic.getValueQuantity()))"),
            @Mapping(target = "description", expression = "java(getTextFromList(fluxCharacteristic.getDescriptions()))"),
            @Mapping(target = "descriptionLanguageId", expression = "java(getLanguageIdFromList(fluxCharacteristic.getDescriptions()))"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)")
    })
    public abstract FluxCharacteristicEntity mapToFluxCharEntity(FLUXCharacteristic fluxCharacteristic, FishingActivityEntity fishingActivityEntity);

    @Mappings({
            @Mapping(target = "typeCode", source = "fluxCharacteristic.typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "fluxCharacteristic.typeCode.listID"),
            @Mapping(target = "valueMeasure", source = "fluxCharacteristic.valueMeasure.value"),
            @Mapping(target = "valueMeasureUnitCode", source = "fluxCharacteristic.valueMeasure.unitCode"),
            @Mapping(target = "calculatedValueMeasure", expression = "java(getCalculatedMeasure(fluxCharacteristic.getValueMeasure()))"),
            @Mapping(target = "valueDateTime", source = "fluxCharacteristic.valueDateTime.dateTime"),
            @Mapping(target = "valueIndicator", source = "fluxCharacteristic.valueIndicator.indicatorString.value"),
            @Mapping(target = "valueCode", source = "fluxCharacteristic.valueCode.value"),
            @Mapping(target = "valueText", expression = "java(getTextFromList(fluxCharacteristic.getValues()))"),
            @Mapping(target = "valueLanguageId", expression = "java(getLanguageIdFromList(fluxCharacteristic.getValues()))"),
            @Mapping(target = "valueQuantity", source = "fluxCharacteristic.valueQuantity.value"),
            @Mapping(target = "valueQuantityCode", source = "fluxCharacteristic.valueQuantity.unitCode"),
            @Mapping(target = "calculatedValueQuantity", expression = "java(getCalculatedQuantity(fluxCharacteristic.getValueQuantity()))"),
            @Mapping(target = "description", expression = "java(getTextFromList(fluxCharacteristic.getDescriptions()))"),
            @Mapping(target = "descriptionLanguageId", expression = "java(getLanguageIdFromList(fluxCharacteristic.getDescriptions()))"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)")
    })
    public abstract FluxCharacteristicEntity mapToFluxCharEntity(FLUXCharacteristic fluxCharacteristic, FaCatchEntity faCatchEntity);

    public abstract FluxCharacteristicsDto mapToFluxCharacteristicsDto(FluxCharacteristicEntity fluxCharacteristicEntity);

}