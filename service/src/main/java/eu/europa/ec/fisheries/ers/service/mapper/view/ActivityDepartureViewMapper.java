/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.mapper.view;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Created by padhyad on 3/7/2017.
 */
@Mapper
public abstract class ActivityDepartureViewMapper extends BaseActivityViewMapper {

    public static final ActivityDepartureViewMapper INSTANCE = Mappers.getMapper(ActivityDepartureViewMapper.class);

    @Override
    @Mappings({
            @Mapping(target = "activityDetails",   expression = "java(mapActivityDetails(faEntity))"),
            @Mapping(target = "ports",     expression = "java(getPortsFromFluxLocation(faEntity.getFluxLocations()))"),
            @Mapping(target = "gears",     expression = "java(getGearsFromEntity(faEntity.getFishingGears()))"),
            @Mapping(target = "reportDetails", expression = "java(getReportDocsFromEntity(faEntity.getFaReportDocument()))"),
            @Mapping(target = "catches",   expression = "java(mapCatchesToGroupDto(faEntity))")
    })
    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity);

    @Override
    protected ActivityDetailsDto populateActivityDetails(FishingActivityEntity faEntity, ActivityDetailsDto activityDetails) {
        activityDetails.setFisheryType(faEntity.getFisheryTypeCode());
        activityDetails.setSpeciesTarget(faEntity.getSpeciesTargetCode());
        return activityDetails;
    }
}
