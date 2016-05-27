package eu.europa.ec.fisheries.mdr.domain;

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
public abstract class ExtendedMasterDataRegistry extends MasterDataRegistry {

	private String code;
	private String description;
	
	protected final static String CODE        = "CODE";
	protected final static String DESCRIPTION = "DESCRIPTION";
	
	public ExtendedMasterDataRegistry() {
	}
	
	public ExtendedMasterDataRegistry(String code, String desc) {
		this.code 		 = code;
		this.description = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
