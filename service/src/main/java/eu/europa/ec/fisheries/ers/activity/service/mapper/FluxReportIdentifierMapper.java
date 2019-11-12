/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.activity.service.mapper;

import eu.europa.ec.fisheries.ers.activity.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.service.dto.FluxReportIdentifierDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FluxReportIdentifierMapper {

    FluxReportIdentifierMapper INSTANCE = Mappers.getMapper(FluxReportIdentifierMapper.class);

    @Mappings({
            @Mapping(target = "fluxReportIdentifierId", source = "value"),
            @Mapping(target = "fluxReportIdentifierSchemeId", source = "schemeID"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "fluxReportDocument", ignore = true)
    })
    FluxReportIdentifierEntity mapToFluxReportIdentifierEntity(IDType idType);


    @Mappings({
            @Mapping(target = "schemeName", ignore = true),
            @Mapping(target = "schemeAgencyID", ignore = true),
            @Mapping(target = "schemeAgencyName", ignore = true),
            @Mapping(target = "schemeVersionID", ignore = true),
            @Mapping(target = "schemeDataURI", ignore = true),
            @Mapping(target = "schemeURI", ignore = true),
    })
    @InheritInverseConfiguration
    IDType mapToFluxReportIdentifier(FluxReportIdentifierEntity idType);

    @Mappings({
            @Mapping(target = "fluxReportId", source = "fluxReportIdentifierId"),
            @Mapping(target = "fluxReportSchemeId", source = "fluxReportIdentifierSchemeId")
    })
    FluxReportIdentifierDTO mapToFluxReportIdentifierDTO(FluxReportIdentifierEntity identifierEntity);

}
