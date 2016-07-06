/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

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
@Table(name = "activity_fishing_trip_identifier")
public class FishingTripIdentifierEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_trip_id")
	private FishingTripEntity fishingTrip;

	@Column(name = "trip_id", nullable = false)
	private String tripId;


	@Column(name = "trip_scheme_id", nullable = false)
	private String tripSchemeId;

	public FishingTripIdentifierEntity() {
	}

	public FishingTripIdentifierEntity(String tripId,
									   String tripSchemeId) {
		this.tripId = tripId;
		this.tripSchemeId = tripSchemeId;
	}

	public FishingTripIdentifierEntity(FishingTripEntity fishingTrip, String tripId,
									   String tripSchemeId) {
		this.fishingTrip = fishingTrip;
		this.tripId = tripId;
		this.tripSchemeId = tripSchemeId;
	}

	public int getId() {
		return this.id;
	}

	public FishingTripEntity getFishingTrip() {
		return this.fishingTrip;
	}

	public void setFishingTrip(FishingTripEntity fishingTrip) {
		this.fishingTrip = fishingTrip;
	}

	public String getTripId() {
		return this.tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public String getTripSchemeId() {
		return this.tripSchemeId;
	}

	public void setTripSchemeId(String tripSchemeId) {
		this.tripSchemeId = tripSchemeId;
	}

}