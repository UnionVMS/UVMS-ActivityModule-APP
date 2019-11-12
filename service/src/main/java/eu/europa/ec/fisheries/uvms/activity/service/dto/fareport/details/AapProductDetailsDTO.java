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

package eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by padhyad on 8/12/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AapProductDetailsDTO {

    @JsonProperty("packagingTypeCode")
    private String packagingTypeCode;

    @JsonProperty("packagingUnitAverageWeight")
    private Double packagingUnitAvarageWeight;

    @JsonProperty("packagingUnitCount")
    private Double packagingUnitCount;

    public AapProductDetailsDTO(String packagingTypeCode, Double packagingUnitAvarageWeight, Double packagingUnitCount) {
        this.packagingTypeCode = packagingTypeCode;
        this.packagingUnitAvarageWeight = packagingUnitAvarageWeight;
        this.packagingUnitCount = packagingUnitCount;
    }

    public AapProductDetailsDTO() {

    }

    @JsonProperty("packagingTypeCode")
    public String getPackagingTypeCode() {
        return packagingTypeCode;
    }

    @JsonProperty("packagingTypeCode")
    public void setPackagingTypeCode(String packagingTypeCode) {
        this.packagingTypeCode = packagingTypeCode;
    }

    @JsonProperty("packagingUnitAverageWeight")
    public Double getPackagingUnitAvarageWeight() {
        return packagingUnitAvarageWeight;
    }

    @JsonProperty("packagingUnitAverageWeight")
    public void setPackagingUnitAvarageWeight(Double packagingUnitAvarageWeight) {
        this.packagingUnitAvarageWeight = packagingUnitAvarageWeight;
    }

    @JsonProperty("packagingUnitCount")
    public Double getPackagingUnitCount() {
        return packagingUnitCount;
    }

    @JsonProperty("packagingUnitCount")
    public void setPackagingUnitCount(Double packagingUnitCount) {
        this.packagingUnitCount = packagingUnitCount;
    }
}
