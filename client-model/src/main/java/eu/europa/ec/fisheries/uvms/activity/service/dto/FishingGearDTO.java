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

package eu.europa.ec.fisheries.uvms.activity.service.dto;

public class FishingGearDTO {

    private String gearTypeCode;

    private String gearRoleCode;

    private String gearMeasure;

    private String gearQuantity;

    private String meshSize;

    private String nominalLength;

    public String getGearTypeCode() {
        return gearTypeCode;
    }

    public void setGearTypeCode(String gearTypeCode) {
        this.gearTypeCode = gearTypeCode;
    }

    public String getGearRoleCode() {
        return gearRoleCode;
    }

    public void setGearRoleCode(String gearRoleCode) {
        this.gearRoleCode = gearRoleCode;
    }

    public String getGearMeasure() {
        return gearMeasure;
    }

    public void setGearMeasure(String gearMeasure) {
        this.gearMeasure = gearMeasure;
    }

    public String getGearQuantity() {
        return gearQuantity;
    }

    public void setGearQuantity(String gearQuantity) {
        this.gearQuantity = gearQuantity;
    }

    public String getMeshSize() {
        return meshSize;
    }

    public void setMeshSize(String meshSize) {
        this.meshSize = meshSize;
    }

    public String getNominalLength() {
        return nominalLength;
    }

    public void setNominalLength(String nominalLength) {
        this.nominalLength = nominalLength;
    }
}
