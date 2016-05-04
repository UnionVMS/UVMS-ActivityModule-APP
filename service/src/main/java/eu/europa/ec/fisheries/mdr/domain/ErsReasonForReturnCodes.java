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
@Table(name = "ers_reason_for_return_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErsReasonForReturnCodes extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
}
