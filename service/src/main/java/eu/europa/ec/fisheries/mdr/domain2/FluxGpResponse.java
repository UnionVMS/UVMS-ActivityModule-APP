package eu.europa.ec.fisheries.mdr.domain2;

import eu.europa.ec.fisheries.mdr.domain.ExtendedMasterDataRegistry;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "mdr_flux_gp_response")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FluxGpResponse extends ExtendedMasterDataRegistry {
	private static final long serialVersionUID = 1L;

	@Override
	public String getAcronym() {
		return "FLUX_GP_RESPONSE";
	}

}