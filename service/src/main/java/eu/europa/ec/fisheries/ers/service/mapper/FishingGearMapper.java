/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.FishingGearDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.GearCharacteristic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class FishingGearMapper extends BaseMapper {

    public static FishingGearMapper INSTANCE = Mappers.getMapper(FishingGearMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fishingGear.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fishingGear.getTypeCode()))"),
            @Mapping(target = "roleCode", expression = "java(getCodeTypeFromList(fishingGear.getRoleCodes()))"),
            @Mapping(target = "roleCodeListId", expression = "java(getCodeTypeListIdFromList(fishingGear.getRoleCodes()))"),
            @Mapping(target = "gearCharacteristics", expression = "java(getGearCharacteristicEntities(fishingGear.getApplicableGearCharacteristics(), fishingGearEntity))"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)")
    })
    public abstract FishingGearEntity mapToFishingGearEntity(FishingGear fishingGear, FishingActivityEntity fishingActivityEntity, @MappingTarget FishingGearEntity fishingGearEntity);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fishingGear.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fishingGear.getTypeCode()))"),
            @Mapping(target = "roleCode", expression = "java(getCodeTypeFromList(fishingGear.getRoleCodes()))"),
            @Mapping(target = "roleCodeListId", expression = "java(getCodeTypeListIdFromList(fishingGear.getRoleCodes()))"),
            @Mapping(target = "gearCharacteristics", expression = "java(getGearCharacteristicEntities(fishingGear.getApplicableGearCharacteristics(), fishingGearEntity))"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)")
    })
    public abstract FishingGearEntity mapToFishingGearEntity(FishingGear fishingGear, FaCatchEntity faCatchEntity, @MappingTarget FishingGearEntity fishingGearEntity);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fishingGear.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fishingGear.getTypeCode()))"),
            @Mapping(target = "roleCode", expression = "java(getCodeTypeFromList(fishingGear.getRoleCodes()))"),
            @Mapping(target = "roleCodeListId", expression = "java(getCodeTypeListIdFromList(fishingGear.getRoleCodes()))"),
            @Mapping(target = "gearCharacteristics", expression = "java(getGearCharacteristicEntities(fishingGear.getApplicableGearCharacteristics(), fishingGearEntity))"),
            @Mapping(target = "gearProblem", expression = "java(gearProblemEntity)")
    })
    public abstract FishingGearEntity mapToFishingGearEntity(FishingGear fishingGear, GearProblemEntity gearProblemEntity, @MappingTarget FishingGearEntity fishingGearEntity);


    @Mappings({
            @Mapping(source = "typeCode",target = "gearTypeCode"),
            @Mapping(source = "roleCode",target = "gearRoleCode")
    })
    public abstract FishingGearDTO mapToFishingGearDTO(FishingGearEntity fishingGearEntity);

    protected Set<GearCharacteristicEntity> getGearCharacteristicEntities(List<GearCharacteristic> gearCharacteristics, FishingGearEntity fishingGearEntity) {
        if (gearCharacteristics == null || gearCharacteristics.isEmpty()) {
             return null;
        }
        Set<GearCharacteristicEntity> gearCharacteristicEntities = new HashSet<>();
        for (GearCharacteristic gearCharacteristic : gearCharacteristics) {
            GearCharacteristicEntity gearCharacteristicEntity = GearCharacteristicsMapper.INSTANCE.mapToGearCharacteristicEntity(gearCharacteristic, fishingGearEntity, new GearCharacteristicEntity());
            gearCharacteristicEntities.add(gearCharacteristicEntity);
        }
        return gearCharacteristicEntities;
    }
}