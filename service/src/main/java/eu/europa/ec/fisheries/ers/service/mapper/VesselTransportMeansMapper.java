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
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselPositionEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(uses = {FaReportDocumentMapper.class, VesselIdentifierMapper.class, ContactPartyMapper.class, FlapDocumentMapper.class,VesselStorageCharacteristicsMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class VesselTransportMeansMapper extends BaseMapper {

    public static final VesselTransportMeansMapper INSTANCE = Mappers.getMapper(VesselTransportMeansMapper.class);

    @Mappings({
            @Mapping(target = "roleCode", source = "roleCode.value"),
            @Mapping(target = "roleCodeListId", source = "roleCode.listID"),
            @Mapping(target = "name", expression = "java(getTextFromList(vesselTransportMeans.getNames()))"),
            @Mapping(target = "country", source = "registrationVesselCountry.ID.value"),
            @Mapping(target = "countrySchemeId", source = "registrationVesselCountry.ID.schemeID"),
            @Mapping(target = "vesselIdentifiers", expression = "java(mapToVesselIdentifierEntities(vesselTransportMeans.getIDS(), vesselTransportMeansEntity))"),
            @Mapping(target = "contactParty", expression = "java(getContactPartyEntity(vesselTransportMeans.getSpecifiedContactParties(), vesselTransportMeansEntity))"),
            @Mapping(target = "registrationEvent", expression = "java(getRegistrationEventEntity(vesselTransportMeans.getSpecifiedRegistrationEvents(), vesselTransportMeansEntity))"),
            @Mapping(target = "vesselPositionEvents", expression = "java(getVesselPositionEventEntities(vesselTransportMeans.getSpecifiedVesselPositionEvents(),vesselTransportMeansEntity))"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "guid", ignore = true),
            @Mapping(target = "fishingActivity", ignore = true),
            @Mapping(target = "faReportDocument", ignore = true),
            @Mapping(target = "flapDocuments", ignore = true),
            @Mapping(target = "vesselIdentifiersMap", ignore = true)
    })
    public abstract VesselTransportMeansEntity mapToVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans);


    @Mappings({
            @Mapping(target = "vesselIdentifierId", source = "value"),
            @Mapping(target = "vesselIdentifierSchemeId", source = "schemeID"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "vesselTransportMeans", ignore = true)
    })
    protected abstract VesselIdentifierEntity mapToVesselIdentifierEntity(IDType idType);

    @Mappings({
           @Mapping(target = "contactPartyDetailsDTOSet", source = "contactParty"),
            @Mapping(target = "storageDto", ignore = true)
    })
    public abstract VesselDetailsDTO map(VesselTransportMeansEntity entity);

    public abstract List<VesselDetailsDTO> map(Set<VesselTransportMeansEntity> entity);

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

}