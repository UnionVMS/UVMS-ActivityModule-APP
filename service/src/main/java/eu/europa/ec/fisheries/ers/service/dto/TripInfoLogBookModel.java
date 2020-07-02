package eu.europa.ec.fisheries.ers.service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TripInfoLogBookModel {

    private String tripId;
    private Date firstActivityDateForTrip;
    private Date lastActivityDateForTrip;
    private String duration;
}
