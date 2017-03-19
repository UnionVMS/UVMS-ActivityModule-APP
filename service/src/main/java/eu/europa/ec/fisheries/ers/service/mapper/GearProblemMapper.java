/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemRecoveryEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearProblem;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper(uses = {FishingGearMapper.class, FluxLocationMapper.class})
public abstract class GearProblemMapper extends BaseMapper {

    public static final GearProblemMapper INSTANCE = Mappers.getMapper(GearProblemMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(gearProblem.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(gearProblem.getTypeCode()))"),
            @Mapping(target = "affectedQuantity", source = "gearProblem.affectedQuantity.value"),
            @Mapping(target = "gearProblemRecovery", expression = "java(mapToGearProblemRecoveries(gearProblem.getRecoveryMeasureCodes(), gearProblemEntity))"),
            @Mapping(target = "fishingGears", expression = "java(getFishingGearsEntities(gearProblem.getRelatedFishingGears(), gearProblemEntity))"),
            @Mapping(target = "locations", expression = "java(mapToFluxLocations(gearProblem.getSpecifiedFLUXLocations(), gearProblemEntity))")
    })
    public abstract GearProblemEntity mapToGearProblemEntity(GearProblem gearProblem);

    @Mappings({
            @Mapping(target = "recoveryMeasureCode", expression = "java(getCodeType(codeType))"),
            @Mapping(target = "recoveryMeasureCodeListId", expression = "java(getCodeTypeListId(codeType))")
    })
    public abstract GearProblemRecoveryEntity mapToGearProblemRecoveryEntity(CodeType codeType);


    protected Set<FluxLocationEntity> mapToFluxLocations(List<FLUXLocation> flLocList, GearProblemEntity gearProbEntity){
        if(CollectionUtils.isEmpty(flLocList)){
            return Collections.emptySet();
        }
        Set<FluxLocationEntity> entitiesSet = new HashSet<>();
        for(FLUXLocation flLocAct : flLocList){
            entitiesSet.add(FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(flLocAct, FluxLocationCatchTypeEnum.GEAR_PROBLEM, gearProbEntity, new FluxLocationEntity()));
        }
        return null;
    }

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
            FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear);
            fishingGearEntity.setGearProblem(gearProblemEntity);
            fishingGearEntities.add(fishingGearEntity);
        }
        return fishingGearEntities;
    }
}