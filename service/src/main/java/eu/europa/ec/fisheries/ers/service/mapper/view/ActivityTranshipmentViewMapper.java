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
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.IdentifierDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.DelimitedPeriodMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityIdentifierMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import io.jsonwebtoken.lang.Collections;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by padhyad on 3/23/2017.
 */
@Mapper
public abstract class ActivityTranshipmentViewMapper extends BaseActivityViewMapper {

    public static final ActivityTranshipmentViewMapper INSTANCE = Mappers.getMapper(ActivityTranshipmentViewMapper.class);

    @Override
    @Mappings({
            @Mapping(target = "activityDetails", expression = "java(mapActivityDetails(faEntity))"),
            @Mapping(target = "locations", expression = "java(mapFromFluxLocation(faEntity.getFluxLocations()))"),
            @Mapping(target = "reportDetails", expression = "java(getReportDocsFromEntity(faEntity.getFaReportDocument()))"),
            @Mapping(target = "catches", expression = "java(mapCatchesToGroupDto(faEntity))"),
            @Mapping(target = "processingProducts", expression = "java(getProcessingProductsByFaCatches(faEntity.getFaCatchs()))")
    })
    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity);

    protected ActivityDetailsDto populateActivityDetails(FishingActivityEntity faEntity, ActivityDetailsDto activityDetails) {
        Map<String, String> idMap = new HashMap<>();
        for (FishingActivityIdentifierEntity idEntity : faEntity.getFishingActivityIdentifiers()) {
            idMap.put(idEntity.getFaIdentifierId(), idEntity.getFaIdentifierSchemeId());
        }
        Set<IdentifierDto> identifierDtos = FishingActivityIdentifierMapper.INSTANCE.mapToIdentifierDTOSet(faEntity.getFishingActivityIdentifiers());
        activityDetails.setIdentifiers(identifierDtos);
        Set<DelimitedPeriodEntity> delimitedPeriodEntitySet = faEntity.getDelimitedPeriods();
        if (!Collections.isEmpty(delimitedPeriodEntitySet)) {
            DelimitedPeriodDTO delimitedPeriodDTO = DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodDTO(delimitedPeriodEntitySet.iterator().next());
            activityDetails.setTranshipmentTime(delimitedPeriodDTO);
        }
        return activityDetails;
    }
}
