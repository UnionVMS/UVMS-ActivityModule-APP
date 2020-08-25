package eu.europa.ec.fisheries.ers.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class HeaderModel {
	
	private String tripId;
	private Date generationDate;
	

}
