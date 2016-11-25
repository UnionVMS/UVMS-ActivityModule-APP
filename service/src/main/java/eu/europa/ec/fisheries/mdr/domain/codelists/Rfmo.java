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
import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mdr_rfmo_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Indexed
public class Rfmo extends MasterDataRegistry {
	private static final long serialVersionUID = 1L;

	@Column(name = "code_2")
	@Field(name="code_2", analyze= Analyze.NO, store = Store.YES)
	private String code2;

	@Override
	public String getAcronym() {
		return "RFMO";
	}

	@Override
	public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
		populateCommonFields(mdrDataType);
		for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
			String fieldName  = field.getName().getValue();
			String fieldValue  = field.getName().getValue();
			if(StringUtils.equalsIgnoreCase("CODE2", fieldName)){
				this.setCode2(fieldValue);
			} else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
			}
		}
	}

	public String getCode2() {
		return code2;
	}
	public void setCode2(String code2) {
		this.code2 = code2;
	}
}