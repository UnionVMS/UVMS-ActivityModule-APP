package eu.europa.ec.fisheries.mdr.domain_temp;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;

/**
 * Created by georgige on 11/22/2016.
 */
public class GearType extends MasterDataRegistry {

    private String category;
    private String subCategory;
    private String iccatCode;
    private String ISSCFGCODE;
    private String TARGET;

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateCommonFields(mdrDataType);
    }

    @Override
    public String getAcronym() {
        return "GEAR_TYPE";
    }
}
