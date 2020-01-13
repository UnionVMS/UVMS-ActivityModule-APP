/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MeasureType {

    @Column(name = "duration_value")
    private Double value;

    @Column(name = "duration_unit_code")
    private String unitCode;

    @Column(name = "duration_unit_code_list_id")
    private String unitCodeListVersionID;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitCodeListVersionID() {
        return unitCodeListVersionID;
    }

    public void setUnitCodeListVersionID(String unitCodeListVersionID) {
        this.unitCodeListVersionID = unitCodeListVersionID;
    }
}
