/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@NamedQueries({
		@NamedQuery(name = FishingTripIdentifierEntity.FIND_CURRENT_TRIP,
				query = "SELECT fa.fishingTrip.fishingTripIdentifier FROM FishingActivityEntity fa " +
						"INNER JOIN fa.faReportDocument frd " +
						"INNER JOIN frd.vesselTransportMeans vtm " +
						"INNER JOIN vtm.vesselIdentifiers vi " +
						"WHERE vi.vesselIdentifierId = :vesselId " +
						"AND vi.vesselIdentifierSchemeId = :vesselSchemeId")

		/*
		//TODO: We need to rewrite this...
		@NamedQuery(name = FishingTripIdentifierEntity.FIND_PREVIOUS_TRIP,
				query = "SELECT fa.fishingTrip.fishingTripIdentifier FROM FishingActivityEntity fa " +
						"INNER JOIN fa.fishingTrip ft " +
						"INNER JOIN fa.faReportDocument frd " +
						"INNER JOIN frd.vesselTransportMeans vtm " +
						"INNER JOIN vtm.vesselIdentifiers vi " +
						"WHERE vi.vesselIdentifierId = :vesselId " +
						"AND vi.vesselIdentifierSchemeId = :vesselSchemeId " +
						"AND frd.acceptedDatetime < (" +
													"SELECT max(sfrd.acceptedDatetime) " +
													"FROM FishingTripIdentifierEntity sfti " +
													"INNER JOIN sfti.fishingTrip sft " +
													"INNER JOIN sft.fishingActivity sfa " +
													"INNER JOIN sfa.faReportDocument sfrd " +
													"WHERE sfti.tripId = :tripId" +
													")" +
						"ORDER BY frd.acceptedDatetime ASC"),

		@NamedQuery(name = FishingTripIdentifierEntity.FIND_NEXT_TRIP,
				query = "SELECT fti from FishingTripIdentifierEntity fti " +
						"INNER JOIN fti.fishingTrip ft " +
						"INNER JOIN ft.fishingActivity fa " +
						"INNER JOIN fa.faReportDocument frd " +
						"INNER JOIN frd.vesselTransportMeans vtm " +
						"INNER JOIN vtm.vesselIdentifiers vi " +
						"WHERE vi.vesselIdentifierId = :vesselId " +
						"AND vi.vesselIdentifierSchemeId = :vesselSchemeId " +
						"AND frd.acceptedDatetime > (" +
													"SELECT max(sfrd.acceptedDatetime) " +
													"FROM FishingTripIdentifierEntity sfti " +
													"INNER JOIN sfti.fishingTrip sft " +
													"INNER JOIN sft.fishingActivity sfa " +
													"INNER JOIN sfa.faReportDocument sfrd " +
													"WHERE sfti.tripId = :tripId" +
													")" +
						"ORDER BY frd.acceptedDatetime ASC")
		 */
})

@Entity
@Table(name = "activity_fishing_trip_identifier")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = {"tripId", "tripSchemeId"})
public class FishingTripIdentifierEntity implements Serializable {

	public static final String FIND_CURRENT_TRIP = "findCurrentTrip";
	public static final String FIND_PREVIOUS_TRIP = "findPreviousTrip";
	public static final String FIND_NEXT_TRIP = "findNextTrip";

	@Id
	@Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "trip_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@Column(name = "trip_id", nullable = false)
	private String tripId;

	@Column(name = "trip_scheme_id", nullable = false)
	private String tripSchemeId;

	@Column(name = "calculated_trip_start_date")
	private Instant calculatedTripStartDate;

	@Column(name = "calculated_trip_end_date")
	private Instant calculatedTripEndDate;

	/*
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fishing_trip_id", referencedColumnName = "id")
	private FishingTripEntity fishingTrip;
	*/
}
