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

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.ContactPartyDetailsDTO;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.StructuredAddress;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by padhyad on 7/27/2016.
 */
public class ContactPartyMapperTest {

    @Test
    public void testContactPartyMapper() {
        ContactParty contactParty = MapperUtil.getContactParty();
        ContactPartyEntity contactPartyEntity = new ContactPartyEntity();
        ContactPartyMapper.INSTANCE.mapToContactPartyEntity(contactParty, null, contactPartyEntity);

        assertEquals(contactParty.getRoleCodes().get(0).getValue(), contactPartyEntity.getRoleCode());
        assertEquals(contactParty.getRoleCodes().get(0).getListID(), contactPartyEntity.getRoleCodeListId());

        assertNotNull(contactPartyEntity.getContactPerson());
        assertEquals(contactParty.getRoleCodes().get(0).getValue(), contactPartyEntity.getContactPerson().getContactParty().getRoleCode());
        assertEquals(contactParty.getRoleCodes().get(0).getListID(), contactPartyEntity.getContactPerson().getContactParty().getRoleCodeListId());

        assertNotNull(contactPartyEntity.getStructuredAddresses());
        StructuredAddressEntity structuredAddressEntity = contactPartyEntity.getStructuredAddresses().iterator().next();
        assertNotNull(structuredAddressEntity);
        assertEquals(contactParty.getRoleCodes().get(0).getValue(), structuredAddressEntity.getContactParty().getRoleCode());
        assertEquals(contactParty.getRoleCodes().get(0).getListID(), structuredAddressEntity.getContactParty().getRoleCodeListId());
    }
}
