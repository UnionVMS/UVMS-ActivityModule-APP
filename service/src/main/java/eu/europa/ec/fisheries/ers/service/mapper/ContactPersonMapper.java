/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPersonEntity;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.ContactPersonDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactPerson;

@Mapper
public interface ContactPersonMapper {

    ContactPersonMapper INSTANCE = Mappers.getMapper(ContactPersonMapper.class);

    @Mappings({
            @Mapping(target = "title", source = "title.value"),
            @Mapping(target = "givenName", source = "givenName.value"),
            @Mapping(target = "middleName", source = "middleName.value"),
            @Mapping(target = "familyName", source = "familyName.value"),
            @Mapping(target = "familyNamePrefix", source = "familyNamePrefix.value"),
            @Mapping(target = "nameSuffix", source = "nameSuffix.value"),
            @Mapping(target = "gender", source = "genderCode.value"),
            @Mapping(target = "alias", source = "alias.value"),
    })
    ContactPersonEntity mapToContactPersonEntity(ContactPerson contactPerson);

    ContactPersonDetailsDTO mapToContactPersonDetailsDTO(ContactPersonEntity contactPersonEntity);
}
