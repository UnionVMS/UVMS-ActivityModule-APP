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
 * Created by kovian on 23/11/2016.
 */
@Entity
@Table(name = "mdr_weight_measure")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Indexed
public class WeightMeasureCode 	extends MasterDataRegistry {

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateCommonFields(mdrDataType);
    }
    @Override
    public String getAcronym() {
        return "WEIGHT_MEASURE";
    }

}
