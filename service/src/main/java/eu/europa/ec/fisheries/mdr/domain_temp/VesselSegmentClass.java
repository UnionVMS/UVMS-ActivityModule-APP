package eu.europa.ec.fisheries.mdr.domain_temp;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;

/**
 * Created by kovian on 22/11/2016.
 */
public class VesselSegmentClass extends MasterDataRegistry {

    @Column(name = "outer_reg_ind")
    private String outerRegInd;

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateCommonFields(mdrDataType);
        for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
            String fieldName  = field.getName().getValue();
            String fieldValue  = field.getName().getValue();
            if(StringUtils.equalsIgnoreCase("OUTERIND", fieldName)){
                this.setOuterRegInd(fieldValue);
                return;
            }
        }
    }

    @Override
    public String getAcronym() {
        return "VESSEL_SEGMENT_CLASS";
    }

    public String getOuterRegInd() {
        return outerRegInd;
    }
    public void setOuterRegInd(String outerRegInd) {
        this.outerRegInd = outerRegInd;
    }
}
