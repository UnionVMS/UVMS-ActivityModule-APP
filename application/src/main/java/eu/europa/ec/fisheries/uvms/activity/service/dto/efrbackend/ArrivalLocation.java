package eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend;

import javax.validation.Valid;

/**
 * Copied from the EFR Backend project, these DTO classes are how it sends the
 * reports of completed fishing trips to this module
 */
public class ArrivalLocation {

    @Valid
    private UserSpecifiedLocation userSpecifiedLocation;
    private String portCode;

    public UserSpecifiedLocation getUserSpecifiedLocation() {
        return userSpecifiedLocation;
    }

    public void setUserSpecifiedLocation(UserSpecifiedLocation userSpecifiedLocation) {
        this.userSpecifiedLocation = userSpecifiedLocation;
    }

    public String getPortCode() {
        return portCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }
}
