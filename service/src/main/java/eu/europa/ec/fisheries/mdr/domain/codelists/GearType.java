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
 * Created by kovian on 11/23/2016.
 */
@Entity
@Table(name = "mdr_gear_type")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Indexed
public class GearType extends MasterDataRegistry {
	private static final long serialVersionUID = 1L; 
	
	@Column(name = "group_name")
	@Field(name="group_name", analyze= Analyze.NO, store = Store.YES)
	private String category;
	
	@Column(name = "sub_group_name")
	@Field(name="sub_group_name", analyze= Analyze.NO, store = Store.YES)
	private String subCategory;

	@Column(name = "iss_cfg_code")
	@Field(name="iss_cfg_code", analyze= Analyze.NO, store = Store.YES)
	private String issCfgCode;

	@Column(name = "iccat_code")
	@Field(name="iccat_code", analyze= Analyze.NO, store = Store.YES)
	private String iccatCode;

	@Column(name = "target")
	@Field(name="target", analyze= Analyze.NO, store = Store.YES)
	private String target;

	@Override
	public String getAcronym() {
		return "GEAR_TYPE";
	}


	@Override
	public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
		populateCommonFields(mdrDataType);
		for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
			String fieldName  = field.getName().getValue();
			String fieldValue  = field.getName().getValue();
			if(StringUtils.equalsIgnoreCase("CATEGORY", fieldName)){
				this.setCategory(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("SUBCATEGORY", fieldName)){
				this.setSubCategory(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("ICCATCODE", fieldName)){
				this.setIccatCode(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("ISSCFGCODE", fieldName)){
				this.setIssCfgCode(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("TARGET", fieldName)){
				this.setTarget(fieldValue);
			} else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
			}
		}
	}


	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	public String getIssCfgCode() {
		return issCfgCode;
	}
	public void setIssCfgCode(String issCfgCode) {
		this.issCfgCode = issCfgCode;
	}
	public String getIccatCode() {
		return iccatCode;
	}
	public void setIccatCode(String iccatCode) {
		this.iccatCode = iccatCode;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
}