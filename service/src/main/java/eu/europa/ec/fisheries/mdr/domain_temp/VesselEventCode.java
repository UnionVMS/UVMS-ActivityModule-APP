package eu.europa.ec.fisheries.mdr.domain_temp;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;

/**
 * Created by kovian on 23/11/2016.
 */
public class VesselEventCode extends MasterDataRegistry {

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateCommonFields(mdrDataType);
    }

    @Override
    public String getAcronym() {
        return "VESSEL_EVENT";
    }
}
