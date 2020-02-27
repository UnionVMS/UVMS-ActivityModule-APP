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


package eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDetailsDTO {

    @JsonbTransient
    private String blockName;

    @JsonbTransient
    private String buildingName;

    private String cityName;

    @JsonbTransient
    private String citySubdivisionName;

    @JsonbProperty("countryName")
    private String countryName;

    @JsonbProperty("countryCode")
    private String countryIDValue;

    @JsonbProperty("postCode")
    private String postalAreaValue;

    @JsonbTransient
    private String countrySubdivisionName;

    @JsonbTransient
    private String addressId;

    private String plotId;

    @JsonbTransient
    private String postOfficeBox;

    private String postcode;

    private String streetName;

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCitySubdivisionName() {
        return citySubdivisionName;
    }

    public void setCitySubdivisionName(String citySubdivisionName) {
        this.citySubdivisionName = citySubdivisionName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountrySubdivisionName() {
        return countrySubdivisionName;
    }

    public void setCountrySubdivisionName(String countrySubdivisionName) {
        this.countrySubdivisionName = countrySubdivisionName;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getPlotId() {
        return plotId;
    }

    public void setPlotId(String plotId) {
        this.plotId = plotId;
    }

    public String getPostOfficeBox() {
        return postOfficeBox;
    }

    public void setPostOfficeBox(String postOfficeBox) {
        this.postOfficeBox = postOfficeBox;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostalAreaValue() {
        return postalAreaValue;
    }

    public void setPostalAreaValue(String postalAreaValue) {
        this.postalAreaValue = postalAreaValue;
    }

    public String getCountryIDValue() {
        return countryIDValue;
    }

    public void setCountryIDValue(String countryIDValue) {
        this.countryIDValue = countryIDValue;
    }

    @JsonbProperty("characteristics")
    public Map<String, String> getCharacteristicsMap() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("buildingName", buildingName);
        stringStringHashMap.put("blockName", blockName);
        stringStringHashMap.put("citySubdivisionName", citySubdivisionName);
        stringStringHashMap.put("countrySubdivisionName", countrySubdivisionName);
        stringStringHashMap.put("addressId", addressId);
        stringStringHashMap.put("postOfficeBox", postOfficeBox);
        stringStringHashMap.values().removeAll(Collections.singleton(null));
        if (stringStringHashMap.isEmpty()) {
            stringStringHashMap = null;
        }
        return stringStringHashMap;
    }
}
