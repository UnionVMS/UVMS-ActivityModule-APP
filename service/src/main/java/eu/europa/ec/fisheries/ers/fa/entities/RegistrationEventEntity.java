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

	@OneToOne(fetch = FetchType.LAZY)
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

	public RegistrationEventEntity(RegistrationLocationEntity registrationLocation) {
		this.registrationLocation = registrationLocation;
	}

	public RegistrationEventEntity(RegistrationLocationEntity registrationLocation,
								   String description, Date occurrenceDatetime,
								   VesselTransportMeansEntity vesselTransportMeans) {
		this.registrationLocation = registrationLocation;
		this.description = description;
		this.occurrenceDatetime = occurrenceDatetime;
		this.vesselTransportMeanses = vesselTransportMeans;
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
