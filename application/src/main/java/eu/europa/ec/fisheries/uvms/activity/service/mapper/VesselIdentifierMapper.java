/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(uses = BaseMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface VesselIdentifierMapper {

    VesselIdentifierMapper INSTANCE = Mappers.getMapper(VesselIdentifierMapper.class);


    @Mapping(target = "identifierSchemeId", source = "vesselIdentifierSchemeId")
    @Mapping(target = "faIdentifierId", source = "vesselIdentifierId")
    @Mapping(target = "faIdentifierSchemeId", ignore = true)
    @Mapping(target = "fromAssets", ignore = true)
    AssetIdentifierDto mapToIdentifierDto(VesselIdentifierEntity entity);

    Set<AssetIdentifierDto> mapToIdentifierDotSet(Set<VesselIdentifierEntity> entity);

    @ValueMapping(source = "EXT_MARK", target = "EXTERNAL_MARKING")
    @ValueMapping(source = "UVI", target = MappingConstants.NULL)
    @ValueMapping(source = "ICCAT", target = MappingConstants.NULL)
    @ValueMapping(source = "GFCM", target = MappingConstants.NULL)
    ConfigSearchField map(VesselIdentifierSchemeIdEnum schemeIdEnum);
}
