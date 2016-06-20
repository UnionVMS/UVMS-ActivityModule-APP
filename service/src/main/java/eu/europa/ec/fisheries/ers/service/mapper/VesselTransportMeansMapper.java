package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.*;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.TextType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper(uses = {FaReportDocumentMapper.class})
public abstract class VesselTransportMeansMapper extends BaseMapper {

    public static VesselTransportMeansMapper INSTANCE = Mappers.getMapper(VesselTransportMeansMapper.class);

    @Mappings({
            @Mapping(target = "roleCode", expression = "java(getCodeType(vesselTransportMeans.getRoleCode()))"),
            @Mapping(target = "roleCodeListId", expression = "java(getCodeTypeListId(vesselTransportMeans.getRoleCode()))"),
            @Mapping(target = "name", expression = "java(getTextType(vesselTransportMeans.getNames()))"),
            @Mapping(target = "flapDocumentId", expression = "java(getFlapDocumentId(vesselTransportMeans.getGrantedFLAPDocuments()))"),
            @Mapping(target = "flapDocumentSchemeId", expression = "java(getFlapDocumentschemaId(vesselTransportMeans.getGrantedFLAPDocuments()))"),
            @Mapping(target = "vesselIdentifiers", expression = "java(mapToVesselIdentifierEntities(vesselTransportMeans.getIDS(), vesselTransportMeansEntity))"),
            @Mapping(target = "contactParty", expression = "java(getContactPartyEntity(vesselTransportMeans.getSpecifiedContactParties(), vesselTransportMeansEntity))"),
            @Mapping(target = "registrationEvent", expression = "java(getRegistrationEventEntity(vesselTransportMeans.getSpecifiedRegistrationEvents(), vesselTransportMeansEntity))"),
            @Mapping(target = "faReportDocument", expression = "java(faReportDocumentEntity)")
    })
    public abstract VesselTransportMeansEntity mapToVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans, FaReportDocumentEntity faReportDocumentEntity, @MappingTarget VesselTransportMeansEntity vesselTransportMeansEntity);

    @Mappings({
            @Mapping(target = "vesselIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "vesselIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    public abstract VesselIdentifierEntity mapToVesselIdentifierEntity(IDType idType);

    protected Set<ContactPartyEntity> getContactPartyEntity(List<ContactParty> contactParties, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (contactParties == null || contactParties.isEmpty()) {
            return null;
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
            return null;
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
