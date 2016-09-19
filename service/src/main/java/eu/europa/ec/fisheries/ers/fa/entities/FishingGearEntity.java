/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "activity_fishing_gear")
public class FishingGearEntity implements Serializable {

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

	public FishingGearEntity() {
		super();
	}

	public FishingGearEntity(Set<GearCharacteristicEntity> gearCharacteristics, String typeCodeListId, String typeCode, FishingActivityEntity fishingActivity, GearProblemEntity gearProblem, FaCatchEntity faCatch) {
       	this.gearCharacteristics = gearCharacteristics;
		this.typeCode = typeCode;
		this.fishingActivity = fishingActivity;
		this.gearProblem = gearProblem;
		this.faCatch = faCatch;
		this.typeCodeListId = typeCodeListId;
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