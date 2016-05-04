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
@Table(name = "sovereignty_waters_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)	
public class SovereigntyWatersCodes extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
	// TODO : See the excel for this class; further implementation is needed;
}
