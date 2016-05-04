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
@Table(name = "cr_report_indicator_list")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrReportIndicatorList extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
}
