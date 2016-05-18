package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_fishing_activity_identifier")
public class FishingActivityIdentifierEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;

	@Column(name = "fa_identifier_id")
	private String faIdentifierId;

	@Column(name = "fa_identifier_scheme_id")
	private String faIdentifierSchemeId;

	public FishingActivityIdentifierEntity() {
	}

	public FishingActivityIdentifierEntity(FishingActivityEntity fishingActivity,
										   String faIdentifierId, String faIdentifierSchemeId) {
		this.fishingActivity = fishingActivity;
		this.faIdentifierId = faIdentifierId;
		this.faIdentifierSchemeId = faIdentifierSchemeId;
	}

	public int getId() {
		return this.id;
	}

	public FishingActivityEntity getFishingActivity() {
		return this.fishingActivity;
	}

	public void setFishingActivity(
			FishingActivityEntity fishingActivity) {
		this.fishingActivity = fishingActivity;
	}

	public String getFaIdentifierId() {
		return this.faIdentifierId;
	}

	public void setFaIdentifierId(String faIdentifierId) {
		this.faIdentifierId = faIdentifierId;
	}

	public String getFaIdentifierSchemeId() {
		return this.faIdentifierSchemeId;
	}

	public void setFaIdentifierSchemeId(String faIdentifierSchemeId) {
		this.faIdentifierSchemeId = faIdentifierSchemeId;
	}

}
