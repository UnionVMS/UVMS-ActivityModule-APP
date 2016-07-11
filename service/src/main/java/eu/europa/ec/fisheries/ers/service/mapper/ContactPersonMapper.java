/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
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