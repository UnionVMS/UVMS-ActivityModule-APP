package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.RegistrationLocation;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.TextType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class RegistrationEventMapper extends BaseMapper {

    public static RegistrationEventMapper INSTANCE = Mappers.getMapper(RegistrationEventMapper.class);

    @Mappings({
            @Mapping(target = "description", expression = "java(getDescription(registrationEvent.getDescriptions()))"),
            @Mapping(target = "occurrenceDatetime", expression = "java(convertToDate(registrationEvent.getOccurrenceDateTime()))"),
            @Mapping(target = "registrationLocation", expression = "java(mapToRegistrationLocationEntity(registrationEvent.getRelatedRegistrationLocation(), registrationEventEntity))"),
            @Mapping(target = "vesselTransportMeanses", expression = "java(vesselTransportMeansEntity)")
    })
    public abstract RegistrationEventEntity mapToRegistrationEventEntity(RegistrationEvent registrationEvent, VesselTransportMeansEntity vesselTransportMeansEntity, @MappingTarget RegistrationEventEntity registrationEventEntity);

    protected RegistrationLocationEntity mapToRegistrationLocationEntity(RegistrationLocation registrationLocation, RegistrationEventEntity registrationEventEntity) {
        return RegistrationLocationMapper.INSTANCE.mapToRegistrationLocationEntity(registrationLocation, registrationEventEntity, new RegistrationLocationEntity());
    }

    protected String getDescription(List<TextType> descriptions) {
        return (descriptions == null || descriptions.isEmpty()) ? null : descriptions.get(0).getValue();
    }
}
