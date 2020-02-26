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
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearProblemRecoveryEntity;
import eu.europa.ec.fisheries.uvms.activity.service.util.GeomUtil;
import org.apache.commons.collections.CollectionUtils;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearProblem;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi", uses = {FishingGearMapper.class, FluxLocationMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class GearProblemMapper extends BaseMapper {

    @Inject
    GearProblemMapper gearProblemMapper;

    @Inject
    FishingGearMapper fishingGearMapper;

    @Mapping(target = "typeCode", source = "typeCode.value")
    @Mapping(target = "typeCodeListId", source = "typeCode.listID")
    @Mapping(target = "affectedQuantity", source = "affectedQuantity.value")
    @Mapping(target = "gearProblemRecovery", expression = "java(mapToGearProblemRecoveries(gearProblem.getRecoveryMeasureCodes(), gearProblemEntity))")
    @Mapping(target = "fishingGears", expression = "java(getFishingGearsEntities(gearProblem.getRelatedFishingGears(), gearProblemEntity))")
    @Mapping(target = "geom", expression = "java(mapToFluxLocations(gearProblem.getSpecifiedFLUXLocations()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fishingActivity", ignore = true)
    public abstract GearProblemEntity mapToGearProblemEntity(GearProblem gearProblem);


    @Mapping(target = "recoveryMeasureCode", source = "value")
    @Mapping(target = "recoveryMeasureCodeListId", source = "listID")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "gearProblem", ignore = true)
    public abstract GearProblemRecoveryEntity mapToGearProblemRecoveryEntity(CodeType codeType);

    protected Point mapToFluxLocations(List<FLUXLocation> flLocList){
        if(CollectionUtils.isEmpty(flLocList)){
            return null;
        }
        if(flLocList.get(0).getSpecifiedPhysicalFLUXGeographicalCoordinate() != null ){
            return GeomUtil.createPoint(flLocList.get(0).getSpecifiedPhysicalFLUXGeographicalCoordinate().getLongitudeMeasure().getValue().doubleValue(),
                    flLocList.get(0).getSpecifiedPhysicalFLUXGeographicalCoordinate().getLatitudeMeasure().getValue().doubleValue());
        }
        return null;
    }

    protected Set<GearProblemRecoveryEntity> mapToGearProblemRecoveries(List<CodeType> codeTypes, GearProblemEntity gearProblemEntity) {
        if (codeTypes == null || codeTypes.isEmpty()) {
            return Collections.emptySet();
        }
        Set<GearProblemRecoveryEntity> gearProblemRecoveries = new HashSet<>();
        for (CodeType codeType : codeTypes) {
            GearProblemRecoveryEntity gearProblemRecovery = gearProblemMapper.mapToGearProblemRecoveryEntity(codeType);
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
            FishingGearEntity fishingGearEntity = fishingGearMapper.mapToFishingGearEntity(fishingGear);
            fishingGearEntity.setGearProblem(gearProblemEntity);
            fishingGearEntities.add(fishingGearEntity);
        }
        return fishingGearEntities;
    }
}
