package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.CountryEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationLocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.RegistrationLocation;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.TextType;

import java.util.List;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class RegistrationLocationMapper {

    public static RegistrationLocationMapper INSTANCE = Mappers.getMapper(RegistrationLocationMapper.class);

    @Mappings({
            @Mapping(target = "description", expression = "java(getText(registrationLocation.getDescriptions()))"),
            @Mapping(target = "regionCode", expression = "java(registrationLocation.getGeopoliticalRegionCode().getValue())"),
            @Mapping(target = "regionCodeListId", expression = "java(registrationLocation.getGeopoliticalRegionCode().getListID())"),
            @Mapping(target = "name", expression = "java(getText(registrationLocation.getNames()))"),
            @Mapping(target = "typeCode", expression = "java(registrationLocation.getTypeCode().getValue())"),
            @Mapping(target = "typeCodeListId", expression = "java(registrationLocation.getTypeCode().getListID())"),
            @Mapping(target = "locationCountryId", expression = "java(registrationLocation.getCountryID().getValue())"),
            @Mapping(target = "locationCountrySchemeId", expression = "java(registrationLocation.getCountryID().getSchemeID())")
    })
    public abstract RegistrationLocationEntity mapToRegistrationLocationEntity(RegistrationLocation registrationLocation);


    protected String getText(List<TextType> descriptions) {
        if (descriptions == null || descriptions.isEmpty()) {
            return null;
        }
        StringBuilder description = new StringBuilder();
        for (TextType text : descriptions) {
            description.append(text.getValue());
        }
        return description.toString();
    }
}
