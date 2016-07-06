/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

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
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXCharacteristic;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class FluxCharacteristicsMapper extends BaseMapper {

    public static FluxCharacteristicsMapper INSTANCE = Mappers.getMapper(FluxCharacteristicsMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fluxCharacteristic.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fluxCharacteristic.getTypeCode()))"),
            @Mapping(target = "valueMeasure", expression = "java(getMeasure(fluxCharacteristic.getValueMeasure()))"),
            @Mapping(target = "valueDateTime", expression = "java(convertToDate(fluxCharacteristic.getValueDateTime()))"),
            @Mapping(target = "valueIndicator", expression = "java(getValueIndicator(fluxCharacteristic.getValueIndicator()))"),
            @Mapping(target = "valueCode", expression = "java(getCodeType(fluxCharacteristic.getValueCode()))"),
            @Mapping(target = "valueText", expression = "java(getTextFromList(fluxCharacteristic.getValues()))"),
            @Mapping(target = "valueQuantity", expression = "java(getQuantity(fluxCharacteristic.getValueQuantity()))"),
            @Mapping(target = "description", expression = "java(getTextFromList(fluxCharacteristic.getDescriptions()))"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)")
    })
    public abstract FluxCharacteristicEntity mapToFluxCharEntity(FLUXCharacteristic fluxCharacteristic, FishingActivityEntity fishingActivityEntity, @MappingTarget FluxCharacteristicEntity fluxCharacteristicEntity);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fluxCharacteristic.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fluxCharacteristic.getTypeCode()))"),
            @Mapping(target = "valueMeasure", expression = "java(getMeasure(fluxCharacteristic.getValueMeasure()))"),
            @Mapping(target = "valueDateTime", expression = "java(convertToDate(fluxCharacteristic.getValueDateTime()))"),
            @Mapping(target = "valueIndicator", expression = "java(getValueIndicator(fluxCharacteristic.getValueIndicator()))"),
            @Mapping(target = "valueCode", expression = "java(getCodeType(fluxCharacteristic.getValueCode()))"),
            @Mapping(target = "valueText", expression = "java(getTextFromList(fluxCharacteristic.getValues()))"),
            @Mapping(target = "valueQuantity", expression = "java(getQuantity(fluxCharacteristic.getValueQuantity()))"),
            @Mapping(target = "description", expression = "java(getTextFromList(fluxCharacteristic.getDescriptions()))"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)")
    })
    public abstract FluxCharacteristicEntity mapToFluxCharEntity(FLUXCharacteristic fluxCharacteristic, FaCatchEntity faCatchEntity, @MappingTarget FluxCharacteristicEntity fluxCharacteristicEntity);


    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fluxCharacteristic.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fluxCharacteristic.getTypeCode()))"),
            @Mapping(target = "valueMeasure", expression = "java(getMeasure(fluxCharacteristic.getValueMeasure()))"),
            @Mapping(target = "valueDateTime", expression = "java(convertToDate(fluxCharacteristic.getValueDateTime()))"),
            @Mapping(target = "valueIndicator", expression = "java(getValueIndicator(fluxCharacteristic.getValueIndicator()))"),
            @Mapping(target = "valueCode", expression = "java(getCodeType(fluxCharacteristic.getValueCode()))"),
            @Mapping(target = "valueText", expression = "java(getTextFromList(fluxCharacteristic.getValues()))"),
            @Mapping(target = "valueQuantity", expression = "java(getQuantity(fluxCharacteristic.getValueQuantity()))"),
            @Mapping(target = "description", expression = "java(getTextFromList(fluxCharacteristic.getDescriptions()))"),
            @Mapping(target = "fluxLocation", expression = "java(fluxLocationEntity)")
    })
    public abstract FluxCharacteristicEntity mapToFluxCharEntity(FLUXCharacteristic fluxCharacteristic, FluxLocationEntity fluxLocationEntity, @MappingTarget FluxCharacteristicEntity fluxCharacteristicEntity);

}