package eu.europa.ec.fisheries.mdr.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

@Entity
@Table(name = "mdr_cr_landing_indicator_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)	
public class CrLandingIndicatorCodes extends ExtendedMasterDataRegistry {

	private static final long serialVersionUID = 1L;

	@Override
	public String getAcronym() {		
		return "CR_LAND_IND";
	}

	@Override
	public void populate(List<FieldType> fields) {
		// TODO Auto-generated method stub

	}

}
