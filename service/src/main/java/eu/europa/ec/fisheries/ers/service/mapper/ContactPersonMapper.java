package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.ContactPerson;

/**
 * Created by padhyad on 6/8/2016.
 */
@Mapper
public abstract class ContactPersonMapper extends BaseMapper {

    public static ContactPersonMapper INSTANCE = Mappers.getMapper(ContactPersonMapper.class);

    @Mappings({
            @Mapping(target = "title", expression = "java(getTextType(contactPerson.getTitle()))"),
            @Mapping(target = "givenName", expression = "java(getTextType(contactPerson.getGivenName()))"),
            @Mapping(target = "middleName", expression = "java(getTextType(contactPerson.getMiddleName()))"),
            @Mapping(target = "familyName", expression = "java(getTextType(contactPerson.getFamilyName()))"),
            @Mapping(target = "familyNamePrefix", expression = "java(getTextType(contactPerson.getFamilyNamePrefix()))"),
            @Mapping(target = "nameSuffix", expression = "java(getTextType(contactPerson.getNameSuffix()))"),
            @Mapping(target = "gender", expression = "java(getCodeType(contactPerson.getGenderCode()))"),
            @Mapping(target = "alias", expression = "java(getTextType(contactPerson.getAlias()))"),
            @Mapping(target = "contactParty", expression = "java(contactPartyEntity)")
    })
    public abstract ContactPersonEntity mapToContactPersonEntity(ContactPerson contactPerson, ContactPartyEntity contactPartyEntity, @MappingTarget ContactPersonEntity contactPersonEntity);
}
