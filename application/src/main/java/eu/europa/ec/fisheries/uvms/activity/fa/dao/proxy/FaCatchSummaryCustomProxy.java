/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.dao.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This is base class used to Group by results.
 * If you want to group by results with different set of variables. Then just extend this class and override equals and hashcode method with
 * variables with which you want to GROUP BY
 */

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"count", "typeCode", "species", "fishClass"})
@Data
public class FaCatchSummaryCustomProxy {

    private String day;
    private String month;
    private String year;
    private String date;
    private String vesselTransportGuid;
    private String flagState;
    private String gearType;
    private String presentation;
    private String territory;
    private String faoArea;
    private String icesStatRectangle;
    private String effortZone;
    private String rfmo;
    private String gfcmGsa;
    private String gfcmStatRectangle;
    private String fishClass;
    private String species;
    private String typeCode;
    private double count;

}
