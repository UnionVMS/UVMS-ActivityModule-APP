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

package eu.europa.ec.fisheries.ers.activity.service.mapper.view;

import eu.europa.ec.fisheries.ers.activity.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.activity.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.activity.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.activity.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.activity.service.dto.view.IdentifierDto;
import eu.europa.ec.fisheries.ers.activity.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.activity.service.mapper.DelimitedPeriodMapper;
import eu.europa.ec.fisheries.ers.activity.service.mapper.FishingActivityIdentifierMapper;
import eu.europa.ec.fisheries.ers.activity.service.mapper.VesselStorageCharacteristicsMapper;
import eu.europa.ec.fisheries.ers.activity.service.mapper.VesselTransportMeansMapper;
import eu.europa.ec.fisheries.ers.activity.service.mapper.view.base.BaseActivityViewMapper;
import eu.europa.ec.fisheries.ers.activity.service.dto.DelimitedPeriodDTO;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(uses = {VesselTransportMeansMapper.class}, imports = FluxLocationCatchTypeEnum.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class ActivityTranshipmentViewMapper extends BaseActivityViewMapper {

    public static final ActivityTranshipmentViewMapper INSTANCE = Mappers.getMapper(ActivityTranshipmentViewMapper.class);

    @Override
    @Mappings({
            @Mapping(target = "activityDetails", expression = "java(mapActivityDetails(faEntity))"),
            @Mapping(target = "locations", expression = "java(mapFromFluxLocation(faEntity.getFluxLocations()))"),
            @Mapping(target = "reportDetails", expression = "java(getReportDocsFromEntity(faEntity.getFaReportDocument()))"),
            @Mapping(target = "catches", expression = "java(mapCatchesToGroupDto(faEntity))"),
            @Mapping(target = "processingProducts", expression = "java(getProcessingProductsByFaCatches(faEntity.getFaCatchs()))"),
            @Mapping(target = "vesselDetails", expression = "java(getVesselDetailsDTO(faEntity))"),
            @Mapping(target = "gearProblems", ignore = true),
            @Mapping(target = "gears", ignore = true),
            @Mapping(target = "gearShotRetrievalList", ignore = true),
            @Mapping(target = "areas", ignore = true),
            @Mapping(target = "tripDetails", ignore = true),
            @Mapping(target = "relocations", ignore = true),
            @Mapping(target = "history", ignore = true)
    })
    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity);

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
        Set<IdentifierDto> identifierDtos = FishingActivityIdentifierMapper.INSTANCE.mapToIdentifierDTOSet(faEntity.getFishingActivityIdentifiers());
        activityDetails.setIdentifiers(identifierDtos);
        Set<DelimitedPeriodEntity> delimitedPeriodEntitySet = faEntity.getDelimitedPeriods();

        if (!Collections.isEmpty(delimitedPeriodEntitySet)) {
            DelimitedPeriodEntity delimitedPeriod = delimitedPeriodEntitySet.iterator().next();
            DelimitedPeriodDTO delimitedPeriodDTO = DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodDTO(delimitedPeriod);
            activityDetails.setTranshipmentTime(delimitedPeriodDTO);

            //Override occurrence date from delimited period
            activityDetails.setOccurrence(delimitedPeriod.getStartDateAsDate().orElse(null));
        }

        return activityDetails;
    }
}
