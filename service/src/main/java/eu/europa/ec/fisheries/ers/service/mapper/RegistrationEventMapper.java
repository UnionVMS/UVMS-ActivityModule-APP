/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.List;

import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class RegistrationEventMapper extends BaseMapper {

    public static final RegistrationEventMapper INSTANCE = Mappers.getMapper(RegistrationEventMapper.class);

    @Mappings({
            @Mapping(target = "description", expression = "java(getDescription(registrationEvent.getDescriptions()))"),
            @Mapping(target = "descLanguageId", expression = "java(getLanguageIdFromList(registrationEvent.getDescriptions()))"),
            @Mapping(target = "occurrenceDatetime", source = "registrationEvent.occurrenceDateTime.dateTime"),
            @Mapping(target = "registrationLocation", expression = "java(mapToRegistrationLocationEntity(registrationEvent.getRelatedRegistrationLocation(), registrationEventEntity))"),
            @Mapping(target = "vesselTransportMeanses", expression = "java(vesselTransportMeansEntity)")
    })
    public abstract RegistrationEventEntity mapToRegistrationEventEntity(RegistrationEvent registrationEvent, VesselTransportMeansEntity vesselTransportMeansEntity, @MappingTarget RegistrationEventEntity registrationEventEntity);

    protected RegistrationLocationEntity mapToRegistrationLocationEntity(RegistrationLocation registrationLocation, RegistrationEventEntity registrationEventEntity) {
        if (registrationLocation == null) {
            return null;
        }
        return RegistrationLocationMapper.INSTANCE.mapToRegistrationLocationEntity(registrationLocation, registrationEventEntity, new RegistrationLocationEntity());
    }

    protected String getDescription(List<TextType> descriptions) {
        return (descriptions == null || descriptions.isEmpty()) ? null : descriptions.get(0).getValue();
    }
}