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
import org.apache.commons.lang.StringUtils;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "mdr_ers_events_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErsEventsCodes extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "presentation")
	private String presentation;

	public String getPresentation() {
		return presentation;
	}

	public void setPresentation(String presentation) {
		this.presentation = presentation;
	}
	
	@Override
	public String getAcronym() {
		return "EVENTS_CODES";
	}

	@Override
	public void populate(List<FieldType> fields) throws FieldNotMappedException {
		super.populate(fields);
		String fieldName;
		for(FieldType field : fields){
			fieldName  = field.getFieldName().getValue();
			if(StringUtils.equalsIgnoreCase("presentation", fieldName)){
				this.setPresentation(field.getFieldValue().getValue());
			}  else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(), field.getFieldName().getValue());
			}
		}
	}
}