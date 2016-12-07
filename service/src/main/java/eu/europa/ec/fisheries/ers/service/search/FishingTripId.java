package eu.europa.ec.fisheries.ers.service.search;

import java.util.Objects;

/**
 * Created by sanera on 01/12/2016.
 */
public class FishingTripId {
    private String tripId;
    private String schemeID;

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

    public FishingTripId(){

    }

    public FishingTripId(String tripId, String schemeID) {
        this.tripId = tripId;
        this.schemeID = schemeID;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
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
