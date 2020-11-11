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

import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.IdentifierDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.DelimitedPeriodMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityIdentifierMapper;
import eu.europa.ec.fisheries.ers.service.mapper.VesselStorageCharacteristicsMapper;
import eu.europa.ec.fisheries.ers.service.mapper.VesselTransportMeansMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper(uses = {VesselTransportMeansMapper.class}, imports = FluxLocationCatchTypeEnum.class)
public abstract class ActivityTranshipmentViewMapper extends BaseActivityViewMapper {

    public static final ActivityTranshipmentViewMapper INSTANCE = Mappers.getMapper(ActivityTranshipmentViewMapper.class);

    @Override
    @Mappings({
            @Mapping(target = "activityDetails", expression = "java(mapActivityDetails(faEntity))"),
            @Mapping(target = "locations", expression = "java(mapFromFluxLocation(faEntity.getFluxLocations()))"),
            @Mapping(target = "reportDetails", expression = "java(getReportDocsFromEntity(faEntity.getFaReportDocument()))"),
            @Mapping(target = "vesselDetails", expression = "java(getVesselDetailsDTO(faEntity))"),
            @Mapping(target = "gearProblems", ignore = true),
            @Mapping(target = "catches", ignore = true),
            @Mapping(target = "processingProducts", ignore = true)
    })
    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity);

    @BeforeMapping
    public void mapCatchesFirst(FishingActivityEntity faEntity, @MappingTarget FishingActivityViewDTO target) {
        target.setCatches(mapCatchesToGroupDto(faEntity));
        target.setProcessingProducts(getProcessingProductsByFaCatches(faEntity.getFaCatchs()));
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



    protected ActivityDetailsDto populateActivityDetails(FishingActivityEntity faEntity, ActivityDetailsDto activityDetails) {
        Map<String, String> idMap = new HashMap<>();
        for (FishingActivityIdentifierEntity idEntity : faEntity.getFishingActivityIdentifiers()) {
            idMap.put(idEntity.getFaIdentifierId(), idEntity.getFaIdentifierSchemeId());
        }
        Set<IdentifierDto> identifierDtos = FishingActivityIdentifierMapper.INSTANCE.mapToIdentifierDTOSet(faEntity.getFishingActivityIdentifiers());
        activityDetails.setIdentifiers(identifierDtos);
        Set<DelimitedPeriodEntity> delimitedPeriodEntitySet = faEntity.getDelimitedPeriods();

        if (!Collections.isEmpty(delimitedPeriodEntitySet)) {
            DelimitedPeriodEntity delimitedPeriod = delimitedPeriodEntitySet.iterator().next();
            DelimitedPeriodDTO delimitedPeriodDTO = DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodDTO(delimitedPeriod);
            activityDetails.setTranshipmentTime(delimitedPeriodDTO);

            //Override occurrence date from delimited period
            activityDetails.setOccurrence(delimitedPeriod.getStartDate());
        }

        return activityDetails;
    }
}
