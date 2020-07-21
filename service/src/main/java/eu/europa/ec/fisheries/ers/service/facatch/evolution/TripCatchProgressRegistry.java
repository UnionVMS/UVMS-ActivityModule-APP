package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;

public class TripCatchProgressRegistry extends CatchProgressRegistry {

    @Override
    public void initRegistry() {
        addToRegistry(FishingActivityTypeEnum.DEPARTURE, new DepCatchProgressHandler());
        addToRegistry(FishingActivityTypeEnum.AREA_ENTRY, new EntryCatchProgressHandler());
        addToRegistry(FishingActivityTypeEnum.AREA_EXIT, new ExitCatchProgressHandler());
        addToRegistry(FishingActivityTypeEnum.FISHING_OPERATION, new FopCatchProgressHandler());
        addToRegistry(FishingActivityTypeEnum.JOINT_FISHING_OPERATION, new JfopCatchProgressHandler());
        addToRegistry(FishingActivityTypeEnum.DISCARD, new DisCatchProgressHandler());
        addToRegistry(FishingActivityTypeEnum.RELOCATION, new RlcCatchProgressHandler());
        addToRegistry(FishingActivityTypeEnum.ARRIVAL, new ArrCatchProgressHandler());
        addToRegistry(FishingActivityTypeEnum.TRANSHIPMENT, new TraCatchProgressHandler());
        addToRegistry(FishingActivityTypeEnum.LANDING, new LanCatchProgressHandler());
    }

}
