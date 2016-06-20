package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationLocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.RegistrationLocation;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class RegistrationLocationMapper extends BaseMapper {

    public static RegistrationLocationMapper INSTANCE = Mappers.getMapper(RegistrationLocationMapper.class);

    @Mappings({
            @Mapping(target = "description", expression = "java(getTextFromList(registrationLocation.getDescriptions()))"),
            @Mapping(target = "regionCode", expression = "java(getCodeType(registrationLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "regionCodeListId", expression = "java(getCodeTypeListId(registrationLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "name", expression = "java(getTextFromList(registrationLocation.getNames()))"),
            @Mapping(target = "typeCode", expression = "java(getCodeType(registrationLocation.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(registrationLocation.getTypeCode()))"),
            @Mapping(target = "locationCountryId", expression = "java(getIdType(registrationLocation.getCountryID()))"),
            @Mapping(target = "locationCountrySchemeId", expression = "java(getIdTypeSchemaId(registrationLocation.getCountryID()))"),
            @Mapping(target = "registrationEvent", expression = "java(registrationEventEntity)")
    })
    public abstract RegistrationLocationEntity mapToRegistrationLocationEntity(RegistrationLocation registrationLocation, RegistrationEventEntity registrationEventEntity, @MappingTarget RegistrationLocationEntity registrationLocationEntity);
}
