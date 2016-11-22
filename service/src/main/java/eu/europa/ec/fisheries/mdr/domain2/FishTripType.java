package eu.europa.ec.fisheries.mdr.domain2;


import eu.europa.ec.fisheries.mdr.domain.base.ExtendedMasterDataRegistry;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mdr_fish_trip_type")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FishTripType extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;

	@Override
	public String getAcronym() {
		return "FISHING_TRIP_TYPE";
	}

}