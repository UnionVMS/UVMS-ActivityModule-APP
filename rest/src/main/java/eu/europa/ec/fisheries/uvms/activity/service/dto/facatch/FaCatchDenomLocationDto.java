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

/**
 * Created by kovian on 03/03/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FaCatchDenomLocationDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String territory;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String faoArea;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icesStatRectangle;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String effortZone;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rfmo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gfcmGsa;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gfcmStatRectangle;


    public String getTerritory() {
        return territory;
    }
    public void setTerritory(String territory) {
        this.territory = territory;
    }
    public String getFaoArea() {
        return faoArea;
    }
    public void setFaoArea(String faoArea) {
        this.faoArea = faoArea;
    }
    public String getIcesStatRectangle() {
        return icesStatRectangle;
    }
    public void setIcesStatRectangle(String icesStatRectangle) {
        this.icesStatRectangle = icesStatRectangle;
    }
    public String getEffortZone() {
        return effortZone;
    }
    public void setEffortZone(String effortZone) {
        this.effortZone = effortZone;
    }
    public String getRfmo() {
        return rfmo;
    }
    public void setRfmo(String rfmo) {
        this.rfmo = rfmo;
    }
    public String getGfcmGsa() {
        return gfcmGsa;
    }
    public void setGfcmGsa(String gfcmGsa) {
        this.gfcmGsa = gfcmGsa;
    }
    public String getGfcmStatRectangle() {
        return gfcmStatRectangle;
    }
    public void setGfcmStatRectangle(String gfcmStatRectangle) {
        this.gfcmStatRectangle = gfcmStatRectangle;
    }
}
