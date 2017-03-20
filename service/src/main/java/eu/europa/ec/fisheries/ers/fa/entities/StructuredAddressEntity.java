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

@Entity
@Table(name = "activity_structured_address")
public class StructuredAddressEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name="SEQ_GEN", sequenceName="str_add_seq", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contact_party_id")
	private ContactPartyEntity contactParty;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flux_location_id")
	private FluxLocationEntity fluxLocation;

	@Column(name = "block_name", length = 1000)
	private String blockName;

	@Column(name = "building_name", length = 1000)
	private String buildingName;

	@Column(name = "city_name")
	private String cityName;

	@Column(name = "city_subdivision_name")
	private String citySubdivisionName;

	@Column(name = "country")
	private String country;

	@Column(name = "country_name")
	private String countryName;

	@Column(name = "country_subdivision_name")
	private String countrySubdivisionName;

	@Column(name = "address_id")
	private String addressId;

	@Column(name = "plot_id", length = 1000)
	private String plotId;

	@Column(name = "post_office_box")
	private String postOfficeBox;

	@Column(name = "postcode")
	private String postcode;

	@Column(name = "streetname", length = 1000)
	private String streetname;

	@Column(name = "structured_address_type")
	private String structuredAddressType;

	public StructuredAddressEntity() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public ContactPartyEntity getContactParty() {
		return this.contactParty;
	}

	public void setContactParty(
			ContactPartyEntity contactParty) {
		this.contactParty = contactParty;
	}

	public FluxLocationEntity getFluxLocation() {
		return this.fluxLocation;
	}

	public void setFluxLocation(
			FluxLocationEntity fluxLocation) {
		this.fluxLocation = fluxLocation;
	}

	public String getBlockName() {
		return this.blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public String getBuildingName() {
		return this.buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getCityName() {
		return this.cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCitySubdivisionName() {
		return this.citySubdivisionName;
	}

	public void setCitySubdivisionName(String citySubdivisionName) {
		this.citySubdivisionName = citySubdivisionName;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountrySubdivisionName() {
		return this.countrySubdivisionName;
	}

	public void setCountrySubdivisionName(String countrySubdivisionName) {
		this.countrySubdivisionName = countrySubdivisionName;
	}

	public String getAddressId() {
		return this.addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public String getPlotId() {
		return this.plotId;
	}

	public void setPlotId(String plotId) {
		this.plotId = plotId;
	}

	public String getPostOfficeBox() {
		return this.postOfficeBox;
	}

	public void setPostOfficeBox(String postOfficeBox) {
		this.postOfficeBox = postOfficeBox;
	}

	public String getPostcode() {
		return this.postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getStreetname() {
		return this.streetname;
	}

	public void setStreetname(String streetname) {
		this.streetname = streetname;
	}

	public String getStructuredAddressType() {
		return structuredAddressType;
	}

	public void setStructuredAddressType(String structuredAddressType) {
		this.structuredAddressType = structuredAddressType;
	}
}