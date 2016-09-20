/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.StructuredAddressTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.ContactPartyDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.ContactPerson;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.StructuredAddress;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.CodeType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 6/8/2016.
 */
@Mapper(uses = {ContactPersonMapper.class, StructuredAddressMapper.class})
public abstract class ContactPartyMapper extends BaseMapper {

    public static final ContactPartyMapper INSTANCE = Mappers.getMapper(ContactPartyMapper.class);

    @Mappings({
            @Mapping(target = "contactPerson", expression = "java(getContactPersonEntity(contactParty.getSpecifiedContactPersons(), contactPartyEntity))"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntity(contactParty.getSpecifiedStructuredAddresses(), contactPartyEntity))"),
            @Mapping(target = "vesselTransportMeans", expression = "java(vesselTransportMeansEntity)"),
            @Mapping(target = "contactPartyRole", expression = "java(getContactPartyRoles(contactParty.getRoleCodes(), contactPartyEntity))")
    })
    public abstract ContactPartyEntity mapToContactPartyEntity(ContactParty contactParty, VesselTransportMeansEntity vesselTransportMeansEntity, @MappingTarget ContactPartyEntity contactPartyEntity);

    @Mappings({
            @Mapping(target = "roleCode", expression = "java(getCodeType(codeType))"),
            @Mapping(target = "roleCodeListId", expression = "java(getCodeTypeListId(codeType))")
    })
    public abstract ContactPartyRoleEntity mapToContactPartyRoleEntity(CodeType codeType);

    protected Set<ContactPartyRoleEntity> getContactPartyRoles(List<CodeType> codeTypes, ContactPartyEntity contactPartyEntity) {
        if (codeTypes == null || codeTypes.isEmpty()) {
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
        if(contactPersons == null || contactPersons.isEmpty()) {
            return null;
        }
        return ContactPersonMapper.INSTANCE.mapToContactPersonEntity(contactPersons.get(0), contactPartyEntity, new ContactPersonEntity());
    }

    protected Set<StructuredAddressEntity> getStructuredAddressEntity(List<StructuredAddress> structuredAddresses, ContactPartyEntity contactPartyEntity) {
        if (structuredAddresses == null || structuredAddresses.isEmpty()) {
            return Collections.emptySet();
        }
        Set<StructuredAddressEntity> structuredAddressEntities = new HashSet<>();
        for (StructuredAddress structuredAddress : structuredAddresses) {
            StructuredAddressEntity structuredAddressEntity = StructuredAddressMapper.INSTANCE.mapToStructuredAddress(structuredAddress, StructuredAddressTypeEnum.CANTACT_PARTY_SPECIFIED, contactPartyEntity, new StructuredAddressEntity());
            structuredAddressEntities.add(structuredAddressEntity);
        }
        return structuredAddressEntities;
    }
}