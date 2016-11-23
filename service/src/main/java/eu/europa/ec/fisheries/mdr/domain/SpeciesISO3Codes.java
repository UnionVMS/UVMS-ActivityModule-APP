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
import org.hibernate.search.annotations.*;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "mdr_species_iso3_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Indexed
public class SpeciesISO3Codes extends MasterDataRegistry {

	@Column(name = "code")
	@Field(name="sort_code", analyze= Analyze.NO, store = Store.YES)
	//@SortableField(forField = "sort_code")
	private String code;
	
	@Column(name = "scientific_name")
	/*@Fields({
			@Field,
			@Field(name="sort_scientificName", analyze= Analyze.NO)
	})
	@SortableField(forField = "sort_scientificName")*/
	private String scientificName;
	
	@Column(name = "english_name")
	/*@Fields({
			@Field,
			@Field(name="sort_englishName", analyze= Analyze.NO)
	})
	@SortableField(forField = "sort_englishName")*/
	private String englishName;
	
	@Column(name = "french_name")
/*	@Fields({
			@Field,
			@Field(name="sort_frenchName", analyze= Analyze.NO)
	})
	@SortableField(forField = "sort_frenchName")*/
	private String frenchName;
	
	@Column(name = "spanish_name")
	/*@Fields({
			@Field,
			@Field(name="sort_spanishName", analyze= Analyze.NO)
	})
	@SortableField(forField = "sort_spanishName")*/
	private String spanishName;
	
	@Column(name = "author")
/*	@Fields({
			@Field,
			@Field(name="sort_author", analyze= Analyze.NO)
	})
	@SortableField(forField = "sort_author")*/
	private String author;
	
	@Column(name = "family")
	/*@Fields({
			@Field,
			@Field(name="sort_family", analyze= Analyze.NO)
	})
	@SortableField(forField = "sort_family")*/
	private String family;
	
	@Column(name = "iso_order")
	/*@Fields({
			@Field,
			@Field(name="sort_isoOrder", analyze= Analyze.NO)
	})
	@SortableField(forField = "sort_isoOrder")*/
	private String isoOrder;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getScientificName() {
		return scientificName;
	}
	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public String getFrenchName() {
		return frenchName;
	}
	public void setFrenchName(String frenchName) {
		this.frenchName = frenchName;
	}
	public String getSpanishName() {
		return spanishName;
	}
	public void setSpanishName(String spanishName) {
		this.spanishName = spanishName;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public String getIsoOrder() {
		return isoOrder;
	}
	public void setIsoOrder(String order) {
		this.isoOrder = order;
	}
	
	@Override
	public String getAcronym() {
		return "FAO_SPECIES";
	}

	@Override
	public void populate(List<FieldType> fields) throws FieldNotMappedException {
		String fieldName;
		for (FieldType field : fields) {
			fieldName = field.getFieldName().getValue();
			if (StringUtils.equalsIgnoreCase("code", fieldName)) {
				this.setCode(field.getFieldValue().getValue());
			} else if (StringUtils.equalsIgnoreCase("scientificName", fieldName)) {
				this.setScientificName(field.getFieldValue().getValue());
			} else if (StringUtils.equalsIgnoreCase("englishName", fieldName)) {
				this.setEnglishName(field.getFieldValue().getValue());
			} else if (StringUtils.equalsIgnoreCase("frenchName", fieldName)) {
				this.setFrenchName(field.getFieldValue().getValue());
			} else if (StringUtils.equalsIgnoreCase("spanishName", fieldName)) {
				this.setSpanishName(field.getFieldValue().getValue());
			} else if (StringUtils.equalsIgnoreCase("author", fieldName)) {
				this.setAuthor(field.getFieldValue().getValue());
			} else if (StringUtils.equalsIgnoreCase("family", fieldName)) {
				this.setFamily(field.getFieldValue().getValue());
			} else if (StringUtils.equalsIgnoreCase("isoOrder", fieldName)) {
				this.setIsoOrder(field.getFieldValue().getValue());
			} else {
				throw new FieldNotMappedException(getClass().getSimpleName(), fieldName);
			}
		}
	}

}
