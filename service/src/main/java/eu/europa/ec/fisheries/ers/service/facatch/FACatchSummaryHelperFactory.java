/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.facatch;

/**
 * Created by sanera on 06/03/2017.
 * Data retrieved from database will be same for FACatchSummaryReports. But the way we want to present data on the frontend is different.
 * This factory class helps creating helper classes to suit different needs.
 * If in future different table structure is required, you need can make this factory provide that class
 *
 */
public class FACatchSummaryHelperFactory {

    public static final String PRESENTATION="presentation";
    public static final String STANDARD="standard";

    private FACatchSummaryHelperFactory(){
        super();
    }
    public static FACatchSummaryHelper getFACatchSummaryHelper(String criteria)
    {
        if ( PRESENTATION.equals(criteria) )
            return new FACatchSummaryPresentationHelper();
        else if ( STANDARD.equals(criteria) )
            return new FACatchSummaryReportHelper();


        return null;
    }
}
