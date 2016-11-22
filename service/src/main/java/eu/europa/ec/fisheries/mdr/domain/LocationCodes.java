/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import eu.europa.ec.fisheries.mdr.domain.base.ExtendedMasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "mdr_location_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LocationCodes extends ExtendedMasterDataRegistry {

	@Column(name = "iso3_country_code")
	private String iso3CountryCode;

	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "longitude")
	private Double longitude;
	
	@Column(name = "fishing_port")
	private Boolean fishingPort;
	
	@Column(name = "landing_place")
	private Boolean landingPlace;
	
	@Column(name = "commercial_port")
	private Boolean commercialPort;
	
	@Column(name = "unlo_code")
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


	@Override
	public String getAcronym() {
		return "LOCATION";
	}

	@Override
	public void populate(List<MDRElementDataNodeType> fields) throws FieldNotMappedException {
		super.populate(fields);
		for(MDRElementDataNodeType field : fields){
			String fieldName  = field.getName().getValue();
			String fieldValue = field.getName().getValue();
			if(StringUtils.equalsIgnoreCase("iso3CountryCode", fieldName)){
				this.setIso3CountryCode(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("latitude", fieldName)){
				this.setLatitude(Double.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("longitude", fieldName)){
				this.setLongitude(Double.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("fishingPort", fieldName)){
				this.setFishingPort(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("landingPlace", fieldName)){
				this.setLandingPlace(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("commercialPort", fieldName)){
				this.setCommercialPort(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("unloCode", fieldName)){
				this.setUnloCode(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("coordinates", fieldName)){
				this.setCoordinates(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("unFunctionCode", fieldName)){
				this.setUnFunctionCode(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("unknownFunction", fieldName)){
				this.setUnknownFunction(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("port", fieldName)){
				this.setPort(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("rail", fieldName)){
				this.setRail(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("road", fieldName)){
				this.setRoad(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("airport", fieldName)){
				this.setAirport(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("postalExchangeOffice", fieldName)){
				this.setPostalExchangeOffice(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("multimodalFunctions", fieldName)){
				this.setMultimodalFunctions(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("fixedTransportFunctions", fieldName)){
				this.setFixedTransportFunctions(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("borderCrossingFunction", fieldName)){
				this.setBorderCrossingFunction(Boolean.valueOf(fieldValue));
			} else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
			}
		}
	}

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