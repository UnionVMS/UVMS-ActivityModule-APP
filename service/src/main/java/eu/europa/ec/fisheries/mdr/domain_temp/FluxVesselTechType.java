package eu.europa.ec.fisheries.mdr.domain_temp;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.search.annotations.Indexed;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by georgige on 11/23/2016.
 */
@Entity
@Table(name = "mdr_flux_vessel_tech_type")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Indexed
public class FluxVesselTechType extends MasterDataRegistry {

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        super.populateCommonFields(mdrDataType);
    }

    @Override
    public String getAcronym() {
        return "FLUX_VESSEL_TECH_TYPE";
    }
}
