package eu.europa.ec.fisheries.mdr.domain_temp;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;

/**
 * Created by georgige on 11/22/2016.
 */
public class GearType extends MasterDataRegistry{

    private String category;
    private String subCategory;
    private String iccatCode;
    private String ISSCFGCODE;
    private String TARGET;

    @Override
    public String getAcronym() {
        return "GEAR_TYPE";
    }
}
