package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_fishing_trip_identifier")
public class FishingTripIdentifierEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_trip_id")
	private FishingTripEntity fishingTrip;

	@Column(name = "trip_id", nullable = false)
	private String tripId;


	@Column(name = "trip_scheme_id", nullable = false)
	private String tripSchemeId;

	public FishingTripIdentifierEntity() {
	}

	public FishingTripIdentifierEntity(String tripId,
									   String tripSchemeId) {
		this.tripId = tripId;
		this.tripSchemeId = tripSchemeId;
	}

	public FishingTripIdentifierEntity(FishingTripEntity fishingTrip, String tripId,
									   String tripSchemeId) {
		this.fishingTrip = fishingTrip;
		this.tripId = tripId;
		this.tripSchemeId = tripSchemeId;
	}

	public int getId() {
		return this.id;
	}

	public FishingTripEntity getFishingTrip() {
		return this.fishingTrip;
	}

	public void setFishingTrip(FishingTripEntity fishingTrip) {
		this.fishingTrip = fishingTrip;
	}

	public String getTripId() {
		return this.tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public String getTripSchemeId() {
		return this.tripSchemeId;
	}

	public void setTripSchemeId(String tripSchemeId) {
		this.tripSchemeId = tripSchemeId;
	}

}
