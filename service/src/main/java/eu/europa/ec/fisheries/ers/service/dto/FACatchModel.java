package eu.europa.ec.fisheries.ers.service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FACatchModel {

    private String identifier;
    private Date calculatedStartTime;
    private Double longitude;
    private Double latitude;
    private String fluxLocationIdentifierSchemeId;
    private String speciesCode;
    private Double calculatedUnitQuantity;
    private String weight;

}
