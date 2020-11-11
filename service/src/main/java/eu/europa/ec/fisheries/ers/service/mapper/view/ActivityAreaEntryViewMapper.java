/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.mapper.view;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class ActivityAreaEntryViewMapper extends BaseActivityViewMapper {

    public static final ActivityAreaEntryViewMapper INSTANCE = Mappers.getMapper(ActivityAreaEntryViewMapper.class);

    @Override
    @Mappings({
            @Mapping(target = "activityDetails", expression = "java(mapActivityDetails(faEntity))"),
            @Mapping(target = "locations", source = "locations_"),
            @Mapping(target = "reportDetails", expression = "java(getReportDocsFromEntity(faEntity.getFaReportDocument()))"),
            @Mapping(target = "areas", expression = "java(getSortedAreas(faEntity, new eu.europa.ec.fisheries.ers.service.mapper.view.base.FluxLocationDTOSchemeIdComparator()))"),
            @Mapping(target = "gearProblems", ignore = true),
            @Mapping(target = "processingProducts", ignore = true),
            @Mapping(target = "catches", ignore = true),
    })
    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity);
    
    @BeforeMapping
    public void mapCatchesFirst(FishingActivityEntity faEntity, @MappingTarget FishingActivityViewDTO target) {
        target.setCatches(mapCatchesToGroupDto(faEntity));
        target.setProcessingProducts(getProcessingProductsByFaCatches(faEntity.getFaCatchs()));
    }
    
    @Override
    protected ActivityDetailsDto populateActivityDetails(FishingActivityEntity faEntity, ActivityDetailsDto activityDetails) {
        activityDetails.setReason(faEntity.getReasonCode());
        activityDetails.setFisheryType(faEntity.getFisheryTypeCode());
        activityDetails.setSpeciesTarget(faEntity.getSpeciesTargetCode());
        return activityDetails;
    }

}
