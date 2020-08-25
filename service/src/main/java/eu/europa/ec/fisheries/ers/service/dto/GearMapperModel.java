package eu.europa.ec.fisheries.ers.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = {"gearCode", "meshCode","dimensions"})
public class GearMapperModel {

    private int identifier;
    private String gearCode;
    private String meshCode;
    private Set<String> dimensions;


}
