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
 * Created by padhyad on 8/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FaCatchDetailsDTO {

    @JsonProperty("typeCode")
    private String typeCode;

    @JsonProperty("speciesCode")
    private String speciesCode;

    @JsonProperty("unitQuantity")
    private Long unitQuantity;

    @JsonProperty("weightMeasure")
    private Double weightMeasure;

    @JsonProperty("weightMeasureUnitCode")
    private String weightMeasureUnitCode;

    @JsonProperty("usageCode")
    private String usageCode;

    @JsonProperty("weighingMeansCode")
    private String weighingMeansCode;

    @JsonProperty("sizeDistributionClassCode")
    private String sizeDistributionClassCode;

    @JsonProperty("sizeDistributionCategoryCode")
    private String sizeDistributionCategoryCode;

    @JsonProperty("fishingGears")
    private List<FishingGearDetailsDTO> fishingGears;

    @JsonProperty("specifiedFluxLocations")
    private List<FluxLocationDetailsDTO> specifiedFluxLocations;

    @JsonProperty("destFluxLocations")
    private List<FluxLocationDetailsDTO> destFluxLocations;

    @JsonProperty("fluxCharacteristics")
    private List<FluxCharacteristicsDetailsDTO> fluxCharacteristics;

    @JsonProperty("fishingTrips")
    private List<FishingTripDetailsDTO> fishingTrips;

    @JsonProperty("aapProcess")
    private List<AapProcessDetailsDTO> aapProcess;

    @JsonProperty("aapStock")
    private List<AapStockDetailsDTO> aapStock;

    public FaCatchDetailsDTO() {
    }

    public FaCatchDetailsDTO(String typeCode, String speciesCode, Long unitQuantity, Double weightMeasure, String weightMeasureUnitCode, String usageCode, String weighingMeansCode, String sizeDistributionClassCode, String sizeDistributionCategoryCode, List<FishingGearDetailsDTO> fishingGears, List<FluxLocationDetailsDTO> specifiedFluxLocations, List<FluxLocationDetailsDTO> destFluxLocations, List<FluxCharacteristicsDetailsDTO> fluxCharacteristics, List<FishingTripDetailsDTO> fishingTrips, List<AapProcessDetailsDTO> aapProcess, List<AapStockDetailsDTO> aapStock) {
        this.typeCode = typeCode;
        this.speciesCode = speciesCode;
        this.unitQuantity = unitQuantity;
        this.weightMeasure = weightMeasure;
        this.weightMeasureUnitCode = weightMeasureUnitCode;
        this.usageCode = usageCode;
        this.weighingMeansCode = weighingMeansCode;
        this.sizeDistributionClassCode = sizeDistributionClassCode;
        this.sizeDistributionCategoryCode = sizeDistributionCategoryCode;
        this.fishingGears = fishingGears;
        this.specifiedFluxLocations = specifiedFluxLocations;
        this.destFluxLocations = destFluxLocations;
        this.fluxCharacteristics = fluxCharacteristics;
        this.fishingTrips = fishingTrips;
        this.aapProcess = aapProcess;
        this.aapStock = aapStock;
    }

    @JsonProperty("typeCode")
    public String getTypeCode() {
        return typeCode;
    }

    @JsonProperty("typeCode")
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @JsonProperty("speciesCode")
    public String getSpeciesCode() {
        return speciesCode;
    }

    @JsonProperty("speciesCode")
    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    @JsonProperty("unitQuantity")
    public Long getUnitQuantity() {
        return unitQuantity;
    }

    @JsonProperty("unitQuantity")
    public void setUnitQuantity(Long unitQuantity) {
        this.unitQuantity = unitQuantity;
    }

    @JsonProperty("weightMeasure")
    public Double getWeightMeasure() {
        return weightMeasure;
    }

    @JsonProperty("weightMeasure")
    public void setWeightMeasure(Double weightMeasure) {
        this.weightMeasure = weightMeasure;
    }

    @JsonProperty("weightMeasureUnitCode")
    public String getWeightMeasureUnitCode() {
        return weightMeasureUnitCode;
    }

    @JsonProperty("weightMeasureUnitCode")
    public void setWeightMeasureUnitCode(String weightMeasureUnitCode) {
        this.weightMeasureUnitCode = weightMeasureUnitCode;
    }

    @JsonProperty("usageCode")
    public String getUsageCode() {
        return usageCode;
    }

    @JsonProperty("usageCode")
    public void setUsageCode(String usageCode) {
        this.usageCode = usageCode;
    }

    @JsonProperty("weighingMeansCode")
    public String getWeighingMeansCode() {
        return weighingMeansCode;
    }

    @JsonProperty("weighingMeansCode")
    public void setWeighingMeansCode(String weighingMeansCode) {
        this.weighingMeansCode = weighingMeansCode;
    }

    @JsonProperty("sizeDistributionClassCode")
    public String getSizeDistributionClassCode() {
        return sizeDistributionClassCode;
    }

    @JsonProperty("sizeDistributionClassCode")
    public void setSizeDistributionClassCode(String sizeDistributionClassCode) {
        this.sizeDistributionClassCode = sizeDistributionClassCode;
    }

    @JsonProperty("sizeDistributionCategoryCode")
    public String getSizeDistributionCategoryCode() {
        return sizeDistributionCategoryCode;
    }

    @JsonProperty("sizeDistributionCategoryCode")
    public void setSizeDistributionCategoryCode(String sizeDistributionCategoryCode) {
        this.sizeDistributionCategoryCode = sizeDistributionCategoryCode;
    }

    @JsonProperty("fishingGears")
    public List<FishingGearDetailsDTO> getFishingGears() {
        return fishingGears;
    }

    @JsonProperty("fishingGears")
    public void setFishingGears(List<FishingGearDetailsDTO> fishingGears) {
        this.fishingGears = fishingGears;
    }

    @JsonProperty("specifiedFluxLocations")
    public List<FluxLocationDetailsDTO> getSpecifiedFluxLocations() {
        return specifiedFluxLocations;
    }

    @JsonProperty("specifiedFluxLocations")
    public void setSpecifiedFluxLocations(List<FluxLocationDetailsDTO> specifiedFluxLocations) {
        this.specifiedFluxLocations = specifiedFluxLocations;
    }

    @JsonProperty("destFluxLocations")
    public List<FluxLocationDetailsDTO> getDestFluxLocations() {
        return destFluxLocations;
    }

    @JsonProperty("destFluxLocations")
    public void setDestFluxLocations(List<FluxLocationDetailsDTO> destFluxLocations) {
        this.destFluxLocations = destFluxLocations;
    }

    @JsonProperty("fluxCharacteristics")
    public List<FluxCharacteristicsDetailsDTO> getFluxCharacteristics() {
        return fluxCharacteristics;
    }

    @JsonProperty("fluxCharacteristics")
    public void setFluxCharacteristics(List<FluxCharacteristicsDetailsDTO> fluxCharacteristics) {
        this.fluxCharacteristics = fluxCharacteristics;
    }

    @JsonProperty("fishingTrips")
    public List<FishingTripDetailsDTO> getFishingTrips() {
        return fishingTrips;
    }

    @JsonProperty("fishingTrips")
    public void setFishingTrips(List<FishingTripDetailsDTO> fishingTrips) {
        this.fishingTrips = fishingTrips;
    }

    @JsonProperty("aapProcess")
    public List<AapProcessDetailsDTO> getAapProcess() {
        return aapProcess;
    }

    @JsonProperty("aapProcess")
    public void setAapProcess(List<AapProcessDetailsDTO> aapProcess) {
        this.aapProcess = aapProcess;
    }

    @JsonProperty("aapStock")
    public List<AapStockDetailsDTO> getAapStock() {
        return aapStock;
    }

    @JsonProperty("aapStock")
    public void setAapStock(List<AapStockDetailsDTO> aapStock) {
        this.aapStock = aapStock;
    }
}
