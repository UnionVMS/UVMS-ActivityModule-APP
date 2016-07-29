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
import eu.europa.ec.fisheries.uvms.domain.RectangleCoordinates;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "mdr_ices_statistical_rectangles_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IcesStatisticalRectanglesCodes extends MasterDataRegistry {

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
	public void populate(List<FieldType> fields) throws FieldNotMappedException {
		String fieldName;
		for (FieldType field : fields) {
			fieldName = field.getFieldName().getValue();
			if (StringUtils.equalsIgnoreCase("icesName", fieldName)) {
				this.setIcesName(field.getFieldValue().getValue());
			} else {
				throw new FieldNotMappedException(getClass().getSimpleName(), fieldName);
			}
		}
	}

}