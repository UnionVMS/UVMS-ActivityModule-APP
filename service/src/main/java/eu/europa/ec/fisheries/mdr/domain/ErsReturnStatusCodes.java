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
@Table(name = "ers_return_status_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErsReturnStatusCodes extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
}
