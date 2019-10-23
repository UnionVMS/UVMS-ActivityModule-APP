package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.BaseUnitilsTest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FishingTripIdWithGeometryMapperTest extends BaseUnitilsTest {

    private FishingTripId fishingTripId;
    private List<FishingActivityEntity> fishingActivities;

    @Before
    public void setUp() {
        fishingTripId = new FishingTripId("fishTripId", "myFirstSchemeId");
        fishingActivities = new ArrayList<>();
    }

    @Test
    public void tripDuration_noDeparture() {
        // Given

        // When
        FishingTripIdWithGeometry response = FishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(fishingTripId, fishingActivities);

        // Then
        assertEquals(0.0, response.getTripDuration(), 1);
    }

    @Test
    public void tripDuration_hasDeparture_noOtherActivity() {
        // Given
        addFishingActivity(FishingActivityTypeEnum.DEPARTURE);

        // When
        FishingTripIdWithGeometry response = FishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(fishingTripId, fishingActivities);

        // Then
        assertEquals(3600005, response.getTripDuration(), 100);
    }

    @Test
    public void tripDuration_noArrival_countToLatestOtherActivity() {
        // Given
        addFishingActivity(FishingActivityTypeEnum.DEPARTURE);
        addFishingActivity(FishingActivityTypeEnum.AREA_ENTRY); // shouldn't count
        addFishingActivity(FishingActivityTypeEnum.AREA_ENTRY);

        // When
        FishingTripIdWithGeometry response = FishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(fishingTripId, fishingActivities);

        // Then
       assertEquals(20000, response.getTripDuration(), 100);
    }

    @Test
    public void tripDuration_hasArrival() {
        // Given
        addFishingActivity(FishingActivityTypeEnum.DEPARTURE);
        addFishingActivity(FishingActivityTypeEnum.AREA_ENTRY); // shouldn't count
        addFishingActivity(FishingActivityTypeEnum.AREA_ENTRY); // shouldn't count
        addFishingActivity(FishingActivityTypeEnum.ARRIVAL);
        addFishingActivity(FishingActivityTypeEnum.LANDING); // shouldn't count

        // When
        FishingTripIdWithGeometry response = FishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(fishingTripId, fishingActivities);

        // Then
        assertEquals(30000, response.getTripDuration(), 100);
    }

    private void addFishingActivity(FishingActivityTypeEnum type) {
        FishingActivityEntity activity = new FishingActivityEntity();
        activity.setTypeCode(type.name());

        // all activities start ten seconds apart
        Instant startTime;
        if (fishingActivities.isEmpty()) {
            startTime = Instant.now().minus(1, ChronoUnit.HOURS);
        } else {
            FishingActivityEntity lastActivity = fishingActivities.get(fishingActivities.size() - 1);
            startTime = lastActivity.getCalculatedStartTime().plus(10, ChronoUnit.SECONDS);
        }

        activity.setCalculatedStartTime(startTime);

        fishingActivities.add(activity);
    }
}