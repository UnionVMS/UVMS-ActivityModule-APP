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
@Table(name = "fish_presentation_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FishPresentationCodes extends ExtendedMasterDataRegistry {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "presentation")
	private String presentation;

	public String getPresentation() {
		return presentation;
	}

	public void setPresentation(String presentation) {
		this.presentation = presentation;
	}

}
