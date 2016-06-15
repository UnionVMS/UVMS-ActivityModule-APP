package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.TextType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 5/13/2016.
 */
@Mapper
public abstract class FluxReportDocumentMapper extends BaseMapper {

    public static FluxReportDocumentMapper INSTANCE = Mappers.getMapper(FluxReportDocumentMapper.class);

    @Mappings({
            @Mapping(target = "fluxReportDocumentId", expression = "java(getIdTypeFromList(fluxReportDocument.getIDS()))"),
            @Mapping(target = "referenceId", expression = "java(getIdType(fluxReportDocument.getReferencedID()))"),
            @Mapping(target = "creationDatetime", expression = "java(convertToDate(fluxReportDocument.getCreationDateTime()))"),
            @Mapping(target = "purposeCode", expression = "java(getCodeType(fluxReportDocument.getPurposeCode()))"),
            @Mapping(target = "purposeCodeListId", expression = "java(getCodeTypeListId(fluxReportDocument.getPurposeCode()))"),
            @Mapping(target = "purpose", expression = "java(getTextType(fluxReportDocument.getPurpose()))"),
            @Mapping(target = "ownerFluxPartyId", expression = "java(getFluxPartyId(fluxReportDocument.getOwnerFLUXParty()))"),
            @Mapping(target = "ownerFluxPartyName", expression = "java(getFluxPartyName(fluxReportDocument.getOwnerFLUXParty()))")
    })
    public abstract FluxReportDocumentEntity mapToFluxReportDocumentEntity(FLUXReportDocument fluxReportDocument);

    protected String getFluxPartyId(FLUXParty fluxParty) {
        if(fluxParty == null) {
            return null;
        }
        return getIdTypeFromList(fluxParty.getIDS());
    }

    protected String getFluxPartyName(FLUXParty fluxParty) {
        if(fluxParty == null) {
            return null;
        }
        return (fluxParty.getNames() == null || fluxParty.getNames().isEmpty() ? null : fluxParty.getNames().get(0).getValue());
    }

}



