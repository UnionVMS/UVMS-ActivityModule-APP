/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.dto.fareport.summary;

/**
 * Created by sanera on 28/02/2017.
 */
public class FACatchDetailsDTO {

    private FACatchSummaryDTO catches;
    private FACatchSummaryDTO landing;

    public FACatchSummaryDTO getCatches() {
        return catches;
    }

    public void setCatches(FACatchSummaryDTO catches) {
        this.catches = catches;
    }

    public FACatchSummaryDTO getLanding() {
        return landing;
    }

    public void setLanding(FACatchSummaryDTO landing) {
        this.landing = landing;
    }
}
