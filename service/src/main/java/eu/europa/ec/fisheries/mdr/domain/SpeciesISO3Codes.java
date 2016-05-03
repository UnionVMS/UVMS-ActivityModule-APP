package eu.europa.ec.fisheries.mdr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kovian
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "species_iso3_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SpeciesISO3Codes extends MasterDataRegistry {

	@Column(name = "code")
	private String code;
	
	@Column(name = "scientific_name")
	private String scientificName;
	
	@Column(name = "english_name")
	private String englishName;
	
	@Column(name = "french_name")
	private String frenchName;
	
	@Column(name = "spanish_name")
	private String spanishName;
	
	@Column(name = "author")
	private String author;
	
	@Column(name = "family")
	private String family;
	
	@Column(name = "order")
	private String order;

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

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	
}
