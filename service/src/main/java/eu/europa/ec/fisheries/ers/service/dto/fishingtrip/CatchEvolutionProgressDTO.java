package eu.europa.ec.fisheries.ers.service.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CatchEvolutionProgressDTO {
    private Map<String, CatchSummaryListDTO> catchEvolution;
    @JsonProperty("activityType")
    private String activityType;

    public Map<String, CatchSummaryListDTO> getCatchEvolution() {
        return catchEvolution;
    }

    public void setCatchEvolution(Map<String, CatchSummaryListDTO> catchEvolution) {
        this.catchEvolution = catchEvolution;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
}
