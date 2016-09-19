/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.VesselDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.*;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.TextType;

import java.util.*;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper(uses = {FaReportDocumentMapper.class, ContactPartyMapper.class})
public abstract class VesselTransportMeansMapper extends BaseMapper {

    public static final VesselTransportMeansMapper INSTANCE = Mappers.getMapper(VesselTransportMeansMapper.class);

    @Mappings({
            @Mapping(target = "roleCode", expression = "java(getCodeType(vesselTransportMeans.getRoleCode()))"),
            @Mapping(target = "roleCodeListId", expression = "java(getCodeTypeListId(vesselTransportMeans.getRoleCode()))"),
            @Mapping(target = "name", expression = "java(getTextType(vesselTransportMeans.getNames()))"),
            @Mapping(target = "flapDocuments", expression = "java(getFlapDocumentEntities(vesselTransportMeans.getGrantedFLAPDocuments(), vesselTransportMeansEntity))"),
            @Mapping(target = "country", expression = "java(getCountry(vesselTransportMeans.getRegistrationVesselCountry()))"),
            @Mapping(target = "countrySchemeId", expression = "java(getCountrySchemeId(vesselTransportMeans.getRegistrationVesselCountry()))"),
            @Mapping(target = "vesselIdentifiers", expression = "java(mapToVesselIdentifierEntities(vesselTransportMeans.getIDS(), vesselTransportMeansEntity))"),
            @Mapping(target = "contactParty", expression = "java(getContactPartyEntity(vesselTransportMeans.getSpecifiedContactParties(), vesselTransportMeansEntity))"),
            @Mapping(target = "registrationEvent", expression = "java(getRegistrationEventEntity(vesselTransportMeans.getSpecifiedRegistrationEvents(), vesselTransportMeansEntity))"),
            @Mapping(target = "faReportDocument", expression = "java(faReportDocumentEntity)")
    })
    public abstract VesselTransportMeansEntity mapToVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans, FaReportDocumentEntity faReportDocumentEntity, @MappingTarget VesselTransportMeansEntity vesselTransportMeansEntity);

    @Mappings({
            @Mapping(target = "vesselRole", source = "roleCode"),
            @Mapping(target = "vesselIds", expression = "java(getVesselIds(vesselTransportMeansEntity.getVesselIdentifiers()))"),
            @Mapping(target = "vesselName", source = "name"),
            @Mapping(target = "registrationDateTime", source = "registrationEvent.occurrenceDatetime"),
            @Mapping(target = "registrationEventDescription", source = "registrationEvent.description"),
            @Mapping(target = "registrationLocationDescription", source = "registrationEvent.registrationLocation.description"),
            @Mapping(target = "registrationRegion", source = "registrationEvent.registrationLocation.regionCode"),
            @Mapping(target = "registrationLocationName", source = "registrationEvent.registrationLocation.name"),
            @Mapping(target = "registrationType", source = "registrationEvent.registrationLocation.typeCode"),
            @Mapping(target = "registrationLocationCountryId", source = "registrationEvent.registrationLocation.locationCountryId"),
            @Mapping(target = "contactPartyDetails", source = "contactParty")
    })
    public abstract VesselDetailsDTO mapToVesselDetailsDTO(VesselTransportMeansEntity vesselTransportMeansEntity);

    @Mappings({
            @Mapping(target = "vesselIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "vesselIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    protected abstract VesselIdentifierEntity mapToVesselIdentifierEntity(IDType idType);

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

    protected List<String> getVesselIds(Set<VesselIdentifierEntity> vesselIdentifierEntities) {
        List<String> ids = new ArrayList<>();
        for (VesselIdentifierEntity vesselIdentifierEntity : vesselIdentifierEntities) {
            ids.add(vesselIdentifierEntity.getVesselIdentifierId());
        }
        return ids;
    }

    protected Set<ContactPartyEntity> getContactPartyEntity(List<ContactParty> contactParties, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (contactParties == null || contactParties.isEmpty()) {
            return Collections.emptySet();
        }
        Set<ContactPartyEntity> contactPartyEntities = new HashSet<>();
        for (ContactParty contactParty : contactParties) {
            ContactPartyEntity contactPartyEntity = ContactPartyMapper.INSTANCE.mapToContactPartyEntity(contactParty, vesselTransportMeansEntity, new ContactPartyEntity());
            contactPartyEntities.add(contactPartyEntity);
        }
        return contactPartyEntities;
    }

    protected RegistrationEventEntity getRegistrationEventEntity(List<RegistrationEvent> registrationEvents, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (registrationEvents == null || registrationEvents.isEmpty()) {
            return null;
        }
        return RegistrationEventMapper.INSTANCE.mapToRegistrationEventEntity(registrationEvents.get(0), vesselTransportMeansEntity, new RegistrationEventEntity());
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

    protected String getTextType(List<TextType> textTypes) {
        return (textTypes == null || textTypes.isEmpty()) ? null : textTypes.get(0).getValue();
    }

    protected String getFlapDocumentId(List<FLAPDocument> flapDocuments) {
        if (flapDocuments == null || flapDocuments.isEmpty()) {
            return null;
        }
        return (flapDocuments.get(0).getID() == null) ? null : flapDocuments.get(0).getID().getValue();
    }

    protected String getFlapDocumentschemaId(List<FLAPDocument> flapDocuments) {
        if (flapDocuments == null || flapDocuments.isEmpty()) {
            return null;
        }
        return (flapDocuments.get(0).getID() == null) ? null : flapDocuments.get(0).getID().getSchemeID();
    }
}