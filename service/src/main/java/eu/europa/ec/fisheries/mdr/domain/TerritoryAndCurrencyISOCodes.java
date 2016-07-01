package eu.europa.ec.fisheries.mdr.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

/**
 * @author kovian
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "mdr_territory_and_currency_iso_codes")
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
	
	@Override
	public String getAcronym() {
		return "TERRITORY_CURR";
	}
	
	@Override
	public void populate(List<FieldType> fields) {
		for(FieldType field : fields){
			String fieldName  = field.getFieldName().getValue();
			String fieldValue = field.getFieldValue().getValue();
			if(StringUtils.equalsIgnoreCase(fieldName, "")){
			} else if(StringUtils.equalsIgnoreCase(fieldName, "")){
			}
		}	
	}
	
}

