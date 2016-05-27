package eu.europa.ec.fisheries.mdr.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "mdr_cr_nafo_stock")
@ToString(callSuper = true)
public class CrNafoStock extends MasterDataRegistry {

    @Column(name = "species_code")
    private String speciesCode;

    @Column(name = "species_name")
    private String speciesName;

    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "area_description")
    private String areaDescription;

    public CrNafoStock() {
        // wyh JPA why
    }

    public String getSpeciesCode() {
        return speciesCode;
    }

    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }
    
	@Override
	public String getAcronym() {
		return "NAFO_STOCK";
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
