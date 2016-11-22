/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain2;


import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "mdr_farm")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FarmCodes extends MasterDataRegistry {

	private static final long serialVersionUID = 1L;

	@Column(name = "iccat")
	private String ICCAT;

	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "country")
	private String country;

	@Column(name = "registry_number")
	private String registryNumber;

	@Column(name = "name_of_ffb")
	private String nameOfFFB;

	@Column(name = "owner_name")
	private String ownerName;

	@Column(name = "owner_address")
	private String ownerAddress;

	@Column(name = "operator_name")
	private String operatorName;

	@Column(name = "operator")
	private String operator;

	@Column(name = "adress_location")
	private String adressLocation;

	@Column(name = "capacity")
	private int capacity;


	@Override
	public String getAcronym() {
		return "FARM";
	}

	@Override
	public void populate(List<MDRElementDataNodeType> fields) throws FieldNotMappedException {
		for(MDRElementDataNodeType field : fields){
			String fieldName  = field.getName().getValue();
			String fieldValue = field.getName().getValue();
			if(StringUtils.equalsIgnoreCase("ICCAT", fieldName)){
				this.setICCAT(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("serialNumber", fieldName)){
				this.setSerialNumber(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("country", fieldName)){
				this.setCountry(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("registryNumber", fieldName)){
				this.setRegistryNumber(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("nameOfFFB", fieldName)){
				this.setNameOfFFB(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("ownerAddress", fieldName)){
				this.setOwnerAddress(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("operatorName", fieldName)){
				this.setOperatorName(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("operator", fieldName)){
				this.setOperator(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("adressLocation", fieldName)){
				this.setAdressLocation(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("capacity", fieldName)){
				this.setCapacity(Integer.valueOf(fieldValue));
			} else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(),fieldName);
			}
		}
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	public String getICCAT() {
		return ICCAT;
	}
	public void setICCAT(String ICCAT) {
		this.ICCAT = ICCAT;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getRegistryNumber() {
		return registryNumber;
	}
	public void setRegistryNumber(String registryNumber) {
		this.registryNumber = registryNumber;
	}
	public String getNameOfFFB() {
		return nameOfFFB;
	}
	public void setNameOfFFB(String nameOfFFB) {
		this.nameOfFFB = nameOfFFB;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerAddress() {
		return ownerAddress;
	}
	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getAdressLocation() {
		return adressLocation;
	}
	public void setAdressLocation(String adressLocation) {
		this.adressLocation = adressLocation;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

}