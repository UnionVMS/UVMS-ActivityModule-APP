package eu.europa.ec.fisheries.ers.service.catchevolution;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.FishingActivityCalculatedDateComparator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TripCatchEvolutionTest {

    @Test
    public void testFishingActivityCalculatedDateComparator() {
        FishingActivityEntity fishingActivityEntity1 = new FishingActivityEntity();
        fishingActivityEntity1.setCalculatedStartTime(new Date(1000002));
        FishingActivityEntity fishingActivityEntity2 = new FishingActivityEntity();
        fishingActivityEntity2.setCalculatedStartTime(new Date(1000000));
        FishingActivityEntity fishingActivityEntity3 = new FishingActivityEntity();
        fishingActivityEntity3.setCalculatedStartTime(new Date(1000001));
        FishingActivityEntity fishingActivityEntity4 = new FishingActivityEntity();
        fishingActivityEntity4.setCalculatedStartTime(null);

        List<FishingActivityEntity> fishingActivities = Arrays.asList(fishingActivityEntity1, fishingActivityEntity2, fishingActivityEntity3, fishingActivityEntity4);

        Collections.sort(fishingActivities, new FishingActivityCalculatedDateComparator());

        Assert.assertEquals(fishingActivities.get(0).getCalculatedStartTime().getTime(), fishingActivityEntity2.getCalculatedStartTime().getTime());
        Assert.assertEquals(fishingActivities.get(2).getCalculatedStartTime().getTime(), fishingActivityEntity1.getCalculatedStartTime().getTime());
        Assert.assertEquals(fishingActivities.get(3).getCalculatedStartTime(), null);
    }

}
