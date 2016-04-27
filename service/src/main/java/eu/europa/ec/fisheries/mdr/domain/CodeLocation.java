package eu.europa.ec.fisheries.mdr.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "mdr_code_location")
@ToString(callSuper = true)
public class CodeLocation extends MasterDataRegistry {

    // TODO implement me ;-)
}
