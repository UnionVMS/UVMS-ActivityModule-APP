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
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.ActivityDetailsMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FluxLocationMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Mapper
public abstract class ActivityNotificationOfArrivalViewMapper extends BaseActivityViewMapper {

    public static final ActivityNotificationOfArrivalViewMapper INSTANCE = Mappers.getMapper(ActivityNotificationOfArrivalViewMapper.class);

    @Override
    public FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity) {
        if ( faEntity == null ) {
            return null;
        }

        FishingActivityViewDTO fishingActivityViewDTO = new FishingActivityViewDTO();


        // Activity Details
        ActivityDetailsDto activityDetailsDto =
                ActivityDetailsMapper.INSTANCE.mapFishingActivityEntityToActivityDetailsDto(faEntity);
        fishingActivityViewDTO.setActivityDetails(activityDetailsDto);

        FishingTripEntity specifiedFishingTrip = faEntity.getSpecifiedFishingTrip();

        // Ports
        Set<FluxLocationEntity> relatedFluxLocation = getRelatedFluxLocations(specifiedFishingTrip);
        Set<FluxLocationDto> fluxLocationDtos = FluxLocationMapper.INSTANCE.mapEntityToFluxLocationDto(relatedFluxLocation);
        fishingActivityViewDTO.setPorts(new ArrayList<>(fluxLocationDtos));

        return fishingActivityViewDTO;
    }


    private Set<FluxLocationEntity> getRelatedFluxLocations(FishingTripEntity fishingTripEntity) {

        Set<FluxLocationEntity> relatedFluxLocations = new HashSet<>();

        if (fishingTripEntity != null){
            relatedFluxLocations = fishingTripEntity.getRelatedFluxLocations();
        }
        return relatedFluxLocations;
    }


}
