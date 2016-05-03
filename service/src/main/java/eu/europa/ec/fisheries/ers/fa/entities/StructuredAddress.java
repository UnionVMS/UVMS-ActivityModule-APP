package eu.europa.ec.fisheries.ers.fa.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "activity_structured_address")
public class StructuredAddress {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "block_name", length = 1000)
	private String blockName;

	@Column(name = "building_name", length = 1000)
	private String buildingName;

	@Column(name = "city_name", nullable = false)
	private String cityName;

	@Column(name = "city_subdivision_name")
	private String citySubdivisionName;

	@Column(name = "country_character", nullable = false)
	private int countryCharacter;

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

	@Column(name = "postcode", nullable = false)
	private String postcode;

	@Column(name = "streetname", length = 1000)
	private String streetname;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "structuredAddress", cascade = CascadeType.ALL)
	private FluxLocation fluxLocations;
	
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "structuredAddress", cascade = CascadeType.ALL)
	private ContactParty contactParties;

	public StructuredAddress() {
	}

	public StructuredAddress(int id, String cityName, int countryCharacter,
			String postcode) {
		this.id = id;
		this.cityName = cityName;
		this.countryCharacter = countryCharacter;
		this.postcode = postcode;
	}

	public StructuredAddress(int id, String blockName, String buildingName,
			String cityName, String citySubdivisionName, int countryCharacter,
			String countryName, String countrySubdivisionName,
			String addressId, String plotId, String postOfficeBox,
			String postcode, String streetname,
			FluxLocation fluxLocations, ContactParty contactParties) {
		this.id = id;
		this.blockName = blockName;
		this.buildingName = buildingName;
		this.cityName = cityName;
		this.citySubdivisionName = citySubdivisionName;
		this.countryCharacter = countryCharacter;
		this.countryName = countryName;
		this.countrySubdivisionName = countrySubdivisionName;
		this.addressId = addressId;
		this.plotId = plotId;
		this.postOfficeBox = postOfficeBox;
		this.postcode = postcode;
		this.streetname = streetname;
		this.fluxLocations = fluxLocations;
		this.contactParties = contactParties;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getCountryCharacter() {
		return this.countryCharacter;
	}

	public void setCountryCharacter(int countryCharacter) {
		this.countryCharacter = countryCharacter;
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

	public FluxLocation getFluxLocations() {
		return fluxLocations;
	}

	public void setFluxLocations(FluxLocation fluxLocations) {
		this.fluxLocations = fluxLocations;
	}

	public ContactParty getContactParties() {
		return contactParties;
	}

	public void setContactParties(ContactParty contactParties) {
		this.contactParties = contactParties;
	}
}
