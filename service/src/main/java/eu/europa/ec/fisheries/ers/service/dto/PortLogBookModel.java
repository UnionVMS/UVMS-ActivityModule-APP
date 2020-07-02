package eu.europa.ec.fisheries.ers.service.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PortLogBookModel {

	private Date date;
	private String coordinates;
	private String activityType;

}
