/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.GearProblemDto;
import eu.europa.ec.fisheries.ers.service.dto.view.GearShotRetrievalDto;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

/**
 * Created by kovian on 09/03/2017.
 *
 * This class maps a fishingActivity to gearShotRetrieval DTO.
 *
 */
@Mapper
public abstract class GearShotRetrievalTileMapper extends BaseActivityViewMapper {

    public static final GearShotRetrievalTileMapper INSTANCE = Mappers.getMapper(GearShotRetrievalTileMapper.class);


    public abstract List<GearShotRetrievalDto> mapEntityListToDtoList(Set<FishingActivityEntity> entity);


    @Mappings({
            @Mapping(target = "type",            source     = "typeCode"),
            @Mapping(target = "occurrence",      source     = "occurence"),
            @Mapping(target = "duration",        expression = "java(getDurationFromActivity(entity.getDelimitedPeriods()))"),
            @Mapping(target = "gear",            expression = "java(mapToFirstFishingGear(entity.getFishingGears()))"),
            @Mapping(target = "gearProblems",    source     = "gearProblems"),
            @Mapping(target = "characteristics", expression = "java(getFluxCharacteristicsTypeCodeValue(entity.getFluxCharacteristics()))"),
            @Mapping(target = "location",        expression = "java(mapSingleFluxLocationFromEntity(entity.getFluxLocations()))")
    })
    protected abstract GearShotRetrievalDto mapSingleEntityToSingleDto(FishingActivityEntity entity);


    protected abstract List<GearProblemDto> mapGearProblemsToGearsDto(Set<GearProblemEntity> gearProblems);


    @Mappings({
            @Mapping(target = "type",            source = "typeCode"),
            @Mapping(target = "nrOfGears",       source = "affectedQuantity"),
            @Mapping(target = "recoveryMeasure", expression = "java(mapToFirstRecoveryMeasure(entity.getGearProblemRecovery()))"),
            @Mapping(target = "location",        expression = "java(getFluxLocationDtoFromEntity(entity.getLocation()))")
    })
    protected abstract GearProblemDto  mapGearProblemToGearsDto(GearProblemEntity entity);


    protected Double getDurationFromActivity(Set<DelimitedPeriodEntity> periodsList){
        if(CollectionUtils.isEmpty(periodsList)){
            return null;
        }
        Double durationSubTotal = null;
        for(DelimitedPeriodEntity period : periodsList){
            durationSubTotal = addDoubles(period.getCalculatedDuration(), durationSubTotal);
        }
        return durationSubTotal;
    }


    protected FluxLocationDto mapSingleFluxLocationFromEntity(Set<FluxLocationEntity> fluxLocations){
        if(CollectionUtils.isEmpty(fluxLocations)){
            return null;
        }
        return getFluxLocationDtoFromEntity(fluxLocations.iterator().next());
    }

    protected String mapToFirstRecoveryMeasure(Set<GearProblemRecoveryEntity> recoveries){
        if(CollectionUtils.isEmpty(recoveries)){
            return null;
        }
        String probRecoveryMeasure = null;
        for(GearProblemRecoveryEntity probRecovEnt : recoveries){
            if(StringUtils.isNotEmpty(probRecovEnt.getRecoveryMeasureCode())){
                probRecoveryMeasure = probRecovEnt.getRecoveryMeasureCode();
                break;
            }
        }
        return probRecoveryMeasure;
    }

}
