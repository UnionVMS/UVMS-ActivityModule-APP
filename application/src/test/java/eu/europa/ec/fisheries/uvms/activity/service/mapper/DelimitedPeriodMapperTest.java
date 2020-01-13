package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import org.junit.BeforeClass;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class DelimitedPeriodMapperTest {

    @BeforeClass
    public static void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void getStartDate() throws Exception {
        // Given
        Instant startDateInstant = Instant.parse("2019-01-01T15:45:32.012Z");

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Date.from(startDateInstant));
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        DateTimeType dateTimeType = new DateTimeType();
        dateTimeType.setDateTime(xmlGregorianCalendar);

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setStartDateTime(dateTimeType);

        // When
        Instant startDate = DelimitedPeriodMapper.getStartDate(delimitedPeriod);

        // Then
        assertEquals(startDateInstant, startDate);
    }

    @Test
    public void getStartDate_noStartDate_onlyEndDateAndDuration() throws Exception {
        // Given
        Instant expectedStartDateInstant = Instant.parse("2019-01-01T14:16:32.012Z");
        Instant endDateInstant = Instant.parse("2019-01-01T15:45:32.012Z");

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Date.from(endDateInstant));
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        DateTimeType dateTimeType = new DateTimeType();
        dateTimeType.setDateTime(xmlGregorianCalendar);

        MeasureType duration = new MeasureType();
        duration.setValue(BigDecimal.valueOf(89));

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setEndDateTime(dateTimeType);
        delimitedPeriod.setDurationMeasure(duration);

        // When
        Instant startDate = DelimitedPeriodMapper.getStartDate(delimitedPeriod);

        // Then
        assertEquals(expectedStartDateInstant, startDate);
    }

    @Test
    public void getStartDate_noStartOrEndDate_expectNull() {
        // Given
        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();

        // When
        Instant startDate = DelimitedPeriodMapper.getStartDate(delimitedPeriod);

        // Then
        assertNull(startDate);
    }
    @Test
    public void getStartDate_nullDelimitedPeriod_expectNull() {
        // When
        Instant startDate = DelimitedPeriodMapper.getStartDate(null);

        // Then
        assertNull(startDate);
    }

    @Test
    public void getEndDate() throws Exception {
        // Given
        Instant endDateInstant = Instant.parse("2019-01-01T15:45:32.012Z");

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Date.from(endDateInstant));
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        DateTimeType dateTimeType = new DateTimeType();
        dateTimeType.setDateTime(xmlGregorianCalendar);

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setEndDateTime(dateTimeType);

        // When
        Instant endDate = DelimitedPeriodMapper.getEndDate(delimitedPeriod);

        // Then
        assertEquals(endDateInstant, endDate);
    }

    @Test
    public void getEndDate_noEndDate_onlyStartDateAndDuration() throws Exception {
        // Given
        Instant startDateInstant = Instant.parse("2019-01-01T14:16:32.012Z");
        Instant expectedEndDateInstant = Instant.parse("2019-01-01T15:45:32.012Z");

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Date.from(startDateInstant));
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        DateTimeType dateTimeType = new DateTimeType();
        dateTimeType.setDateTime(xmlGregorianCalendar);

        MeasureType duration = new MeasureType();
        duration.setValue(BigDecimal.valueOf(89));

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setStartDateTime(dateTimeType);
        delimitedPeriod.setDurationMeasure(duration);

        // When
        Instant endDate = DelimitedPeriodMapper.getEndDate(delimitedPeriod);

        // Then
        assertEquals(expectedEndDateInstant, endDate);
    }

    @Test
    public void getEndDate_noStartOrEndDate_expectNull() {
        // Given
        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();

        // When
        Instant startDate = DelimitedPeriodMapper.getEndDate(delimitedPeriod);

        // Then
        assertNull(startDate);
    }
    @Test
    public void getEndDate_nullDelimitedPeriod_expectNull() {
        // When
        Instant startDate = DelimitedPeriodMapper.getEndDate(null);

        // Then
        assertNull(startDate);
    }

    @Test
    public void convert() {
        // Given
        Instant startDate = Instant.parse("2019-01-01T15:45:32.012Z");
        Instant endDate = startDate.plus(54, ChronoUnit.MINUTES);

        // When
        DelimitedPeriod delimitedPeriod = DelimitedPeriodMapper.convert(startDate, endDate);

        // Then
        DateTimeType startDateTime = delimitedPeriod.getStartDateTime();
        DateTimeType endDateTime = delimitedPeriod.getEndDateTime();
        MeasureType durationMeasure = delimitedPeriod.getDurationMeasure();

        assertEquals("2019-01-01T15:45:32Z", startDateTime.getDateTime().toString());
        assertEquals("2019-01-01T16:39:32Z", endDateTime.getDateTime().toString());

        assertEquals(54, durationMeasure.getValue().doubleValue(), 0.0);
        assertEquals("MIN", durationMeasure.getUnitCode());
    }
}
