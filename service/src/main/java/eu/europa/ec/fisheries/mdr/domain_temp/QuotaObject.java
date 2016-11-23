package eu.europa.ec.fisheries.mdr.domain_temp;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.Indexed;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by georgige on 11/22/2016.
 */
@Entity
@Table(name = "mdr_quota_object")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Indexed
public class QuotaObject extends MasterDataRegistry {

    @Column(name = "en_name")
    private String enName;

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        super.populateCommonFields(mdrDataType);
        for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
            String fieldName  = field.getName().getValue();
            String fieldValue  = field.getName().getValue();
            if(StringUtils.equalsIgnoreCase("ENNAME", fieldName)){
                this.setEnName(fieldValue);
            }
        }
    }

    @Override
    public String getAcronym() {
        return "QUOTA_OBJECT";
    }


    public String getEnName() {
        return enName;
    }
    public void setEnName(String enName) {
        this.enName = enName;
    }

}
