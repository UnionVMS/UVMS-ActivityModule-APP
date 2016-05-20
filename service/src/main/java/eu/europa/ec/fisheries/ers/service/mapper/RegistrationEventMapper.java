package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.RegistrationEvent;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.TextType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class RegistrationEventMapper {

    public static RegistrationEventMapper INSTANCE = Mappers.getMapper(RegistrationEventMapper.class);

    @Mappings({
            @Mapping(target = "description", expression = "java(getDescription(registrationEvent.getDescriptions()))"),
            @Mapping(target = "occurrenceDatetime", expression = "java(convertToDate(registrationEvent.getOccurrenceDateTime().getDateTime()))")
    })
    public abstract RegistrationEventEntity mapToRegistrationEventEntity(RegistrationEvent registrationEvent);


    protected Date convertToDate(XMLGregorianCalendar dateTime) {
        return dateTime == null ? null : dateTime.toGregorianCalendar().getTime();
    }

    protected String getDescription(List<TextType> descriptions) {
        return (descriptions == null || descriptions.isEmpty()) ? null : descriptions.get(0).getValue();
    }

}
