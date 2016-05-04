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
@Table(name = "ers_senders_recipients_list")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErsSendersRecipientsList extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
}
