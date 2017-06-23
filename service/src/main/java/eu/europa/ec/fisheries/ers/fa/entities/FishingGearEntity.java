/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_fishing_gear")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FishingGearEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "fa_gear_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_catch_id")
	private FaCatchEntity faCatch;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gear_problem_id")
	private GearProblemEntity gearProblem;

	@Column(name = "type_code", nullable = false)
	private String typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingGear", cascade = CascadeType.ALL)
	private Set<FishingGearRoleEntity> fishingGearRole;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingGear", cascade = CascadeType.ALL)
	private Set<GearCharacteristicEntity> gearCharacteristics;

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


	public Set<GearCharacteristicEntity> getGearCharacteristics() {
		return this.gearCharacteristics;
	}

	public void setGearCharacteristics(
			Set<GearCharacteristicEntity> gearCharacteristics) {
		this.gearCharacteristics = gearCharacteristics;
	}

	public GearProblemEntity getGearProblem() {
		return gearProblem;
	}

	public void setGearProblem(GearProblemEntity gearProblem) {
		this.gearProblem = gearProblem;
	}

	public Set<FishingGearRoleEntity> getFishingGearRole() {
		return fishingGearRole;
	}

	public void setFishingGearRole(Set<FishingGearRoleEntity> fishingGearRole) {
		this.fishingGearRole = fishingGearRole;
	}

	public String getTypeCodeListId() {
		return typeCodeListId;
	}

	public void setTypeCodeListId(String typeCodeListId) {
		this.typeCodeListId = typeCodeListId;
	}
}