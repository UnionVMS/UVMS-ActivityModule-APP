package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_vessel_identifier")
public class VesselIdentifierEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vessel_transport_mean_id")
	private VesselTransportMeansEntity vesselTransportMeans;

	@Column(name = "vessel_identifier_id")
	private String vesselIdentifierId;

	@Column(name = "vessel_identifier_scheme_id")
	private String vesselIdentifierSchemeId;

	public VesselIdentifierEntity() {
	}

	public VesselIdentifierEntity(VesselTransportMeansEntity vesselTransportMeans,
								  String vesselIdentifierId, String vesselIdentifierSchemeId) {
		this.vesselTransportMeans = vesselTransportMeans;
		this.vesselIdentifierId = vesselIdentifierId;
		this.vesselIdentifierSchemeId = vesselIdentifierSchemeId;
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
