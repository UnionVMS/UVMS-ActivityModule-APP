/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanera on 04/08/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FishingGearDTO {

    @JsonProperty("gearTypeCode")
    private String gearTypeCode;

    @JsonProperty("gearRoleCode")
    private String gearRoleCode;

    @JsonProperty("gearTypeCode")
    public String getGearTypeCode() {
        return gearTypeCode;
    }

    @JsonProperty("gearTypeCode")
    public void setGearTypeCode(String gearTypeCode) {
        this.gearTypeCode = gearTypeCode;
    }

    @JsonProperty("gearRoleCode")
    public String getGearRoleCode() {
        return gearRoleCode;
    }

    @JsonProperty("gearRoleCode")
    public void setGearRoleCode(String gearRoleCode) {
        this.gearRoleCode = gearRoleCode;
    }
}
