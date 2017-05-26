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

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.VesselTransportMeansMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;

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
        viewDTO.setLocations(mapFromFluxLocation(faEntity.getFluxLocations(), FluxLocationEnum.LOCATION));
        viewDTO.setReportDetails(getReportDocsFromEntity(faEntity.getFaReportDocument()));
        viewDTO.setGears(getGearsFromEntity(faEntity.getFishingGears()));
        viewDTO.setVesselDetails(VesselTransportMeansMapper.INSTANCE.map(faEntity.getVesselTransportMeans()));
        viewDTO.setCatches(mapCatchesToGroupDto(faEntity));
        viewDTO.setProcessingProducts(getProcessingProductsByFaCatches(faEntity.getFaCatchs()));
        viewDTO.setGearProblems(GearShotRetrievalTileMapper.INSTANCE.mapGearProblemsToGearsDto(faEntity.getGearProblems()));
        viewDTO.setRelocations(getRelocations(faEntity));
        return viewDTO;
    }
}
