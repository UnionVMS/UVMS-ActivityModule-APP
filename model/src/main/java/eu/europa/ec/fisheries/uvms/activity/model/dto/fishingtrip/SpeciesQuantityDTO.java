package eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Created by kovian on 10/10/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SpeciesQuantityDTO {

    @JsonProperty("speciesCode")
    private String speciesCode;

    @JsonProperty("weight")
    private Double weight;

    public SpeciesQuantityDTO(){
        super();
    }

    public SpeciesQuantityDTO(String code, Double measure){
        setSpeciesCode(code);
        setWeight(measure);
    }

    public String getSpeciesCode() {
        return speciesCode;
    }
    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }
    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
