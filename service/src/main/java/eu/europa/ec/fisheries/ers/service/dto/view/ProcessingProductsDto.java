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

package eu.europa.ec.fisheries.ers.service.dto.view;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.CommonView;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;

@JsonInclude(NON_NULL)
public class ProcessingProductsDto {

    @JsonView(CommonView.class)
    private String type;

    @JsonView(CommonView.class)
    private Map<String, String> locations = new HashMap<>();

    @JsonView(CommonView.class)
    private String species;

    @JsonView(CommonView.class)
    private String gear;

    @JsonView(CommonView.class)
    private String presentation;

    @JsonView(CommonView.class)
    private String preservation;

    @JsonView(CommonView.class)
    private String freshness;

    @JsonView(CommonView.class)
    private Double conversionFactor;

    @JsonView(CommonView.class)
    private Double weight;

    @JsonView(CommonView.class)
    private Double quantity;

    @JsonView(CommonView.class)
    private Double packageWeight;

    @JsonView(CommonView.class)
    private Double packageQuantity;

    @JsonView(CommonView.class)
    private String packagingType;

    @JsonView(CommonView.class)
    private boolean conversionFactorIsFromMdr;

    public ProcessingProductsDto() {
        super();
    }

    @Builder
    public ProcessingProductsDto(String type, Map<String, String> locations, String species, String gear, String presentation, String preservation, String freshness, Double conversionFactor, Double weight, Double quantity, Double packageWeight, Double packageQuantity, String packagingType, boolean conversionFactorIsFromMdr) {
        this.type = type;
        this.locations = locations;
        this.species = species;
        this.gear = gear;
        this.presentation = presentation;
        this.preservation = preservation;
        this.freshness = freshness;
        this.conversionFactor = conversionFactor;
        this.weight = weight;
        this.quantity = quantity;
        this.packageWeight = packageWeight;
        this.packageQuantity = packageQuantity;
        this.packagingType = packagingType;
        this.conversionFactorIsFromMdr = conversionFactorIsFromMdr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, String> locations) {
        this.locations = locations;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getGear() {
        return gear;
    }

    public void setGear(String gear) {
        this.gear = gear;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getPreservation() {
        return preservation;
    }

    public void setPreservation(String preservation) {
        this.preservation = preservation;
    }

    public String getFreshness() {
        return freshness;
    }

    public void setFreshness(String freshness) {
        this.freshness = freshness;
    }

    public Double getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(Double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(Double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public Double getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(Double packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public String getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(String packagingType) {
        this.packagingType = packagingType;
    }

    public boolean getConversionFactorIsFromMdr() {
        return conversionFactorIsFromMdr;
    }

    public void setConversionFactorIsFromMdr(boolean conversionFactorIsFromMdr) {
        this.conversionFactorIsFromMdr = conversionFactorIsFromMdr;
    }
}
