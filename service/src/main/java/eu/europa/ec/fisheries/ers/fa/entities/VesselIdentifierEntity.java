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
		@NamedQuery(name = VesselIdentifierEntity.FIND_LATEST_VESSEL_BY_TRIP_ID,
				query = "SELECT vi from FishingTripIdentifierEntity fti " +
						"INNER JOIN fti.fishingTrip ft " +
						"INNER JOIN ft.fishingActivity fa " +
						"INNER JOIN fa.faReportDocument frd " +
						"INNER JOIN frd.vesselTransportMeans vt " +
						"INNER JOIN vt.vesselIdentifiers vi " +
						"WHERE fti.tripId = :tripId " +
						"ORDER BY frd.acceptedDatetime DESC")
})

@Entity
@Table(name = "activity_vessel_identifier")
public class VesselIdentifierEntity implements Serializable {

	public static final String FIND_LATEST_VESSEL_BY_TRIP_ID = "findLatestVesselByTripId";

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name="SEQ_GEN", sequenceName="vsl_id_seq", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vessel_transport_mean_id")
	private VesselTransportMeansEntity vesselTransportMeans;

	@Column(name = "vessel_identifier_id")
	private String vesselIdentifierId;

	@Column(name = "vessel_identifier_scheme_id")
	private String vesselIdentifierSchemeId;

	public VesselIdentifierEntity() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public VesselTransportMeansEntity getVesselTransportMeans() {
		return this.vesselTransportMeans;
	}

	public void setVesselTransportMeans(
			VesselTransportMeansEntity vesselTransportMeans) {
		this.vesselTransportMeans = vesselTransportMeans;
	}

	public String getVesselIdentifierId() {
		return this.vesselIdentifierId;
	}

	public void setVesselIdentifierId(String vesselIdentifierId) {
		this.vesselIdentifierId = vesselIdentifierId;
	}

	public String getVesselIdentifierSchemeId() {
		return this.vesselIdentifierSchemeId;
	}

	public void setVesselIdentifierSchemeId(String vesselIdentifierSchemeId) {
		this.vesselIdentifierSchemeId = vesselIdentifierSchemeId;
	}

}