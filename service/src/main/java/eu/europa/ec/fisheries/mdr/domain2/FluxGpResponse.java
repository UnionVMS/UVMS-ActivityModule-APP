package eu.europa.ec.fisheries.mdr.domain2;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mdr_flux_gp_response")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FluxGpResponse extends MasterDataRegistry {
	private static final long serialVersionUID = 1L;

	@Override
	public String getAcronym() {
		return "FLUX_GP_RESPONSE";
	}

	@Override
	public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
		populateCommonFields(mdrDataType);
	}
}