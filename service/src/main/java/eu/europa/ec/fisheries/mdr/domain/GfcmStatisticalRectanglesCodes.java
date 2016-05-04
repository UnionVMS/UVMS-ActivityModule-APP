package eu.europa.ec.fisheries.mdr.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import eu.europa.ec.fisheries.uvms.domain.RectangleCoordinates;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kovian
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gfcm_statistical_rectangles_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GfcmStatisticalRectanglesCodes extends MasterDataRegistry {
	
	@Column(name = "code")
	private String code;
	
	@Embedded
	private RectangleCoordinates rectangle;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public RectangleCoordinates getRectangle() {
		return rectangle;
	}

	public void setRectangle(RectangleCoordinates rectangle) {
		this.rectangle = rectangle;
	}
}
