/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "mdr_territory_and_currency_iso_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TerritoryAndCurrencyISOCodes extends MasterDataRegistry {

	@Column(name = "iso3_code")
	private String iso3Code;
	
	@Column(name = "iso4_code")
	private String iso4Code;
	
	@Column(name = "territory_name")
	private String territoryName;
	
	@Column(name = "currency_code")
	private String currencyCode;
	
	@Column(name = "currency_definition")
	private String currencyDefinition;

	@Override
	public String getAcronym() {
		return "TERRITORY_CURR";
	}


	@Override
	public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
		populateCommonFields(mdrDataType);
		for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
			String fieldName  = field.getName().getValue();
			String fieldValue = field.getName().getValue();
			if(StringUtils.equalsIgnoreCase("iso3Code", fieldName)){
				this.setIso3Code(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("iso4Code", fieldName)){
				this.setIso4Code(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("territoryName", fieldName)){
				this.setTerritoryName(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("currencyCode", fieldName)){
				this.setCurrencyCode(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("currencyDefinition", fieldName)){
				this.setCurrencyDefinition(fieldValue);
			} else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
			}
		}
	}

	public String getIso3Code() {
		return iso3Code;
	}
	public void setIso3Code(String iso3Code) {
		this.iso3Code = iso3Code;
	}
	public String getIso4Code() {
		return iso4Code;
	}
	public void setIso4Code(String iso4Code) {
		this.iso4Code = iso4Code;
	}
	public String getTerritoryName() {
		return territoryName;
	}
	public void setTerritoryName(String territoryName) {
		this.territoryName = territoryName;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCurrencyDefinition() {
		return currencyDefinition;
	}
	public void setCurrencyDefinition(String currencyDefinition) {
		this.currencyDefinition = currencyDefinition;
	}
	
}