package eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Copied from the EFR Backend project, these DTO classes are how it sends the
 * reports of completed fishing trips to this module
 */
public class UserSpecifiedLocation {
    private static final String LATITUDE_VALIDATION_REGEXP = "-?\\d{1,2}\\s\\d{1,2}\\s\\d{1,2}";
    private static final String LONGITUDE_VALIDATION_REGEXP = "-?\\d{1,3}\\s\\d{1,2}\\s\\d{1,2}";

    private String name;

    @Pattern(regexp = LATITUDE_VALIDATION_REGEXP)
    @NotNull
    private String latitude;

    @Pattern(regexp = LONGITUDE_VALIDATION_REGEXP)
    @NotNull
    private String longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
