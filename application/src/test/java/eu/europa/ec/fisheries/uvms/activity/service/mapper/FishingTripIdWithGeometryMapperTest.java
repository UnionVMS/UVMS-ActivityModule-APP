package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingTripId;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class FishingTripIdWithGeometryMapperTest extends BaseActivityArquillianTest {

    private FishingTripId fishingTripId;
    private List<FishingActivityEntity> fishingActivities;

    @Inject
    FishingTripIdWithGeometryMapper fishingTripIdWithGeometryMapper;

    @Before
    public void setUp() {
        fishingTripId = new FishingTripId("fishTripId", "myFirstSchemeId");
        fishingActivities = new ArrayList<>();
    }

    @Test
    public void tripDuration_noDeparture() {
        // Given

        // When
        FishingTripIdWithGeometry response = fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(fishingTripId, fishingActivities);

        // Then
        assertEquals(0.0, response.getTripDuration(), 0.0000000000001);
    }

    @Test
    public void tripDuration_hasDeparture_noOtherActivity() {
        // Given
        addFishingActivity(FishingActivityTypeEnum.DEPARTURE);

        // When
        FishingTripIdWithGeometry response = fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(fishingTripId, fishingActivities);

        // Then
        assertEquals(3600, response.getTripDuration(), 0.0000000000001);
    }

    @Test
    public void tripDuration_noArrival_countToLatestOtherActivity() {
        // Given
        addFishingActivity(FishingActivityTypeEnum.DEPARTURE);
        addFishingActivity(FishingActivityTypeEnum.AREA_ENTRY); // shouldn't count
        addFishingActivity(FishingActivityTypeEnum.AREA_ENTRY);

        // When
        FishingTripIdWithGeometry response = fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(fishingTripId, fishingActivities);

        // Then
       assertEquals(20, response.getTripDuration(), 0.0000000000001);
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
        FishingTripIdWithGeometry response = fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(fishingTripId, fishingActivities);

        // Then
        assertEquals(30, response.getTripDuration(), 0.0000000000001);
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
