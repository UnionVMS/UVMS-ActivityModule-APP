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

package eu.europa.ec.fisheries.ers.service.search;

import java.util.Objects;

/**
 * Created by sanera on 01/12/2016.
 */
public class FishingTripId {

    private String tripId;
    private String schemeID;

    public FishingTripId(){
        super();
    }

    public FishingTripId(String tripId, String schemeID) {
        this.tripId = tripId;
        this.schemeID = schemeID;
    }

    public String getTripId() {
        return tripId;
    }
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
    public String getSchemeID() {
        return schemeID;
    }
    public void setSchemeID(String schemeID) {
        this.schemeID = schemeID;
    }


    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof FishingTripId)) {
            return false;
        }
        FishingTripId fishingTripId = (FishingTripId) o;
        return  Objects.equals(tripId, fishingTripId.tripId) &&
                Objects.equals(schemeID, fishingTripId.schemeID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, schemeID);
    }

    @Override
    public String toString() {
        return "FishingTripId{" +
                "tripId='" + tripId + '\'' +
                ", schemeID='" + schemeID + '\'' +
                '}';
    }
}
