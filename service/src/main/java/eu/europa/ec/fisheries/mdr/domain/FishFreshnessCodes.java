package eu.europa.ec.fisheries.mdr.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kovian
 *
 */
@Entity
@Table(name = "fish_freshness_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FishFreshnessCodes extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
}
