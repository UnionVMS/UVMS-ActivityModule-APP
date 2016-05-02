package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "activity_contact_party", uniqueConstraints = {
		@UniqueConstraint(columnNames = "contact_person_id"),
		@UniqueConstraint(columnNames = "structured_address_id") })
public class ContactParty {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "structured_address_id", nullable = true)
	private StructuredAddress structuredAddress;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contact_person_id", nullable = false)
	private ContactPerson contactPerson;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vessel_transport_means_id")
	private VesselTransportMeans vesselTransportMeans;
	
	@Column(name = "role_code", nullable = false)
	private String roleCode;
	
	@Column(name = "role_code_list_id", nullable = false)
	private String roleCodeListId;

	public ContactParty() {
	}

	public ContactParty(int id, ContactPerson contactPerson, String roleCode,
			String roleCodeListId) {
		this.id = id;
		this.contactPerson = contactPerson;
		this.roleCode = roleCode;
		this.roleCodeListId = roleCodeListId;
	}

	public ContactParty(int id, StructuredAddress structuredAddress,
			ContactPerson contactPerson,
			VesselTransportMeans vesselTransportMeans, String roleCode,
			String roleCodeListId) {
		this.id = id;
		this.structuredAddress = structuredAddress;
		this.contactPerson = contactPerson;
		this.vesselTransportMeans = vesselTransportMeans;
		this.roleCode = roleCode;
		this.roleCodeListId = roleCodeListId;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public StructuredAddress getStructuredAddress() {
		return this.structuredAddress;
	}

	public void setStructuredAddress(StructuredAddress structuredAddress) {
		this.structuredAddress = structuredAddress;
	}

	
	public ContactPerson getContactPerson() {
		return this.contactPerson;
	}

	public void setContactPerson(ContactPerson contactPerson) {
		this.contactPerson = contactPerson;
	}

	
	public VesselTransportMeans getVesselTransportMeans() {
		return this.vesselTransportMeans;
	}

	public void setVesselTransportMeans(
			VesselTransportMeans vesselTransportMeans) {
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

}
