package eu.europa.ec.fisheries.mdr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kovian
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "location_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LocationCodes extends ExtendedMasterDataRegistry {

	@Column(name = "iso3_country_code")
	private String iso3CountryCode;
	
	@Column(name = "latitude")
	private Double latitude;
	
	@Column(name = "longitude")
	private Double longitude;
	
	@Column(name = "fishingPort")
	private Boolean fishingPort;
	
	@Column(name = "landingPlace")
	private Boolean landingPlace;
	
	@Column(name = "commercial_port")
	private Boolean commercialPort;
	
	@Column(name = "unloCode")
	private String unloCode ;
	
	@Column(name = "coordinates")
	private String coordinates ;
	
	@Column(name = "un_function_code")
	private String unFunctionCode ;
	
	@Column(name = "unknown_function")
	private Boolean unknownFunction;
	
	@Column(name = "port")
	private Boolean port;
	
	@Column(name = "rail")
	private Boolean rail;
	
	@Column(name = "road")
	private Boolean road;
	
	@Column(name = "airport")
	private Boolean airport;
	
	@Column(name = "postal_exchange_office")
	private Boolean postalExchangeOffice;
	
	@Column(name = "multimodal_functions")
	private Boolean multimodalFunctions;
	
	@Column(name = "fixed_transport_functions")
	private Boolean fixedTransportFunctions;
	
	@Column(name = "border_crossing_function")
	private Boolean borderCrossingFunction;
	
	
	public String getIso3CountryCode() {
		return iso3CountryCode;
	}
	public void setIso3CountryCode(String iso3CountryCode) {
		this.iso3CountryCode = iso3CountryCode;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Boolean getFishingPort() {
		return fishingPort;
	}
	public void setFishingPort(Boolean fishingPort) {
		this.fishingPort = fishingPort;
	}
	public Boolean getLandingPlace() {
		return landingPlace;
	}
	public void setLandingPlace(Boolean landingPlace) {
		this.landingPlace = landingPlace;
	}
	public Boolean getCommercialPort() {
		return commercialPort;
	}
	public void setCommercialPort(Boolean commercialPort) {
		this.commercialPort = commercialPort;
	}
	public String getUnloCode() {
		return unloCode;
	}
	public void setUnloCode(String unloCode) {
		this.unloCode = unloCode;
	}
	public String getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}
	public String getUnFunctionCode() {
		return unFunctionCode;
	}
	public void setUnFunctionCode(String unFunctionCode) {
		this.unFunctionCode = unFunctionCode;
	}
	public Boolean getUnknownFunction() {
		return unknownFunction;
	}
	public void setUnknownFunction(Boolean unknownFunction) {
		this.unknownFunction = unknownFunction;
	}
	public Boolean getPort() {
		return port;
	}
	public void setPort(Boolean port) {
		this.port = port;
	}
	public Boolean getRail() {
		return rail;
	}
	public void setRail(Boolean rail) {
		this.rail = rail;
	}
	public Boolean getRoad() {
		return road;
	}
	public void setRoad(Boolean road) {
		this.road = road;
	}
	public Boolean getAirport() {
		return airport;
	}
	public void setAirport(Boolean airport) {
		this.airport = airport;
	}
	public Boolean getPostalExchangeOffice() {
		return postalExchangeOffice;
	}
	public void setPostalExchangeOffice(Boolean postalExchangeOffice) {
		this.postalExchangeOffice = postalExchangeOffice;
	}
	public Boolean getMultimodalFunctions() {
		return multimodalFunctions;
	}
	public void setMultimodalFunctions(Boolean multimodalFunctions) {
		this.multimodalFunctions = multimodalFunctions;
	}
	public Boolean getFixedTransportFunctions() {
		return fixedTransportFunctions;
	}
	public void setFixedTransportFunctions(Boolean fixedTransportFunctions) {
		this.fixedTransportFunctions = fixedTransportFunctions;
	}
	public Boolean getBorderCrossingFunction() {
		return borderCrossingFunction;
	}
	public void setBorderCrossingFunction(Boolean borderCrossingFunction) {
		this.borderCrossingFunction = borderCrossingFunction;
	}
	
	
	
}
