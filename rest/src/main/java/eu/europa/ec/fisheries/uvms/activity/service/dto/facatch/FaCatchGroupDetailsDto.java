/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.dto.facatch;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.GearDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovian on 28/02/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FaCatchGroupDetailsDto {

    private Double weight;

    private Double unit;

    private String stockId;

    private String size;

    private String weightingMeans;

    private String tripId;

    private String usage;

    private boolean detailsSet;

    private List<DestinationLocationDto> destinationLocation;

    private List<GearDto> gears;

    private List<FluxLocationDto> specifiedFluxLocations;

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
