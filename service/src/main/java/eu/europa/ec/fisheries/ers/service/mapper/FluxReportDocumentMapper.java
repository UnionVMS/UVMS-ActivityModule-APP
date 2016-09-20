/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FluxReportIdentifierDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 5/13/2016.
 */
@Mapper
public abstract class FluxReportDocumentMapper extends BaseMapper {

    public static final FluxReportDocumentMapper INSTANCE = Mappers.getMapper(FluxReportDocumentMapper.class);

    @Mappings({
            @Mapping(target = "referenceId", expression = "java(getIdType(fluxReportDocument.getReferencedID()))"),
            @Mapping(target = "creationDatetime", expression = "java(convertToDate(fluxReportDocument.getCreationDateTime()))"),
            @Mapping(target = "purposeCode", expression = "java(getCodeType(fluxReportDocument.getPurposeCode()))"),
            @Mapping(target = "purposeCodeListId", expression = "java(getCodeTypeListId(fluxReportDocument.getPurposeCode()))"),
            @Mapping(target = "purpose", expression = "java(getTextType(fluxReportDocument.getPurpose()))"),
            @Mapping(target = "fluxParty", expression = "java(getFluxPartyEntity(fluxReportDocument.getOwnerFLUXParty(), fluxReportDocumentEntity))"),
            @Mapping(target = "fluxReportIdentifiers", expression = "java(mapToFluxReportIdentifierEntities(fluxReportDocument.getIDS(), fluxReportDocumentEntity))"),
            @Mapping(target = "faReportDocument", expression = "java(faReportDocumentEntity)")
    })
    public abstract FluxReportDocumentEntity mapToFluxReportDocumentEntity(FLUXReportDocument fluxReportDocument, FaReportDocumentEntity faReportDocumentEntity, @MappingTarget FluxReportDocumentEntity fluxReportDocumentEntity);

    @Mappings({
            @Mapping(target = "fluxReportIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "fluxReportIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    protected abstract FluxReportIdentifierEntity mapToFluxReportIdentifierEntity(IDType idType);

    @Mappings({
            @Mapping(target = "fluxReportId", source = "fluxReportIdentifierId"),
            @Mapping(target = "fluxReportSchemeId", source = "fluxReportIdentifierSchemeId")
    })
    public abstract FluxReportIdentifierDTO mapToFluxReportIdentifierDTO(FluxReportIdentifierEntity identifierEntity);

    protected FluxPartyEntity getFluxPartyEntity(FLUXParty fluxParty, FluxReportDocumentEntity fluxReportDocumentEntity) {
        if (fluxParty == null) {
            return null;
        }
        return FluxPartyMapper.INSTANCE.mapToFluxPartyEntity(fluxParty, fluxReportDocumentEntity, new FluxPartyEntity());
    }

    protected Set<FluxReportIdentifierEntity> mapToFluxReportIdentifierEntities(List<IDType> idTypes, FluxReportDocumentEntity fluxReportDocumentEntity) {
        if (idTypes == null || idTypes.isEmpty()) {
            Collections.emptySet();
        }
        Set<FluxReportIdentifierEntity> identifiers = new HashSet<>();
        for (IDType idType : idTypes) {
            FluxReportIdentifierEntity entity = FluxReportDocumentMapper.INSTANCE.mapToFluxReportIdentifierEntity(idType);
            entity.setFluxReportDocument(fluxReportDocumentEntity);
            identifiers.add(entity);
        }
        return identifiers;
    }
}