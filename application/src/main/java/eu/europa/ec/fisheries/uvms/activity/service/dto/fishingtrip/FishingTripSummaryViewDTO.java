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

package eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip;

import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;
import java.util.Map;

public class FishingTripSummaryViewDTO {

    @JsonbProperty("fishingTripId")
    private String fishingTripId;

    @JsonbProperty("summary")
    private Map<String,FishingActivityTypeDTO> summary;

    @JsonbProperty("activityReports")
    private List<ReportDTO> activityReports;

    private VesselDetailsDTO vesselDetails;

    @JsonbProperty("messagesCount")
    private MessageCountDTO messagesCount;

    @JsonbProperty("cronology")
    private List<CronologyDTO> cronology;

    @JsonbProperty("currentTripId")
    private String currentTripId;

    @JsonbProperty("fishingTripId")
    public String getFishingTripId() {
        return fishingTripId;
    }

    @JsonbProperty("fishingTripId")
    public void setFishingTripId(String fishingTripId) {
        this.fishingTripId = fishingTripId;
    }

    @JsonbProperty("activityReports")
    public List<ReportDTO> getActivityReports() {
        return activityReports;
    }

    @JsonbProperty("activityReports")
    public void setActivityReports(List<ReportDTO> activityReports) {
        this.activityReports = activityReports;
    }

    @JsonbProperty("messagesCount")
    public MessageCountDTO getMessagesCount() {
        return messagesCount;
    }

    @JsonbProperty("messagesCount")
    public void setMessagesCount(MessageCountDTO messagesCount) {
        this.messagesCount = messagesCount;
    }

    @JsonbProperty("cronology")
    public List<CronologyDTO> getCronology() {
        return cronology;
    }

    @JsonbProperty("cronology")
    public void setCronology(List<CronologyDTO> cronology) {
        this.cronology = cronology;
    }

    @JsonbProperty("currentTripId")
    public String getCurrentTripId() {
        return currentTripId;
    }

    @JsonbProperty("currentTripId")
    public void setCurrentTripId(String currentTripId) {
        this.currentTripId = currentTripId;
    }

    @JsonbProperty("summary")
    public Map<String, FishingActivityTypeDTO> getSummary() {
        return summary;
    }

    @JsonbProperty("summary")
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
