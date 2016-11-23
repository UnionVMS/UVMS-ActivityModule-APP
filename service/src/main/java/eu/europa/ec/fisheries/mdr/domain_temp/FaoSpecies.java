package eu.europa.ec.fisheries.mdr.domain_temp;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import org.apache.commons.lang.StringUtils;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;

/**
 * Created by kovian on 22/11/2016.
 */
public class FaoSpecies extends MasterDataRegistry {

    @Column(name = "is_group")
    private String isGroup;

    @Column(name = "scientific_name")
    private String scientificName;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "fr_name")
    private String frName;

    @Column(name = "es_name")
    private String esName;

    @Column(name = "family")
    private String family;

    @Column(name = "bio_order")
    private String bioOrder;

    @Column(name = "taxo_code")
    private String taxoCode;

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateCommonFields(mdrDataType);
        for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
            String fieldName  = field.getName().getValue();
            String fieldValue  = field.getName().getValue();
            if(StringUtils.equalsIgnoreCase("ISGROUP", fieldName)){
                this.setIsGroup(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("SCIENTNAME", fieldName)){
                this.setScientificName(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("ENNAME", fieldName)){
                this.setEnName(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("FRNAME", fieldName)){
                this.setFrName(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("ESNAME", fieldName)){
                this.setEsName(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("FAMILY", fieldName)){
                this.setFamily(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("BIOORDER", fieldName)){
                this.setBioOrder(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("TAXOCODE", fieldName)){
                this.setEnName(fieldValue);
            } else {
                throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
            }
        }
    }

    @Override
    public String getAcronym() {
        return "VESSEL_PROGRAM";
    }

    public String getIsGroup() {
        return isGroup;
    }
    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }
    public String getScientificName() {
        return scientificName;
    }
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }
    public String getEnName() {
        return enName;
    }
    public void setEnName(String enName) {
        this.enName = enName;
    }
    public String getFrName() {
        return frName;
    }
    public void setFrName(String frName) {
        this.frName = frName;
    }
    public String getEsName() {
        return esName;
    }
    public void setEsName(String esName) {
        this.esName = esName;
    }
    public String getFamily() {
        return family;
    }
    public void setFamily(String family) {
        this.family = family;
    }
    public String getBioOrder() {
        return bioOrder;
    }
    public void setBioOrder(String bioOrder) {
        this.bioOrder = bioOrder;
    }
    public String getTaxoCode() {
        return taxoCode;
    }
    public void setTaxoCode(String taxoCode) {
        this.taxoCode = taxoCode;
    }
}
