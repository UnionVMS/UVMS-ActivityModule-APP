package eu.europa.ec.fisheries.ers.fa.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "activity_fishing_gear")
public class FishingGear {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gear_problem_id")
	private GearProblem gearProblem;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_catch_id")	
	private FaCatch faCatch;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivity fishingActivity;
	
	@Column(name = "type_code", nullable = false)
	private String typeCode;
	
	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;
	
	@Column(name = "role_code", nullable = false)
	private String roleCode;
	
	@Column(name = "role_code_list_id", nullable = false)
	private String roleCodeListId;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingGear")
	private Set<GearCharacteristic> gearCharacteristics = new HashSet<GearCharacteristic>(
			0);

	public FishingGear() {
	}

	public FishingGear(int id, String typeCode, String typeCodeListId,
			String roleCode, String roleCodeListId) {
		this.id = id;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.roleCode = roleCode;
		this.roleCodeListId = roleCodeListId;
	}

	public FishingGear(int id, GearProblem gearProblem, FaCatch faCatch,
			FishingActivity fishingActivity, String typeCode,
			String typeCodeListId, String roleCode, String roleCodeListId,
			Set<GearCharacteristic> gearCharacteristics) {
		this.id = id;
		this.gearProblem = gearProblem;
		this.faCatch = faCatch;
		this.fishingActivity = fishingActivity;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.roleCode = roleCode;
		this.roleCodeListId = roleCodeListId;
		this.gearCharacteristics = gearCharacteristics;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public GearProblem getGearProblem() {
		return this.gearProblem;
	}

	public void setGearProblem(GearProblem gearProblem) {
		this.gearProblem = gearProblem;
	}

	
	public FaCatch getFaCatch() {
		return this.faCatch;
	}

	public void setFaCatch(FaCatch faCatch) {
		this.faCatch = faCatch;
	}

	
	public FishingActivity getFishingActivity() {
		return this.fishingActivity;
	}

	public void setFishingActivity(FishingActivity fishingActivity) {
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

	
	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	
	public String getRoleCodeListId() {
		return this.roleCodeListId;
	}

	public void setRoleCodeListId(String roleCodeListId) {
		this.roleCodeListId = roleCodeListId;
	}

	
	public Set<GearCharacteristic> getGearCharacteristics() {
		return this.gearCharacteristics;
	}

	public void setGearCharacteristics(
			Set<GearCharacteristic> gearCharacteristics) {
		this.gearCharacteristics = gearCharacteristics;
	}

}
