/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import eu.europa.ec.fisheries.mdr.domain.base.ExtendedMasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "mdr_conversion_factor")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)	
public class ConversionFactor extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "species")
	private String species;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "presentation")
	private String presentation;
	
	@Column(name = "factor")
	private Double factor;
	
	@Column(name = "collective")
	private Boolean collective;
	
	@Column(name = "source")
	private String source;

	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPresentation() {
		return presentation;
	}
	public void setPresentation(String presentation) {
		this.presentation = presentation;
	}
	public Double getFactor() {
		return factor;
	}
	public void setFactor(Double factor) {
		this.factor = factor;
	}
	public Boolean getCollective() {
		return collective;
	}
	public void setCollective(Boolean collective) {
		this.collective = collective;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	@Override
	public String getAcronym() {
		return "CONVERSION_FACTOR";
	}

	@Override
	public void populate(List<MDRElementDataNodeType> fields) throws FieldNotMappedException {
		super.populate(fields);
		for(MDRElementDataNodeType field : fields){
			String fieldName  = field.getName().getValue();
			String fieldValue  = field.getName().getValue();
			if(StringUtils.equalsIgnoreCase("species", fieldName)){
				this.setSpecies(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("country", fieldName)){
				this.setCountry(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("state", fieldName)){
				this.setState(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("presentation", fieldName)){
				this.setPresentation(fieldValue);
			} else if(StringUtils.equalsIgnoreCase("factor", fieldName)){
				this.setFactor(Double.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("collective", fieldName)){
				this.setCollective(Boolean.valueOf(fieldValue));
			} else if(StringUtils.equalsIgnoreCase("source", fieldName)){
				this.setSource(fieldValue);
			} else {
				throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldValue);
			}
		}
	}
}