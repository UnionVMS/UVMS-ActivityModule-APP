package eu.europa.ec.fisheries.uvms.activity.service;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripKey;

import java.util.HashMap;
import java.util.Map;

public class FaReportMessageFishingTrips {
    private Map<FishingTripKey, FishingTripEntity> fishingTripEntityMap = new HashMap<>();

    private FishingTripEntity add(FishingTripEntity fishingTripEntity) {
        FishingTripEntity existingEntity = fishingTripEntityMap.putIfAbsent(fishingTripEntity.getFishingTripKey(), fishingTripEntity);
        if (existingEntity != null) {
            return existingEntity;
        }
        return fishingTripEntity;
    }

    public void addAndUpdateFishingTripOfActivityIfItExists(FishingActivityEntity fishingActivityEntity) {
        FishingTripEntity fishingTrip = fishingActivityEntity.getFishingTrip();
        FishingTripEntity fishingTripEntity = add(fishingTrip);
        fishingActivityEntity.setFishingTrip(fishingTripEntity);
    }
}
