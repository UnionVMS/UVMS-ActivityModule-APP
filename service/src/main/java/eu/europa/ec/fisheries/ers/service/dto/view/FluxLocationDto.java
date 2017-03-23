/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.dto.view;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.CommonView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.AddressDetailsDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class FluxLocationDto {

    @JsonView(CommonView.class)
    @JsonProperty("country")
    private String countryId;

    @JsonView(CommonView.class)
    private String rfmoCode;

    @JsonView(CommonView.class)
    @JsonInclude(Include.NON_NULL)
    private String geometry;

    @JsonIgnore
    private String fluxLocationIdentifier;

    @JsonIgnore
    private String fluxLocationIdentifierSchemeId;

    @JsonView(CommonView.class)
    private Set<AddressDetailsDTO> structuredAddresses;

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getRfmoCode() {
        return rfmoCode;
    }

    public void setRfmoCode(String rfmoCode) {
        this.rfmoCode = rfmoCode;
    }

    public Set<AddressDetailsDTO> getStructuredAddresses() {
        return structuredAddresses;
    }

    public void setStructuredAddresses(Set<AddressDetailsDTO> structuredAddresses) {
        this.structuredAddresses = structuredAddresses;
    }

    public String getFluxLocationIdentifier() {
        return fluxLocationIdentifier;
    }

    public void setFluxLocationIdentifier(String fluxLocationIdentifier) {
        this.fluxLocationIdentifier = fluxLocationIdentifier;
    }

    public String getFluxLocationIdentifierSchemeId() {
        return fluxLocationIdentifierSchemeId;
    }

    public void setFluxLocationIdentifierSchemeId(String fluxLocationIdentifierSchemeId) {
        this.fluxLocationIdentifierSchemeId = fluxLocationIdentifierSchemeId;
    }

    @JsonProperty("identifier")
    public Map<String, String> getIdentifier() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("id", fluxLocationIdentifier);
        stringStringHashMap.put("schemeId", fluxLocationIdentifierSchemeId);
        stringStringHashMap.values().removeAll(Collections.singleton(null));
        if (stringStringHashMap.isEmpty()) {
            stringStringHashMap = null;
        }
        return stringStringHashMap;
    }
}
