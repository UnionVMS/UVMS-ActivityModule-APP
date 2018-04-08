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

package eu.europa.ec.fisheries.ers.fa.utils;

public enum UnitCodeEnum {

    KGM("KGM", 1),
    TNE("TNE", 1000),
    MMT("MMT", 1),
    MTR("MTR", 1000),
    MIN("MIN", 1),
    C62("C62", 1);

    private String unit;
    private Integer conversionFactor;

    UnitCodeEnum(String unit, Integer conversionFactor) {
        this.unit = unit;
        this.conversionFactor = conversionFactor;
    }

    public String getUnit() {
        return unit;
    }

    public Integer getConversionFactor() {
        return conversionFactor;
    }

    public static UnitCodeEnum getUnitCode(String unitCode) {
        for (UnitCodeEnum unitCodeEnum : UnitCodeEnum.values()) {
            if (unitCodeEnum.getUnit().equalsIgnoreCase(unitCode)) {
                return unitCodeEnum;
            }
        }
        return null;
    }
}
