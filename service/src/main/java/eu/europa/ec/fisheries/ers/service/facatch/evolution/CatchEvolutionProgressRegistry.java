package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class CatchEvolutionProgressRegistry {
    private Map<FishingActivityTypeEnum, CatchEvolutionProgressHandler> registry = new HashMap<>(FishingActivityTypeEnum.values().length);

    public void addToRegistry(FishingActivityTypeEnum fishingActivityType, CatchEvolutionProgressHandler catchEvolutionProgressHandler) {
        if (fishingActivityType == null || catchEvolutionProgressHandler == null) {
            throw new IllegalArgumentException("Neither parameter can be null!");
        }

        registry.put(fishingActivityType, catchEvolutionProgressHandler);
    }

    public CatchEvolutionProgressHandler findHandler(FishingActivityTypeEnum fishingActivityType) {
        return fishingActivityType != null ? registry.get(fishingActivityType) : null;
    }

    public boolean containsHandler(FishingActivityTypeEnum fishingActivityType) {
        if (fishingActivityType == null) {
            return false;
        }

        return MapUtils.getObject(registry, fishingActivityType) != null;
    }

    public abstract void initRegistry();
}
