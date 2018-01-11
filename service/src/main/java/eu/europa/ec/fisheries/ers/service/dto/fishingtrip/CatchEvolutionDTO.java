package eu.europa.ec.fisheries.ers.service.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.fisheries.ers.service.dto.view.TripWidgetDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CatchEvolutionDTO {
    @JsonProperty("catchProgress")
    private List<CatchEvolutionProgressDTO> catchEvolutionProgress;
    @JsonProperty("tripDetails")
    private TripWidgetDto tripDetails;

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
