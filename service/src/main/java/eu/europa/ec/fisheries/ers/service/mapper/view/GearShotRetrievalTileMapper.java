/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.mapper.view;

import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemRecoveryEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.GearProblemDto;
import eu.europa.ec.fisheries.ers.service.dto.view.GearShotRetrievalDto;
import eu.europa.ec.fisheries.ers.service.dto.view.IdentifierDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityIdentifierMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FluxLocationMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Created by kovian on 09/03/2017.
 * <p>
 * This class maps a List of fishingActivities to gearShotRetrieval DTO List.
 */
@Mapper
public abstract class GearShotRetrievalTileMapper extends BaseActivityViewMapper {

    public static final GearShotRetrievalTileMapper INSTANCE = Mappers.getMapper(GearShotRetrievalTileMapper.class);

    /**
     * This should be the entry point of this mapper.
     * As a input parameter it will take the "father" fishingActivity from which we need to get the list of
     * allRelatedFishingActivities.
     * As output it will produce a list of GearShotRetrievalDto.
     *
     * @param fatherFishAct
     * @return List<GearShotRetrievalDto>
     */
    public List<GearShotRetrievalDto> mapFromRelatedFishingActivities(FishingActivityEntity fatherFishAct) {
        return mapEntityListToDtoList(fatherFishAct.getAllRelatedFishingActivities());
    }

    public abstract List<GearShotRetrievalDto> mapEntityListToDtoList(List<FishingActivityEntity> entity);

    public FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity) {

        FishingActivityViewDTO viewDTO = new FishingActivityViewDTO();

        viewDTO.setGearShotRetrievalList(mapFromRelatedFishingActivities(faEntity));

        return viewDTO;
    }

    @Mappings({
            @Mapping(target = "type", source = "typeCode"),
            @Mapping(target = "id", expression = "java(mapListToSingleIdentifier(entity.getFishingActivityIdentifiers()))"),
            @Mapping(target = "occurrence", source = "occurence", dateFormat = DateUtils.DATE_TIME_UI_FORMAT),
            @Mapping(target = "gear", expression = "java(mapToFirstFishingGear(entity.getFishingGears()))"),
            @Mapping(target = "characteristics", expression = "java(getFluxCharacteristicsTypeCodeValue(entity.getFluxCharacteristics()))"),
            @Mapping(target = "location", expression = "java(mapSingleFluxLocationFromEntity(entity.getFluxLocations()))")
    })
    protected abstract GearShotRetrievalDto mapSingleEntityToSingleDto(FishingActivityEntity entity);

    public abstract List<GearProblemDto> mapGearProblemsToGearsDto(Set<GearProblemEntity> gearProblems);

    @Mappings({
            @Mapping(target = "type", source = "typeCode"),
            @Mapping(target = "nrOfGears", source = "affectedQuantity"),
            @Mapping(target = "recoveryMeasure", expression = "java(mapToFirstRecoveryMeasure(entity.getGearProblemRecovery()))"),
            @Mapping(target = "location", expression = "java(mapSingleFluxLocationFromEntity(entity.getLocations()))")
    })
    protected abstract GearProblemDto mapGearProblemToGearsDto(GearProblemEntity entity);

    protected IdentifierDto mapListToSingleIdentifier(Set<FishingActivityIdentifierEntity> identifierList) {
        if (CollectionUtils.isEmpty(identifierList)) {
            return null;
        }
        IdentifierDto idDto = null;
        for (FishingActivityIdentifierEntity ident : identifierList) {
            if (ident != null) {
                idDto = FishingActivityIdentifierMapper.INSTANCE.mapToActivityIdentifierDTO(ident);
            }
        }
        return idDto;
    }

    protected FluxLocationDto mapSingleFluxLocationFromEntity(Set<FluxLocationEntity> fluxLocations) {
        if (CollectionUtils.isEmpty(fluxLocations)) {
            return null;
        }
        return FluxLocationMapper.INSTANCE.mapEntityToFluxLocationDto(fluxLocations.iterator().next());
    }

    protected String mapToFirstRecoveryMeasure(Set<GearProblemRecoveryEntity> recoveries) {
        if (CollectionUtils.isEmpty(recoveries)) {
            return null;
        }
        String probRecoveryMeasure = null;
        for (GearProblemRecoveryEntity probRecovEnt : recoveries) {
            if (StringUtils.isNotEmpty(probRecovEnt.getRecoveryMeasureCode())) {
                probRecoveryMeasure = probRecovEnt.getRecoveryMeasureCode();
                break;
            }
        }
        return probRecoveryMeasure;
    }

}
