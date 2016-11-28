package eu.europa.ec.fisheries.mdr.domain.codelists;

import eu.europa.ec.fisheries.mdr.domain.codelists.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by kovian on 22/11/2016.
 */
@Entity
@Table(name = "mdr_territory")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Indexed
public class Territory extends MasterDataRegistry {

    @Column(name = "code_2")
    @Field(name="code_2", analyze= Analyze.NO, store = Store.YES)
    private String code2;

    @Column(name = "en_name")
    @Field(name="en_name", analyze= Analyze.NO, store = Store.YES)
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
