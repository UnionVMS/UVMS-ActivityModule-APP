package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_vessel_country")
public class VesselCountry implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vessel_transport_means_id")
	private VesselTransportMeans vesselTransportMeans;

	@Column(name = "vessel_country_id", nullable = false)
	private String vesselCountryId;

	@Column(name = "vessel_country_scheme_id", nullable = false)
	private String vesselCountrySchemeId;

	public VesselCountry() {
	}

	public VesselCountry(int id, String vesselCountryId,
			String vesselCountrySchemeId) {
		this.id = id;
		this.vesselCountryId = vesselCountryId;
		this.vesselCountrySchemeId = vesselCountrySchemeId;
	}

	public VesselCountry(int id, VesselTransportMeans vesselTransportMeans,
			String vesselCountryId, String vesselCountrySchemeId) {
		this.id = id;
		this.vesselTransportMeans = vesselTransportMeans;
		this.vesselCountryId = vesselCountryId;
		this.vesselCountrySchemeId = vesselCountrySchemeId;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public VesselTransportMeans getVesselTransportMeans() {
		return this.vesselTransportMeans;
	}

	public void setVesselTransportMeans(
			VesselTransportMeans vesselTransportMeans) {
		this.vesselTransportMeans = vesselTransportMeans;
	}

	public String getVesselCountryId() {
		return this.vesselCountryId;
	}

	public void setVesselCountryId(String vesselCountryId) {
		this.vesselCountryId = vesselCountryId;
	}

	public String getVesselCountrySchemeId() {
		return this.vesselCountrySchemeId;
	}

	public void setVesselCountrySchemeId(String vesselCountrySchemeId) {
		this.vesselCountrySchemeId = vesselCountrySchemeId;
	}

}
