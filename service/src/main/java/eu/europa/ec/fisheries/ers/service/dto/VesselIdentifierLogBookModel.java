package eu.europa.ec.fisheries.ers.service.dto;

import lombok.Data;

@Data
public class VesselIdentifierLogBookModel {

	private int id;
	private String vesselName;
	private String country;
	private String cfr;
	private String extMark;
	private String ircs;
	private String imo;
	private String role;
	private String other;//gfcm,iccat
}
