/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.mapper.view;

import java.util.Date;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Created by kovian on 09/02/2017.
 */
@Mapper(imports = FluxLocationCatchTypeEnum.class)
public abstract class ActivityArrivalViewMapper extends BaseActivityViewMapper {

    public static final ActivityArrivalViewMapper INSTANCE = Mappers.getMapper(ActivityArrivalViewMapper.class);

    @Override
    @Mappings({
            @Mapping(target = "activityDetails", expression = "java(mapActivityDetails(faEntity))"),
            @Mapping(target = "locations", expression = "java(mapFromFluxLocation(faEntity.getFluxLocations(), FluxLocationCatchTypeEnum.FA_RELATED))"),
            @Mapping(target = "gears", expression = "java(getGearsFromEntity(faEntity.getFishingGears()))"),
            @Mapping(target = "reportDetails", expression = "java(getReportDocsFromEntity(faEntity.getFaReportDocument()))"),
            @Mapping(target = "processingProducts", expression = "java(getProcessingProductsByFaCatches(faEntity.getFaCatchs()))"),
            @Mapping(target = "gearProblems", ignore = true)
    })
    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity);

    @Override
    protected ActivityDetailsDto populateActivityDetails(FishingActivityEntity faEntity, ActivityDetailsDto activityDetails){
        activityDetails.setArrivalTime(faEntity.getOccurence());
        activityDetails.setIntendedLandingTime(extractLandingTime(faEntity.getFluxCharacteristics()));
        activityDetails.setReason(faEntity.getReasonCode());
        return activityDetails;
    }

    private Date extractLandingTime(Set<FluxCharacteristicEntity> fluxCharacteristics) {
        if(CollectionUtils.isNotEmpty(fluxCharacteristics)){
            for(FluxCharacteristicEntity charact : fluxCharacteristics){
                if(StringUtils.equals("FA_CHARACTERISTIC", charact.getTypeCodeListId())
                        && StringUtils.equals("START_DATETIME_LANDING", charact.getTypeCode())){
                    return charact.getValueDateTime();
                }
            }
        }
        return null;
    }

}


