/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.activity.service.mapper.view;

import eu.europa.ec.fisheries.ers.activity.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.MeasureType;
import eu.europa.ec.fisheries.ers.activity.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.activity.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.activity.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.activity.service.mapper.view.base.BaseActivityViewMapper;
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Set;

/**
 * Created by kovian on 14/02/2017.
 */
@Mapper(imports = FluxLocationCatchTypeEnum.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class ActivityLandingViewMapper extends BaseActivityViewMapper {

    public static final ActivityLandingViewMapper INSTANCE = Mappers.getMapper(ActivityLandingViewMapper.class);

    @Override
    @Mappings({
            @Mapping(target = "activityDetails", expression = "java(mapActivityDetails(faEntity))"),
            @Mapping(target = "locations", expression = "java(mapFromFluxLocation(faEntity.getFluxLocations()))"),
            @Mapping(target = "reportDetails", expression = "java(getReportDocsFromEntity(faEntity.getFaReportDocument()))"),
            @Mapping(target = "catches", expression = "java(mapCatchesToGroupDto(faEntity))"),
            @Mapping(target = "processingProducts", expression = "java(getProcessingProductsByFaCatches(faEntity.getFaCatchs()))"),
            @Mapping(target = "gears", ignore = true),
            @Mapping(target = "gearProblems", ignore = true),
            @Mapping(target = "gearShotRetrievalList", ignore = true),
            @Mapping(target = "areas", ignore = true),
            @Mapping(target = "tripDetails", ignore = true),
            @Mapping(target = "vesselDetails", ignore = true),
            @Mapping(target = "relocations", ignore = true),
            @Mapping(target = "history", ignore = true)
    })
    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity);

    @Override
    protected ActivityDetailsDto populateActivityDetails(FishingActivityEntity faEntity, ActivityDetailsDto activityDetails) {
        DelimitedPeriodDTO delimitedPeriodDTO = mapToDelimitedPeriodDto(faEntity.getDelimitedPeriods());
        activityDetails.setLandingTime(delimitedPeriodDTO);
        return activityDetails;
    }

    private DelimitedPeriodDTO mapToDelimitedPeriodDto(Set<DelimitedPeriodEntity> delimitedPeriods) {
        Date startDate = null;
        Date endDate = null;
        Double duration = null;
        String unitCode = null;
        if (CollectionUtils.isNotEmpty(delimitedPeriods)) {
            DelimitedPeriodEntity delimPeriod = delimitedPeriods.iterator().next();
            startDate = delimPeriod.getStartDateAsDate().orElse(null);
            endDate = delimPeriod.getEndDateAsDate().orElse(null);
            MeasureType durationMeasure = delimPeriod.getDurationMeasure();
            if (durationMeasure != null){
                duration = delimPeriod.getDurationMeasure().getValue();
                unitCode = delimPeriod.getDurationMeasure().getUnitCode();
            }
        }
        return new DelimitedPeriodDTO(startDate, endDate, duration, unitCode);
    }

}
