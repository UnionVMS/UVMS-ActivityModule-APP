/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FluxLocationDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by sanera on 17/08/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FishingTripSummaryDTO {

    @JsonProperty("departureDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date departureDate;

    @JsonProperty("departureLocations")
    private List<FluxLocationDTO> departureLocations;


    @JsonProperty("arrivalDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date arrivalDate;

    @JsonProperty("arrivalLocations")
    private List<FluxLocationDTO> arrivalLocations;

    @JsonProperty("landingDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date landingDate;

    @JsonProperty("landingLocations")
    private List<FluxLocationDTO> landingLocations;

    @JsonProperty("departureDate")
    public Date getDepartureDate() {
        return departureDate;
    }

    @JsonProperty("departureDate")
    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    @JsonProperty("departureLocations")
    public List<FluxLocationDTO> getDepartureLocations() {
        return departureLocations;
    }

    @JsonProperty("departureLocations")
    public void setDepartureLocations(List<FluxLocationDTO> departureLocations) {
        this.departureLocations = departureLocations;
    }

    @JsonProperty("arrivalDate")
    public Date getArrivalDate() {
        return arrivalDate;
    }

    @JsonProperty("arrivalDate")
    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    @JsonProperty("arrivalLocations")
    public List<FluxLocationDTO> getArrivalLocations() {
        return arrivalLocations;
    }

    @JsonProperty("arrivalLocations")
    public void setArrivalLocations(List<FluxLocationDTO> arrivalLocations) {
        this.arrivalLocations = arrivalLocations;
    }

    @JsonProperty("landingDate")
    public Date getLandingDate() {
        return landingDate;
    }

    @JsonProperty("landingDate")
    public void setLandingDate(Date landingDate) {
        this.landingDate = landingDate;
    }

    @JsonProperty("landingLocations")
    public List<FluxLocationDTO> getLandingLocations() {
        return landingLocations;
    }

    @JsonProperty("landingLocations")
    public void setLandingLocations(List<FluxLocationDTO> landingLocations) {
        this.landingLocations = landingLocations;
    }
}
