package eu.europa.ec.fisheries.ers.service.dto;


import lombok.Data;

@Data
public class FaReportDTO {

    private int id;
    private String typeCode;
    private String dateTime;
    private String fmcMarker;
    private String status;
    private String source;


}
