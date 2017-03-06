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
public class FishingGearDetailsDTO {

    @JsonProperty("gearType")
    private String gearType;

    @JsonProperty("gearRole")
    private String role;

    @JsonProperty("gearCharacteristics")
    private List<GearCharacteristicsDetailsDTO> gearCharacteristics;

    public FishingGearDetailsDTO() {
    }

    public FishingGearDetailsDTO(String gearType, String role, List<GearCharacteristicsDetailsDTO> gearCharacteristics) {
        this.gearType = gearType;
        this.role = role;
        this.gearCharacteristics = gearCharacteristics;
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

    @JsonProperty("gearCharacteristics")
    public List<GearCharacteristicsDetailsDTO> getGearCharacteristics() {
        return gearCharacteristics;
    }

    @JsonProperty("gearCharacteristics")
    public void setGearCharacteristics(List<GearCharacteristicsDetailsDTO> gearCharacteristics) {
        this.gearCharacteristics = gearCharacteristics;
    }
}
