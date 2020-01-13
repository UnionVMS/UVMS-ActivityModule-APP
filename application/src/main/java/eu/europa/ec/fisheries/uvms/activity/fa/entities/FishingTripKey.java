package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FishingTripKey implements Serializable {

    @Column(name = "trip_id")
    private String tripId;

    @Column(name = "trip_scheme_id")
    private String tripSchemeId;

    public FishingTripKey() {
    }

    public FishingTripKey(String tripId, String tripSchemeId) {
        this.tripId = tripId;
        this.tripSchemeId = tripSchemeId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripSchemeId() {
        return tripSchemeId;
    }

    public void setTripSchemeId(String tripSchemeId) {
        this.tripSchemeId = tripSchemeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FishingTripKey that = (FishingTripKey) o;
        return Objects.equals(tripId, that.tripId) &&
                Objects.equals(tripSchemeId, that.tripSchemeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, tripSchemeId);
    }
}
