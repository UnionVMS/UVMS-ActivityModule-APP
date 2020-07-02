package eu.europa.ec.fisheries.ers.service.dto;

import java.util.List;

import lombok.Data;

@Data
public class LogbookModel {
	
	private List<VesselIdentifierLogBookModel>  vesselIdentifier;
	private List<PortLogBookModel> ports;
	private List<TripInfoLogBookModel> tripInfo;
	private List<FACatchModel> catches;
}
