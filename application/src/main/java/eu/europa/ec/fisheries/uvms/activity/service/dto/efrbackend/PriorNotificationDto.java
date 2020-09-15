package eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Collection;

/**
 * Copied from the EFR Backend project, these DTO classes are how it sends the
 * reports of completed fishing trips to this module
 */
public class PriorNotificationDto extends AbstractModelObject {

    @NotNull
    private Instant estimatedLandingTime;

    @Valid
    private ArrivalLocation arrivalLocation;

    @Valid
    private Collection<PriorNotificationEstimatedCatch> estimatedCatches;

    public Instant getEstimatedLandingTime() {
        return estimatedLandingTime;
    }

    public void setEstimatedLandingTime(Instant estimatedLandingTime) {
        this.estimatedLandingTime = estimatedLandingTime;
    }

    public ArrivalLocation getArrivalLocation() {
        return arrivalLocation;
    }

    public void setArrivalLocation(ArrivalLocation arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    public Collection<PriorNotificationEstimatedCatch> getEstimatedCatches() {
        return estimatedCatches;
    }

    public void setEstimatedCatches(Collection<PriorNotificationEstimatedCatch> estimatedCatches) {
        this.estimatedCatches = estimatedCatches;
    }
}
