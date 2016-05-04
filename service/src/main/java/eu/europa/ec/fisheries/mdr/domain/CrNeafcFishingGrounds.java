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
@Table(name = "cr_neafc_fishing_grounds")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrNeafcFishingGrounds extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
}
