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

/**
 * Created by padhyad on 8/2/2016.
 */
public enum FaReportStatusEnum {
    NEW("new", 9),
    UPDATED("updated", 5),
    CANCELED("canceled", 1),
    DELETED("deleted", 3);

    private String type;
    private Integer purposeCode;

    FaReportStatusEnum(String type, Integer purposeCode) {
        this.type = type;
        this.purposeCode = purposeCode;
    }

    public static FaReportStatusEnum getFaReportStatusEnum(Integer purposeCode) {
        for(FaReportStatusEnum faReportStatusEnum : values()) {
            if (purposeCode.equals(faReportStatusEnum.purposeCode)) {
                return faReportStatusEnum;
            }
        }
        return null;
    }

    public String getStatus() {
        return this.type;
    }

    public Integer getPurposeCode() {
        return this.purposeCode;
    }
}
