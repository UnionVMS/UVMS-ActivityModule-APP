package eu.europa.ec.fisheries.ers.activity.service.facatch.evolution;

import com.google.common.collect.Ordering;
import eu.europa.ec.fisheries.ers.activity.fa.entities.FishingActivityEntity;

import java.time.Instant;
import java.util.Comparator;

public class FishingActivityCalculatedDateComparator implements Comparator<FishingActivityEntity> {
    private Ordering<Instant> fluxLocationSchemeIdOrdering = Ordering.natural().nullsLast();

    @Override
    public int compare(FishingActivityEntity fishingActivity1, FishingActivityEntity fishingActivity2) {
        Instant calculatedStartTime1 = fishingActivity1.getCalculatedStartTime();
        Instant calculatedStartTime2 = fishingActivity2.getCalculatedStartTime();

        return fluxLocationSchemeIdOrdering.compare(calculatedStartTime1, calculatedStartTime2);
    }
}
