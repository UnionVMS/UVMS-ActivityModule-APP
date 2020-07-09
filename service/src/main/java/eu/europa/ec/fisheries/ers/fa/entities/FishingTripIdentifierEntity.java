/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NamedQueries({
		@NamedQuery(name = FishingTripIdentifierEntity.FIND_SELECTED_TRIP_START_DATE,
				query = "SELECT MIN(fa.calculatedStartTime) AS startTime " +
						"FROM FishingTripIdentifierEntity fti " +
						"INNER JOIN fti.fishingTrip ft " +
						"INNER JOIN ft.fishingActivity fa " +
						"WHERE fti.tripId = :tripId "),

		@NamedQuery(name = FishingTripIdentifierEntity.FIND_PREVIOUS_TRIPS,
				query = "SELECT fti.tripId, MIN(fa.calculatedStartTime) as startTime " +
                        "FROM FishingTripIdentifierEntity fti " +
						"INNER JOIN fti.fishingTrip ft " +
						"INNER JOIN ft.fishingActivity fa " +
						"WHERE EXISTS( " +
									"SELECT innerTripId.id " +
									"FROM FishingTripIdentifierEntity innerTripId " +
									"INNER JOIN innerTripId.fishingTrip innerTrip " +
									"INNER JOIN innerTrip.fishingActivity innerActivity " +
									"INNER JOIN innerActivity.faReportDocument innerFaReport " +
									"INNER JOIN innerFaReport.vesselTransportMeans innerMeans " +
									"WHERE innerTripId.tripId = fti.tripId " +
									"AND innerMeans.guid = :vesselGuid " +
						")" +
						"GROUP BY fti.tripId " +
						"HAVING MIN(fa.calculatedStartTime) < :selectedTripStartDate " +
						"ORDER BY startTime DESC, fti.tripId DESC"),

        @NamedQuery(name = FishingTripIdentifierEntity.FIND_NEXT_TRIPS,
                query = "SELECT fti.tripId, MIN(fa.calculatedStartTime) as startTime " +
						"FROM FishingTripIdentifierEntity fti " +
                        "INNER JOIN fti.fishingTrip ft " +
                        "INNER JOIN ft.fishingActivity fa " +
						"WHERE EXISTS( " +
									"SELECT innerTripId.id " +
									"FROM FishingTripIdentifierEntity innerTripId " +
									"INNER JOIN innerTripId.fishingTrip innerTrip " +
									"INNER JOIN innerTrip.fishingActivity innerActivity " +
									"INNER JOIN innerActivity.faReportDocument innerFaReport " +
									"INNER JOIN innerFaReport.vesselTransportMeans innerMeans " +
									"WHERE innerTripId.tripId = fti.tripId " +
									"AND innerMeans.guid = :vesselGuid " +
						")" +
						"GROUP BY fti.tripId " +
						"HAVING MIN(fa.calculatedStartTime) > :selectedTripStartDate " +
                        "ORDER BY startTime ASC, fti.tripId ASC"),

		@NamedQuery(name = FishingTripIdentifierEntity.FIND_TRIPS_BETWEEN,
				query = "SELECT fti.tripId, MIN(fa.calculatedStartTime) as startTime " +
						"FROM FishingTripIdentifierEntity fti " +
						"INNER JOIN fti.fishingTrip ft " +
						"INNER JOIN ft.fishingActivity fa " +
						"WHERE EXISTS( " +
							"SELECT innerTripId.id " +
							"FROM FishingTripIdentifierEntity innerTripId " +
							"INNER JOIN innerTripId.fishingTrip innerTrip " +
							"INNER JOIN innerTrip.fishingActivity innerActivity " +
							"INNER JOIN innerActivity.faReportDocument innerFaReport " +
							"INNER JOIN innerFaReport.vesselTransportMeans innerMeans " +
							"WHERE innerTripId.tripId = fti.tripId AND innerMeans.guid = :vesselGuid " +
						")" +
						"GROUP BY fti.tripId " +
						"HAVING MIN(fa.calculatedStartTime) >= :selectedTripStartDate AND MAX(fa.calculatedStartTime) <:selectedTripEndDate " +
						"ORDER BY startTime ASC, fti.tripId ASC"),

		@NamedQuery(name = FishingTripIdentifierEntity.FIND_PREVIOUS_CONCURRENT_TRIPS,
				query = "SELECT fti.tripId, MIN(fa.calculatedStartTime) as startTime " +
						"FROM FishingTripIdentifierEntity fti " +
						"INNER JOIN fti.fishingTrip ft " +
						"INNER JOIN ft.fishingActivity fa " +
						"WHERE EXISTS( " +
									"SELECT innerTripId.id " +
									"FROM FishingTripIdentifierEntity innerTripId " +
									"INNER JOIN innerTripId.fishingTrip innerTrip " +
									"INNER JOIN innerTrip.fishingActivity innerActivity " +
									"INNER JOIN innerActivity.faReportDocument innerFaReport " +
									"INNER JOIN innerFaReport.vesselTransportMeans innerMeans " +
									"WHERE innerTripId.tripId = fti.tripId " +
									"AND innerMeans.guid = :vesselGuid " +
						")" +
						"AND fti.tripId < :tripId " +
						"GROUP BY fti.tripId " +
						"HAVING MIN(fa.calculatedStartTime) = :selectedTripStartDate " +
						"ORDER BY fti.tripId DESC"),

		@NamedQuery(name = FishingTripIdentifierEntity.FIND_NEXT_CONCURRENT_TRIPS,
				query = "SELECT fti.tripId, MIN(fa.calculatedStartTime) as startTime " +
						"FROM FishingTripIdentifierEntity fti " +
						"INNER JOIN fti.fishingTrip ft " +
						"INNER JOIN ft.fishingActivity fa " +
						"WHERE EXISTS( " +
								"SELECT innerTripId.id " +
								"FROM FishingTripIdentifierEntity innerTripId " +
								"INNER JOIN innerTripId.fishingTrip innerTrip " +
								"INNER JOIN innerTrip.fishingActivity innerActivity " +
								"INNER JOIN innerActivity.faReportDocument innerFaReport " +
								"INNER JOIN innerFaReport.vesselTransportMeans innerMeans " +
								"WHERE innerTripId.tripId = fti.tripId " +
								"AND innerMeans.guid = :vesselGuid " +
						")" +
						"AND fti.tripId > :tripId " +
						"GROUP BY fti.tripId " +
						"HAVING MIN(fa.calculatedStartTime) = :selectedTripStartDate " +
						"ORDER BY fti.tripId ASC")
})

@Entity
@Table(name = "activity_fishing_trip_identifier")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = {"tripId", "tripSchemeId"})
public class FishingTripIdentifierEntity implements Serializable {

	public static final String FIND_SELECTED_TRIP_START_DATE = "findSelectedTripStartDate";
	public static final String FIND_PREVIOUS_TRIPS = "findPreviousTrips";
	public static final String FIND_NEXT_TRIPS = "findNextTrips";
	public static final String FIND_TRIPS_BETWEEN = "FishingTripIdentifierEntity.findTripsBetween";
	public static final String FIND_PREVIOUS_CONCURRENT_TRIPS = "findPreviousConcurrentTrips";
	public static final String FIND_NEXT_CONCURRENT_TRIPS = "findNextConcurrentTrips";

	@Id
	@Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN_activity_fishing_trip_identifier", sequenceName = "trip_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN_activity_fishing_trip_identifier")
    private int id;

	@Column(name = "trip_id", nullable = false)
	private String tripId;

	@Column(name = "trip_scheme_id", nullable = false)
	private String tripSchemeId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "calculated_trip_start_date", length = 29)
	private Date calculatedTripStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "calculated_trip_end_date", length = 29)
	private Date calculatedTripEndDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fishing_trip_id")
    private FishingTripEntity fishingTrip;

}