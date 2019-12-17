package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;

import java.time.Instant;

import static org.junit.Assert.*;

public class DelimitedPeriodEmbeddableTest {

    @Test
    public void create() {
        // Given
        DelimitedPeriod delimitedPeriod = MapperUtil.getDelimitedPeriod();

        // When
        DelimitedPeriodEmbeddable delimitedPeriodEmbeddable = DelimitedPeriodEmbeddable.create(delimitedPeriod);

        // Then
        Instant startDate = delimitedPeriodEmbeddable.getStartDate();
        Instant endDate = delimitedPeriodEmbeddable.getEndDate();

        MeasureType durationMeasure = delimitedPeriodEmbeddable.getDurationMeasure();

        assertEquals("2011-07-01T11:15:00Z", startDate.toString());
        assertEquals("2016-07-01T11:15:00Z", endDate.toString());
        assertEquals(500, durationMeasure.getValue(), 0);
        assertEquals("C62", durationMeasure.getUnitCode());
        assertEquals("4rhfy5-fhtydr-tyfr85-ghtyd54", durationMeasure.getUnitCodeListVersionID());
    }

    @Test
    public void convert() {
        // Given
        MeasureType durationMeasure = new MeasureType();
        durationMeasure.setValue(450d);
        durationMeasure.setUnitCode("M");
        durationMeasure.setUnitCodeListVersionID("list-version-id-123");

        DelimitedPeriodEmbeddable delimitedPeriodEmbeddable = new DelimitedPeriodEmbeddable();
        delimitedPeriodEmbeddable.setStartDate(Instant.parse("2011-07-01T11:15:00Z"));
        delimitedPeriodEmbeddable.setEndDate(Instant.parse("2016-07-01T11:15:00Z"));
        delimitedPeriodEmbeddable.setDurationMeasure(durationMeasure);

        // When
        DelimitedPeriod delimitedPeriod = delimitedPeriodEmbeddable.convert();

        // Then
        DateTimeType startDateTime = delimitedPeriod.getStartDateTime();
        DateTimeType endDateTime = delimitedPeriod.getEndDateTime();

        assertEquals("2011-07-01T11:15:00Z", startDateTime.getDateTime().toString());
        assertEquals("2016-07-01T11:15:00Z", endDateTime.getDateTime().toString());

        un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType measureType = delimitedPeriod.getDurationMeasure();

        assertEquals(450d, measureType.getValue().doubleValue(), 0.0);
        assertEquals("M", measureType.getUnitCode());
        assertEquals("list-version-id-123", measureType.getUnitCodeListVersionID());
    }
}
