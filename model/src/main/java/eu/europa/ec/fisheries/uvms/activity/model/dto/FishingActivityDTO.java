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

import java.util.Date;
import java.util.List;

/**
 * Created by sanera on 09/08/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FishingActivityDTO {

    @JsonProperty("uniqueReportId")
    private String uniqueReportId;

    @JsonProperty("fishingActivityId")
    private int fishingActivityId;

    @JsonProperty("activityType")
    private String activityType;

    @JsonProperty("occurence")
    private Date occurence;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("fluxLocations")
    private List<FluxLocationDTO> fluxLocations;

    @JsonProperty("fishingGears")
    private List<FishingGearDTO> fishingGears;

    @JsonProperty("fluxCharacteristics")
    private List<FluxCharacteristicsDTO> fluxCharacteristics;

    @JsonProperty("delimitedPeriod")
    private List<DelimitedPeriodDTO> delimitedPeriod;

    @JsonProperty("uniqueReportId")
    public String getUniqueReportId() {
        return uniqueReportId;
    }

    @JsonProperty("uniqueReportId")
    public void setUniqueReportId(String uniqueReportId) {
        this.uniqueReportId = uniqueReportId;
    }

    @JsonProperty("fishingActivityId")
    public int getFishingActivityId() {
        return fishingActivityId;
    }

    @JsonProperty("fishingActivityId")
    public void setFishingActivityId(int fishingActivityId) {
        this.fishingActivityId = fishingActivityId;
    }

    @JsonProperty("activityType")
    public String getActivityType() {
        return activityType;
    }

    @JsonProperty("activityType")
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    @JsonProperty("occurence")
    public Date getOccurence() {
        return occurence;
    }

    @JsonProperty("occurence")
    public void setOccurence(Date occurence) {
        this.occurence = occurence;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    @JsonProperty("reason")
    public void setReason(String reason) {
        this.reason = reason;
    }

    @JsonProperty("fluxLocations")
    public List<FluxLocationDTO> getFluxLocations() {
        return fluxLocations;
    }

    @JsonProperty("fluxLocations")
    public void setFluxLocations(List<FluxLocationDTO> fluxLocations) {
        this.fluxLocations = fluxLocations;
    }

    @JsonProperty("fishingGears")
    public List<FishingGearDTO> getFishingGears() {
        return fishingGears;
    }

    @JsonProperty("fishingGears")
    public void setFishingGears(List<FishingGearDTO> fishingGears) {
        this.fishingGears = fishingGears;
    }

    @JsonProperty("fluxCharacteristics")
    public List<FluxCharacteristicsDTO> getFluxCharacteristics() {
        return fluxCharacteristics;
    }

    @JsonProperty("fluxCharacteristics")
    public void setFluxCharacteristics(List<FluxCharacteristicsDTO> fluxCharacteristics) {
        this.fluxCharacteristics = fluxCharacteristics;
    }

    @JsonProperty("delimitedPeriod")
    public List<DelimitedPeriodDTO> getDelimitedPeriod() {
        return delimitedPeriod;
    }

    @JsonProperty("delimitedPeriod")
    public void setDelimitedPeriod(List<DelimitedPeriodDTO> delimitedPeriod) {
        this.delimitedPeriod = delimitedPeriod;
    }
}
