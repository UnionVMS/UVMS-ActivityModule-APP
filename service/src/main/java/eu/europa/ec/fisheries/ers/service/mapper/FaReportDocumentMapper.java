package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 5/13/2016.
 */
@Mapper
public abstract class FaReportDocumentMapper extends BaseMapper {

    public static FaReportDocumentMapper INSTANCE = Mappers.getMapper(FaReportDocumentMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(faReportDocument.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(faReportDocument.getTypeCode()))"),
            @Mapping(target = "acceptedDatetime", expression = "java(convertToDate(faReportDocument.getAcceptanceDateTime()))"),
            @Mapping(target = "fmcMarker", expression = "java(getCodeType(faReportDocument.getFMCMarkerCode()))"),
            @Mapping(target = "fmcMarkerListId", expression = "java(getCodeTypeListId(faReportDocument.getFMCMarkerCode()))"),
            @Mapping(target = "vesselTransportMeans", expression = "java(getVesselTransportMeansEntity(faReportDocument.getSpecifiedVesselTransportMeans(), faReportDocumentEntity))"),
            @Mapping(target = "fluxReportDocument", expression = "java(getFluxReportDocument(faReportDocument.getRelatedFLUXReportDocument(), faReportDocumentEntity))"),
            @Mapping(target = "faReportIdentifiers", expression = "java(mapToFAReportIdentifierEntities(faReportDocument.getRelatedReportIDs(), faReportDocumentEntity))")
    })
    public abstract FaReportDocumentEntity mapToFAReportDocumentEntity(FAReportDocument faReportDocument, @MappingTarget FaReportDocumentEntity faReportDocumentEntity);

    @Mappings({
            @Mapping(target = "faReportIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "faReportIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    public abstract FaReportIdentifierEntity mapToFAReportIdentifierEntity(IDType idType);

    protected VesselTransportMeansEntity getVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans, FaReportDocumentEntity faReportDocumentEntity) {
        return VesselTransportMeansMapper.INSTANCE.mapToVesselTransportMeansEntity(vesselTransportMeans, faReportDocumentEntity, new VesselTransportMeansEntity());
    }

    protected FluxReportDocumentEntity getFluxReportDocument(FLUXReportDocument fluxReportDocument, FaReportDocumentEntity faReportDocumentEntity) {
        return FluxReportDocumentMapper.INSTANCE.mapToFluxReportDocumentEntity(fluxReportDocument, faReportDocumentEntity, new FluxReportDocumentEntity());
    }

    protected Set<FaReportIdentifierEntity> mapToFAReportIdentifierEntities(List<IDType> idTypes, FaReportDocumentEntity faReportDocumentEntity) {
        if (idTypes == null || idTypes.isEmpty()) {
            return null;
        }
        Set<FaReportIdentifierEntity> faReportIdentifierEntities = new HashSet<>();
        for (IDType idType : idTypes) {
            FaReportIdentifierEntity faReportIdentifierEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportIdentifierEntity(idType);
            faReportIdentifierEntity.setFaReportDocument(faReportDocumentEntity);
            faReportIdentifierEntities.add(faReportIdentifierEntity);
        }
        return faReportIdentifierEntities;
    }
}
