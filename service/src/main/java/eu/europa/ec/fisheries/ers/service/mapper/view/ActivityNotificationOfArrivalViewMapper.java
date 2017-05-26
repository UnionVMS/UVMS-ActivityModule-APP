/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.mapper.view;

import static eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum.FA_RELATED;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;

public class ActivityNotificationOfArrivalViewMapper extends BaseActivityViewMapper {

    @Override
    public FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity) {

        FishingActivityViewDTO fishingActivityViewDTO = new FishingActivityViewDTO();

        if (faEntity != null) {

            // Fishing Activity Tile
            ActivityDetailsDto activityDetailsDto = mapActivityDetails(faEntity);
            activityDetailsDto.setReason(faEntity.getReasonCode());
            fishingActivityViewDTO.setActivityDetails(activityDetailsDto);

            // Intented ports of Landing Tile
            fishingActivityViewDTO.setLocations(mapFromFluxLocation(faEntity.getFluxLocations(), FA_RELATED));

            // Activity Report document tile
            fishingActivityViewDTO.setReportDetails(getReportDocsFromEntity(faEntity.getFaReportDocument()));

            // Catch tile
            fishingActivityViewDTO.setCatches(mapCatchesToGroupDto(faEntity));

            // Applied AAP process Tile
            fishingActivityViewDTO.setProcessingProducts(getProcessingProductsByFaCatches(faEntity.getFaCatchs()));
        }

        return fishingActivityViewDTO;
    }

}
