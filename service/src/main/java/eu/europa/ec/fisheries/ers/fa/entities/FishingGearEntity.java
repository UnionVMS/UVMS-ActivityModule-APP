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

	@Column(name = "role_code", nullable = false)
	private String roleCode;

	@Column(name = "role_code_list_id", nullable = false)
	private String roleCodeListId;


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingGear", cascade = CascadeType.ALL)
	private Set<GearCharacteristicEntity> gearCharacteristics;

	public FishingGearEntity() {
		super();
	}

	public FishingGearEntity(int id,Set<GearCharacteristicEntity> gearCharacteristics, String roleCodeListId, String roleCode, String typeCodeListId, String typeCode, FishingActivityEntity fishingActivity, GearProblemEntity gearProblem, FaCatchEntity faCatch) {
        this.id =id;
		this.gearCharacteristics = gearCharacteristics;
		this.roleCodeListId = roleCodeListId;
		this.roleCode = roleCode;
		this.typeCodeListId = typeCodeListId;
		this.typeCode = typeCode;
		this.fishingActivity = fishingActivity;
		this.gearProblem = gearProblem;
		this.faCatch = faCatch;
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

	@Override
	public String toString() {
		return "FishingGearEntity{" +
				"id=" + id +
				", faCatch=" + faCatch +
				", fishingActivity=" + fishingActivity +
				", gearProblem=" + gearProblem +
				", typeCode='" + typeCode + '\'' +
				", typeCodeListId='" + typeCodeListId + '\'' +
				", roleCode='" + roleCode + '\'' +
				", roleCodeListId='" + roleCodeListId + '\'' +
				'}';
	}
}