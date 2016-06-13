package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_structured_address")
public class StructuredAddressEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Column(name = "city_name", nullable = false)
	private String cityName;

	@Column(name = "city_subdivision_name")
	private String citySubdivisionName;

	@Column(name = "country", nullable = false)
	private int country;

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

	public StructuredAddressEntity() {
	}

	public StructuredAddressEntity(String cityName,
								   int country, String postcode) {
		this.cityName = cityName;
		this.country = country;
		this.postcode = postcode;
	}

	public StructuredAddressEntity(ContactPartyEntity contactParty,
								   FluxLocationEntity fluxLocation, String blockName,
								   String buildingName, String cityName, String citySubdivisionName,
								   int country, String countryName,
								   String countrySubdivisionName, String addressId, String plotId,
								   String postOfficeBox, String postcode, String streetname) {
		this.contactParty = contactParty;
		this.fluxLocation = fluxLocation;
		this.blockName = blockName;
		this.buildingName = buildingName;
		this.cityName = cityName;
		this.citySubdivisionName = citySubdivisionName;
		this.country = country;
		this.countryName = countryName;
		this.countrySubdivisionName = countrySubdivisionName;
		this.addressId = addressId;
		this.plotId = plotId;
		this.postOfficeBox = postOfficeBox;
		this.postcode = postcode;
		this.streetname = streetname;
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

	public int getCountry() {
		return this.country;
	}

	public void setCountry(int countryCharacter) {
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

}
