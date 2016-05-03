package eu.europa.ec.fisheries.mdr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kovian
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "fao_area_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FAOAreaCodes extends MasterDataRegistry {

	@Column(name = "area")
	private String area;
	
	@Column(name = "subarea")
	private String subarea;
	
	@Column(name = "division")
	private String division;
	
	@Column(name = "subdivision")
	private String subdivision;
	
	@Column(name = "unit")
	private String unit;
	
	@Column(name = "description")
	private String description;

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getSubarea() {
		return subarea;
	}

	public void setSubarea(String subarea) {
		this.subarea = subarea;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getSubdivision() {
		return subdivision;
	}

	public void setSubdivision(String subdivision) {
		this.subdivision = subdivision;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
