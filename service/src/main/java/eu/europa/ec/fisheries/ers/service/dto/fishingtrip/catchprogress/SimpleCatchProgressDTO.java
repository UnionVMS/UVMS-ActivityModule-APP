package eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class SimpleCatchProgressDTO extends CatchProgressDTO {

    Map<String, Double> total = new HashMap<>();

    public SimpleCatchProgressDTO(String activityType, String reportType, boolean affectsCumulative, Date date) {
        super(activityType, reportType, affectsCumulative, date);
    }

    public void updateTotal(String species, Double weight){
        total.merge(species, weight, ADD);
    }

    public Map<String, Double> getTotal() {
        return total;
    }

}
