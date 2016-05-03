package eu.europa.ec.fisheries.mdr.domain;

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
@Table(name = "sender_type_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SenderTypeCodes extends ExtendedMasterDataRegistry {

}
