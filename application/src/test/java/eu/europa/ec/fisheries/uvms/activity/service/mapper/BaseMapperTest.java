/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.util.Date;
import static org.junit.Assert.assertEquals;

public class BaseMapperTest {

    private BaseMapper baseMapper;

    @Before
    public void setUp() {
        baseMapper = new BaseMapper();
    }

    @Test
    public void instantToDate() {
        // Given
        Instant instant = Instant.now();

        // When
        Date date = baseMapper.instantToDate(instant);

        // Then
        // note: It seems to be impossible to create a Date that is reliably always UTC
        assertEquals(instant.toEpochMilli(), date.getTime());
    }

    @Test
    public void instantToDateUtilsStringFormat() {
        // Given
        Instant instant = Instant.ofEpochSecond(1_000_000);

        // When
        String formattedString = baseMapper.instantToDateUtilsStringFormat(instant);

        // Then
        assertEquals("1970-01-12T13:46:40", formattedString);
    }

    @Test
    public void instantToXMLGregorianCalendarUTC() {
        // Given
        Instant instant = Instant.now();

        // When
        XMLGregorianCalendar calendar = baseMapper.instantToXMLGregorianCalendarUTC(instant);

        // Then
        // Make sure there where has been no funny business with timezones
        assertEquals(0, calendar.getTimezone());
        assertEquals(instant.getEpochSecond(), calendar.toGregorianCalendar().toInstant().getEpochSecond());
    }

    @Test
    public void instantToDateTimeTypeUTC() {
        // Given
        Instant instant = Instant.now();

        // When
        DateTimeType dateTimeType = baseMapper.instantToDateTimeTypeUTC(instant);

        // Then
        XMLGregorianCalendar calendar = dateTimeType.getDateTime();
        // Make sure there where has been no funny business with timezones
        assertEquals(0, calendar.getTimezone());
        assertEquals(instant.getEpochSecond(), calendar.toGregorianCalendar().toInstant().getEpochSecond());
    }

    @Test
    public void dateTimeTypeToInstant() {
        // Given
        XMLGregorianCalendar xmlGregorianCalendar = baseMapper.instantToXMLGregorianCalendarUTC(Instant.now());
        DateTimeType dateTimeType = new DateTimeType();
        dateTimeType.setDateTime(xmlGregorianCalendar);

        // When
        Instant instant = baseMapper.dateTimeTypeToInstant(dateTimeType);

        // Then
        assertEquals(dateTimeType.getDateTime().toGregorianCalendar().toInstant(), instant);
    }
}
