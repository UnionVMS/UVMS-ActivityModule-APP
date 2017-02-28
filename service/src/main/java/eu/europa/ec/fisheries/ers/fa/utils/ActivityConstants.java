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

package eu.europa.ec.fisheries.ers.fa.utils;

/**
 * Created by sanera on 17/08/2016.
 */
public class ActivityConstants {

    private ActivityConstants(){
        super();
    }

    public static final String DEPARTURE ="DEPARTURE";
    public static final String ARRIVAL ="ARRIVAL";
    public static final String LANDING ="LANDING";
    public static final String DECLARATION ="Declaration";
    public static final String NOTIFICATION ="Notification";
    public static final String FISHING_OPERATION ="FISHING_OPERATION";
    public static final String JOINT_FISHING_OPERATION ="JOINT_FISHING_OPERATION";
    public static final String RELOCATION ="RELOCATION";

    public static final String DELETE = "DELETE";
    public static final String CANCELLATION = "CANCELLATION";
    public static final String CORRECTION = "REPLACEMENT";

}
