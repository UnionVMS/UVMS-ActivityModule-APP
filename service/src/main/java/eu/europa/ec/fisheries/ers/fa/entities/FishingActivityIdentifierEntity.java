/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries � European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
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