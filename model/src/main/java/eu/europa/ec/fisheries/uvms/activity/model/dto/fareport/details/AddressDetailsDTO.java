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

/**
 * Created by padhyad on 8/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AddressDetailsDTO {

    @JsonProperty("blockName")
    private String blockName;

    @JsonProperty("buildingName")
    private String buildingName;

    @JsonProperty("cityName")
    private String cityName;

    @JsonProperty("citySubdivisionName")
    private String citySubdivisionName;

    @JsonProperty("country")
    private String country;

    @JsonProperty("countryName")
    private String countryName;

    @JsonProperty("countrySubdivisionName")
    private String countrySubdivisionName;

    @JsonProperty("addressId")
    private String addressId;

    @JsonProperty("plotId")
    private String plotId;

    @JsonProperty("postOfficeBox")
    private String postOfficeBox;

    @JsonProperty("postcode")
    private String postcode;

    @JsonProperty("streetname")
    private String streetname;

    public AddressDetailsDTO() {
    }

    public AddressDetailsDTO(String blockName, String buildingName, String cityName, String citySubdivisionName, String country, String countryName, String countrySubdivisionName, String addressId, String plotId, String postOfficeBox, String postcode, String streetname) {
        this.blockName = blockName;
        this.buildingName = buildingName;
        this.cityName = cityName;
        this.citySubdivisionName = citySubdivisionName;
        this.country = country;
        this.countryName = countryName;
        this.countrySubdivisionName = countrySubdivisionName;
        this.addressId = addressId;
        this.plotId = plotId;
        this.postOfficeBox = postOfficeBox;
        this.postcode = postcode;
        this.streetname = streetname;
    }

    @JsonProperty("blockName")
    public String getBlockName() {
        return blockName;
    }

    @JsonProperty("blockName")
    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    @JsonProperty("buildingName")
    public String getBuildingName() {
        return buildingName;
    }

    @JsonProperty("buildingName")
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    @JsonProperty("cityName")
    public String getCityName() {
        return cityName;
    }

    @JsonProperty("cityName")
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @JsonProperty("citySubdivisionName")
    public String getCitySubdivisionName() {
        return citySubdivisionName;
    }

    @JsonProperty("citySubdivisionName")
    public void setCitySubdivisionName(String citySubdivisionName) {
        this.citySubdivisionName = citySubdivisionName;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("countryName")
    public String getCountryName() {
        return countryName;
    }

    @JsonProperty("countryName")
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @JsonProperty("countrySubdivisionName")
    public String getCountrySubdivisionName() {
        return countrySubdivisionName;
    }

    @JsonProperty("countrySubdivisionName")
    public void setCountrySubdivisionName(String countrySubdivisionName) {
        this.countrySubdivisionName = countrySubdivisionName;
    }

    @JsonProperty("addressId")
    public String getAddressId() {
        return addressId;
    }

    @JsonProperty("addressId")
    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    @JsonProperty("plotId")
    public String getPlotId() {
        return plotId;
    }

    @JsonProperty("plotId")
    public void setPlotId(String plotId) {
        this.plotId = plotId;
    }

    @JsonProperty("postOfficeBox")
    public String getPostOfficeBox() {
        return postOfficeBox;
    }

    @JsonProperty("postOfficeBox")
    public void setPostOfficeBox(String postOfficeBox) {
        this.postOfficeBox = postOfficeBox;
    }

    @JsonProperty("postcode")
    public String getPostcode() {
        return postcode;
    }

    @JsonProperty("postcode")
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @JsonProperty("streetname")
    public String getStreetname() {
        return streetname;
    }

    @JsonProperty("streetname")
    public void setStreetname(String streetname) {
        this.streetname = streetname;
    }
}
