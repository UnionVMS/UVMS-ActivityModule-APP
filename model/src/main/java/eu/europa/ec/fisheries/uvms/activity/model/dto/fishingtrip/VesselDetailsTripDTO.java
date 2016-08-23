/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.AddressDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.ContactPersonDetailsDTO;

/**
 * Created by sanera on 22/08/2016.
 */
public class VesselDetailsTripDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("exMark")
    private String exMark;

    @JsonProperty("flagState")
    private String flagState;

    @JsonProperty("ircs")
    private String ircs;

    @JsonProperty("cfr")
    private String cfr;

    @JsonProperty("contactPerson")
    private ContactPersonDetailsDTO contactPerson;

    @JsonProperty("structuredAddress")
    private AddressDetailsDTO structuredAddress;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("exMark")
    public String getExMark() {
        return exMark;
    }

    @JsonProperty("exMark")
    public void setExMark(String exMark) {
        this.exMark = exMark;
    }

    @JsonProperty("flagState")
    public String getFlagState() {
        return flagState;
    }

    @JsonProperty("flagState")
    public void setFlagState(String flagState) {
        this.flagState = flagState;
    }

    @JsonProperty("ircs")
    public String getIrcs() {
        return ircs;
    }

    @JsonProperty("ircs")
    public void setIrcs(String ircs) {
        this.ircs = ircs;
    }

    @JsonProperty("cfr")
    public String getCfr() {
        return cfr;
    }

    @JsonProperty("cfr")
    public void setCfr(String cfr) {
        this.cfr = cfr;
    }

    @JsonProperty("contactPerson")
    public ContactPersonDetailsDTO getContactPerson() {
        return contactPerson;
    }

    @JsonProperty("contactPerson")
    public void setContactPerson(ContactPersonDetailsDTO contactPerson) {
        this.contactPerson = contactPerson;
    }

    @JsonProperty("structuredAddress")
    public AddressDetailsDTO getStructuredAddress() {
        return structuredAddress;
    }

    @JsonProperty("structuredAddress")
    public void setStructuredAddress(AddressDetailsDTO structuredAddress) {
        this.structuredAddress = structuredAddress;
    }
}
