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

package eu.europa.ec.fisheries.uvms.activity.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanera on 04/08/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FluxLocationDTO {

    @JsonProperty("locationType")
    private String locationType;

    @JsonProperty("fluxLocationListId")
    private String fluxLocationListId;

    @JsonProperty("fluxLocationIdentifier")
    private String fluxLocationIdentifier;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("rfmoCode")
    private String rfmoCode;

    @JsonProperty("locationType")
    public String getLocationType() {
        return locationType;
    }

    @JsonProperty("locationType")
    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    @JsonProperty("fluxLocationListId")
    public String getFluxLocationListId() {
        return fluxLocationListId;
    }

    @JsonProperty("fluxLocationListId")
    public void setFluxLocationListId(String fluxLocationListId) {
        this.fluxLocationListId = fluxLocationListId;
    }

    @JsonProperty("fluxLocationIdentifier")
    public String getFluxLocationIdentifier() {
        return fluxLocationIdentifier;
    }

    @JsonProperty("fluxLocationIdentifier")
    public void setFluxLocationIdentifier(String fluxLocationIdentifier) {
        this.fluxLocationIdentifier = fluxLocationIdentifier;
    }

    @JsonProperty("longitude")
    public Double getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("latitude")
    public Double getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("rfmoCode")
    public String getRfmoCode() {
        return rfmoCode;
    }

    @JsonProperty("rfmoCode")
    public void setRfmoCode(String rfmoCode) {
        this.rfmoCode = rfmoCode;
    }
}
