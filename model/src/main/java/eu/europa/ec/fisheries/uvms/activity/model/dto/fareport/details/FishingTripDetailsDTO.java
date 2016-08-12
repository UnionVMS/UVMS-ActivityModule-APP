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

package eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by padhyad on 8/11/2016.
 */
public class FishingTripDetailsDTO {

    @JsonProperty("tripType")
    private String tripType;

    @JsonProperty("tripId")
    private List<String> tripIds;

    @JsonProperty("delimitedPeriods")
    private List<DelimitedPeriodDetailsDTO> delimitedPeriods;

    public FishingTripDetailsDTO() {
    }

    public FishingTripDetailsDTO(String tripType, List<String> tripIds, List<DelimitedPeriodDetailsDTO> delimitedPeriods) {
        this.tripType = tripType;
        this.tripIds = tripIds;
        this.delimitedPeriods = delimitedPeriods;
    }

    @JsonProperty("tripType")
    public String getTripType() {
        return tripType;
    }

    @JsonProperty("tripType")
    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    @JsonProperty("tripId")
    public List<String> getTripIds() {
        return tripIds;
    }

    @JsonProperty("tripId")
    public void setTripIds(List<String> tripIds) {
        this.tripIds = tripIds;
    }

    @JsonProperty("delimitedPeriods")
    public List<DelimitedPeriodDetailsDTO> getDelimitedPeriods() {
        return delimitedPeriods;
    }

    @JsonProperty("delimitedPeriods")
    public void setDelimitedPeriods(List<DelimitedPeriodDetailsDTO> delimitedPeriods) {
        this.delimitedPeriods = delimitedPeriods;
    }
}
