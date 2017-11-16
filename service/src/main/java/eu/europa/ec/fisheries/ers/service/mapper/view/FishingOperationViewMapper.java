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
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityIdentifierMapper;
import eu.europa.ec.fisheries.ers.service.mapper.VesselStorageCharacteristicsMapper;
import eu.europa.ec.fisheries.ers.service.mapper.VesselTransportMeansMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FishingOperationViewMapper extends BaseActivityViewMapper {

    @Override
    public FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity) {

        FishingActivityViewDTO viewDTO = new FishingActivityViewDTO();

        ActivityDetailsDto detailsDto = mapActivityDetails(faEntity);
        detailsDto.setOccurrence(faEntity.getOccurence());
        detailsDto.setVesselActivity(faEntity.getVesselActivityCode());

        if (faEntity.getOperationQuantity() != null) {
            detailsDto.setNrOfOperation(faEntity.getOperationQuantity().intValue());
        }

        detailsDto.setFisheryType(faEntity.getFisheryTypeCode());
        detailsDto.setSpeciesTarget(faEntity.getSpeciesTargetCode());

        Set<FishingActivityIdentifierEntity> fishingActivityIdentifiers = faEntity.getFishingActivityIdentifiers();
        detailsDto.setIdentifiers(FishingActivityIdentifierMapper.INSTANCE.mapToIdentifierDTOSet(fishingActivityIdentifiers));

        detailsDto.setFishingTime(this.calculateFishingTime(faEntity.getDelimitedPeriods()));

        viewDTO.setActivityDetails(detailsDto);

        viewDTO.setGears(this.getGearsFromEntity(faEntity.getFishingGears()));

        viewDTO.setReportDetails(this.getReportDocsFromEntity(faEntity.getFaReportDocument()));

        viewDTO.setCatches(this.mapCatchesToGroupDto(faEntity));

        viewDTO.setProcessingProducts(this.getProcessingProductsByFaCatches(faEntity.getFaCatchs()));

        viewDTO.setGearShotRetrievalList(GearShotRetrievalTileMapperImpl.INSTANCE.mapFromRelatedFishingActivities(faEntity));

        Set<FluxLocationDto> fluxLocationDtos = mapFromFluxLocation(faEntity.getFluxLocations());

        viewDTO.setLocations(fluxLocationDtos);
        viewDTO.setVesselDetails( getVesselDetailsDTO(faEntity) );

        return viewDTO;
    }

    /**
     * Addded this method as we want to set storage information explicitely in VesselDetailsDTO.Storage informaation we can only get from activities
     * @param faEntity
     * @return VesselDetailsDTO
     */
    protected List<VesselDetailsDTO> getVesselDetailsDTO(FishingActivityEntity faEntity){
        if(faEntity==null || CollectionUtils.isEmpty(faEntity.getVesselTransportMeans()))
            return null;

        List<VesselDetailsDTO> vesselDetailsDTOs = new ArrayList<>();
        Set<VesselTransportMeansEntity> entities=  faEntity.getVesselTransportMeans();

        for(VesselTransportMeansEntity vesselTransportMeansEntity : entities) {
            VesselDetailsDTO vesselDetails = VesselTransportMeansMapper.INSTANCE.map(vesselTransportMeansEntity);
            if (vesselDetails != null && faEntity.getDestVesselCharId() != null) {
                vesselDetails.setStorageDto(VesselStorageCharacteristicsMapper.INSTANCE.mapToStorageDto(faEntity.getDestVesselCharId()));
            }
            vesselDetailsDTOs.add(vesselDetails);
        }

        return vesselDetailsDTOs;
    }
}
