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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "gearProblem", cascade = CascadeType.ALL)
	private Set<GearProblemRecoveryEntity> gearProblemRecovery;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "gearProblem", cascade = CascadeType.ALL)
	private Set<FishingGearEntity> fishingGears;

	public GearProblemEntity() {
		super();
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
	public Set<GearProblemRecoveryEntity> getGearProblemRecovery() {
		return gearProblemRecovery;
	}
	public void setGearProblemRecovery(Set<GearProblemRecoveryEntity> gearProblemRecovery) {
		this.gearProblemRecovery = gearProblemRecovery;
	}
	public Set<FishingGearEntity> getFishingGears() {
		return fishingGears;
	}
	public void setFishingGears(Set<FishingGearEntity> fishingGears) {
		this.fishingGears = fishingGears;
	}
}