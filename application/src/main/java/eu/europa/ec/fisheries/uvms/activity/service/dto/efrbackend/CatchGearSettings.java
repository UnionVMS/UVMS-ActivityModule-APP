package eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Instant;

/**
 * Copied from the EFR Backend project, these DTO classes are how it sends the
 * reports of completed fishing trips to this module
 */
public class CatchGearSettings extends AbstractModelObject {

    @Positive
    private Integer meshSizeInMillimeter;

    @Positive
    @NotNull
    private Integer quantity;

    @NotNull
    private Instant deployedPeriodStartDate;

    @NotNull
    private Instant deployedPeriodEndDate;

    @NotNull
    private UserSpecifiedLocation gearLocation;

    public Integer getMeshSizeInMillimeter() {
        return meshSizeInMillimeter;
    }

    public void setMeshSizeInMillimeter(Integer meshSizeInMillimeter) {
        this.meshSizeInMillimeter = meshSizeInMillimeter;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Instant getDeployedPeriodStartDate() {
        return deployedPeriodStartDate;
    }

    public void setDeployedPeriodStartDate(Instant deployedPeriodStartDate) {
        this.deployedPeriodStartDate = deployedPeriodStartDate;
    }

    public Instant getDeployedPeriodEndDate() {
        return deployedPeriodEndDate;
    }

    public void setDeployedPeriodEndDate(Instant deployedPeriodEndDate) {
        this.deployedPeriodEndDate = deployedPeriodEndDate;
    }

    public UserSpecifiedLocation getGearLocation() {
        return gearLocation;
    }

    public void setGearLocation(UserSpecifiedLocation gearLocation) {
        this.gearLocation = gearLocation;
    }
}
