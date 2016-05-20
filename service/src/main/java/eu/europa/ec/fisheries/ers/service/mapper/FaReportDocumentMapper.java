package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportIdentifierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 5/13/2016.
 */
@Mapper
public abstract class FaReportDocumentMapper {

    public static FaReportDocumentMapper INSTANCE = Mappers.getMapper(FaReportDocumentMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(faReportDocument.getTypeCode().getValue())"),
            @Mapping(target = "typeCodeListId", expression = "java(faReportDocument.getTypeCode().getListID())"),
            @Mapping(target = "acceptedDatetime", expression = "java(convertToDate(faReportDocument.getAcceptanceDateTime().getDateTime()))"),
            @Mapping(target = "fmcMarker", expression = "java(faReportDocument.getFMCMarkerCode().getValue())"),
            @Mapping(target = "fmcMarkerListId", expression = "java(faReportDocument.getFMCMarkerCode().getListID())")
    })
    public abstract FaReportDocumentEntity mapToFAReportDocumentEntity(FAReportDocument faReportDocument);

    public abstract List<FaReportIdentifierEntity> mapToFAReportIdentifierEntities(List<IDType> idTypes);

    @Mappings({
            @Mapping(target = "faReportIdentifierId", expression = "java(idType.getValue())"),
            @Mapping(target = "faReportIdentifierSchemeId", expression = "java(idType.getSchemeID())")
    })
    public abstract FaReportIdentifierEntity mapToFAReportIdentifierEntity(IDType idType);

    protected String getId(List<IDType> ids) {
        return (ids == null || ids.isEmpty()) ? null : ids.get(0).getValue();
    }

    protected Date convertToDate(XMLGregorianCalendar dateTime) {
        return dateTime == null ? null : dateTime.toGregorianCalendar().getTime();
    }
}
