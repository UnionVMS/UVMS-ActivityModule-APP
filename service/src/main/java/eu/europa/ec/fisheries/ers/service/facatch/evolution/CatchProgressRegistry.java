package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class CatchProgressRegistry {
    private Map<FishingActivityTypeEnum, CatchProgressHandler> registry = new HashMap<>(FishingActivityTypeEnum.values().length);

    public CatchProgressRegistry(){
        initRegistry();
    }

    public void addToRegistry(FishingActivityTypeEnum fishingActivityType, CatchProgressHandler catchProgressHandler) {
        if (fishingActivityType == null || catchProgressHandler == null) {
            throw new IllegalArgumentException("Neither parameter can be null!");
        }

        registry.put(fishingActivityType, catchProgressHandler);
    }

    public CatchProgressHandler findHandler(FishingActivityTypeEnum fishingActivityType) {
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
