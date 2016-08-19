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

import eu.europa.ec.fisheries.ers.fa.entities.ContactPersonEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.ContactPersonDetailsDTO;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.ContactPerson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by padhyad on 7/27/2016.
 */
public class ContactPersonMapperTest {

    @Test
    public void  testContactPersonMapper() {
        ContactPerson contactPerson = MapperUtil.getContactPerson();
        ContactPersonEntity contactPersonEntity = new ContactPersonEntity();
        ContactPersonMapper.INSTANCE.mapToContactPersonEntity(contactPerson, null, contactPersonEntity);

        assertEquals(contactPerson.getAlias().getValue(), contactPersonEntity.getAlias());
        assertEquals(contactPerson.getFamilyName().getValue(), contactPersonEntity.getFamilyName());
        assertEquals(contactPerson.getFamilyNamePrefix().getValue(), contactPersonEntity.getFamilyNamePrefix());
        assertEquals(contactPerson.getGenderCode().getValue(), contactPersonEntity.getGender());
        assertEquals(contactPerson.getGivenName().getValue(), contactPersonEntity.getGivenName());
        assertEquals(contactPerson.getMiddleName().getValue(), contactPersonEntity.getMiddleName());
        assertEquals(contactPerson.getNameSuffix().getValue(), contactPersonEntity.getNameSuffix());
        assertEquals(contactPerson.getTitle().getValue(), contactPersonEntity.getTitle());
        assertNull(contactPersonEntity.getContactParty());
    }

    @Test
    public void testContactPersonDetailsDTOMapper() {
        ContactPerson contactPerson = MapperUtil.getContactPerson();
        ContactPersonEntity contactPersonEntity = new ContactPersonEntity();
        ContactPersonMapper.INSTANCE.mapToContactPersonEntity(contactPerson, null, contactPersonEntity);

        ContactPersonDetailsDTO contactPersonDetailsDTO = ContactPersonMapper.INSTANCE.mapToContactPersonDetailsDTO(contactPersonEntity);

        assertEquals(contactPersonEntity.getAlias(), contactPersonDetailsDTO.getAlias());
        assertEquals(contactPersonEntity.getFamilyName(), contactPersonDetailsDTO.getFamilyName());
        assertEquals(contactPersonEntity.getFamilyNamePrefix(), contactPersonDetailsDTO.getFamilyNamePrefix());
        assertEquals(contactPersonEntity.getGender(), contactPersonDetailsDTO.getGender());
        assertEquals(contactPersonEntity.getGivenName(), contactPersonDetailsDTO.getGivenName());
        assertEquals(contactPersonEntity.getMiddleName(), contactPersonDetailsDTO.getMiddleName());
        assertEquals(contactPersonEntity.getNameSuffix(), contactPersonDetailsDTO.getNameSuffix());
        assertEquals(contactPersonEntity.getTitle(), contactPersonDetailsDTO.getTitle());
    }
}
