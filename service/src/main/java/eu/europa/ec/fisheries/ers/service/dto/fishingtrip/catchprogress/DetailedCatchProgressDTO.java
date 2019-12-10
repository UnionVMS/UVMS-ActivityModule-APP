package eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class DetailedCatchProgressDTO extends CatchProgressDTO{

    private Map<String, Double> loaded = new HashMap<>();
    private Map<String, Double> onBoard = new HashMap<>();
    private Map<String, Double> unLoaded = new HashMap<>();

    public DetailedCatchProgressDTO(String activityType, String reportType, boolean affectsCumulative, Date date) {
        super(activityType, reportType, affectsCumulative, date);
    }

    public Map<String, Double> getLoaded() {
        return loaded;
    }

    public void addLoaded(String species, Double weight) {
        loaded.merge(species, weight, ADD);
    }

    public Map<String, Double> getOnBoard() {
        return onBoard;
    }

    public void addOnboard(String species, Double weight) {
        onBoard.merge(species, weight, ADD);
    }

    public Map<String, Double> getUnLoaded() {
        return unLoaded;
    }

    public void addUnloaded(String species, Double weight) {
        unLoaded.merge(species, weight, ADD);
    }

}
