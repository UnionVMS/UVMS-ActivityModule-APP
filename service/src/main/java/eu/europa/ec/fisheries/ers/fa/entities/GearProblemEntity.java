package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_gear_problem")
public class GearProblemEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;

	@Column(name = "type_code", nullable = false)
	private String typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@Column(name = "affected_quantity", nullable = false)
	private int affectedQuantity;

	@Column(name = "recovery_measure_code", nullable = false)
	private String recoveryMeasureCode;

	@Column(name = "recovery_measure_code_list_id", nullable = false)
	private String recoveryMeasureCodeListId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "gearProblem", cascade = CascadeType.ALL)
	private Set<FishingGearEntity> fishingGears = new HashSet<FishingGearEntity>(0);

	public GearProblemEntity() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getAffectedQuantity() {
		return this.affectedQuantity;
	}

	public void setAffectedQuantity(int affectedQuantity) {
		this.affectedQuantity = affectedQuantity;
	}

	public String getRecoveryMeasureCode() {
		return this.recoveryMeasureCode;
	}

	public void setRecoveryMeasureCode(String recoveryMeasureCode) {
		this.recoveryMeasureCode = recoveryMeasureCode;
	}

	public String getRecoveryMeasureCodeListId() {
		return this.recoveryMeasureCodeListId;
	}

	public void setRecoveryMeasureCodeListId(String recoveryMeasureCodeListId) {
		this.recoveryMeasureCodeListId = recoveryMeasureCodeListId;
	}

	public Set<FishingGearEntity> getFishingGears() {
		return fishingGears;
	}

	public void setFishingGears(Set<FishingGearEntity> fishingGears) {
		this.fishingGears = fishingGears;
	}
}
