/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain.codelists;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import eu.europa.ec.fisheries.uvms.domain.RectangleCoordinates;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.Indexed;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mdr_ices_statistical_rectangles")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Indexed
public class IcesStatisticalRectangles extends MasterDataRegistry {

	private static final long serialVersionUID = 1L;

	@Column(name = "ices_name")
	private String icesName;
	
	@Embedded
	private RectangleCoordinates rectangle;

	public String getIcesName() {
		return icesName;
	}
	public void setIcesName(String icesName) {
		this.icesName = icesName;
	}
	public RectangleCoordinates getRectangle() {
		return rectangle;
	}
	public void setRectangle(RectangleCoordinates rectangle) {
		this.rectangle = rectangle;
	}
	
	@Override
	public String getAcronym() { 
		return "ICES_STAT_RECTANGLE";
	}

	// TODO ; check the response from flux for the RectangleCoordinates!! and change populate accordingly

	@Override
	public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
		populateCommonFields(mdrDataType);
		rectangle = new RectangleCoordinates();
		for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
			String fieldName  = field.getName().getValue();
			String fieldValue = field.getName().getValue();
			if (StringUtils.equalsIgnoreCase("icesName", fieldName)) {
				this.setIcesName(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("WEST", fieldName)){
				rectangle.setWest(Double.parseDouble(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("EAST", fieldName)){
				rectangle.setEast(Double.parseDouble(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("NORTH", fieldName)){
				rectangle.setNorth(Double.parseDouble(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("SOUTH", fieldName)){
				rectangle.setSouth(Double.parseDouble(fieldValue));
			} else {
				throw new FieldNotMappedException(getClass().getSimpleName(), fieldName);
			}
		}
	}

}