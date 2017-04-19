/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.dto.view;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.CommonView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.FluxCharacteristicsDto;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.AddressDetailsDTO;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor
@JsonInclude(NON_NULL)
@EqualsAndHashCode(of = {"fluxLocationIdentifier", "fluxLocationIdentifierSchemeId"})
public class FluxLocationDto {

    @JsonView(CommonView.class)
    @JsonProperty("country")
    private String countryId;

    @JsonView(CommonView.class)
    private String rfmoCode;

    @JsonView(CommonView.class)
    private String geometry;

    @JsonIgnore
    private String fluxLocationIdentifier;

    @JsonIgnore
    private FluxCharacteristicsDto fluxCharacteristic;

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

    public FluxCharacteristicsDto getFluxCharacteristic() {
        return fluxCharacteristic;
    }

    public void setFluxCharacteristic(FluxCharacteristicsDto fluxCharacteristic) {
        this.fluxCharacteristic = fluxCharacteristic;
    }

    @JsonProperty("characteristics")
    public Map<String, String> getCharacteristicsMap() {
        HashMap<String, String> stringStringHashMap = null;
        if (fluxCharacteristic != null) {
            stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("valueCode", fluxCharacteristic.getValueCode());
            stringStringHashMap.put("valueIndicator", fluxCharacteristic.getValueIndicator());
            stringStringHashMap.put("valueLanguageId", fluxCharacteristic.getValueLanguageId());
            stringStringHashMap.put("valueMeasureUnitCode", fluxCharacteristic.getValueMeasureUnitCode());
            stringStringHashMap.put("valueMeasure", String.valueOf(fluxCharacteristic.getValueMeasure()));
            stringStringHashMap.put("valueQuantityCode", fluxCharacteristic.getValueQuantityCode());
            stringStringHashMap.put("valueQuantity", String.valueOf(fluxCharacteristic.getValueQuantity()));
            stringStringHashMap.put("calculatedValueMeasure", String.valueOf(fluxCharacteristic.getCalculatedValueMeasure()));
            stringStringHashMap.put("calculatedValueQuantity", String.valueOf(fluxCharacteristic.getCalculatedValueQuantity()));
            stringStringHashMap.put("description", fluxCharacteristic.getDescription());
            stringStringHashMap.put("valueLanguageId", fluxCharacteristic.getValueLanguageId());
            stringStringHashMap.put("typeCode", fluxCharacteristic.getTypeCode());
            stringStringHashMap.put("typeCodeListId", fluxCharacteristic.getTypeCodeListId());
            stringStringHashMap.put("valueDateTime", DateUtils.UI_FORMATTER.print(new DateTime((fluxCharacteristic.getValueDateTime()))));
            stringStringHashMap.values().removeAll(Collections.singleton(null));
        }

        return stringStringHashMap;
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
