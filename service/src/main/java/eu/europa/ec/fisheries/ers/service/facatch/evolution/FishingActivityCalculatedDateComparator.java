package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import com.google.common.collect.Ordering;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;

import java.util.Comparator;
import java.util.Date;

public class FishingActivityCalculatedDateComparator implements Comparator<FishingActivityEntity> {
    private Ordering<Date> fluxLocationSchemeIdOrdering = Ordering.natural().nullsLast();

    @Override
    public int compare(FishingActivityEntity fishingActivity1, FishingActivityEntity fishingActivity2) {
        Date calculatedStartTime1 = fishingActivity1.getCalculatedStartTime();
        Date calculatedStartTime2 = fishingActivity2.getCalculatedStartTime();

        return fluxLocationSchemeIdOrdering.compare(calculatedStartTime1, calculatedStartTime2);
    }
}
