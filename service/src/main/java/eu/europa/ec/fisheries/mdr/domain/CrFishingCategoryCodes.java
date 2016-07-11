/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

/***
 */
@Entity
@Table(name = "mdr_cr_fishing_category_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)	
public class CrFishingCategoryCodes extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "sovereignty_waters_report_declaration")
	private String sovereigntyWatersReportDeclaration;

	public String getSovereigntyWatersReportDeclaration() {
		return sovereigntyWatersReportDeclaration;
	}

	public void setSovereigntyWatersReportDeclaration(String sovereigntyWatersReportDeclaration) {
		this.sovereigntyWatersReportDeclaration = sovereigntyWatersReportDeclaration;
	}
	
	@Override
	public String getAcronym() {
		return "CR_FISH_CATEGORY";
	}
	
	@Override
	public void populate(List<FieldType> fields) {
		for(FieldType field : fields){
			String fieldName  = field.getFieldName().getValue();
			String fieldValue = field.getFieldValue().getValue();
			if(StringUtils.equalsIgnoreCase(CODE, fieldName)){
				this.setCode(fieldValue);
			} else if(StringUtils.equalsIgnoreCase(DESCRIPTION, fieldName)){
				this.setDescription(fieldValue);
			}
		}
		
	}
}