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
public abstract class FaReportDocumentMapper extends BaseMapper {

    public static FaReportDocumentMapper INSTANCE = Mappers.getMapper(FaReportDocumentMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(faReportDocument.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(faReportDocument.getTypeCode()))"),
            @Mapping(target = "acceptedDatetime", expression = "java(convertToDate(faReportDocument.getAcceptanceDateTime()))"),
            @Mapping(target = "fmcMarker", expression = "java(getCodeType(faReportDocument.getFMCMarkerCode()))"),
            @Mapping(target = "fmcMarkerListId", expression = "java(getCodeTypeListId(faReportDocument.getFMCMarkerCode()))")
    })
    public abstract FaReportDocumentEntity mapToFAReportDocumentEntity(FAReportDocument faReportDocument);

    public abstract List<FaReportIdentifierEntity> mapToFAReportIdentifierEntities(List<IDType> idTypes);

    @Mappings({
            @Mapping(target = "faReportIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "faReportIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    public abstract FaReportIdentifierEntity mapToFAReportIdentifierEntity(IDType idType);
}
