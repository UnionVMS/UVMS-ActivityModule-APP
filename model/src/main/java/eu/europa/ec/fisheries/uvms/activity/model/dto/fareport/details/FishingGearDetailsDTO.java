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

package eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by padhyad on 8/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FishingGearDetailsDTO {

    @JsonProperty("gearType")
    private String gearType;

    @JsonProperty("gearRole")
    private String role;

    @JsonProperty("gearCharacterstics")
    private List<GearCharacteristicsDetailsDTO> gearCharacterstics;

    public FishingGearDetailsDTO() {
    }

    public FishingGearDetailsDTO(String gearType, String role, List<GearCharacteristicsDetailsDTO> gearCharacterstics) {
        this.gearType = gearType;
        this.role = role;
        this.gearCharacterstics = gearCharacterstics;
    }

    @JsonProperty("gearType")
    public String getGearType() {
        return gearType;
    }

    @JsonProperty("gearType")
    public void setGearType(String gearType) {
        this.gearType = gearType;
    }

    @JsonProperty("gearRole")
    public String getRole() {
        return role;
    }

    @JsonProperty("gearRole")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("gearCharacterstics")
    public List<GearCharacteristicsDetailsDTO> getGearCharacterstics() {
        return gearCharacterstics;
    }

    @JsonProperty("gearCharacterstics")
    public void setGearCharacterstics(List<GearCharacteristicsDetailsDTO> gearCharacterstics) {
        this.gearCharacterstics = gearCharacterstics;
    }
}
