package eu.europa.ec.fisheries.uvms.activity.service.facatch.evolution;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;

import java.time.Instant;
import java.util.Comparator;

public class FishingActivityCalculatedDateComparator implements Comparator<FishingActivityEntity> {
    @Override
    public int compare(FishingActivityEntity fishingActivity1, FishingActivityEntity fishingActivity2) {
        Instant calculatedStartTime1 = fishingActivity1.getCalculatedStartTime();
        Instant calculatedStartTime2 = fishingActivity2.getCalculatedStartTime();

        if(calculatedStartTime1 != null) {
            return calculatedStartTime1.compareTo(calculatedStartTime2);
        }

        return 1;
    }
}
