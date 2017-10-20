/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FlapDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselPositionEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.uvms.common.utils.GeometryUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLAPDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselPositionEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(uses = {FaReportDocumentMapper.class, VesselIdentifierMapper.class, ContactPartyMapper.class, FlapDocumentMapper.class,VesselStorageCharacteristicsMapper.class})
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
            @Mapping(target = "vesselPositionEvents", expression = "java(getVesselPositionEventEntities(vesselTransportMeans.getSpecifiedVesselPositionEvents(),vesselTransportMeansEntity))"),
            @Mapping(target = "faReportDocument", expression = "java(faReportDocumentEntity)"),
    })
    public abstract VesselTransportMeansEntity mapToVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans,FaReportDocumentEntity faReportDocumentEntity);


    @Mappings({
            @Mapping(target = "vesselIdentifierId", source = "value"),
            @Mapping(target = "vesselIdentifierSchemeId", source = "schemeID")
    })
    protected abstract VesselIdentifierEntity mapToVesselIdentifierEntity(IDType idType);


   @Mappings({
           @Mapping(target = "contactPartyDetailsDTOSet", source = "contactParty")
   })
    public abstract VesselDetailsDTO map(VesselTransportMeansEntity entity);

    public abstract List<VesselDetailsDTO> map(Set<VesselTransportMeansEntity> entity);

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

    protected Set<VesselPositionEventEntity> getVesselPositionEventEntities(List<VesselPositionEvent> specifiedVesselPositionEvents, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (specifiedVesselPositionEvents == null || specifiedVesselPositionEvents.isEmpty()) {
            return Collections.emptySet();
        }
        Set<VesselPositionEventEntity> vesselPositionEventEntities = new HashSet<>();
        for (VesselPositionEvent vesselPositionEvent : specifiedVesselPositionEvents) {
            VesselPositionEventEntity entity = VesselPositionEventMapper.INSTANCE.mapToVesselPositionEventEntity(vesselPositionEvent,vesselTransportMeansEntity);
            Geometry point = GeometryUtils.createPoint(entity.getLongitude(), entity.getLatitude());
            entity.setGeom(point);
            vesselPositionEventEntities.add(entity);
        }
        return vesselPositionEventEntities;
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
            ContactPartyEntity contactPartyEntity = ContactPartyMapper.INSTANCE.mapToContactPartyEntity(contactParty,vesselTransportMeansEntity);
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