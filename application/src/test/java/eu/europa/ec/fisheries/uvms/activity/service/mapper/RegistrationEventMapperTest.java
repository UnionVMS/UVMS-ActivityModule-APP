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

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class RegistrationEventMapperTest extends BaseActivityArquillianTest {

    @Inject
    RegistrationEventMapper registrationEventMapper;

    @Test
    public void testRegistrationEventMapper() {
        RegistrationEvent registrationEvent = MapperUtil.getRegistrationEvent();
        RegistrationEventEntity registrationEventEntity = registrationEventMapper.mapToRegistrationEventEntity(registrationEvent);

        assertTrue(registrationEventEntity.getDescription().startsWith(registrationEvent.getDescriptions().get(0).getValue()));
        assertEquals(registrationEvent.getOccurrenceDateTime().getDateTime().toGregorianCalendar().toInstant(), registrationEventEntity.getOccurrenceDatetime());
        assertNotNull(registrationEventEntity.getRegistrationLocation());

        registrationEventEntity = registrationEventEntity.getRegistrationLocation().getRegistrationEvent();
        assertTrue(registrationEventEntity.getDescription().startsWith(registrationEvent.getDescriptions().get(0).getValue()));
        assertEquals(registrationEvent.getOccurrenceDateTime().getDateTime().toGregorianCalendar().toInstant(), registrationEventEntity.getOccurrenceDatetime());

    }
}
