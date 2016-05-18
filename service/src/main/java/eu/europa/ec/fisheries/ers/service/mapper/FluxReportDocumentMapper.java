package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
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
public abstract class FluxReportDocumentMapper {

    @Mappings({
            @Mapping(target = "fluxReportDocumentId", expression = "java(getId(fluxReportDocument.getIDS()))"),
            @Mapping(target = "referenceId", expression = "java(fluxReportDocument.getReferencedID().getValue())"),
            @Mapping(target = "creationDatetime", expression = "java(convertToDate(fluxReportDocument.getCreationDateTime().getDateTime()))"),
            @Mapping(target = "purposeCode", expression = "java(fluxReportDocument.getPurposeCode().getValue())"),
            @Mapping(target = "purposeCodeListId", expression = "java(fluxReportDocument.getPurposeCode().getListID())"),
            @Mapping(target = "purpose", expression = "java(fluxReportDocument.getPurpose().getValue())"),
            @Mapping(target = "ownerFluxPartyId", expression = "java(getId(fluxReportDocument.getOwnerFLUXParty().getIDS()))"),
            @Mapping(target = "ownerFluxPartyName", expression = "java(getName(fluxReportDocument.getOwnerFLUXParty().getNames()))")
    })
    public abstract FluxReportDocumentEntity mapToFluxReportDocumentEntity(FLUXReportDocument fluxReportDocument);

    protected String getId(List<IDType> ids) {
        return (ids == null || ids.isEmpty()) ? null : ids.get(0).getValue();
    }

    protected String getName(List<TextType> names) {
        return (names == null || names.isEmpty()) ? null : names.get(0).getValue();
    }

    protected Date convertToDate(XMLGregorianCalendar dateTime) {
        return dateTime == null ? null : dateTime.toGregorianCalendar().getTime();
    }
}



