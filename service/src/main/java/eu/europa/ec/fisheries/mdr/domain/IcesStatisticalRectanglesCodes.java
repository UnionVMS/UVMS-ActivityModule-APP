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

}
