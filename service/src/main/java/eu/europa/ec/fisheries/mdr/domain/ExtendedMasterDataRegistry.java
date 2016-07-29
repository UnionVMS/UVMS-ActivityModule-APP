/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.ArrayList;
import java.util.List;

/***
 * Extend this Entity when code and description fields (columns) are needed
 *
 */
@SuppressWarnings("serial")
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class ExtendedMasterDataRegistry extends MasterDataRegistry {
	
	@Column(name="code")
	private String code;
	
	@Column(name="description")
	private String description;
	
	protected final static String CODE           = "code";
	protected final static String DESCRIPTION    = "description";
	protected final static String EN_DESCRIPTION = "EN_description";
	
	public ExtendedMasterDataRegistry() {
	}
	
	public ExtendedMasterDataRegistry(String code, String desc) {
		this.code 		 = code;
		this.description = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Populates Code or Description of Entities that extend ExtendedMasterDataRegistry;
	 * If an Entity extends this class and has more attributes (not only code/description)
	 * then it has to override populate(...) method and the first thing to do (in the overriden populate(...))
	 * is to call super.populate(...);
	 *
	 * @param fields
	 * @return
	 * @throws FieldNotMappedException 
     */
	@Override
	public void populate(List<FieldType> fields) throws FieldNotMappedException {
		String fieldName;
		List<FieldType> fieldsToRemove = new ArrayList<FieldType>();
		for(FieldType field : fields){
			fieldName  = field.getFieldName().getValue();
			if(StringUtils.equalsIgnoreCase(CODE, fieldName)){
				setCode(field.getFieldValue().getValue());
				fieldsToRemove.add(field);
			} else if(StringUtils.equalsIgnoreCase(DESCRIPTION, fieldName) || StringUtils.equalsIgnoreCase(EN_DESCRIPTION, fieldName)){
				setDescription(field.getFieldValue().getValue());
				fieldsToRemove.add(field);
			}
		}
		// If we are inside here it means that code and description have to be both set, otherwise we have attributes missing.
		if(StringUtils.isEmpty(getCode()) || StringUtils.isEmpty(getDescription())){
			throw new FieldNotMappedException(this.getClass().getSimpleName(), "Code or Description");
		}
		fields.removeAll(fieldsToRemove);
	}

}