/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain_temp;

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

	@Column(name = "level")
	private String level;

	@Column(name = "en_level_name")
	private String enLevelName;

	@Column(name = "terminal_ind")
	private String terminalInd;

	@Override
	public String getAcronym() {
		return "FAO_AREA";
	}


	@Override
	public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
		populateCommonFields(mdrDataType);
		for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
			String fieldName  = field.getName().getValue();
			String fieldValue  = field.getName().getValue();
			if(StringUtils.equalsIgnoreCase("LEVEL", fieldName)){
				this.setLevel(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("ENLEVELNAME", fieldName)){
				this.setEnLevelName(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("TERMINALIND", fieldName)){
				this.setTerminalInd(fieldValue);
			} else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
			}
		}
	}

	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getEnLevelName() {
		return enLevelName;
	}
	public void setEnLevelName(String enLevelName) {
		this.enLevelName = enLevelName;
	}
	public String getTerminalInd() {
		return terminalInd;
	}
	public void setTerminalInd(String terminalInd) {
		this.terminalInd = terminalInd;
	}
}