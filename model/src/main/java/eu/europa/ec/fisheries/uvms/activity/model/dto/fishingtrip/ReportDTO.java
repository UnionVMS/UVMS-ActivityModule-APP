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

import java.util.Date;
import java.util.List;

/**
 * Created by sanera on 04/08/2016.
 */
public class ReportDTO {

    private int fishingActivityId;
    private Date occurence;
    private Date faReportAcceptedDateTime;
    private String activityType;
    private String reason;
    private String faReportDocumentType;
    private List<FluxLocationDTO> fluxLocations;
    private List<FishingGearDTO> fishingGears;
    private List<FluxCharacteristicsDTO> fluxCharacteristics;
    private List<DelimitedPeriodDTO> delimitedPeriod;



    public int getFishingActivityId() {
        return fishingActivityId;
    }

    public void setFishingActivityId(int fishingActivityId) {
        this.fishingActivityId = fishingActivityId;
    }

    public Date getOccurence() {
        return occurence;
    }

    public void setOccurence(Date occurence) {
        this.occurence = occurence;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public List<FluxLocationDTO> getFluxLocations() {
        return fluxLocations;
    }

    public void setFluxLocations(List<FluxLocationDTO> fluxLocations) {
        this.fluxLocations = fluxLocations;
    }

    public List<FishingGearDTO> getFishingGears() {
        return fishingGears;
    }

    public void setFishingGears(List<FishingGearDTO> fishingGears) {
        this.fishingGears = fishingGears;
    }

    public List<FluxCharacteristicsDTO> getFluxCharacteristics() {
        return fluxCharacteristics;
    }

    public void setFluxCharacteristics(List<FluxCharacteristicsDTO> fluxCharacteristics) {
        this.fluxCharacteristics = fluxCharacteristics;
    }

    public List<DelimitedPeriodDTO> getDelimitedPeriod() {
        return delimitedPeriod;
    }

    public void setDelimitedPeriod(List<DelimitedPeriodDTO> delimitedPeriod) {
        this.delimitedPeriod = delimitedPeriod;
    }

    public String getFaReportDocumentType() {
        return faReportDocumentType;
    }

    public void setFaReportDocumentType(String faReportDocumentType) {
        this.faReportDocumentType = faReportDocumentType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getFaReportAcceptedDateTime() {
        return faReportAcceptedDateTime;
    }

    public void setFaReportAcceptedDateTime(Date faReportAcceptedDateTime) {
        this.faReportAcceptedDateTime = faReportAcceptedDateTime;
    }
}
