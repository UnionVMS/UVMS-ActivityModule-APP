package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityIdentifierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingActivity;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class FishingActivityMapper {

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(fishingActivity.getTypeCode().getValue())"),
            @Mapping(target = "typeCodeListid", expression = "java(fishingActivity.getTypeCode().getListID())"),
            @Mapping(target = "occurence", expression = "java(convertToDate(fishingActivity.getOccurrenceDateTime().getDateTime()))"),
            @Mapping(target = "reasonCode", expression = "java(fishingActivity.getReasonCode().getValue())"),
            @Mapping(target = "reasonCodeListId", expression = "java(fishingActivity.getReasonCode().getListID())"),
            @Mapping(target = "vesselActivityCode", expression = "java(fishingActivity.getVesselRelatedActivityCode().getValue())"),
            @Mapping(target = "vesselActivityCodeListId", expression = "java(fishingActivity.getVesselRelatedActivityCode().getListID())"),
            @Mapping(target = "fisheryTypeCode", expression = "java(fishingActivity.getFisheryTypeCode().getValue())"),
            @Mapping(target = "fisheryTypeCodeListId", expression = "java(fishingActivity.getFisheryTypeCode().getListID())"),
            @Mapping(target = "speciesTargetCode", expression = "java(fishingActivity.getSpeciesTargetCode().getValue())"),
            @Mapping(target = "speciesTargetCodeListId", expression = "java(fishingActivity.getSpeciesTargetCode().getListID())"),
            @Mapping(target = "operationQuantity", expression = "java(fishingActivity.getOperationsQuantity().getValue().longValue())"),
            @Mapping(target = "fishingDurationMeasure", expression = "java(fishingActivity.getFishingDurationMeasure().getValue().doubleValue())"),
            @Mapping(target = "flapDocumentId", expression = "java(fishingActivity.getSpecifiedFLAPDocument().getID().getValue())"),
            @Mapping(target = "flapDocumentSchemaId", expression = "java(fishingActivity.getSpecifiedFLAPDocument().getID().getSchemeID())")
    })
    public abstract FishingActivityEntity mapToFishingActivityEntity(FishingActivity fishingActivity);

    public abstract List<FishingActivityIdentifierEntity> mapToFishingActivityEntities(List<IDType> idTypes);

    @Mappings({
            @Mapping(target = "faIdentifierId", expression = "java(idType.getValue())"),
            @Mapping(target = "faIdentifierSchemeId", expression = "java(idType.getSchemeID())")
    })
    public abstract FishingActivityIdentifierEntity mapToFishingActivityEntity(IDType idType);

    protected Date convertToDate(XMLGregorianCalendar dateTime) {
        return dateTime == null ? null : dateTime.toGregorianCalendar().getTime();
    }

}
