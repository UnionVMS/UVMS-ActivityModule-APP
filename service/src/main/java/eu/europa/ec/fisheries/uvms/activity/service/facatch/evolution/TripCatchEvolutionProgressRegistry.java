package eu.europa.ec.fisheries.uvms.activity.service.facatch.evolution;

import eu.europa.ec.fisheries.uvms.activity.fa.utils.FishingActivityTypeEnum;

public class TripCatchEvolutionProgressRegistry extends CatchEvolutionProgressRegistry {
    @Override
    public void initRegistry() {
        addToRegistry(FishingActivityTypeEnum.DEPARTURE, new DEPCatchEvolutionProgressHandler());
        addToRegistry(FishingActivityTypeEnum.AREA_ENTRY, new ENTRYCatchEvolutionProgressHandler());
        addToRegistry(FishingActivityTypeEnum.AREA_EXIT, new EXITCatchEvolutionProgressHandler());
        addToRegistry(FishingActivityTypeEnum.FISHING_OPERATION, new FOPCatchEvolutionProgressHandler());
        addToRegistry(FishingActivityTypeEnum.JOINED_FISHING_OPERATION, new JFOPCatchEvolutionProgressHandler());
        addToRegistry(FishingActivityTypeEnum.DISCARD, new DISCatchEvolutionProgressHandler());
        addToRegistry(FishingActivityTypeEnum.RELOCATION, new RLCCatchEvolutionProgressHandler());
        addToRegistry(FishingActivityTypeEnum.ARRIVAL, new ARRCatchEvolutionProgressHandler());
        addToRegistry(FishingActivityTypeEnum.TRANSHIPMENT, new TRACatchEvolutionProgressHandler());
        addToRegistry(FishingActivityTypeEnum.LANDING, new LANCatchEvolutionProgressHandler());
    }
}
