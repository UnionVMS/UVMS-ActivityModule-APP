package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FishingTripEntityTest {

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

        Set<DelimitedPeriodEntity> delimitedPeriods = fishingTripEntity.getDelimitedPeriods();
        assertEquals(1, delimitedPeriods.size());

        DelimitedPeriodEntity delimitedPeriodEntity = delimitedPeriods.iterator().next();
        Instant startDate = delimitedPeriodEntity.getStartDate();
        Instant endDate = delimitedPeriodEntity.getEndDate();
        MeasureType durationMeasure = delimitedPeriodEntity.getDurationMeasure();

        assertEquals("2011-07-01T11:15:00Z", startDate.toString());
        assertEquals("2016-07-01T11:15:00Z", endDate.toString());
        assertEquals(500, durationMeasure.getValue(), 0);
        assertEquals("C62", durationMeasure.getUnitCode());
        assertEquals("4rhfy5-fhtydr-tyfr85-ghtyd54", durationMeasure.getUnitCodeListVersionID());

        assertTrue(fishingTripEntity.getFishingActivities().isEmpty());
    }

    @Test
    public void convert() {
        // Given
        FishingTripEntity fishingTripEntity = MapperUtil.getFishingTripEntity();

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
        assertTrue(specifiedDelimitedPeriods.isEmpty());
    }
}
