package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLAPDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.TextType;

import java.util.List;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class VesselTransportMeansMapper extends BaseMapper {

    public static VesselTransportMeansMapper INSTANCE = Mappers.getMapper(VesselTransportMeansMapper.class);

    @Mappings({
            @Mapping(target = "roleCode", expression = "java(getCodeType(vesselTransportMeans.getRoleCode()))"),
            @Mapping(target = "roleCodeListId", expression = "java(getCodeTypeListId(vesselTransportMeans.getRoleCode()))"),
            @Mapping(target = "name", expression = "java(getTextType(vesselTransportMeans.getNames()))"),
            @Mapping(target = "flapDocumentId", expression = "java(getFlapDocumentId(vesselTransportMeans.getGrantedFLAPDocuments()))"),
            @Mapping(target = "flapDocumentSchemeId", expression = "java(getFlapDocumentschemaId(vesselTransportMeans.getGrantedFLAPDocuments()))")
    })
    public abstract VesselTransportMeansEntity mapToVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans);

    public abstract List<VesselIdentifierEntity> mapToVesselIdentifierEntities(List<IDType> idTypes);

    @Mappings({
            @Mapping(target = "vesselIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "vesselIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    public abstract VesselIdentifierEntity mapToVesselIdentifierEntity(IDType idType);


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
