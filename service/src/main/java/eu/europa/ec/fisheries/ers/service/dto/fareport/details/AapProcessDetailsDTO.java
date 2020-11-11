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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by padhyad on 8/12/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AapProcessDetailsDTO {

    @JsonProperty("typeCode")
    private String typeCode;

    @JsonProperty("conversionFactor")
    private Integer conversionFactor;

    @JsonProperty("aapProduct")
    private List<AapProductDetailsDTO> aapProductDetails;

    @JsonProperty("conversionFactorIsFromMdr")
    private Boolean conversionFactorIsFromMdr;

    public AapProcessDetailsDTO() {
    }

    public AapProcessDetailsDTO(String typeCode, Integer conversionFactor, List<AapProductDetailsDTO> aapProductDetails) {
        this.typeCode = typeCode;
        this.conversionFactor = conversionFactor;
        this.aapProductDetails = aapProductDetails;
    }

    @JsonProperty("typeCode")
    public String getTypeCode() {
        return typeCode;
    }

    @JsonProperty("typeCode")
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @JsonProperty("conversionFactor")
    public Integer getConversionFactor() {
        return conversionFactor;
    }

    @JsonProperty("conversionFactor")
    public void setConversionFactor(Integer conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @JsonProperty("aapProduct")
    public List<AapProductDetailsDTO> getAapProductDetails() {
        return aapProductDetails;
    }

    @JsonProperty("aapProduct")
    public void setAapProductDetails(List<AapProductDetailsDTO> aapProductDetails) {
        this.aapProductDetails = aapProductDetails;
    }

    public Boolean getConversionFactorIsFromMdr() {
        return conversionFactorIsFromMdr;
    }

    public void setConversionFactorIsFromMdr(Boolean conversionFactorIsFromMdr) {
        this.conversionFactorIsFromMdr = conversionFactorIsFromMdr;
    }
}
