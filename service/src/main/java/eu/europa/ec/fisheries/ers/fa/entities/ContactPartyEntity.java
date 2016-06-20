package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_contact_party")
public class ContactPartyEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "contact_person_id", nullable = false)
	private ContactPersonEntity contactPerson;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vessel_transport_means_id")
	private VesselTransportMeansEntity vesselTransportMeans;
	
	@Column(name = "role_code", nullable = false)
	private String roleCode;
	
	@Column(name = "role_code_list_id", nullable = false)
	private String roleCodeListId;
	

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contactParty", cascade = CascadeType.ALL)
	private Set<StructuredAddressEntity> structuredAddresses = new HashSet<StructuredAddressEntity>(
			0);

	public ContactPartyEntity() {
	}

	public ContactPartyEntity(ContactPersonEntity contactPerson, String roleCode,
							  String roleCodeListId) {
		this.contactPerson = contactPerson;
		this.roleCode = roleCode;
		this.roleCodeListId = roleCodeListId;
	}

	public ContactPartyEntity(ContactPersonEntity contactPerson,
							  VesselTransportMeansEntity vesselTransportMeans,
							  String roleCode, String roleCodeListId,
							  Set<StructuredAddressEntity> structuredAddresses) {
		this.contactPerson = contactPerson;
		this.vesselTransportMeans = vesselTransportMeans;
		this.roleCode = roleCode;
		this.roleCodeListId = roleCodeListId;
		this.structuredAddresses = structuredAddresses;
	}

	
	public int getId() {
		return this.id;
	}

	
	public ContactPersonEntity getContactPerson() {
		return this.contactPerson;
	}

	public void setContactPerson(
			ContactPersonEntity contactPerson) {
		this.contactPerson = contactPerson;
	}


	public VesselTransportMeansEntity getVesselTransportMeans() {
		return this.vesselTransportMeans;
	}

	public void setVesselTransportMeans(
			VesselTransportMeansEntity vesselTransportMeans) {
		this.vesselTransportMeans = vesselTransportMeans;
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

	public Set<StructuredAddressEntity> getStructuredAddresses() {
		return this.structuredAddresses;
	}

	public void setStructuredAddresses(
			Set<StructuredAddressEntity> structuredAddresses) {
		this.structuredAddresses = structuredAddresses;
	}

}
