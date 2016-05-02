package eu.europa.ec.fisheries.ers.fa.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "activity_gear_problem")
public class GearProblem {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
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
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "GearProblem", cascade = CascadeType.ALL)
	private FluxLocation fluxLocations;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "gearProblem")	
	private Set<FishingGear> fishingGears = new HashSet<FishingGear>(0);

	public GearProblem() {
	}

	public GearProblem(int id, String typeCode, String typeCodeListId,
			int affectedQuantity, String recoveryMeasureCode,
			String recoveryMeasureCodeListId) {
		this.id = id;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.affectedQuantity = affectedQuantity;
		this.recoveryMeasureCode = recoveryMeasureCode;
		this.recoveryMeasureCodeListId = recoveryMeasureCodeListId;
	}

	public GearProblem(int id, String typeCode, String typeCodeListId,
			int affectedQuantity, String recoveryMeasureCode,
			String recoveryMeasureCodeListId,FluxLocation fluxLocations,
			Set<FishingGear> fishingGears) {
		this.id = id;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.affectedQuantity = affectedQuantity;
		this.recoveryMeasureCode = recoveryMeasureCode;
		this.recoveryMeasureCodeListId = recoveryMeasureCodeListId;
		this.fluxLocations = fluxLocations;
		this.fishingGears = fishingGears;
	}


	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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


	

	public FluxLocation getFluxLocations() {
		return fluxLocations;
	}

	public void setFluxLocations(FluxLocation fluxLocations) {
		this.fluxLocations = fluxLocations;
	}


	public Set<FishingGear> getFishingGears() {
		return this.fishingGears;
	}

	public void setFishingGears(Set<FishingGear> fishingGears) {
		this.fishingGears = fishingGears;
	}

}
