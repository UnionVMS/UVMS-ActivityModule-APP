/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain.codelists;

import eu.europa.ec.fisheries.mdr.domain.codelists.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by kovian on 11/22/2016.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "mdr_location")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Indexed
public class Location extends MasterDataRegistry {

	@Column(name = "code_2")
	@Field(name="code_2", analyze= Analyze.NO, store = Store.YES)
	private String code2;

	@Column(name = "en_name")
	@Field(name="en_name", analyze= Analyze.NO, store = Store.YES)
	private String enName;

	@Column(name = "latitude")
	@Field(name="latitude", analyze= Analyze.NO, store = Store.YES)
	private Double latitude;

	@Column(name = "longitude")
	@Field(name="longitude", analyze= Analyze.NO, store = Store.YES)
	private Double longitude;
	
	@Column(name = "fishing_port_ind")
	@Field(name="fishing_port_ind", analyze= Analyze.NO, store = Store.YES)
	private Boolean fishingPortInd;
	
	@Column(name = "landing_place_ind")
	@Field(name="landing_place_ind", analyze= Analyze.NO, store = Store.YES)
	private Boolean landingPlaceInd;
	
	@Column(name = "commercial_port_ind")
	@Field(name="commercial_port_ind", analyze= Analyze.NO, store = Store.YES)
	private Boolean commercialPortInd;
	
	@Column(name = "unlo_code")
	@Field(name="unlo_code", analyze= Analyze.NO, store = Store.YES)
	private String unloCode ;

	@Column(name = "coordinates")
	@Field(name="coordinates", analyze= Analyze.NO, store = Store.YES)
	private String coordinates ;
	
	@Column(name = "un_function_code")
	@Field(name="un_function_code", analyze= Analyze.NO, store = Store.YES)
	private String unFunctionCode ;
	
	@Column(name = "unknown_function")
	@Field(name="unknown_function", analyze= Analyze.NO, store = Store.YES)
	private Boolean unknownFunction;

	@Column(name = "port")
	@Field(name="port", analyze= Analyze.NO, store = Store.YES)
	private Boolean port;

	@Column(name = "rail")
	@Field(name="rail", analyze= Analyze.NO, store = Store.YES)
	private Boolean rail;

	@Column(name = "road")
	@Field(name="road", analyze= Analyze.NO, store = Store.YES)
	private Boolean road;

	@Column(name = "airport")
	@Field(name="airport", analyze= Analyze.NO, store = Store.YES)
	private Boolean airport;
	
	@Column(name = "postal_exchange_office")
	@Field(name="postal_exchange_office", analyze= Analyze.NO, store = Store.YES)
	private Boolean postalExchangeOffice;
	
	@Column(name = "multimodal_functions_ind")
	@Field(name="multimodal_functions_ind", analyze= Analyze.NO, store = Store.YES)
	private Boolean multimodalFunctionsInd;
	
	@Column(name = "fixed_transport_functions_ind")
	@Field(name="fixed_transport_functions_ind", analyze= Analyze.NO, store = Store.YES)
	private Boolean fixedTransportFunctionsInd;
	
	@Column(name = "border_crossing_function")
	@Field(name="border_crossing_function", analyze= Analyze.NO, store = Store.YES)
	private Boolean borderCrossingFunction;


	@Override
	public String getAcronym() {
		return "LOCATION";
	}


	@Override
	public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
		populateCommonFields(mdrDataType);
		for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
			String fieldName  = field.getName().getValue();
			String fieldValue = field.getName().getValue();
			if(StringUtils.equalsIgnoreCase("CODE2", fieldName)){
				this.setCode2(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("LATITUDE", fieldName)){
				this.setLatitude(Double.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("LONGITUDE", fieldName)){
				this.setLongitude(Double.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("FISHINGPORTIND", fieldName)){
				this.setFishingPortInd(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("LANDINGPLACEIND", fieldName)){
				this.setLandingPlaceInd(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("COMMERCIALPORTIND", fieldName)){
				this.setCommercialPortInd(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("LOCODE", fieldName)){
				this.setUnloCode(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("COORDINATES", fieldName)){
				this.setCoordinates(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("UNFCTCODE", fieldName)){
				this.setUnFunctionCode(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("UNKIND", fieldName)){
				this.setUnknownFunction(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("PORTIND", fieldName)){
				this.setPort(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("RAILIND", fieldName)){
				this.setRail(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("ROADIND", fieldName)){
				this.setRoad(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("AIRPORTIND", fieldName)){
				this.setAirport(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("POSTALIND", fieldName)){
				this.setPostalExchangeOffice(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("MULTIMODEIND", fieldName)){
				this.setMultimodalFunctionsInd(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("TRANSPIND", fieldName)){
				this.setFixedTransportFunctionsInd(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("BORDERIND", fieldName)){
				this.setBorderCrossingFunction(Boolean.valueOf(fieldValue));
			} else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
			}
		}
	}

	public String getCode2() {
		return code2;
	}
	public void setCode2(String iso3CountryCode) {
		this.code2 = iso3CountryCode;
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
	public Boolean getFishingPortInd() {
		return fishingPortInd;
	}
	public void setFishingPortInd(Boolean fishingPortInd) {
		this.fishingPortInd = fishingPortInd;
	}
	public Boolean getLandingPlaceInd() {
		return landingPlaceInd;
	}
	public void setLandingPlaceInd(Boolean landingPlaceInd) {
		this.landingPlaceInd = landingPlaceInd;
	}
	public Boolean getCommercialPortInd() {
		return commercialPortInd;
	}
	public void setCommercialPortInd(Boolean commercialPortInd) {
		this.commercialPortInd = commercialPortInd;
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
	public Boolean getMultimodalFunctionsInd() {
		return multimodalFunctionsInd;
	}
	public void setMultimodalFunctionsInd(Boolean multimodalFunctionsInd) {
		this.multimodalFunctionsInd = multimodalFunctionsInd;
	}
	public Boolean getFixedTransportFunctionsInd() {
		return fixedTransportFunctionsInd;
	}
	public void setFixedTransportFunctionsInd(Boolean fixedTransportFunctionsInd) {
		this.fixedTransportFunctionsInd = fixedTransportFunctionsInd;
	}
	public Boolean getBorderCrossingFunction() {
		return borderCrossingFunction;
	}
	public void setBorderCrossingFunction(Boolean borderCrossingFunction) {
		this.borderCrossingFunction = borderCrossingFunction;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	
}