package eu.europa.ec.fisheries.mdr.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import eu.europa.ec.fisheries.uvms.domain.RectangleCoordinates;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

/**
 * @author kovian
 *
 */
@Entity
@Table(name = "ices_statistical_rectangles_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IcesStatisticalRectanglesCodes extends MasterDataRegistry {

	private static final long serialVersionUID = 1L;

	@Column(name = "ices_name")
	private String icesName;
	
	@Embedded
	private RectangleCoordinates rectangle;

	public String getIcesName() {
		return icesName;
	}

	public void setIcesName(String icesName) {
		this.icesName = icesName;
	}

	public RectangleCoordinates getRectangle() {
		return rectangle;
	}

	public void setRectangle(RectangleCoordinates rectangle) {
		this.rectangle = rectangle;
	}
	
	@Override
	public String getAcronym() {
		return "ICES_STAT_RECTANGLE";
	}
	
	@Override
	public void populate(List<FieldType> fields) {
		for(FieldType field : fields){
			String fieldName  = field.getFieldName().getValue();
			String fieldValue = field.getFieldValue().getValue();
			if(StringUtils.equalsIgnoreCase(fieldName, "")){
			} else if(StringUtils.equalsIgnoreCase(fieldName, "")){
			}
		}	
	}

}
