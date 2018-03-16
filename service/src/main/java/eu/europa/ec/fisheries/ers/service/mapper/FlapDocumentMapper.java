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

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FlapDocumentEntity;
import eu.europa.ec.fisheries.ers.service.dto.FlapDocumentDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLAPDocument;

@Mapper(uses = BaseMapper.class)
public interface FlapDocumentMapper {

    FlapDocumentMapper INSTANCE = Mappers.getMapper(FlapDocumentMapper.class);

    @Mappings({
            @Mapping(target = "flapDocumentId", source = "ID.value"),
            @Mapping(target = "flapDocumentSchemeId", source = "ID.schemeID"),
            @Mapping(target = "flapTypeCode", source = "typeCode.value"),
            @Mapping(target = "flapTypeCodeListId", source = "typeCode.listID")
    })
    FlapDocumentEntity mapToFlapDocumentEntity(FLAPDocument flapDocument);

    @InheritInverseConfiguration
    FLAPDocument mapToFlapDocument(FlapDocumentEntity flapDocument);

    List<FLAPDocument> mapToFlapDocumentList(Set<FlapDocumentEntity> flapDocument);

    @Mappings({
            @Mapping(target = "faIdentifierId", source = "flapDocumentId"),
            @Mapping(target = "faIdentifierSchemeId", source = "flapDocumentSchemeId")
    })
    FlapDocumentDto mapToFlapDocumentDto(FlapDocumentEntity entity);

    Set<FlapDocumentDto> mapToFlapDocumentDto(Set<FlapDocumentEntity> entity);

}
