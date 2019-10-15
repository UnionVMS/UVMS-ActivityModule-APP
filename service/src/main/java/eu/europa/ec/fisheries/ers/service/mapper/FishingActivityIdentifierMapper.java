/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityIdentifierEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.IdentifierDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FishingActivityIdentifierMapper {

    FishingActivityIdentifierMapper INSTANCE = Mappers.getMapper(FishingActivityIdentifierMapper.class);

    IdentifierDto mapToActivityIdentifierDTO(FishingActivityIdentifierEntity entity);

    Set<IdentifierDto> mapToIdentifierDTOSet(Set<FishingActivityIdentifierEntity> entity);

    @Mappings({
            @Mapping(target = "faIdentifierId", source = "value"),
            @Mapping(target = "faIdentifierSchemeId", source = "schemeID"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "fishingActivity", ignore = true)
    })
    FishingActivityIdentifierEntity mapToFishingActivityIdentifierEntity(IDType idType);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "schemeName", ignore = true),
            @Mapping(target = "schemeAgencyID", ignore = true),
            @Mapping(target = "schemeAgencyName", ignore = true),
            @Mapping(target = "schemeVersionID", ignore = true),
            @Mapping(target = "schemeDataURI", ignore = true),
            @Mapping(target = "schemeURI", ignore = true)
    })
    IDType mapToIDType(FishingActivityIdentifierEntity identifierEntity);

    List<IDType> mapToIDTypeList(Set<FishingActivityIdentifierEntity> identifierEntities);

}