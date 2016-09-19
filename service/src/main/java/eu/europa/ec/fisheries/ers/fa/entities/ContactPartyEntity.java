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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contactParty", cascade = CascadeType.ALL)
	private Set<ContactPartyRoleEntity> contactPartyRole;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contactParty", cascade = CascadeType.ALL)
	private Set<StructuredAddressEntity> structuredAddresses;

	public ContactPartyEntity() {
		super();
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

	public Set<StructuredAddressEntity> getStructuredAddresses() {
		return this.structuredAddresses;
	}

	public void setStructuredAddresses(
			Set<StructuredAddressEntity> structuredAddresses) {
		this.structuredAddresses = structuredAddresses;
	}

	public Set<ContactPartyRoleEntity> getContactPartyRole() {
		return contactPartyRole;
	}

	public void setContactPartyRole(Set<ContactPartyRoleEntity> contactPartyRole) {
		this.contactPartyRole = contactPartyRole;
	}
}