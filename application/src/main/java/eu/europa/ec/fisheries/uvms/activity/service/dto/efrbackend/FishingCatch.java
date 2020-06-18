package eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Copied from the EFR Backend project, these DTO classes are how it sends the
 * reports of completed fishing trips to this module
 */
public class FishingCatch extends AbstractModelObject {

    @NotNull
    private UUID id;

    @NotEmpty
    private String gearCode;
    private CatchGearSettings catchGearSettings;

    private boolean emptyCatch;
    private List<CatchSpecies> species;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getGearCode() {
        return gearCode;
    }

    public void setGearCode(String gearCode) {
        this.gearCode = gearCode;
    }

    public CatchGearSettings getCatchGearSettings() {
        return catchGearSettings;
    }

    public void setCatchGearSettings(CatchGearSettings catchGearSettings) {
        this.catchGearSettings = catchGearSettings;
    }

    public boolean isEmptyCatch() {
        return emptyCatch;
    }

    public void setEmptyCatch(boolean emptyCatch) {
        this.emptyCatch = emptyCatch;
    }

    public List<CatchSpecies> getSpecies() {
        return species;
    }

    public void setSpecies(List<CatchSpecies> species) {
        this.species = species;
    }
}
