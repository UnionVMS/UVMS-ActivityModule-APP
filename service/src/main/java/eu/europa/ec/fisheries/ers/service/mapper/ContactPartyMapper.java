/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPersonEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.fa.utils.StructuredAddressTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.ContactPartyDetailsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactPerson;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

/**
 * Created by padhyad on 6/8/2016.
 */
@Mapper(uses = {ContactPersonMapper.class, StructuredAddressMapper.class})
public abstract class ContactPartyMapper extends BaseMapper {

    public static final ContactPartyMapper INSTANCE = Mappers.getMapper(ContactPartyMapper.class);

    @Mappings({
            @Mapping(target = "contactPerson", expression = "java(getContactPersonEntity(contactParty.getSpecifiedContactPersons(), contactPartyEntity))"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntity(contactParty.getSpecifiedStructuredAddresses(), contactPartyEntity))"),
            @Mapping(target = "contactPartyRole", expression = "java(getContactPartyRoles(contactParty.getRoleCodes(), contactPartyEntity))")
    })
    public abstract ContactPartyEntity mapToContactPartyEntity(ContactParty contactParty);

    @Mappings({
            @Mapping(target = "roleCode", expression = "java(getCodeType(codeType))"),
            @Mapping(target = "roleCodeListId", expression = "java(getCodeTypeListId(codeType))")
    })
    public abstract ContactPartyRoleEntity mapToContactPartyRoleEntity(CodeType codeType);

    protected Set<ContactPartyRoleEntity> getContactPartyRoles(List<CodeType> codeTypes, ContactPartyEntity contactPartyEntity) {
        if (CollectionUtils.isEmpty(codeTypes)) {
            return Collections.emptySet();
        }
        Set<ContactPartyRoleEntity> contactPartyRoles = new HashSet<>();
        for(CodeType codeType : codeTypes) {
            ContactPartyRoleEntity contactPartyRoleEntity = ContactPartyMapper.INSTANCE.mapToContactPartyRoleEntity(codeType);
            contactPartyRoleEntity.setContactParty(contactPartyEntity);
            contactPartyRoles.add(contactPartyRoleEntity);
        }
        return contactPartyRoles;
    }

    protected ContactPersonEntity getContactPersonEntity(List<ContactPerson> contactPersons, ContactPartyEntity contactPartyEntity) {
        if(CollectionUtils.isEmpty(contactPersons)) {
            return null;
        }
        ContactPersonEntity contactPersonEntity = ContactPersonMapper.INSTANCE.mapToContactPersonEntity(contactPersons.get(0));
        contactPersonEntity.setContactParty(contactPartyEntity);
        return contactPersonEntity;
    }

    protected Set<StructuredAddressEntity> getStructuredAddressEntity(List<StructuredAddress> structuredAddresses, ContactPartyEntity contactPartyEntity) {
        if (CollectionUtils.isEmpty(structuredAddresses)) {
            return Collections.emptySet();
        }
        Set<StructuredAddressEntity> structuredAddressEntities = new HashSet<>();
        for (StructuredAddress structuredAddress : structuredAddresses) {
            StructuredAddressEntity structuredAddressEntity = StructuredAddressMapper.INSTANCE.mapToStructuredAddress(structuredAddress, StructuredAddressTypeEnum.CANTACT_PARTY_SPECIFIED, contactPartyEntity, new StructuredAddressEntity());
            structuredAddressEntities.add(structuredAddressEntity);
        }
        return structuredAddressEntities;
    }

    public abstract ContactPartyDetailsDTO map(ContactPartyEntity entity);

    public abstract Set<ContactPartyDetailsDTO> map(Set<ContactPartyEntity> entities);

}