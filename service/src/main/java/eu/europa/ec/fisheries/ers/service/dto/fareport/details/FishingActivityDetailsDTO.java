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

package eu.europa.ec.fisheries.ers.service.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 8/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FishingActivityDetailsDTO {

    @JsonProperty("sourceVesselId")
    private String sourceVesselId;

    @JsonProperty("sourceVesselTypeCode")
    private String sourceVesselTypeCode;

    @JsonProperty("destVesselId")
    private String destVesselId;

    @JsonProperty("destVesselTypeCode")
    private String destVesselTypeCode;

    @JsonProperty("activityTypeCode")
    private String activityTypeCode;

    @JsonProperty("occurence")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date occurence;

    @JsonProperty("reasonCode")
    private String reasonCode;

    @JsonProperty("vesselActivityCode")
    private String vesselActivityCode;

    @JsonProperty("fisheryTypeCode")
    private String fisheryTypeCode;

    @JsonProperty("speciesTargetCode")
    private String speciesTargetCode;

    @JsonProperty("operationQuantity")
    private Long operationQuantity;

    @JsonProperty("fishingDurationMeasure")
    private Double fishingDurationMeasure;

    @JsonProperty("flapDocumentId")
    private String flapDocumentId;

    @JsonProperty("fishingActivityIds")
    private List<String> fishingActivityIds;

    @JsonProperty("fishingTrip")
    private FishingTripDetailsDTO fishingTrip;

    @JsonProperty("faCatches")
    private List<FaCatchDetailsDTO> faCatches;

    @JsonProperty("fishingGears")
    private List<FishingGearDetailsDTO> fishingGears;

    @JsonProperty("gearProblems")
    private List<GearProblemDetailsDTO> gearProblems;

    @JsonProperty("delimitedPeriods")
    private List<DelimitedPeriodDetailsDTO> delimitedPeriods;

    @JsonProperty("fluxCharacteristics")
    private List<FluxCharacteristicsDetailsDTO> fluxCharacteristics;

    @JsonProperty("fluxLocations")
    private List<FluxLocationDetailsDTO> fluxLocations;

    public FishingActivityDetailsDTO() {
    }

    public FishingActivityDetailsDTO(String sourceVesselId, String sourceVesselTypeCode, String destVesselId, String destVesselTypeCode, String activityTypeCode, Date occurence, String reasonCode, String vesselActivityCode, String fisheryTypeCode, String speciesTargetCode, Long operationQuantity, Double fishingDurationMeasure, String flapDocumentId, List<String> fishingActivityIds, FishingTripDetailsDTO fishingTrip, List<FaCatchDetailsDTO> faCatches, List<FishingGearDetailsDTO> fishingGears, List<GearProblemDetailsDTO> gearProblems, List<DelimitedPeriodDetailsDTO> delimitedPeriods, List<FluxCharacteristicsDetailsDTO> fluxCharacteristics, List<FluxLocationDetailsDTO> fluxLocations) {
        this.sourceVesselId = sourceVesselId;
        this.sourceVesselTypeCode = sourceVesselTypeCode;
        this.destVesselId = destVesselId;
        this.destVesselTypeCode = destVesselTypeCode;
        this.activityTypeCode = activityTypeCode;
        this.occurence = occurence;
        this.reasonCode = reasonCode;
        this.vesselActivityCode = vesselActivityCode;
        this.fisheryTypeCode = fisheryTypeCode;
        this.speciesTargetCode = speciesTargetCode;
        this.operationQuantity = operationQuantity;
        this.fishingDurationMeasure = fishingDurationMeasure;
        this.flapDocumentId = flapDocumentId;
        this.fishingActivityIds = fishingActivityIds;
        this.fishingTrip = fishingTrip;
        this.faCatches = faCatches;
        this.fishingGears = fishingGears;
        this.gearProblems = gearProblems;
        this.delimitedPeriods = delimitedPeriods;
        this.fluxCharacteristics = fluxCharacteristics;
        this.fluxLocations = fluxLocations;
    }

    @JsonProperty("sourceVesselId")
    public String getSourceVesselId() {
        return sourceVesselId;
    }

    @JsonProperty("sourceVesselId")
    public void setSourceVesselId(String sourceVesselId) {
        this.sourceVesselId = sourceVesselId;
    }

    @JsonProperty("sourceVesselTypeCode")
    public String getSourceVesselTypeCode() {
        return sourceVesselTypeCode;
    }

    @JsonProperty("sourceVesselTypeCode")
    public void setSourceVesselTypeCode(String sourceVesselTypeCode) {
        this.sourceVesselTypeCode = sourceVesselTypeCode;
    }

    @JsonProperty("destVesselId")
    public String getDestVesselId() {
        return destVesselId;
    }

    @JsonProperty("destVesselId")
    public void setDestVesselId(String destVesselId) {
        this.destVesselId = destVesselId;
    }

    @JsonProperty("destVesselTypeCode")
    public String getDestVesselTypeCode() {
        return destVesselTypeCode;
    }

    @JsonProperty("destVesselTypeCode")
    public void setDestVesselTypeCode(String destVesselTypeCode) {
        this.destVesselTypeCode = destVesselTypeCode;
    }

    @JsonProperty("activityTypeCode")
    public String getActivityTypeCode() {
        return activityTypeCode;
    }

    @JsonProperty("activityTypeCode")
    public void setActivityTypeCode(String activityTypeCode) {
        this.activityTypeCode = activityTypeCode;
    }

    @JsonProperty("occurence")
    public Date getOccurence() {
        return occurence;
    }

    @JsonProperty("occurence")
    public void setOccurence(Date occurence) {
        this.occurence = occurence;
    }

    @JsonProperty("reasonCode")
    public String getReasonCode() {
        return reasonCode;
    }

    @JsonProperty("reasonCode")
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    @JsonProperty("vesselActivityCode")
    public String getVesselActivityCode() {
        return vesselActivityCode;
    }

    @JsonProperty("vesselActivityCode")
    public void setVesselActivityCode(String vesselActivityCode) {
        this.vesselActivityCode = vesselActivityCode;
    }

    @JsonProperty("fisheryTypeCode")
    public String getFisheryTypeCode() {
        return fisheryTypeCode;
    }

    @JsonProperty("fisheryTypeCode")
    public void setFisheryTypeCode(String fisheryTypeCode) {
        this.fisheryTypeCode = fisheryTypeCode;
    }

    @JsonProperty("speciesTargetCode")
    public String getSpeciesTargetCode() {
        return speciesTargetCode;
    }

    @JsonProperty("speciesTargetCode")
    public void setSpeciesTargetCode(String speciesTargetCode) {
        this.speciesTargetCode = speciesTargetCode;
    }

    @JsonProperty("operationQuantity")
    public Long getOperationQuantity() {
        return operationQuantity;
    }

    @JsonProperty("operationQuantity")
    public void setOperationQuantity(Long operationQuantity) {
        this.operationQuantity = operationQuantity;
    }

    @JsonProperty("fishingDurationMeasure")
    public Double getFishingDurationMeasure() {
        return fishingDurationMeasure;
    }

    @JsonProperty("fishingDurationMeasure")
    public void setFishingDurationMeasure(Double fishingDurationMeasure) {
        this.fishingDurationMeasure = fishingDurationMeasure;
    }

    @JsonProperty("flapDocumentId")
    public String getFlapDocumentId() {
        return flapDocumentId;
    }

    @JsonProperty("flapDocumentId")
    public void setFlapDocumentId(String flapDocumentId) {
        this.flapDocumentId = flapDocumentId;
    }

    @JsonProperty("fishingActivityIds")
    public List<String> getFishingActivityIds() {
        return fishingActivityIds;
    }

    @JsonProperty("fishingActivityIds")
    public void setFishingActivityIds(List<String> fishingActivityIds) {
        this.fishingActivityIds = fishingActivityIds;
    }

    @JsonProperty("fishingTrip")
    public FishingTripDetailsDTO getFishingTrip() {
        return fishingTrip;
    }

    @JsonProperty("fishingTrip")
    public void setFishingTrip(FishingTripDetailsDTO fishingTrip) {
        this.fishingTrip = fishingTrip;
    }

    @JsonProperty("faCatches")
    public List<FaCatchDetailsDTO> getFaCatches() {
        return faCatches;
    }

    @JsonProperty("faCatches")
    public void setFaCatches(List<FaCatchDetailsDTO> faCatches) {
        this.faCatches = faCatches;
    }

    @JsonProperty("fishingGears")
    public List<FishingGearDetailsDTO> getFishingGears() {
        return fishingGears;
    }

    @JsonProperty("fishingGears")
    public void setFishingGears(List<FishingGearDetailsDTO> fishingGears) {
        this.fishingGears = fishingGears;
    }

    @JsonProperty("gearProblems")
    public List<GearProblemDetailsDTO> getGearProblems() {
        return gearProblems;
    }

    @JsonProperty("gearProblems")
    public void setGearProblems(List<GearProblemDetailsDTO> gearProblems) {
        this.gearProblems = gearProblems;
    }

    @JsonProperty("delimitedPeriods")
    public List<DelimitedPeriodDetailsDTO> getDelimitedPeriods() {
        return delimitedPeriods;
    }

    @JsonProperty("delimitedPeriods")
    public void setDelimitedPeriods(List<DelimitedPeriodDetailsDTO> delimitedPeriods) {
        this.delimitedPeriods = delimitedPeriods;
    }

    @JsonProperty("fluxCharacteristics")
    public List<FluxCharacteristicsDetailsDTO> getFluxCharacteristics() {
        return fluxCharacteristics;
    }

    @JsonProperty("fluxCharacteristics")
    public void setFluxCharacteristics(List<FluxCharacteristicsDetailsDTO> fluxCharacteristics) {
        this.fluxCharacteristics = fluxCharacteristics;
    }

    @JsonProperty("fluxLocations")
    public List<FluxLocationDetailsDTO> getFluxLocations() {
        return fluxLocations;
    }

    @JsonProperty("fluxLocations")
    public void setFluxLocations(List<FluxLocationDetailsDTO> fluxLocations) {
        this.fluxLocations = fluxLocations;
    }
}
