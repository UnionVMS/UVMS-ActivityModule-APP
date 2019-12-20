package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import org.junit.BeforeClass;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class DelimitedPeriodMapperTest {

    @BeforeClass
    public static void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
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
