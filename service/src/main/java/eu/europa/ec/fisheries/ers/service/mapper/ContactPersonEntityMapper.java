package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPersonEntity;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.ContactPerson;

/**
 * Created by padhyad on 6/8/2016.
 */
public abstract class ContactPersonEntityMapper {

    public static ContactPersonEntityMapper INSTANCE = Mappers.getMapper(ContactPersonEntityMapper.class);

    @Mappings({
            @Mapping(target = "title", expression = "java(contactPerson.getTitle().getValue())"),
            @Mapping(target = "givenName", expression = "java(contactPerson.getGivenName().getValue())"),
            @Mapping(target = "middleName", expression = "java(contactPerson.getMiddleName().getValue())"),
            @Mapping(target = "familyName", expression = "java(contactPerson.getFamilyName().getValue())"),
            @Mapping(target = "familyNamePrefix", expression = "java(contactPerson.getFamilyNamePrefix().getValue())"),
            @Mapping(target = "nameSuffix", expression = "java(contactPerson.getNameSuffix().getValue())"),
            @Mapping(target = "gender", expression = "java(contactPerson.getGenderCode().getValue())"),
            @Mapping(target = "alias", expression = "java(contactPerson.getAlias().getValue())")
    })
    public abstract ContactPersonEntity mapToContactPersonEntity(ContactPerson contactPerson);
}
