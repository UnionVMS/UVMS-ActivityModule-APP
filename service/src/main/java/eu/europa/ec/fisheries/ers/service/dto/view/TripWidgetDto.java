/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
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
