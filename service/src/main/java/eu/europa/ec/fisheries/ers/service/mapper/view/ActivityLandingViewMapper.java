/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view;

import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Set;

/**
 * Created by kovian on 14/02/2017.
 */
@Mapper
public abstract class ActivityLandingViewMapper extends BaseActivityViewMapper {

    public static final ActivityLandingViewMapper INSTANCE = Mappers.getMapper(ActivityLandingViewMapper.class);

    @Override
    @Mappings({
            @Mapping(target = "activityDetails",   expression = "java(mapActivityDetails(faEntity))"),
            @Mapping(target = "ports",       expression = "java(getPortsFromFluxLocation(faEntity.getFluxLocations()))"),
            @Mapping(target = "reportDetails",   expression = "java(getReportDocsFromEntity(faEntity.getFaReportDocument()))"),
            @Mapping(target = "catches",   expression = "java(mapCatchesToGroupDto(faEntity))")
    })
    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity);

    @Override
    protected ActivityDetailsDto populateActivityDetails(FishingActivityEntity faEntity, ActivityDetailsDto activityDetails){
        DelimitedPeriodDTO delimitedPeriodDTO = mapToDelimitedPeriodDto(faEntity.getDelimitedPeriods());
        activityDetails.setLandingTime(delimitedPeriodDTO);
        return activityDetails;
    }

    private DelimitedPeriodDTO mapToDelimitedPeriodDto(Set<DelimitedPeriodEntity> delimitedPeriods) {
        Date startDate  = null;
        Date endDate    = null;
        Double duration = null;
        if(CollectionUtils.isNotEmpty(delimitedPeriods)){
            DelimitedPeriodEntity delimPeriod = delimitedPeriods.iterator().next();
            startDate = delimPeriod.getStartDate();
            endDate   = delimPeriod.getEndDate();
            duration  = delimPeriod.getDuration();
        }
        return new DelimitedPeriodDTO(startDate, endDate, duration);
    }

}
