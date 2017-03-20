/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FlapDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLAPDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@Mapper(uses = {FaReportDocumentMapper.class, ContactPartyMapper.class, VesselIdentifierMapper.class})
public abstract class VesselTransportMeansMapper extends BaseMapper {

    public static final VesselTransportMeansMapper INSTANCE = Mappers.getMapper(VesselTransportMeansMapper.class);

    @Mappings({
            @Mapping(target = "roleCode", source = "vesselTransportMeans.roleCode.value"),
            @Mapping(target = "roleCodeListId", source = "vesselTransportMeans.roleCode.listID"),
            @Mapping(target = "name", expression = "java(getTextFromList(vesselTransportMeans.getNames()))"),
            @Mapping(target = "flapDocuments", expression = "java(getFlapDocumentEntities(vesselTransportMeans.getGrantedFLAPDocuments(), vesselTransportMeansEntity))"),
            @Mapping(target = "country", source = "vesselTransportMeans.registrationVesselCountry.ID.value"),
            @Mapping(target = "countrySchemeId", source = "vesselTransportMeans.registrationVesselCountry.ID.schemeID"),
            @Mapping(target = "vesselIdentifiers", expression = "java(mapToVesselIdentifierEntities(vesselTransportMeans.getIDS(), vesselTransportMeansEntity))"),
            @Mapping(target = "contactParty", expression = "java(getContactPartyEntity(vesselTransportMeans.getSpecifiedContactParties(), vesselTransportMeansEntity))"),
            @Mapping(target = "registrationEvent", expression = "java(getRegistrationEventEntity(vesselTransportMeans.getSpecifiedRegistrationEvents(), vesselTransportMeansEntity))"),
            @Mapping(target = "faReportDocument", expression = "java(faReportDocumentEntity)")
    })
    public abstract VesselTransportMeansEntity mapToVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans, FaReportDocumentEntity faReportDocumentEntity, @MappingTarget VesselTransportMeansEntity vesselTransportMeansEntity);

    @Mappings({
            @Mapping(target = "vesselIdentifierId", source = "value"),
            @Mapping(target = "vesselIdentifierSchemeId", source = "schemeID")
    })
    protected abstract VesselIdentifierEntity mapToVesselIdentifierEntity(IDType idType);

    public abstract VesselDetailsDTO map(VesselTransportMeansEntity entity);

    protected Set<FlapDocumentEntity> getFlapDocumentEntities(List<FLAPDocument> flapDocuments, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (flapDocuments == null || flapDocuments.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FlapDocumentEntity> flapDocumentEntities = new HashSet<>();
        for (FLAPDocument flapDocument : flapDocuments) {
            FlapDocumentEntity entity = FlapDocumentMapper.INSTANCE.mapToFlapDocumentEntity(flapDocument, vesselTransportMeansEntity, new FlapDocumentEntity());
            flapDocumentEntities.add(entity);
        }
        return flapDocumentEntities;
    }

    protected Set<ContactPartyEntity> getContactPartyEntity(List<ContactParty> contactParties, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (contactParties == null || contactParties.isEmpty()) {
            return Collections.emptySet();
        }
        Set<ContactPartyEntity> contactPartyEntities = new HashSet<>();
        for (ContactParty contactParty : contactParties) {
            ContactPartyEntity contactPartyEntity = ContactPartyMapper.INSTANCE.mapToContactPartyEntity(contactParty);
            contactPartyEntity.setVesselTransportMeans(vesselTransportMeansEntity);
            contactPartyEntities.add(contactPartyEntity);
        }
        return contactPartyEntities;
    }

    protected RegistrationEventEntity getRegistrationEventEntity(List<RegistrationEvent> registrationEvents, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (registrationEvents == null || registrationEvents.isEmpty()) {
            return null;
        }
        RegistrationEventEntity registrationEventEntity = RegistrationEventMapper.INSTANCE.mapToRegistrationEventEntity(registrationEvents.get(0));
        registrationEventEntity.setVesselTransportMeanses(vesselTransportMeansEntity);
        return registrationEventEntity;
    }

    protected Set<VesselIdentifierEntity> mapToVesselIdentifierEntities(List<IDType> idTypes, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (idTypes == null || idTypes.isEmpty()) {
            return Collections.emptySet();
        }
        Set<VesselIdentifierEntity> vesselIdentifierEntities = new HashSet<>();
        for (IDType idType : idTypes) {
            VesselIdentifierEntity vesselIdentifierEntity = VesselTransportMeansMapper.INSTANCE.mapToVesselIdentifierEntity(idType);
            vesselIdentifierEntity.setVesselTransportMeans(vesselTransportMeansEntity);
            vesselIdentifierEntities.add(vesselIdentifierEntity);
        }
        return vesselIdentifierEntities;
    }

}