package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FishingTripEntityTest {

    @BeforeClass
    public static void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void create() {
        // Given
        FishingTrip fishingTrip = MapperUtil.getFishingTrip();

        // When
        FishingTripEntity fishingTripEntity = FishingTripEntity.create(fishingTrip);

        // Then
        FishingTripKey fishingTripKey = fishingTripEntity.getFishingTripKey();

        List<IDType> ids = fishingTrip.getIDS();
        IDType idType = ids.get(0);

        assertEquals(idType.getValue(), fishingTripKey.getTripId());
        assertEquals(idType.getSchemeID(), fishingTripKey.getTripSchemeId());

        assertEquals(fishingTrip.getTypeCode().getValue(), fishingTripEntity.getTypeCode());
        assertEquals(fishingTrip.getTypeCode().getListID(), fishingTripEntity.getTypeCodeListId());

        assertEquals(Instant.parse("2011-07-01T11:15:00Z"), fishingTripEntity.getCalculatedTripStartDate());
        assertEquals(Instant.parse("2016-07-01T11:15:00Z"), fishingTripEntity.getCalculatedTripEndDate());

        assertTrue(fishingTripEntity.getFishingActivities().isEmpty());
    }

    @Test
    public void convert() {
        // Given
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        FishingTripEntity fishingTripEntity = MapperUtil.getFishingTripEntity();

        Instant startDate = Instant.parse("2011-07-01T11:15:00.023Z");
        Instant endDate = startDate.plus(54, ChronoUnit.MINUTES);
        fishingTripEntity.setCalculatedTripStartDate(startDate);
        fishingTripEntity.setCalculatedTripEndDate(endDate);

        // When
        FishingTrip fishingTrip = fishingTripEntity.convert();

        // Then
        FishingTripKey fishingTripKey = fishingTripEntity.getFishingTripKey();

        List<IDType> ids = fishingTrip.getIDS();
        IDType idType = ids.get(0);

        assertEquals(fishingTripKey.getTripId(), idType.getValue());
        assertEquals(fishingTripKey.getTripSchemeId(), idType.getSchemeID());

        assertEquals(fishingTripEntity.getTypeCode(), fishingTrip.getTypeCode().getValue());
        assertEquals(fishingTripEntity.getTypeCodeListId(), fishingTrip.getTypeCode().getListID());

        List<DelimitedPeriod> specifiedDelimitedPeriods = fishingTrip.getSpecifiedDelimitedPeriods();
        assertEquals(1, specifiedDelimitedPeriods.size());

        DelimitedPeriod delimitedPeriod = specifiedDelimitedPeriods.get(0);

        DateTimeType startDateTime = delimitedPeriod.getStartDateTime();
        DateTimeType endDateTime = delimitedPeriod.getEndDateTime();
        un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType durationMeasure = delimitedPeriod.getDurationMeasure();

        assertEquals("2011-07-01T11:15:00Z", startDateTime.getDateTime().toString());
        assertEquals("2011-07-01T12:09:00Z", endDateTime.getDateTime().toString());

        assertEquals(54, durationMeasure.getValue().doubleValue(), 0.0);
        assertEquals("MIN", durationMeasure.getUnitCode());
    }
}
