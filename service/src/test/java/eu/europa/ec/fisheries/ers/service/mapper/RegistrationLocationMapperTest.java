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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import eu.europa.ec.fisheries.ers.fa.entities.RegistrationLocationEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;

/**
 * Created by padhyad on 7/28/2016.
 */
public class RegistrationLocationMapperTest {

    @Test
    public void testRegistrationLocationMapper() {
        RegistrationLocation registrationLocation = MapperUtil.getRegistrationLocation();
        RegistrationLocationEntity registrationLocationEntity = RegistrationLocationMapper.INSTANCE.mapToRegistrationLocationEntity(registrationLocation);

        assertTrue(registrationLocationEntity.getDescription().startsWith(registrationLocation.getDescriptions().get(0).getValue()));
        assertTrue(registrationLocationEntity.getName().startsWith(registrationLocation.getNames().get(0).getValue()));
        assertEquals(registrationLocation.getGeopoliticalRegionCode().getValue(), registrationLocationEntity.getRegionCode());
        assertEquals(registrationLocation.getGeopoliticalRegionCode().getListID(), registrationLocationEntity.getRegionCodeListId());
        assertEquals(registrationLocation.getCountryID().getValue(), registrationLocationEntity.getLocationCountryId());
        assertEquals(registrationLocation.getCountryID().getSchemeID(), registrationLocationEntity.getLocationCountrySchemeId());
        assertEquals(registrationLocation.getTypeCode().getValue(), registrationLocationEntity.getTypeCode());
        assertEquals(registrationLocation.getTypeCode().getListID(), registrationLocationEntity.getTypeCodeListId());
        assertNull(registrationLocationEntity.getRegistrationEvent());
    }
}
