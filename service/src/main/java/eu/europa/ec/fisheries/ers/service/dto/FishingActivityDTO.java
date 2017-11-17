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

package eu.europa.ec.fisheries.ers.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.FluxLocationDetailsDTO;
import eu.europa.ec.fisheries.uvms.commons.date.CustomDateSerializer;

import java.util.Date;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class FishingActivityDTO {

    @JsonProperty("uniqueReportIdList")
    private List<FluxReportIdentifierDTO> uniqueFAReportId;

    @JsonProperty("fishingActivityId")
    private int fishingActivityId;

    @JsonProperty("faReportID")
    private int faReportID;

    @JsonProperty("faUniqueReportID")
    private String faUniqueReportID;

    @JsonProperty("faUniqueReportSchemeID")
    private String faUniqueReportSchemeID;

    @JsonProperty("faReferenceID")
    private String faReferenceID;

    @JsonProperty("faReferenceSchemeID")
    private String faReferenceSchemeID;

    @JsonProperty("activityType")
    private String activityType;

    @JsonProperty("geometry")
    private String geometry;

    @JsonProperty("occurence")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date occurence;

    @JsonProperty("reason")
    private String reason;

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    @JsonProperty("purposeCode")
    private String purposeCode;

    @JsonProperty("fluxLocations")
    private List<FluxLocationDetailsDTO> fluxLocations;

    @JsonProperty("fishingGears")
    private List<FishingGearDTO> fishingGears;

    @JsonProperty("fluxCharacteristics")
    private List<FluxCharacteristicsDto> fluxCharacteristics;

    @JsonProperty("delimitedPeriod")
    private List<DelimitedPeriodDTO> delimitedPeriod;

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
    public List<FluxLocationDetailsDTO> getFluxLocations() {
        return fluxLocations;
    }

    @JsonProperty("fluxLocations")
    public void setFluxLocations(List<FluxLocationDetailsDTO> fluxLocations) {
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
    public List<FluxCharacteristicsDto> getFluxCharacteristics() {
        return fluxCharacteristics;
    }

    @JsonProperty("fluxCharacteristics")
    public void setFluxCharacteristics(List<FluxCharacteristicsDto> fluxCharacteristics) {
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

    @JsonProperty("uniqueReportIdList")
    public List<FluxReportIdentifierDTO> getUniqueFAReportId() {
        return uniqueFAReportId;
    }

    @JsonProperty("uniqueReportIdList")
    public void setUniqueFAReportId(List<FluxReportIdentifierDTO> uniqueFAReportId) {
        this.uniqueFAReportId = uniqueFAReportId;
    }

    @JsonProperty("faReportID")
    public int getFaReportID() {
        return faReportID;
    }

    @JsonProperty("faReportID")
    public void setFaReportID(int faReportID) {
        this.faReportID = faReportID;
    }

    public int getFishingActivityId() {
        return fishingActivityId;
    }

    public void setFishingActivityId(int fishingActivityId) {
        this.fishingActivityId = fishingActivityId;
    }

    public String getFaUniqueReportID() {
        return faUniqueReportID;
    }

    public void setFaUniqueReportID(String faUniqueReportID) {
        this.faUniqueReportID = faUniqueReportID;
    }

    public String getFaUniqueReportSchemeID() {
        return faUniqueReportSchemeID;
    }

    public void setFaUniqueReportSchemeID(String faUniqueReportSchemeID) {
        this.faUniqueReportSchemeID = faUniqueReportSchemeID;
    }

    public String getFaReferenceID() {
        return faReferenceID;
    }

    public void setFaReferenceID(String faReferenceID) {
        this.faReferenceID = faReferenceID;
    }

    public String getFaReferenceSchemeID() {
        return faReferenceSchemeID;
    }

    public void setFaReferenceSchemeID(String faReferenceSchemeID) {
        this.faReferenceSchemeID = faReferenceSchemeID;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }
}
