package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingActivity;
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
            @Mapping(target = "faReportIdentifiers", expression = "java(mapToFAReportIdentifierEntities(faReportDocument.getRelatedReportIDs(), faReportDocumentEntity))"),
            @Mapping(target = "fishingActivities", expression = "java(getFishingActivityEntities(faReportDocument.getSpecifiedFishingActivities(),faReportDocumentEntity))")
    })
    public abstract FaReportDocumentEntity mapToFAReportDocumentEntity(FAReportDocument faReportDocument, @MappingTarget FaReportDocumentEntity faReportDocumentEntity);

    @Mappings({
            @Mapping(target = "faReportIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "faReportIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    protected abstract FaReportIdentifierEntity mapToFAReportIdentifierEntity(IDType idType);

    protected Set<FishingActivityEntity> getFishingActivityEntities(List<FishingActivity> fishingActivities, FaReportDocumentEntity faReportDocumentEntity) {
        if (fishingActivities == null || fishingActivities.isEmpty()) {
            return null;
        }
        Set<FishingActivityEntity> fishingActivityEntities =  new HashSet<>();
        for (FishingActivity fishingActivity : fishingActivities) {
            FishingActivityEntity fishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(fishingActivity, faReportDocumentEntity, new FishingActivityEntity());
            fishingActivityEntities.add(fishingActivityEntity);
        }
        return fishingActivityEntities;
    }

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
