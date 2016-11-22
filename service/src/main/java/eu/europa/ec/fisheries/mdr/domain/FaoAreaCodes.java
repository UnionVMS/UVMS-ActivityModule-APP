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
@Table(name = "mdr_fao_area_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FaoAreaCodes extends MasterDataRegistry {

	@Column(name = "area")
	private String area;

	@Column(name = "subarea")
	private String subarea;

	@Column(name = "division")
	private String division;

	@Column(name = "subdivision")
	private String subdivision;

	@Column(name = "unit")
	private String unit;

	@Column(name = "description")
	private String description;

	@Override
	public String getAcronym() {
		return "FAO_LIKE_AREA";
	}


	@Override
	public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
		populateCommonFields(mdrDataType);
		for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
			String fieldName  = field.getName().getValue();
			String fieldValue  = field.getName().getValue();
			if(StringUtils.equalsIgnoreCase("area", fieldName)){
				this.setArea(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("subarea", fieldName)){
				this.setSubarea(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("division", fieldName)){
				this.setDivision(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("subdivision", fieldName)){
				this.setSubdivision(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("unit", fieldName)){
				this.setUnit(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("description", fieldName)){
				this.setDescription(fieldValue);
			}  else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
			}
		}
	}

	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getSubarea() {
		return subarea;
	}
	public void setSubarea(String subarea) {
		this.subarea = subarea;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getSubdivision() {
		return subdivision;
	}
	public void setSubdivision(String subdivision) {
		this.subdivision = subdivision;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


}