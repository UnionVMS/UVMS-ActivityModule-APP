package eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

/**
 * Copied from the EFR Backend project, these DTO classes are how it sends the
 * reports of completed fishing trips to this module.
 *
 * This is the root object in the JSON message.
 */
public class FishingReport extends AbstractModelObject {

    private UUID fishingReportId;

    @NotBlank(message = "Ship CFR missing")
    private String shipCfr;

    @NotBlank(message = "Target species code missing")
    private String targetSpeciesCode;

    private PriorNotificationDto priorNotification;

    private List<FishingCatch> fishingCatches;

    public UUID getFishingReportId() {
        return fishingReportId;
    }

    public void setFishingReportId(UUID fishingReportId) {
        this.fishingReportId = fishingReportId;
    }

    public String getShipCfr() {
        return shipCfr;
    }

    public void setShipCfr(String shipCfr) {
        this.shipCfr = shipCfr;
    }

    public String getTargetSpeciesCode() {
        return targetSpeciesCode;
    }

    public void setTargetSpeciesCode(String targetSpeciesCode) {
        this.targetSpeciesCode = targetSpeciesCode;
    }

    public PriorNotificationDto getPriorNotification() {
        return priorNotification;
    }

    public void setPriorNotification(PriorNotificationDto priorNotification) {
        this.priorNotification = priorNotification;
    }

    public List<FishingCatch> getFishingCatches() {
        return fishingCatches;
    }

    public void setFishingCatches(List<FishingCatch> fishingCatches) {
        this.fishingCatches = fishingCatches;
    }

    @Override
    public String toString() {
        return "FishingReport{" +
                "fishingReportId=" + fishingReportId +
                ", shipCfr='" + shipCfr + '\'' +
                ", targetSpeciesCode='" + targetSpeciesCode + '\'' +
                ", priorNotification=" + priorNotification +
                ", fishingCatches=" + fishingCatches +
                '}';
    }
}
