/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPersonEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.StructuredAddressTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.ContactPartyDetailsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactPerson;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(uses = {ContactPersonMapper.class, StructuredAddressMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class ContactPartyMapper extends BaseMapper {

    public static final ContactPartyMapper INSTANCE = Mappers.getMapper(ContactPartyMapper.class);


    @Mapping(target = "contactPerson", expression = "java(getContactPersonEntity(contactParty.getSpecifiedContactPersons(), contactPartyEntity))")
    @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntity(contactParty.getSpecifiedStructuredAddresses(), contactPartyEntity))")
    @Mapping(target = "vesselTransportMeans", expression = "java(vesselTransportMeansEntity)")
    @Mapping(target = "contactPartyRole", expression = "java(getContactPartyRoles(contactParty.getRoleCodes(), contactPartyEntity))")
    public abstract ContactPartyEntity mapToContactPartyEntity(ContactParty contactParty, VesselTransportMeansEntity vesselTransportMeansEntity);


    @Mapping(target = "roleCode", source = "value")
    @Mapping(target = "roleCodeListId", source = "listID")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactParty", ignore = true)
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
            StructuredAddressEntity structuredAddressEntity =
                    StructuredAddressMapper.INSTANCE.mapToStructuredAddressEntity(structuredAddress);
            structuredAddressEntity.setStructuredAddressType(StructuredAddressTypeEnum.CONTACT_PARTY_SPECIFIED.getType());
            structuredAddressEntity.setContactParty(contactPartyEntity);
            structuredAddressEntities.add(structuredAddressEntity);
        }
        return structuredAddressEntities;
    }


    @Mapping(target = "role", expression = "java(getFirstRoleCode(entity))")
    public abstract ContactPartyDetailsDTO map(ContactPartyEntity entity);

    public abstract Set<ContactPartyDetailsDTO> map(Set<ContactPartyEntity> entities);

    protected String getFirstRoleCode(ContactPartyEntity contactPartyEntity) {
        if(contactPartyEntity ==null || CollectionUtils.isEmpty(contactPartyEntity.getContactPartyRole())) {
            return null;
        }
       return contactPartyEntity.getContactPartyRole().iterator().next().getRoleCode();
    }

}
