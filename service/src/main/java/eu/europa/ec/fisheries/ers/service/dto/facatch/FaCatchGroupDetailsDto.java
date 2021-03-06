/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.dto.facatch;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.GearDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView;

/**
 * Created by kovian on 28/02/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FaCatchGroupDetailsDto {

    @JsonView(FishingActivityView.CommonView.class)
    private Double weight;

    @JsonView(FishingActivityView.CommonView.class)
    private Double unit;

    @JsonView(FishingActivityView.CommonView.class)
    private String stockId;

    @JsonView(FishingActivityView.CommonView.class)
    private String size;

    @JsonView(FishingActivityView.CommonView.class)
    private String weightingMeans;

    @JsonView(FishingActivityView.CommonView.class)
    private String tripId;

    @JsonView(FishingActivityView.CommonView.class)
    private String usage;

    @JsonView(FishingActivityView.NoView.class)
    private boolean detailsSet;

    @JsonView(FishingActivityView.CommonView.class)
    private List<DestinationLocationDto> destinationLocation;

    @JsonView(FishingActivityView.CommonView.class)
    private List<GearDto> gears;

    @JsonView(FishingActivityView.CommonView.class)
    private List<FluxLocationDto> specifiedFluxLocations;

    @JsonView(FishingActivityView.CommonView.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FluxCharacteristicsViewDto> characteristics;

    public FaCatchGroupDetailsDto() {
        destinationLocation   = new ArrayList<>();
        gears                 = new ArrayList<>();
        specifiedFluxLocations = new ArrayList<>();
        characteristics = new ArrayList<>();
    }

    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    public Double getUnit() {
        return unit;
    }
    public void setUnit(Double unit) {
        this.unit = unit;
    }
    public String getStockId() {
        return stockId;
    }
    public void setStockId(String stockId) {
        this.stockId = stockId;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getWeightingMeans() {
        return weightingMeans;
    }
    public void setWeightingMeans(String wightingMeans) {
        this.weightingMeans = wightingMeans;
    }
    public String getTripId() {
        return tripId;
    }
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
    public String getUsage() {
        return usage;
    }
    public void setUsage(String usage) {
        this.usage = usage;
    }
    public List<DestinationLocationDto> getDestinationLocation() {
        return destinationLocation;
    }
    public void setDestinationLocation(List<DestinationLocationDto> destinationLocation) {
        this.destinationLocation = destinationLocation;
    }
    public List<GearDto> getGears() {
        return gears;
    }
    public void setGears(List<GearDto> gears) {
        this.gears = gears;
    }
    public List<FluxLocationDto> getSpecifiedFluxLocations() {
        return specifiedFluxLocations;
    }
    public void setSpecifiedFluxLocations(List<FluxLocationDto> specifiedFluxLocations) {
        this.specifiedFluxLocations = specifiedFluxLocations;
    }
    public List<FluxCharacteristicsViewDto> getCharacteristics() {
        return characteristics;
    }
    public void setCharacteristics(List<FluxCharacteristicsViewDto> characteristics) {
        this.characteristics = characteristics;
    }
    public boolean areDetailsSet() {
        return detailsSet;
    }
    public void setDetailsAreSet(boolean detailsSet) {
        this.detailsSet = detailsSet;
    }
}
