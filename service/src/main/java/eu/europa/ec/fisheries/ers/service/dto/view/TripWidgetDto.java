package eu.europa.ec.fisheries.ers.service.dto.view;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView;

import java.util.List;

/**
 * Created by sanera on 23/03/2017.
 */
public class TripWidgetDto {

    @JsonView(FishingActivityView.CommonView.class)
    private VesselDetailsDTO vesselDetails;

    @JsonView(FishingActivityView.CommonView.class)
    private List<TripOverviewDto> tripOverviewDto;

    public VesselDetailsDTO getVesselDetails() {
        return vesselDetails;
    }

    public void setVesselDetails(VesselDetailsDTO vesselDetails) {
        this.vesselDetails = vesselDetails;
    }

    public List<TripOverviewDto> getTripOverviewDto() {
        return tripOverviewDto;
    }

    public void setTripOverviewDto(List<TripOverviewDto> tripOverviewDto) {
        this.tripOverviewDto = tripOverviewDto;
    }
}
