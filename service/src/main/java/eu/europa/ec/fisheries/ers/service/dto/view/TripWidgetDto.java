/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.dto.view;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.CommonView;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.FlapDocumentDto;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;

@JsonInclude(NON_NULL)
public class TripWidgetDto {

    @JsonView(CommonView.class)
    private VesselDetailsDTO vesselDetails;

    @JsonView(CommonView.class)
    @JsonProperty("authorizations")
    private Set<FlapDocumentDto> flapDocuments;

    @JsonView(CommonView.class)
    private List<TripOverviewDto> trips;

    public VesselDetailsDTO getVesselDetails() {
        return vesselDetails;
    }

    public void setVesselDetails(VesselDetailsDTO vesselDetails) {
        this.vesselDetails = vesselDetails;
    }

    public List<TripOverviewDto> getTrips() {
        return trips;
    }

    public void setTrips(List<TripOverviewDto> trips) {
        this.trips = trips;
    }

    public Set<FlapDocumentDto> getFlapDocuments() {
        return flapDocuments;
    }

    public void setFlapDocuments(Set<FlapDocumentDto> flapDocuments) {
        this.flapDocuments = flapDocuments;
    }
}
