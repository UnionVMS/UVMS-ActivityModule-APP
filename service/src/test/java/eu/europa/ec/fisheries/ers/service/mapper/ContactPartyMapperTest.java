/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPersonEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;

/**
 * Created by padhyad on 7/27/2016.
 */
public class ContactPartyMapperTest {

    @Test
    public void testContactPartyMapper() {
        ContactParty contactParty = MapperUtil.getContactParty();
        ContactPartyEntity contactPartyEntity = ContactPartyMapper.INSTANCE.mapToContactPartyEntity(contactParty);

        ContactPartyRoleEntity entity = contactPartyEntity.getContactPartyRole().iterator().next();
        assertEquals(contactParty.getRoleCodes().get(0).getValue(), entity.getRoleCode());
        assertEquals(contactParty.getRoleCodes().get(0).getListID(), entity.getRoleCodeListId());

        assertNotNull(contactPartyEntity.getContactPerson());
        entity = contactPartyEntity.getContactPerson().getContactParty().getContactPartyRole().iterator().next();
        assertEquals(contactParty.getRoleCodes().get(0).getValue(), entity.getRoleCode());
        assertEquals(contactParty.getRoleCodes().get(0).getListID(), entity.getRoleCodeListId());

        assertNotNull(contactPartyEntity.getStructuredAddresses());
        StructuredAddressEntity structuredAddressEntity = contactPartyEntity.getStructuredAddresses().iterator().next();
        assertNotNull(structuredAddressEntity);
        entity = structuredAddressEntity.getContactParty().getContactPartyRole().iterator().next();
        assertEquals(contactParty.getRoleCodes().get(0).getValue(), entity.getRoleCode());
        assertEquals(contactParty.getRoleCodes().get(0).getListID(), entity.getRoleCodeListId());
    }


    @Test
    public void testContactPartyMapperNullReturns() {
        Set<ContactPartyRoleEntity> contactPartyRoles = ContactPartyMapper.INSTANCE.getContactPartyRoles(null, new ContactPartyEntity());
        assertTrue(contactPartyRoles.size() == 0);
        ContactPersonEntity contactPersonEntity = ContactPartyMapper.INSTANCE.getContactPersonEntity(null, new ContactPartyEntity());
        assertNull(contactPersonEntity);
        Set<StructuredAddressEntity> structuredAddressEntity = ContactPartyMapper.INSTANCE.getStructuredAddressEntity(null, new ContactPartyEntity());
        assertTrue(structuredAddressEntity.size() == 0);
    }
}
