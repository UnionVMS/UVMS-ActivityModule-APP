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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "activity_fishing_trip")
public class FishingTripEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name="SEQ_GEN", sequenceName="fa_trip_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
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
	private Set<DelimitedPeriodEntity> delimitedPeriods;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingTrip", cascade = CascadeType.ALL)
	private Set<FishingTripIdentifierEntity> fishingTripIdentifiers;

	public FishingTripEntity() {
		super();
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

	public void setDelimitedPeriods(Set<DelimitedPeriodEntity> delimitedPeriods) {
		this.delimitedPeriods = delimitedPeriods;
	}

	public Set<FishingTripIdentifierEntity> getFishingTripIdentifiers() {
		return this.fishingTripIdentifiers;
	}

	public void setFishingTripIdentifiers(Set<FishingTripIdentifierEntity> fishingTripIdentifiers) {
		this.fishingTripIdentifiers = fishingTripIdentifiers;
	}

	@Override
	public String toString() {
		return "FishingTripEntity{" +
				"id=" + id +
				", typeCode='" + typeCode + '\'' +
				", typeCodeListId='" + typeCodeListId + '\'' +
				'}';
	}
}