package eu.europa.ec.fisheries.mdr.domain_temp;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import eu.europa.ec.fisheries.uvms.domain.RectangleCoordinates;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by kovian on 23/11/2016.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "mdr_statistical_rectangles")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StatisticalRectangle extends MasterDataRegistry {

    @Embedded
    private RectangleCoordinates rectangle;

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateCommonFields(mdrDataType);
        rectangle = new RectangleCoordinates();
        for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
            String fieldName  = field.getName().getValue();
            Double fieldValue  = Double.parseDouble(field.getName().getValue());
            if(StringUtils.equalsIgnoreCase("WEST", fieldName)){
                rectangle.setWest(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("EAST", fieldName)){
                rectangle.setEast(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("NORTH", fieldName)){
                rectangle.setNorth(fieldValue);
            } else if(StringUtils.equalsIgnoreCase("SOUTH", fieldName)){
                rectangle.setSouth(fieldValue);
            } else {
                throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
            }
        }

    }

    @Override
    public String getAcronym() {
        return "STAT_RECTANGLE";
    }

    public RectangleCoordinates getRectangle() {
        return rectangle;
    }
    public void setRectangle(RectangleCoordinates rectangle) {
        this.rectangle = rectangle;
    }
}
