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
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.VesselStorageCharacteristicsMapper;
import eu.europa.ec.fisheries.ers.service.mapper.VesselTransportMeansMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import eu.europa.ec.fisheries.ers.service.util.Utils;

public class ActivityRelocationViewMapper extends BaseActivityViewMapper {

    @Override
    public FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity) {

        FishingActivityViewDTO viewDTO = new FishingActivityViewDTO();

        if (faEntity != null) {
            viewDTO.setActivityDetails(mapActivityDetails(faEntity));
            viewDTO.setLocations(mapFromFluxLocation(faEntity.getFluxLocations()));
            viewDTO.setReportDetails(getReportDocsFromEntity(faEntity.getFaReportDocument()));
            viewDTO.setCatches(mapCatchesToGroupDto(faEntity));
            viewDTO.setProcessingProducts(getProcessingProductsByFaCatches(faEntity.getFaCatchs()));
            viewDTO.setVesselDetails(VesselTransportMeansMapper.INSTANCE.map(faEntity.getVesselTransportMeans()));

            for (VesselDetailsDTO detailsDTO : Utils.safeIterable(viewDTO.getVesselDetails())) {
                detailsDTO.setStorageDto(VesselStorageCharacteristicsMapper.INSTANCE.mapToStorageDto(faEntity.getDestVesselCharId()));
             }
        }

        return viewDTO;
    }

}
