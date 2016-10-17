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

import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 9/16/2016.
 */
@Mapper
public abstract class FluxPartyMapper extends BaseMapper {

    public static final FluxPartyMapper INSTANCE = Mappers.getMapper(FluxPartyMapper.class);

    @Mappings({
            @Mapping(target = "fluxPartyName", expression = "java(getTextFromList(fluxParty.getNames()))"),
            @Mapping(target = "nameLanguageId", expression = "java(getLanguageIdFromList(fluxParty.getNames()))"),
            @Mapping(target = "fluxPartyIdentifiers", expression = "java(mapToFluxPartyIdentifierEntities(fluxParty.getIDS(), fluxPartyEntity))"),
            @Mapping(target = "fluxReportDocument", expression = "java(fluxReportDocumentEntity)")
    })
    public abstract FluxPartyEntity mapToFluxPartyEntity(FLUXParty fluxParty, FluxReportDocumentEntity fluxReportDocumentEntity, @MappingTarget FluxPartyEntity fluxPartyEntity);

    @Mappings({
            @Mapping(target = "fluxPartyIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "fluxPartyIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    public abstract FluxPartyIdentifierEntity mapToFluxPartyIdentifierEntity(IDType idType);

    protected Set<FluxPartyIdentifierEntity> mapToFluxPartyIdentifierEntities(List<IDType> idTypes, FluxPartyEntity fluxPartyEntity) {
        if (idTypes == null || idTypes.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FluxPartyIdentifierEntity> fluxPartyIdentifierEntities = new HashSet<>();
        for (IDType idType : idTypes) {
            FluxPartyIdentifierEntity entity = FluxPartyMapper.INSTANCE.mapToFluxPartyIdentifierEntity(idType);
            entity.setFluxParty(fluxPartyEntity);
            fluxPartyIdentifierEntities.add(entity);
        }
        return fluxPartyIdentifierEntities;
    }
}
