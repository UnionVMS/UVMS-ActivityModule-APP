/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

@NamedQueries({
		@NamedQuery(name = VesselIdentifierEntity.FIND_LATEST_VESSEL_BY_TRIP_ID,
				//TODO: Rewrite
				query = "SELECT vi FROM FishingActivityEntity fa " +
						"INNER JOIN fa.faReportDocument frd " +
						"INNER JOIN frd.vesselTransportMeans vtm " +
						"INNER JOIN vtm.vesselIdentifiers vi " +
						"WHERE fa.fishingTrip.fishingTripKey.tripId = :tripId " +
						"ORDER BY frd.acceptedDatetime DESC")

})
@Entity
@Table(name = "activity_vessel_identifier")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
//@EqualsAndHashCode(of = {"vesselIdentifierId", "vesselIdentifierSchemeId"})
public class VesselIdentifierEntity implements Serializable {

	public static final String FIND_LATEST_VESSEL_BY_TRIP_ID = "findLatestVesselByTripId";

	@Id
	@Column(unique = true, nullable = false)
	@SequenceGenerator(name = "SEQ_GEN", sequenceName = "vsl_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vessel_transport_mean_id")
	private VesselTransportMeansEntity vesselTransportMeans;

	@Column(name = "vessel_identifier_id")
	private String vesselIdentifierId;

	@Column(name = "vessel_identifier_scheme_id")
	private String vesselIdentifierSchemeId;

}
