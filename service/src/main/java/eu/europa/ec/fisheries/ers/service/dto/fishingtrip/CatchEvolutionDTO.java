package eu.europa.ec.fisheries.ers.service.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.fisheries.ers.service.dto.view.TripWidgetDto;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CatchEvolutionDTO {
    @JsonProperty("finalCatch")
    private Map<String, CatchEvolutionSummaryListDTO> finalCatchEvolutionSummary;
    @JsonProperty("catchProgress")
    private List<CatchEvolutionProgressDTO> catchEvolutionProgress;
    @JsonProperty("tripDetails")
    private TripWidgetDto tripDetails;

    public Map<String, CatchEvolutionSummaryListDTO> getFinalCatchEvolutionSummary() {
        return finalCatchEvolutionSummary;
    }

    public void setFinalCatchEvolutionSummary(Map<String, CatchEvolutionSummaryListDTO> finalCatchEvolutionSummary) {
        this.finalCatchEvolutionSummary = finalCatchEvolutionSummary;
    }

    public List<CatchEvolutionProgressDTO> getCatchEvolutionProgress() {
        return catchEvolutionProgress;
    }

    public void setCatchEvolutionProgress(List<CatchEvolutionProgressDTO> catchEvolutionProgress) {
        this.catchEvolutionProgress = catchEvolutionProgress;
    }

    public TripWidgetDto getTripDetails() {
        return tripDetails;
    }

    public void setTripDetails(TripWidgetDto tripDetails) {
        this.tripDetails = tripDetails;
    }
}
