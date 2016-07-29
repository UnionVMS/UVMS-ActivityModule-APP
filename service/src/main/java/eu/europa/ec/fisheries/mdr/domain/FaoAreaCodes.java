/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

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

	@Override
	public String getAcronym() {
		return "FAO_LIKE_AREA";
	}

	@Override
	public void populate(List<FieldType> fields) throws FieldNotMappedException {
		String fieldName;
		for(FieldType field : fields){
			fieldName  = field.getFieldName().getValue();
			if(StringUtils.equalsIgnoreCase("area", fieldName)){
				this.setArea(field.getFieldValue().getValue());
			} else if(StringUtils.equalsIgnoreCase("subarea", fieldName)){
				this.setSubarea(field.getFieldValue().getValue());
			} else if(StringUtils.equalsIgnoreCase("division", fieldName)){
				this.setDivision(field.getFieldValue().getValue());
			} else if(StringUtils.equalsIgnoreCase("subdivision", fieldName)){
				this.setSubdivision(field.getFieldValue().getValue());
			} else if(StringUtils.equalsIgnoreCase("unit", fieldName)){
				this.setUnit(field.getFieldValue().getValue());
			} else if(StringUtils.equalsIgnoreCase("description", fieldName)){
				this.setDescription(field.getFieldValue().getValue());
			}  else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(), field.getFieldName().getValue());
			}
		}
	}

}