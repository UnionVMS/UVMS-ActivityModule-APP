package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_fishing_trip")
public class FishingTripEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_catch_id")
	private FaCatchEntity faCatch;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;

	@Column(name = "type_code")
	private String typeCode;

	@Column(name = "type_code_list_id")
	private String typeCodeListId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingTrip", cascade = CascadeType.ALL)
	private Set<DelimitedPeriodEntity> delimitedPeriods = new HashSet<DelimitedPeriodEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingTrip", cascade = CascadeType.ALL)
	private Set<FishingTripIdentifierEntity> fishingTripIdentifiers = new HashSet<FishingTripIdentifierEntity>(0);

	public FishingTripEntity() {
	}

	public FishingTripEntity(FaCatchEntity faCatch,
							 FishingActivityEntity fishingActivity, String typeCode,
							 String typeCodeListId,
							 Set<DelimitedPeriodEntity> delimitedPeriods,
							 Set<FishingTripIdentifierEntity> fishingTripIdentifiers) {
		this.faCatch = faCatch;
		this.fishingActivity = fishingActivity;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.delimitedPeriods = delimitedPeriods;
		this.fishingTripIdentifiers = fishingTripIdentifiers;
	}

	public int getId() {
		return this.id;
	}

	public FaCatchEntity getFaCatch() {
		return this.faCatch;
	}

	public void setFaCatch(FaCatchEntity faCatch) {
		this.faCatch = faCatch;
	}

	public FishingActivityEntity getFishingActivity() {
		return this.fishingActivity;
	}

	public void setFishingActivity(
			FishingActivityEntity fishingActivity) {
		this.fishingActivity = fishingActivity;
	}

	public String getTypeCode() {
		return this.typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeCodeListId() {
		return this.typeCodeListId;
	}

	public void setTypeCodeListId(String typeCodeListId) {
		this.typeCodeListId = typeCodeListId;
	}

	public Set<DelimitedPeriodEntity> getDelimitedPeriods() {
		return this.delimitedPeriods;
	}

	public void setDelimitedPeriods(
			Set<DelimitedPeriodEntity> delimitedPeriods) {
		this.delimitedPeriods = delimitedPeriods;
	}

	public Set<FishingTripIdentifierEntity> getFishingTripIdentifiers() {
		return this.fishingTripIdentifiers;
	}

	public void setFishingTripIdentifiers(
			Set<FishingTripIdentifierEntity> fishingTripIdentifiers) {
		this.fishingTripIdentifiers = fishingTripIdentifiers;
	}

}
