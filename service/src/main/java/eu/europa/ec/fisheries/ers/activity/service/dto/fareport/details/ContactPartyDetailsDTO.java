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

package eu.europa.ec.fisheries.ers.activity.service.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactPartyDetailsDTO {

    private String role;

    private ContactPersonDetailsDTO contactPerson;

    @JsonProperty("structuredAddress")
    private List<AddressDetailsDTO> structuredAddresses;

    public ContactPersonDetailsDTO getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(ContactPersonDetailsDTO contactPerson) {
        this.contactPerson = contactPerson;
    }

    public List<AddressDetailsDTO> getStructuredAddresses() {
        return structuredAddresses;
    }

    public void setStructuredAddresses(List<AddressDetailsDTO> structuredAddresses) {
        this.structuredAddresses = structuredAddresses;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
