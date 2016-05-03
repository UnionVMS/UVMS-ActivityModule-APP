package eu.europa.ec.fisheries.mdr.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@SuppressWarnings("serial")
@Entity
@Table(name = "withdrawn_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WithdrawnCodes extends ExtendedMasterDataRegistry {

}
