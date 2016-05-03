package eu.europa.ec.fisheries.ers.fa.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.OneToOne;

@Entity
@Table(name = "activity_vessel_transport_means")
public class VesselTransportMeans {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "role_code")
	private String roleCode;

	@Column(name = "role_code_list_id")
	private String roleCodeListId;

	@Column(name = "vessel_transport_means_id", nullable = false)
	private String vesselTransportMeansId;

	@Column(name = "means_schema_id")
	private String meansSchemaId;

	@Column(columnDefinition = "text", name = "name")
	private String name;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans")
	private Set<VesselCountry> vesselCountries = new HashSet<VesselCountry>(0);
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
	private FaReportDocument faReportDocuments;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans")
	private Set<ContactParty> contactParties = new HashSet<ContactParty>(0);

	public VesselTransportMeans() {
	}

	public VesselTransportMeans(int id, String vesselTransportMeansId) {
		this.id = id;
		this.vesselTransportMeansId = vesselTransportMeansId;
	}

	public VesselTransportMeans(int id, String roleCode, String roleCodeListId,
			String vesselTransportMeansId, String meansSchemaId, String name,
			Set<VesselCountry> vesselCountries,
			FaReportDocument faReportDocuments,
			Set<ContactParty> contactParties) {
		this.id = id;
		this.roleCode = roleCode;
		this.roleCodeListId = roleCodeListId;
		this.vesselTransportMeansId = vesselTransportMeansId;
		this.meansSchemaId = meansSchemaId;
		this.name = name;
		this.vesselCountries = vesselCountries;
		this.faReportDocuments = faReportDocuments;
		this.contactParties = contactParties;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleCodeListId() {
		return this.roleCodeListId;
	}

	public void setRoleCodeListId(String roleCodeListId) {
		this.roleCodeListId = roleCodeListId;
	}

	public String getVesselTransportMeansId() {
		return this.vesselTransportMeansId;
	}

	public void setVesselTransportMeansId(String vesselTransportMeansId) {
		this.vesselTransportMeansId = vesselTransportMeansId;
	}

	public String getMeansSchemaId() {
		return this.meansSchemaId;
	}

	public void setMeansSchemaId(String meansSchemaId) {
		this.meansSchemaId = meansSchemaId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<VesselCountry> getVesselCountries() {
		return this.vesselCountries;
	}

	public void setVesselCountries(Set<VesselCountry> vesselCountries) {
		this.vesselCountries = vesselCountries;
	}

	public FaReportDocument getFaReportDocuments() {
		return faReportDocuments;
	}

	public void setFaReportDocuments(FaReportDocument faReportDocuments) {
		this.faReportDocuments = faReportDocuments;
	}

	public Set<ContactParty> getContactParties() {
		return this.contactParties;
	}

	public void setContactParties(Set<ContactParty> contactParties) {
		this.contactParties = contactParties;
	}

}
