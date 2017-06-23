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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by padhyad on 8/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GearProblemDetailsDTO {

    @JsonProperty("problemType")
    private String problemType;

    @JsonProperty("affectedQuantity")
    private Integer affectedQuantity;

    @JsonProperty("recoveryMeasure")
    private String recoveryMeasure;

    @JsonProperty("fishingGears")
    private List<FishingGearDetailsDTO> fishingGears;

    public GearProblemDetailsDTO() {
    }

    public GearProblemDetailsDTO(String problemType, Integer affectedQuantity, String recoveryMeasure, List<FishingGearDetailsDTO> fishingGears) {
        this.problemType = problemType;
        this.affectedQuantity = affectedQuantity;
        this.recoveryMeasure = recoveryMeasure;
        this.fishingGears = fishingGears;
    }

    @JsonProperty("problemType")
    public String getProblemType() {
        return problemType;
    }

    @JsonProperty("problemType")
    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    @JsonProperty("affectedQuantity")
    public Integer getAffectedQuantity() {
        return affectedQuantity;
    }

    @JsonProperty("affectedQuantity")
    public void setAffectedQuantity(Integer affectedQuantity) {
        this.affectedQuantity = affectedQuantity;
    }

    @JsonProperty("recoveryMeasure")
    public String getRecoveryMeasure() {
        return recoveryMeasure;
    }

    @JsonProperty("recoveryMeasure")
    public void setRecoveryMeasure(String recoveryMeasure) {
        this.recoveryMeasure = recoveryMeasure;
    }

    @JsonProperty("fishingGears")
    public List<FishingGearDetailsDTO> getFishingGears() {
        return fishingGears;
    }

    @JsonProperty("fishingGears")
    public void setFishingGears(List<FishingGearDetailsDTO> fishingGears) {
        this.fishingGears = fishingGears;
    }
}
