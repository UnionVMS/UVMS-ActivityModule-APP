/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.RegistrationEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;

@Mapper(imports = BaseMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RegistrationEventMapper {

    RegistrationEventMapper INSTANCE = Mappers.getMapper(RegistrationEventMapper.class);

    @Mappings({
            @Mapping(target = "description", expression = "java(BaseMapper.getTextFromList(registrationEvent.getDescriptions()))"),
            @Mapping(target = "descLanguageId", expression = "java(BaseMapper.getLanguageIdFromList(registrationEvent.getDescriptions()))"),
            @Mapping(target = "occurrenceDatetime", source = "occurrenceDateTime.dateTime"),
            @Mapping(target = "registrationLocation", expression = "java(BaseMapper.mapToRegistrationLocationEntity(registrationEvent.getRelatedRegistrationLocation(), registrationEventEntity))"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "vesselTransportMeanses", ignore = true),
    })
    RegistrationEventEntity mapToRegistrationEventEntity(RegistrationEvent registrationEvent);
}
