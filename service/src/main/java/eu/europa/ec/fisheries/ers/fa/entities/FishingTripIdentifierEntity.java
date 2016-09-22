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

@NamedQueries({
		@NamedQuery(name = FishingTripIdentifierEntity.FIND_CURRENT_TRIP,
		query = "SELECT fti from FishingTripIdentifierEntity fti " +
				"INNER JOIN fti.fishingTrip ft " +
				"INNER JOIN ft.fishingActivity fa " +
				"INNER JOIN fa.faReportDocument frd " +
				"INNER JOIN frd.vesselTransportMeans vtm " +
				"INNER JOIN vtm.vesselIdentifiers vi " +
				"WHERE vi.vesselIdentifierId = :vesselId " +
				"AND vi.vesselIdentifierSchemeId = :vesselSchemeId " +
				"ORDER BY frd.acceptedDatetime DESC"),



		@NamedQuery(name = FishingTripIdentifierEntity.FIND_LESS_THAN_TRIPID,
				query = "select distinct fi.tripId,dp.startDate from FishingTripIdentifierEntity fi  " +
				"INNER JOIN fi.fishingTrip ft " +
				"INNER JOIN ft.delimitedPeriods dp where dp.startDate < " +
				"(SELECT max(dp1.startDate) as sDate from FishingTripIdentifierEntity fi1  "+
				"  INNER JOIN fi1.fishingTrip ft1 " +
				"  INNER JOIN ft1.delimitedPeriods dp1 " +
				"  where fi1.tripId = :fishingTripId ) and fi.tripId != :fishingTripId order by dp.startDate desc "),
		@NamedQuery(name = FishingTripIdentifierEntity.FIND_GREATER_THAN_TRTIPID,
				query = "select distinct fi.tripId,dp.startDate from FishingTripIdentifierEntity fi  " +
						"INNER JOIN fi.fishingTrip ft " +
						"INNER JOIN ft.delimitedPeriods dp where dp.startDate >= " +
						"(SELECT max(dp1.startDate) as sDate from FishingTripIdentifierEntity fi1  "+
						"  INNER JOIN fi1.fishingTrip ft1 " +
						"  INNER JOIN ft1.delimitedPeriods dp1 " +
						"  where fi1.tripId = :fishingTripId ) order by dp.startDate asc "),
		@NamedQuery(name = FishingTripIdentifierEntity.FIND_CURRENT_TRTIPID,
				query = "select max(fi.tripId) from FishingTripIdentifierEntity fi  " +
						"INNER JOIN fi.fishingTrip ft " +
						"INNER JOIN ft.delimitedPeriods dp where dp.startDate = " +
						"(SELECT max(dp1.startDate) as sDate from DelimitedPeriodEntity dp1 )  ")
})

@Entity
@Table(name = "activity_fishing_trip_identifier")
public class FishingTripIdentifierEntity implements Serializable {

	public static final String FIND_CURRENT_TRIP = "findCurrentTrip";
	public static final String FIND_LESS_THAN_TRIPID = "findLessThanTripId";
	public static final String FIND_GREATER_THAN_TRTIPID = "findGreaterThanTripId";
	public static final String FIND_CURRENT_TRTIPID = "findCurrentTripId";

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
		super();
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

	@Override
	public String toString() {
		return "FishingTripIdentifierEntity{" +
				"id=" + id +
				", tripId='" + tripId + '\'' +
				", tripSchemeId='" + tripSchemeId + '\'' +
				'}';
	}
}