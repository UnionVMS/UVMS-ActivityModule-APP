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
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FluxLocationMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Set;

@Mapper
public abstract class ActivityNotificationOfArrivalViewMapper extends BaseActivityViewMapper {

    public static final ActivityNotificationOfArrivalViewMapper INSTANCE = Mappers.getMapper(ActivityNotificationOfArrivalViewMapper.class);

    @Override
    public FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity) {

        FishingActivityViewDTO fishingActivityViewDTO = new FishingActivityViewDTO();

        // Fishing Activity Tile
        ActivityDetailsDto activityDetailsDto = FishingActivityMapper.INSTANCE.
                mapFishingActivityEntityToActivityDetailsDto(faEntity);
        fishingActivityViewDTO.setActivityDetails(activityDetailsDto);

        // Intented Port of Landing Tile
        Set<FluxLocationEntity> relatedFluxLocation = faEntity.getRelatedFluxLocations();
        Set<FluxLocationDto> fluxLocationDtos = FluxLocationMapper.INSTANCE.mapEntityToFluxLocationDto(relatedFluxLocation);
        fishingActivityViewDTO.setPorts(new ArrayList<>(fluxLocationDtos));

        // Activity Report document tile
        ReportDocumentDto reportDocumentDto = FaReportDocumentMapper.INSTANCE.
                mapFaReportDocumentToReportDocumentDto(faEntity.getFaReportDocument(), faEntity.getFluxReportDocument());
        fishingActivityViewDTO.setReportDetails(reportDocumentDto);

        // Catch tile
        fishingActivityViewDTO.setCatches(FaCatchesProcessorMapper.getCatchGroupsFromListEntity(faEntity.getFaCatchs()));

        // Applied AAP process Tile
        //TODO

        return fishingActivityViewDTO;
    }

}
