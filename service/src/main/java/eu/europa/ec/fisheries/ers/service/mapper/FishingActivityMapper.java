package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityIdentifierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLAPDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingActivity;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class FishingActivityMapper extends BaseMapper {

    public static FishingActivityMapper INSTANCE = Mappers.getMapper(FishingActivityMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fishingActivity.getTypeCode()))"),
            @Mapping(target = "typeCodeListid", expression = "java(getCodeTypeListId(fishingActivity.getTypeCode()))"),
            @Mapping(target = "occurence", expression = "java(convertToDate(fishingActivity.getOccurrenceDateTime()))"),
            @Mapping(target = "reasonCode", expression = "java(getCodeType(fishingActivity.getReasonCode()))"),
            @Mapping(target = "reasonCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getReasonCode()))"),
            @Mapping(target = "vesselActivityCode", expression = "java(getCodeType(fishingActivity.getVesselRelatedActivityCode()))"),
            @Mapping(target = "vesselActivityCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getVesselRelatedActivityCode()))"),
            @Mapping(target = "fisheryTypeCode", expression = "java(getCodeType(fishingActivity.getFisheryTypeCode()))"),
            @Mapping(target = "fisheryTypeCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getFisheryTypeCode()))"),
            @Mapping(target = "speciesTargetCode", expression = "java(getCodeType(fishingActivity.getSpeciesTargetCode()))"),
            @Mapping(target = "speciesTargetCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getSpeciesTargetCode()))"),
            @Mapping(target = "operationQuantity", expression = "java(getQuantityInLong(fishingActivity.getOperationsQuantity()))"),
            @Mapping(target = "fishingDurationMeasure", expression = "java(getMeasure(fishingActivity.getFishingDurationMeasure()))"),
            @Mapping(target = "flapDocumentId", expression = "java(getFlapDocId(fishingActivity.getSpecifiedFLAPDocument()))"),
            @Mapping(target = "flapDocumentSchemeId", expression = "java(getFlapDocSchemeId(fishingActivity.getSpecifiedFLAPDocument()))")
    })
    public abstract FishingActivityEntity mapToFishingActivityEntity(FishingActivity fishingActivity);

    public abstract List<FishingActivityIdentifierEntity> mapToFishingActivityIdentifierEntities(List<IDType> idTypes);

    @Mappings({
            @Mapping(target = "faIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "faIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    public abstract FishingActivityIdentifierEntity mapToFishingActivityIdentifierEntity(IDType idType);

    protected String getFlapDocId(FLAPDocument flapDocument) {
        if (flapDocument == null) {
            return null;
        }
        return (flapDocument.getID() == null) ? null : getIdType(flapDocument.getID());
    }

    protected String getFlapDocSchemeId(FLAPDocument flapDocument) {
        if (flapDocument == null) {
            return null;
        }
        return (flapDocument.getID() == null) ? null : getIdTypeSchemaId(flapDocument.getID());
    }
}
