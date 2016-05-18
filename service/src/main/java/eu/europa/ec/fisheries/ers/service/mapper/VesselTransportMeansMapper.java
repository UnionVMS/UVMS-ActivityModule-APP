package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLAPDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.TextType;

import java.util.List;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class VesselTransportMeansMapper {

    @Mappings({
            @Mapping(target = "roleCode", expression = "java(vesselTransportMeans.getRoleCode().getValue())"),
            @Mapping(target = "roleCodeListId", expression = "java(vesselTransportMeans.getRoleCode().getListID())"),
            @Mapping(target = "name", expression = "java(getTextType(vesselTransportMeans.getNames()))"),
            @Mapping(target = "flapDocumentId", expression = "java(getFlapDocumentId(vesselTransportMeans.getGrantedFLAPDocuments()))"),
            @Mapping(target = "flapDocumentSchemeId", expression = "java(getFlapDocumentschemaId(vesselTransportMeans.getGrantedFLAPDocuments()))")
    })
    public abstract VesselTransportMeansEntity mapToVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans);

    public abstract List<VesselIdentifierEntity> mapToVesselIdentifierEntities(List<IDType> idTypes);

    @Mappings({
            @Mapping(target = "vesselIdentifierId", expression = "java(idType.getValue())"),
            @Mapping(target = "vesselIdentifierSchemeId", expression = "java(idType.getSchemeID())")
    })
    public abstract VesselIdentifierEntity mapToVesselIdentifierEntity(IDType idType);


    protected String getTextType(List<TextType> textTypes) {
        return (textTypes == null || textTypes.isEmpty()) ? null : textTypes.get(0).getValue();
    }

    protected String getFlapDocumentId(List<FLAPDocument> flapDocuments) {
        if (flapDocuments == null || flapDocuments.isEmpty()) {
            return null;
        }
        return flapDocuments.get(0).getID().getValue();
    }

    protected String getFlapDocumentschemaId(List<FLAPDocument> flapDocuments) {
        if (flapDocuments == null || flapDocuments.isEmpty()) {
            return null;
        }
        return flapDocuments.get(0).getID().getSchemeID();
    }
}
