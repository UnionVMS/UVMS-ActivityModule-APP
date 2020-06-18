package eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend;

import javax.validation.constraints.NotNull;

/**
 * Copied from the EFR Backend project, these DTO classes are how it sends the
 * reports of completed fishing trips to this module
 */
public class PriorNotificationEstimatedCatch {

    @NotNull
    private String code;

    private Integer quantity;
    private Double weightInKilos;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getWeightInKilos() {
        return weightInKilos;
    }

    public void setWeightInKilos(Double weightInKilos) {
        this.weightInKilos = weightInKilos;
    }
}
