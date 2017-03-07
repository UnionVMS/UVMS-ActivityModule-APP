/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 8/10/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VesselDetailsDTO implements Serializable {

    @JsonProperty("vesselRole")
    private String vesselRole;

    @JsonProperty("vesselIds")
    private List<String> vesselIds;

    @JsonProperty("vesselName")
    private String vesselName;

    @JsonProperty("registrationDateTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date registrationDateTime;

    @JsonProperty("registrationEventDescription")
    private String registrationEventDescription;

    @JsonProperty("registrationLocationDescription")
    private String registrationLocationDescription;

    @JsonProperty("registrationRegion")
    private String registrationRegion;

    @JsonProperty("registrationLocationName")
    private String registrationLocationName;

    @JsonProperty("registrationType")
    private String registrationType;

    @JsonProperty("registrationLocationCountryId")
    private String registrationLocationCountryId;

    @JsonProperty("contactParty")
    private List<ContactPartyDetailsDTO> contactPartyDetails;

    public VesselDetailsDTO() {
    }

    public VesselDetailsDTO(String vesselRole, List<String> vesselIds, String vesselName, Date registrationDateTime, String registrationEventDescription, String registrationLocationDescription, String registrationRegion, String registrationLocationName, String registrationType, String registrationLocationCountryId, List<ContactPartyDetailsDTO> contactPartyDetails) {
        this.vesselRole = vesselRole;
        this.vesselIds = vesselIds;
        this.vesselName = vesselName;
        this.registrationDateTime = registrationDateTime;
        this.registrationEventDescription = registrationEventDescription;
        this.registrationLocationDescription = registrationLocationDescription;
        this.registrationRegion = registrationRegion;
        this.registrationLocationName = registrationLocationName;
        this.registrationType = registrationType;
        this.registrationLocationCountryId = registrationLocationCountryId;
        this.contactPartyDetails = contactPartyDetails;
    }

    @JsonProperty("vesselRole")
    public String getVesselRole() {
        return vesselRole;
    }

    @JsonProperty("vesselRole")
    public void setVesselRole(String vesselRole) {
        this.vesselRole = vesselRole;
    }

    @JsonProperty("vesselIds")
    public List<String> getVesselIds() {
        return vesselIds;
    }

    @JsonProperty("vesselIds")
    public void setVesselIds(List<String> vesselIds) {
        this.vesselIds = vesselIds;
    }

    @JsonProperty("vesselName")
    public String getVesselName() {
        return vesselName;
    }

    @JsonProperty("vesselName")
    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    @JsonProperty("registrationDateTime")
    public Date getRegistrationDateTime() {
        return registrationDateTime;
    }

    @JsonProperty("registrationDateTime")
    public void setRegistrationDateTime(Date registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    @JsonProperty("registrationEventDescription")
    public String getRegistrationEventDescription() {
        return registrationEventDescription;
    }

    @JsonProperty("registrationEventDescription")
    public void setRegistrationEventDescription(String registrationEventDescription) {
        this.registrationEventDescription = registrationEventDescription;
    }

    @JsonProperty("registrationLocationDescription")
    public String getRegistrationLocationDescription() {
        return registrationLocationDescription;
    }

    @JsonProperty("registrationLocationDescription")
    public void setRegistrationLocationDescription(String registrationLocationDescription) {
        this.registrationLocationDescription = registrationLocationDescription;
    }

    @JsonProperty("registrationRegion")
    public String getRegistrationRegion() {
        return registrationRegion;
    }

    @JsonProperty("registrationRegion")
    public void setRegistrationRegion(String registrationRegion) {
        this.registrationRegion = registrationRegion;
    }

    @JsonProperty("registrationLocationName")
    public String getRegistrationLocationName() {
        return registrationLocationName;
    }

    @JsonProperty("registrationLocationName")
    public void setRegistrationLocationName(String registrationLocationName) {
        this.registrationLocationName = registrationLocationName;
    }

    @JsonProperty("registrationType")
    public String getRegistrationType() {
        return registrationType;
    }

    @JsonProperty("registrationType")
    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    @JsonProperty("registrationLocationCountryId")
    public String getRegistrationLocationCountryId() {
        return registrationLocationCountryId;
    }

    @JsonProperty("registrationLocationCountryId")
    public void setRegistrationLocationCountryId(String registrationLocationCountryId) {
        this.registrationLocationCountryId = registrationLocationCountryId;
    }

    @JsonProperty("contactParty")
    public List<ContactPartyDetailsDTO> getContactPartyDetails() {
        return contactPartyDetails;
    }

    @JsonProperty("contactParty")
    public void setContactPartyDetails(List<ContactPartyDetailsDTO> contactPartyDetails) {
        this.contactPartyDetails = contactPartyDetails;
    }
}
