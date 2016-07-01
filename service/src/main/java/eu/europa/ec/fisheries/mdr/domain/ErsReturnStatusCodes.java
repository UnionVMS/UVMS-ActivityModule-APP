package eu.europa.ec.fisheries.mdr.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

/**
 * @author kovian
 *
 */
@Entity
@Table(name = "mdr_ers_return_status_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErsReturnStatusCodes extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getAcronym() {
		return "RETURN_STATUS";
	}
	
	@Override
	public void populate(List<FieldType> fields) {
		for(FieldType field : fields){
			String fieldName  = field.getFieldName().getValue();
			String fieldValue = field.getFieldValue().getValue();
			if(StringUtils.equalsIgnoreCase(CODE, fieldName)){
				this.setCode(fieldValue);
			} else if(StringUtils.equalsIgnoreCase(DESCRIPTION, fieldName)){
				this.setDescription(fieldValue);
			}
		}
		
	}
}
