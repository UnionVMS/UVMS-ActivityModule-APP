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
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.GearProblemDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.GearProblem;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.QuantityType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper(uses = {FishingGearMapper.class})
public abstract class GearProblemMapper extends BaseMapper {

    public static final GearProblemMapper INSTANCE = Mappers.getMapper(GearProblemMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(gearProblem.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(gearProblem.getTypeCode()))"),
            @Mapping(target = "affectedQuantity", expression = "java(getAffectedQuantity(gearProblem.getAffectedQuantity()))"),
            @Mapping(target = "gearProblemRecovery", expression = "java(mapToGearProblemRecoveries(gearProblem.getRecoveryMeasureCodes(), gearProblemEntity))"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)"),
            @Mapping(target = "fishingGears", expression = "java(getFishingGearsEntities(gearProblem.getRelatedFishingGears(), gearProblemEntity))")
    })
    public abstract GearProblemEntity mapToGearProblemEntity(GearProblem gearProblem, FishingActivityEntity fishingActivityEntity, @MappingTarget GearProblemEntity gearProblemEntity);

    @Mappings({
            @Mapping(target = "recoveryMeasureCode", expression = "java(getCodeType(codeType))"),
            @Mapping(target = "recoveryMeasureCodeListId", expression = "java(getCodeTypeListId(codeType))")
    })
    public abstract GearProblemRecoveryEntity mapToGearProblemRecoveryEntity(CodeType codeType);

    protected Set<GearProblemRecoveryEntity> mapToGearProblemRecoveries(List<CodeType> codeTypes, GearProblemEntity gearProblemEntity) {
        if (codeTypes == null || codeTypes.isEmpty()) {
            Collections.emptySet();
        }
        Set<GearProblemRecoveryEntity> gearProblemRecoveries = new HashSet<>();
        for (CodeType codeType : codeTypes) {
            GearProblemRecoveryEntity gearProblemRecovery = GearProblemMapper.INSTANCE.mapToGearProblemRecoveryEntity(codeType);
            gearProblemRecovery.setGearProblem(gearProblemEntity);
            gearProblemRecoveries.add(gearProblemRecovery);
        }
        return gearProblemRecoveries;
    }

    protected Set<FishingGearEntity> getFishingGearsEntities(List<FishingGear> fishingGears, GearProblemEntity gearProblemEntity) {
        if (fishingGears == null || fishingGears.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FishingGearEntity> fishingGearEntities = new HashSet<>();
        for (FishingGear fishingGear : fishingGears) {
            FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear, gearProblemEntity, new FishingGearEntity());
            fishingGearEntities.add(fishingGearEntity);
        }
        return fishingGearEntities;
    }

    protected Integer getAffectedQuantity(QuantityType quantityType) {
        Double qty = super.getQuantity(quantityType);
        if (qty != null) {
            return qty.intValue();
        } else {
            return null;
        }
    }
}