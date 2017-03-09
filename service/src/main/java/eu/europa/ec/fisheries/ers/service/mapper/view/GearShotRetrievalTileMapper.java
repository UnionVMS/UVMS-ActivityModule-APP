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
import eu.europa.ec.fisheries.ers.service.dto.view.GearShotRetrievalDto;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * Created by kovian on 09/03/2017.
 */
@Mapper
public abstract class GearShotRetrievalTileMapper extends BaseActivityViewMapper {

    protected abstract List<GearShotRetrievalDto> mapEntityListToDtoList(List<FishingActivityEntity> entity);

    @Mappings({
            @Mapping(target = "type",            source = "typeCode"),
            @Mapping(target = "occurrence",      source = "occurrence"),
            @Mapping(target = "duration",        expression = "java(getDurationFromActivity(entity.delimitedPeriods))"),
            @Mapping(target = "gear",            expression = ""),
            @Mapping(target = "characteristics", expression = ""),
            @Mapping(target = "location",        expression = "java()")
    })
    protected abstract GearShotRetrievalDto mapSingleEntityToSingleDto(FishingActivityEntity entity);


    protected abstract String mapDelimitedPerion(DelimitedPeriodEntity delimitPeriod);


    protected Double getDurationFromActivity(List<DelimitedPeriodEntity> periodsList){
        if(CollectionUtils.isEmpty(periodsList)){
            return null;
        }
        Double duration = null;
        for(DelimitedPeriodEntity period : periodsList){
            Double calculatedDuration = period.getCalculatedDuration();

        }


        return duration;

    }

/*
    @JsonView(FishingActivityView.CommonView.class)
    private String ;

    @JsonView(FishingActivityView.CommonView.class)
    private String ;

    @JsonView(FishingActivityView.CommonView.class)
    private String ;

    @JsonView(FishingActivityView.CommonView.class)
    private GearDto ;

    @JsonView(FishingActivityView.CommonView.class)
    private Map<String, String> ;

    @JsonView(FishingActivityView.CommonView.class)
    private FluxLocationDto ;*/
}
