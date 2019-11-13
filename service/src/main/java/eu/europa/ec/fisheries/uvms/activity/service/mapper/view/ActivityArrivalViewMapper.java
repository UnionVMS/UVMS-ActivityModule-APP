/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.uvms.activity.service.mapper.view;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.view.base.BaseActivityViewMapper;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

/**
 * Created by kovian on 09/02/2017.
 */
@Mapper(imports = FluxLocationCatchTypeEnum.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class ActivityArrivalViewMapper extends BaseActivityViewMapper {

    public static final ActivityArrivalViewMapper INSTANCE = Mappers.getMapper(ActivityArrivalViewMapper.class);

    @Override
    @Mappings({
            @Mapping(target = "activityDetails", expression = "java(mapActivityDetails(faEntity))"),
            @Mapping(target = "locations", expression = "java(mapFromFluxLocation(faEntity.getFluxLocations()))"),
            @Mapping(target = "gears", expression = "java(getGearsFromEntity(faEntity.getFishingGears()))"),
            @Mapping(target = "reportDetails", expression = "java(getReportDocsFromEntity(faEntity.getFaReportDocument()))"),
            @Mapping(target = "processingProducts", expression = "java(getProcessingProductsByFaCatches(faEntity.getFaCatchs()))"),
            @Mapping(target = "gearProblems", ignore = true),
            @Mapping(target = "catches", ignore = true),
            @Mapping(target = "gearShotRetrievalList", ignore = true),
            @Mapping(target = "areas", ignore = true),
            @Mapping(target = "tripDetails", ignore = true),
            @Mapping(target = "vesselDetails", ignore = true),
            @Mapping(target = "relocations", ignore = true),
            @Mapping(target = "history", ignore = true),
    })
    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity);

    @Override
    protected ActivityDetailsDto populateActivityDetails(FishingActivityEntity faEntity, ActivityDetailsDto activityDetails){
        activityDetails.setArrivalTime(faEntity.getOccurrenceAsDate().orElse(null));
        activityDetails.setIntendedLandingTime(extractLandingTime(faEntity.getFluxCharacteristics()));
        activityDetails.setReason(faEntity.getReasonCode());
        return activityDetails;
    }

    private Date extractLandingTime(Set<FluxCharacteristicEntity> fluxCharacteristics) {
        for(FluxCharacteristicEntity charact : Utils.safeIterable(fluxCharacteristics)) {
            if(StringUtils.equals("FA_CHARACTERISTIC", charact.getTypeCodeListId())
                    && StringUtils.equals("START_DATETIME_LANDING", charact.getTypeCode())){
                Instant valueDateTime = charact.getValueDateTime();
                if (valueDateTime != null) {
                    return Date.from(valueDateTime);
                }
            }
        }

        return null;
    }

}


