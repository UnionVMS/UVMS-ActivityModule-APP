package eu.europa.ec.fisheries.mdr.domain_temp;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by georgige on 11/22/2016.
 */
@Entity
@Table(name = "mdr_flux_gp_party")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FluxGpParty extends MasterDataRegistry {

    @Column(name = "code_2")
    private String code2;

    @Column(name = "enname")
    private String enName;

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        super.populateCommonFields(mdrDataType);
    }

    @Override
    public String getAcronym() {
        return "FLUX_GP_PARTY";
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
