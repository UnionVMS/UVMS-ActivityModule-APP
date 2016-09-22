/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by padhyad on 9/22/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CronologyTripDTO {

    @JsonProperty("currentTrip")
    private String currentTrip;

    @JsonProperty("previousTrips")
    private List<String> previousTrips;

    @JsonProperty("selectedTrip")
    private String selectedTrip;

    @JsonProperty("nextTrips")
    private List<String> nextTrips;

    @JsonProperty("currentTrip")
    public String getCurrentTrip() {
        return currentTrip;
    }

    @JsonProperty("currentTrip")
    public void setCurrentTrip(String currentTrip) {
        this.currentTrip = currentTrip;
    }

    @JsonProperty("previousTrips")
    public List<String> getPreviousTrips() {
        return previousTrips;
    }

    @JsonProperty("previousTrips")
    public void setPreviousTrips(List<String> previousTrips) {
        this.previousTrips = previousTrips;
    }

    @JsonProperty("selectedTrip")
    public String getSelectedTrip() {
        return selectedTrip;
    }

    @JsonProperty("selectedTrip")
    public void setSelectedTrip(String selectedTrip) {
        this.selectedTrip = selectedTrip;
    }

    @JsonProperty("nextTrips")
    public List<String> getNextTrips() {
        return nextTrips;
    }

    @JsonProperty("nextTrips")
    public void setNextTrips(List<String> nextTrips) {
        this.nextTrips = nextTrips;
    }
}
