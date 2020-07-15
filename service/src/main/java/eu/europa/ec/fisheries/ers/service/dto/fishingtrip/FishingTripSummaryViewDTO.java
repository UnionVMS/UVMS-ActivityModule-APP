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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FishingTripSummaryViewDTO {

    @JsonProperty("fishingTripId")
    private String fishingTripId;

    @JsonProperty("summary")
    private Map<String,FishingActivityTypeDTO> summary;

    @JsonProperty("activityReports")
    private List<ReportDTO> activityReports;

    private VesselDetailsDTO vesselDetails;

    @JsonProperty("messagesCount")
    private MessageCountDTO messagesCount;

    @JsonProperty("chronology")
    private List<ChronologyDTO> chronology;

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

    @JsonProperty("messagesCount")
    public MessageCountDTO getMessagesCount() {
        return messagesCount;
    }

    @JsonProperty("messagesCount")
    public void setMessagesCount(MessageCountDTO messagesCount) {
        this.messagesCount = messagesCount;
    }

    @JsonProperty("chronology")
    public List<ChronologyDTO> getChronology() {
        return chronology;
    }

    @JsonProperty("chronology")
    public void setChronology(List<ChronologyDTO> chronology) {
        this.chronology = chronology;
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

    public VesselDetailsDTO getVesselDetails() {
        return vesselDetails;
    }

    public void setVesselDetails(VesselDetailsDTO vesselDetails) {
        this.vesselDetails = vesselDetails;
    }
}
