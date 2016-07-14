/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;


import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "activity_registration_event")
public class RegistrationEventEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "registration_location_id", nullable = false)
	private RegistrationLocationEntity registrationLocation;

	@Column(columnDefinition = "text", name = "description")
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "occurrence_datetime", length = 29)
	private Date occurrenceDatetime;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "registrationEvent")
	private VesselTransportMeansEntity vesselTransportMeanses;

	public RegistrationEventEntity() {
	}

	public int getId() {
		return this.id;
	}

	public RegistrationLocationEntity getRegistrationLocation() {
		return this.registrationLocation;
	}

	public void setRegistrationLocation(
			RegistrationLocationEntity registrationLocation) {
		this.registrationLocation = registrationLocation;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getOccurrenceDatetime() {
		return this.occurrenceDatetime;
	}

	public void setOccurrenceDatetime(Date occurrenceDatetime) {
		this.occurrenceDatetime = occurrenceDatetime;
	}

	public VesselTransportMeansEntity getVesselTransportMeanses() {
		return vesselTransportMeanses;
	}

	public void setVesselTransportMeanses(VesselTransportMeansEntity vesselTransportMeanses) {
		this.vesselTransportMeanses = vesselTransportMeanses;
	}
}