/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.dto.view;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author sanera
 * @author Gregory Rinaldi
 */
@JsonInclude(Include.NON_NULL)
public class AreaDto {

    @JsonIgnore
    private Set<FluxLocationDto> fluxLocations;

    private PositionDto transmission;

    private PositionDto crossing;

    private PositionDto startActivity;

    private PositionDto startFishing;

    @JsonAnyGetter
    public Map<String, String> getIdentifiers() {

        HashMap<String, String> stringStringHashMap = null;

        if (fluxLocations != null && fluxLocations.iterator().hasNext()) {
            stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("schemeId", fluxLocations.iterator().next().getFluxLocationIdentifierSchemeId());
            stringStringHashMap.put("id", fluxLocations.iterator().next().getFluxLocationIdentifier());
            stringStringHashMap.values().removeAll(Collections.singleton(null));
        }

        return stringStringHashMap;
    }

    public PositionDto getTransmission() {
        return transmission;
    }

    public void setTransmission(PositionDto transmission) {
        this.transmission = transmission;
    }

    public PositionDto getCrossing() {
        return crossing;
    }

    public void setCrossing(PositionDto crossing) {
        this.crossing = crossing;
    }

    public PositionDto getStartActivity() {
        return startActivity;
    }

    public void setStartActivity(PositionDto startActivity) {
        this.startActivity = startActivity;
    }

    public PositionDto getStartFishing() {
        return startFishing;
    }

    public void setStartFishing(PositionDto startFishing) {
        this.startFishing = startFishing;
    }

    public Set<FluxLocationDto> getFluxLocations() {
        return fluxLocations;
    }

    public void setFluxLocations(Set<FluxLocationDto> fluxLocations) {
        this.fluxLocations = fluxLocations;
    }
}
