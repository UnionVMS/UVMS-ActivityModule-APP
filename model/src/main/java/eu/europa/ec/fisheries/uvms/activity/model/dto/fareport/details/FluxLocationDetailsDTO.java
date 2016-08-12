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

package eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by padhyad on 8/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FluxLocationDetailsDTO {

    @JsonProperty("typeCode")
    private String typeCode;

    @JsonProperty("countryId")
    private String countryId;

    @JsonProperty("rfmoCode")
    private String rfmoCode;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("altitude")
    private Double altitude;

    @JsonProperty("fluxLocationType")
    private String fluxLocationType;

    @JsonProperty("fluxLocationIdentifier")
    private String fluxLocationIdentifier;

    @JsonProperty("geopoliticalRegionCode")
    private String geopoliticalRegionCode;

    @JsonProperty("name")
    private String name;

    @JsonProperty("sovereignRightsCountryCode")
    private String sovereignRightsCountryCode;

    @JsonProperty("jurisdictionCountryCode")
    private String jurisdictionCountryCode;

    @JsonProperty("systemId")
    private String systemId;

    @JsonProperty("fluxCharacteristics")
    private List<FluxCharacteristicsDetailsDTO> fluxCharacteristics;

    public FluxLocationDetailsDTO() {
    }

    public FluxLocationDetailsDTO(String typeCode, String countryId, String rfmoCode, Double longitude, Double latitude, Double altitude, String fluxLocationType, String fluxLocationIdentifier, String geopoliticalRegionCode, String name, String sovereignRightsCountryCode, String jurisdictionCountryCode, String systemId, List<FluxCharacteristicsDetailsDTO> fluxCharacteristics) {
        this.typeCode = typeCode;
        this.countryId = countryId;
        this.rfmoCode = rfmoCode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.fluxLocationType = fluxLocationType;
        this.fluxLocationIdentifier = fluxLocationIdentifier;
        this.geopoliticalRegionCode = geopoliticalRegionCode;
        this.name = name;
        this.sovereignRightsCountryCode = sovereignRightsCountryCode;
        this.jurisdictionCountryCode = jurisdictionCountryCode;
        this.systemId = systemId;
        this.fluxCharacteristics = fluxCharacteristics;
    }

    @JsonProperty("typeCode")
    public String getTypeCode() {
        return typeCode;
    }

    @JsonProperty("typeCode")
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @JsonProperty("countryId")
    public String getCountryId() {
        return countryId;
    }

    @JsonProperty("countryId")
    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    @JsonProperty("rfmoCode")
    public String getRfmoCode() {
        return rfmoCode;
    }

    @JsonProperty("rfmoCode")
    public void setRfmoCode(String rfmoCode) {
        this.rfmoCode = rfmoCode;
    }

    @JsonProperty("longitude")
    public Double getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("latitude")
    public Double getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("altitude")
    public Double getAltitude() {
        return altitude;
    }

    @JsonProperty("altitude")
    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    @JsonProperty("fluxLocationType")
    public String getFluxLocationType() {
        return fluxLocationType;
    }

    @JsonProperty("fluxLocationType")
    public void setFluxLocationType(String fluxLocationType) {
        this.fluxLocationType = fluxLocationType;
    }

    @JsonProperty("fluxLocationIdentifier")
    public String getFluxLocationIdentifier() {
        return fluxLocationIdentifier;
    }

    @JsonProperty("fluxLocationIdentifier")
    public void setFluxLocationIdentifier(String fluxLocationIdentifier) {
        this.fluxLocationIdentifier = fluxLocationIdentifier;
    }

    @JsonProperty("geopoliticalRegionCode")
    public String getGeopoliticalRegionCode() {
        return geopoliticalRegionCode;
    }

    @JsonProperty("geopoliticalRegionCode")
    public void setGeopoliticalRegionCode(String geopoliticalRegionCode) {
        this.geopoliticalRegionCode = geopoliticalRegionCode;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("sovereignRightsCountryCode")
    public String getSovereignRightsCountryCode() {
        return sovereignRightsCountryCode;
    }

    @JsonProperty("sovereignRightsCountryCode")
    public void setSovereignRightsCountryCode(String sovereignRightsCountryCode) {
        this.sovereignRightsCountryCode = sovereignRightsCountryCode;
    }

    @JsonProperty("jurisdictionCountryCode")
    public String getJurisdictionCountryCode() {
        return jurisdictionCountryCode;
    }

    @JsonProperty("jurisdictionCountryCode")
    public void setJurisdictionCountryCode(String jurisdictionCountryCode) {
        this.jurisdictionCountryCode = jurisdictionCountryCode;
    }

    @JsonProperty("systemId")
    public String getSystemId() {
        return systemId;
    }

    @JsonProperty("systemId")
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @JsonProperty("fluxCharacteristics")
    public List<FluxCharacteristicsDetailsDTO> getFluxCharacteristics() {
        return fluxCharacteristics;
    }

    @JsonProperty("fluxCharacteristics")
    public void setFluxCharacteristics(List<FluxCharacteristicsDetailsDTO> fluxCharacteristics) {
        this.fluxCharacteristics = fluxCharacteristics;
    }
}
