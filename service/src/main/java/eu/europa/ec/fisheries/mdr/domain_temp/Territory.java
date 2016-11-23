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
public class Territory extends MasterDataRegistry {

    @Column(name = "code_2")
    private String code2;

    @Column(name = "en_name")
    private String enName;

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateCommonFields(mdrDataType);
        for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
            String fieldName  = field.getName().getValue();
            String fieldValue  = field.getName().getValue();
            if(StringUtils.equalsIgnoreCase("CODE2", fieldName)){
                this.setCode2(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("ENNAME", fieldName)){
                this.setEnName(fieldValue);
            }
        }
    }

    @Override
    public String getAcronym() {
        return "TERRITORY";
    }

    public String getCode2() {
        return code2;
    }
    public void setCode2(String code2) {
        this.code2 = code2;
    }
    public String getEnName() {
        return enName;
    }
    public void setEnName(String enName) {
        this.enName = enName;
    }
}
