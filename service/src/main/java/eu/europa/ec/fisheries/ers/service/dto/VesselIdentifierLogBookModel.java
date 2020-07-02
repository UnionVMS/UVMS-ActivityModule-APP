package eu.europa.ec.fisheries.ers.service.dto;

import lombok.Data;

@Data
public class VesselIdentifierLogBookModel {
	
	private String name;
	private String country;
	private String identifier;//ircs,cfr, etc..
	private Double dimensions;
	private String address;

}
