/*
 ﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.ers.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FACatchModel {

    private String date;
    private double fop;
    private String gear;
    private String gearSet;
    private String gearHaul;
    private long totalMin;
    private double depth;
    private String faoArea;
    private String gfcmArea;
    private String statRect;
    private String eez;
    private String position;//lat and long
    private String activityType;
    private String species;
    private double weightLSC;
    private double nbLSC;
    private double weightBMS;
    private double nbBMS;

}