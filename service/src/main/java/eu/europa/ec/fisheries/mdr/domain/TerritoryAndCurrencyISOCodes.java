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
@Table(name = "territory_and_currency_iso_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TerritoryAndCurrencyISOCodes extends MasterDataRegistry {

	@Column(name = "iso3_code")
	private String iso3Code;
	
	@Column(name = "iso4_code")
	private String iso2Code;
	
	@Column(name = "territory_name")
	private String territoryName;
	
	@Column(name = "currency_code")
	private String currencyCode;
	
	@Column(name = "currency_definition")
	private String currencyDefinition;

	public String getIso3Code() {
		return iso3Code;
	}

	public void setIso3Code(String iso3Code) {
		this.iso3Code = iso3Code;
	}

	public String getIso2Code() {
		return iso2Code;
	}

	public void setIso2Code(String iso2Code) {
		this.iso2Code = iso2Code;
	}

	public String getTerritoryName() {
		return territoryName;
	}

	public void setTerritoryName(String territoryName) {
		this.territoryName = territoryName;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCurrencyDefinition() {
		return currencyDefinition;
	}

	public void setCurrencyDefinition(String currencyDefinition) {
		this.currencyDefinition = currencyDefinition;
	}
	
}

