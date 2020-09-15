package eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend;

import javax.validation.constraints.NotEmpty;

/**
 * Copied from the EFR Backend project, these DTO classes are how it sends the
 * reports of completed fishing trips to this module
 */
public class CatchSpecies extends AbstractModelObject {

    @NotEmpty
    private String code;

    @NotEmpty
    private String variant;

    private Double lscWeightInKg;
    private Integer lscQuantity;

    private Double bmsWeightInKg;
    private Integer bmsQuantity;

    private Double disWeightInKg;
    private Integer disQuantity;

    private Double dimWeightInKg;
    private Integer dimQuantity;

    private Double rovWeightInKg;
    private Integer rovQuantity;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Double getLscWeightInKg() {
        return lscWeightInKg;
    }

    public void setLscWeightInKg(Double lscWeightInKg) {
        this.lscWeightInKg = lscWeightInKg;
    }

    public Integer getLscQuantity() {
        return lscQuantity;
    }

    public void setLscQuantity(Integer lscQuantity) {
        this.lscQuantity = lscQuantity;
    }

    public Double getBmsWeightInKg() {
        return bmsWeightInKg;
    }

    public void setBmsWeightInKg(Double bmsWeightInKg) {
        this.bmsWeightInKg = bmsWeightInKg;
    }

    public Integer getBmsQuantity() {
        return bmsQuantity;
    }

    public void setBmsQuantity(Integer bmsQuantity) {
        this.bmsQuantity = bmsQuantity;
    }

    public Double getDisWeightInKg() {
        return disWeightInKg;
    }

    public void setDisWeightInKg(Double disWeightInKg) {
        this.disWeightInKg = disWeightInKg;
    }

    public Integer getDisQuantity() {
        return disQuantity;
    }

    public void setDisQuantity(Integer disQuantity) {
        this.disQuantity = disQuantity;
    }

    public Double getDimWeightInKg() {
        return dimWeightInKg;
    }

    public void setDimWeightInKg(Double dimWeightInKg) {
        this.dimWeightInKg = dimWeightInKg;
    }

    public Integer getDimQuantity() {
        return dimQuantity;
    }

    public void setDimQuantity(Integer dimQuantity) {
        this.dimQuantity = dimQuantity;
    }

    public Double getRovWeightInKg() {
        return rovWeightInKg;
    }

    public void setRovWeightInKg(Double rovWeightInKg) {
        this.rovWeightInKg = rovWeightInKg;
    }

    public Integer getRovQuantity() {
        return rovQuantity;
    }

    public void setRovQuantity(Integer rovQuantity) {
        this.rovQuantity = rovQuantity;
    }
}
