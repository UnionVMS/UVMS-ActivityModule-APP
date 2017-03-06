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

package eu.europa.ec.fisheries.ers.service.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 17/08/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FishingTripSummaryViewDTO {

    @JsonProperty("fishingTripId")
    private String fishingTripId;


    @JsonProperty("summary")
    private Map<String,FishingActivityTypeDTO> summary;

    @JsonProperty("activityReports")
    private List<ReportDTO> activityReports;

    @JsonProperty("vesselDetails")
    private VesselDetailsTripDTO vesselDetails;

    @JsonProperty("messagesCount")
    private MessageCountDTO messagesCount;

    @JsonProperty("cronology")
    private List<CronologyDTO> cronology;

    @JsonProperty("currentTripId")
    private String currentTripId;


    @JsonProperty("fishingTripId")
    public String getFishingTripId() {
        return fishingTripId;
    }

    @JsonProperty("fishingTripId")
    public void setFishingTripId(String fishingTripId) {
        this.fishingTripId = fishingTripId;
    }



    @JsonProperty("activityReports")
    public List<ReportDTO> getActivityReports() {
        return activityReports;
    }

    @JsonProperty("activityReports")
    public void setActivityReports(List<ReportDTO> activityReports) {
        this.activityReports = activityReports;
    }

    @JsonProperty("vesselDetails")
    public VesselDetailsTripDTO getVesselDetails() {
        return vesselDetails;
    }

    @JsonProperty("vesselDetails")
    public void setVesselDetails(VesselDetailsTripDTO vesselDetails) {
        this.vesselDetails = vesselDetails;
    }

    @JsonProperty("messagesCount")
    public MessageCountDTO getMessagesCount() {
        return messagesCount;
    }

    @JsonProperty("messagesCount")
    public void setMessagesCount(MessageCountDTO messagesCount) {
        this.messagesCount = messagesCount;
    }

    @JsonProperty("cronology")
    public List<CronologyDTO> getCronology() {
        return cronology;
    }

    @JsonProperty("cronology")
    public void setCronology(List<CronologyDTO> cronology) {
        this.cronology = cronology;
    }

    @JsonProperty("currentTripId")
    public String getCurrentTripId() {
        return currentTripId;
    }

    @JsonProperty("currentTripId")
    public void setCurrentTripId(String currentTripId) {
        this.currentTripId = currentTripId;
    }

    @JsonProperty("summary")
    public Map<String, FishingActivityTypeDTO> getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(Map<String, FishingActivityTypeDTO> summary) {
        this.summary = summary;
    }
}
