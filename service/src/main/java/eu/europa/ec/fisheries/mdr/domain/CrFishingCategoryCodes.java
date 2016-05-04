package eu.europa.ec.fisheries.mdr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kovian
 *
 */
@Entity
@Table(name = "cr_fishing_category_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)	
public class CrFishingCategoryCodes extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "sovereignty_waters_report_declaration")
	private String sovereigntyWatersReportDeclaration;

	public String getSovereigntyWatersReportDeclaration() {
		return sovereigntyWatersReportDeclaration;
	}

	public void setSovereigntyWatersReportDeclaration(String sovereigntyWatersReportDeclaration) {
		this.sovereigntyWatersReportDeclaration = sovereigntyWatersReportDeclaration;
	}
}
