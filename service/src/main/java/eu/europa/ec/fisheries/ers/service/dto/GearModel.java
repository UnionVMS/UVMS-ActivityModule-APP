package eu.europa.ec.fisheries.ers.service.dto;


import lombok.Data;

@Data
public class GearModel {

    private int rowNo;
    private String gearCode;
    private String meshSize;
    private String dimensions;
}
