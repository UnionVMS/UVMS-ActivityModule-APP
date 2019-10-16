/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.VesselStorageCharacteristicsMapper;
import eu.europa.ec.fisheries.ers.service.mapper.VesselTransportMeansMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import org.apache.commons.collections.CollectionUtils;

/**
 * Created by padhyad on 3/28/2017.
 */
public class JointFishingOperationViewMapper extends BaseActivityViewMapper {

    @Override
    public FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity) {
        FishingActivityViewDTO viewDTO = new FishingActivityViewDTO();

        ActivityDetailsDto detailsDto = mapActivityDetails(faEntity);
        detailsDto.setVesselActivity(faEntity.getVesselActivityCode());
        detailsDto.setFisheryType(faEntity.getFisheryTypeCode());
        detailsDto.setSpeciesTarget(faEntity.getSpeciesTargetCode());
        detailsDto.setFishingTime(calculateFishingTime(faEntity.getDelimitedPeriods()));

        viewDTO.setActivityDetails(detailsDto);
        viewDTO.setLocations(mapFromFluxLocation(faEntity.getFluxLocations()));
        viewDTO.setReportDetails(getReportDocsFromEntity(faEntity.getFaReportDocument()));
        viewDTO.setGears(getGearsFromEntity(faEntity.getFishingGears()));
        viewDTO.setVesselDetails(getVesselDetailsDTO(faEntity));
        viewDTO.setCatches(mapCatchesToGroupDto(faEntity));
        viewDTO.setProcessingProducts(getProcessingProductsByFaCatches(faEntity.getFaCatchs()));
        viewDTO.setGearProblems(GearShotRetrievalTileMapper.INSTANCE.mapGearProblemsToGearsDto(faEntity.getGearProblems()));
        viewDTO.setRelocations(getRelocations(faEntity));
        return viewDTO;
    }

    /**
     * Addded this method as we want to set storage information explicitely in VesselDetailsDTO.Storage informaation we can only get from activities
     * @param faEntity
     * @return VesselDetailsDTO
     */
    private List<VesselDetailsDTO> getVesselDetailsDTO(FishingActivityEntity faEntity){
        if(faEntity==null)
            return null;

        List<VesselDetailsDTO> vesselDetailsDTOs = new ArrayList<>();
        Set<VesselTransportMeansEntity> entities = faEntity.getVesselTransportMeans();
        if (CollectionUtils.isEmpty(entities)) {
            entities = new HashSet<>();
        }

        if (CollectionUtils.isNotEmpty(faEntity.getAllRelatedFishingActivities())) {
            for (FishingActivityEntity fishingActivityEntity : faEntity.getAllRelatedFishingActivities()) {
                entities.addAll(fishingActivityEntity.getVesselTransportMeans());
            }
        }

        for (VesselTransportMeansEntity vesselTransportMeansEntity : entities) {
            VesselDetailsDTO vesselDetails = VesselTransportMeansMapper.INSTANCE.map(vesselTransportMeansEntity);
            if (vesselDetails != null && faEntity.getDestVesselCharId() != null) {
                vesselDetails.setStorageDto(VesselStorageCharacteristicsMapper.INSTANCE.mapToStorageDto(faEntity.getDestVesselCharId()));
            }
            vesselDetailsDTOs.add(vesselDetails);
        }

        return vesselDetailsDTOs;
    }
}
