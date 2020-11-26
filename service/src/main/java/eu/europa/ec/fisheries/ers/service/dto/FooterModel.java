package eu.europa.ec.fisheries.ers.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
public class FooterModel {

	private String activityType;
	private String date;
	private String port;
	
	List<TranshipmentLandingModel> elementList;
    
}
