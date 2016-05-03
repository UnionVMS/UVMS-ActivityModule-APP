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
@Table(name = "vessel_type_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VesselTypeCodes extends ExtendedMasterDataRegistry {
	
	@Column(name = "vessel_type")
	private String vesselType;

	public String getVesselType() {
		return vesselType;
	}

	public void setVesselType(String vesselType) {
		this.vesselType = vesselType;
	}
}
