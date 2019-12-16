/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearProblemRecoveryEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationCatchTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearProblem;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(uses = {FishingGearMapper.class, FluxLocationMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class GearProblemMapper extends BaseMapper {

    public static final GearProblemMapper INSTANCE = Mappers.getMapper(GearProblemMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "affectedQuantity", source = "affectedQuantity.value"),
            @Mapping(target = "gearProblemRecovery", expression = "java(mapToGearProblemRecoveries(gearProblem.getRecoveryMeasureCodes(), gearProblemEntity))"),
            @Mapping(target = "fishingGears", expression = "java(getFishingGearsEntities(gearProblem.getRelatedFishingGears(), gearProblemEntity))"),
            @Mapping(target = "locations", expression = "java(mapToFluxLocations(gearProblem.getSpecifiedFLUXLocations(), gearProblemEntity))"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "fishingActivity", ignore = true),
    })
    public abstract GearProblemEntity mapToGearProblemEntity(GearProblem gearProblem);

    @Mappings({
            @Mapping(target = "recoveryMeasureCode", source = "value"),
            @Mapping(target = "recoveryMeasureCodeListId", source = "listID"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "gearProblem", ignore = true),
    })
    public abstract GearProblemRecoveryEntity mapToGearProblemRecoveryEntity(CodeType codeType);

    protected Set<FluxLocationEntity> mapToFluxLocations(List<FLUXLocation> flLocList, GearProblemEntity gearProbEntity){
        if(CollectionUtils.isEmpty(flLocList)){
            return Collections.emptySet();
        }
        Set<FluxLocationEntity> entitiesSet = new HashSet<>();
        for(FLUXLocation flLocAct : flLocList){
            FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(flLocAct);
            fluxLocationEntity.setFluxLocationCatchTypeMapperInfo(FluxLocationCatchTypeEnum.GEAR_PROBLEM.getType());
            fluxLocationEntity.setGearProblem(gearProbEntity);
            FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(flLocAct);
            entitiesSet.add(FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(flLocAct));
        }
        return entitiesSet;
    }

    protected Set<GearProblemRecoveryEntity> mapToGearProblemRecoveries(List<CodeType> codeTypes, GearProblemEntity gearProblemEntity) {
        if (codeTypes == null || codeTypes.isEmpty()) {
            return Collections.emptySet();
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
