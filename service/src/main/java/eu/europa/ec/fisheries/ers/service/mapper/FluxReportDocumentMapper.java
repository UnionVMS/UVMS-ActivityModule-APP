/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.service.dto.FluxReportIdentifierDTO;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@Mapper(imports = {BaseMapper.class}, uses = XMLDateUtils.class)
public interface FluxReportDocumentMapper {

    FluxReportDocumentMapper INSTANCE = Mappers.getMapper(FluxReportDocumentMapper.class);

    @Mappings({
            @Mapping(target = "referenceId", source = "referencedID.value"),
            @Mapping(target = "referenceSchemeId", source = "referencedID.schemeID"),
            @Mapping(target = "creationDatetime", source = "fluxReportDocument.creationDateTime.dateTime"),
            @Mapping(target = "purposeCode", source = "purposeCode.value"),
            @Mapping(target = "purposeCodeListId", source = "purposeCode.listID"),
            @Mapping(target = "purpose", source = "purpose.value"),
            @Mapping(target = "fluxParty", expression = "java(BaseMapper.getFluxPartyEntity(fluxReportDocument.getOwnerFLUXParty(), fluxReportDocumentEntity))"),
            @Mapping(target = "fluxReportIdentifiers", expression = "java(BaseMapper.mapToFluxReportIdentifierEntities(fluxReportDocument.getIDS(), fluxReportDocumentEntity))"),
    })
    FluxReportDocumentEntity mapToFluxReportDocumentEntity(FLUXReportDocument fluxReportDocument);

    @Mappings({
            @Mapping(target = "fluxReportIdentifierId", source = "value"),
            @Mapping(target = "fluxReportIdentifierSchemeId", source = "schemeID")
    })
    FluxReportIdentifierEntity mapToFluxReportIdentifierEntity(IDType idType);

    @Mappings({
            @Mapping(target = "fluxReportId", source = "fluxReportIdentifierId"),
            @Mapping(target = "fluxReportSchemeId", source = "fluxReportIdentifierSchemeId")
    })
    FluxReportIdentifierDTO mapToFluxReportIdentifierDTO(FluxReportIdentifierEntity identifierEntity);

}