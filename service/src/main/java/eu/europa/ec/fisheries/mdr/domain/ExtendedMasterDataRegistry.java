package eu.europa.ec.fisheries.mdr.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kovian
 *
 * Extend this Entity when code and description fields (columns) are needed
 *
 */
@SuppressWarnings("serial")
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExtendedMasterDataRegistry extends MasterDataRegistry {

	@Column(name = "code")
	private String code;
	
	@Column(name = "description")
	private String Description;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	
}
