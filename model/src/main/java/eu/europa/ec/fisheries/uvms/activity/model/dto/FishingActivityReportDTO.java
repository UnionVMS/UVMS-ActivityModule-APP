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

package eu.europa.ec.fisheries.uvms.activity.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.ContactPersonDetailsDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sanera on 19/07/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FishingActivityReportDTO extends FishingActivityDTO implements Serializable  {

    @JsonProperty("from")
    private String from;

    @JsonProperty("vesselTransportMeansName")
    private String vesselTransportMeansName;

    @JsonProperty("vesselTransportMeansIdList")
    private List<String> vesselTransportMeansIdList;

    @JsonProperty("purposeCode")
    private String purposeCode;

    @JsonProperty("FAReportType")
    private String FAReportType;

    @JsonProperty("areas")
    private List<String> areas;

    @JsonProperty("port")
    private List<String> port;

    @JsonProperty("fishingGear")
    private List<String> fishingGear;

    @JsonProperty("speciesCode")
    private List<String> speciesCode;

    @JsonProperty("quantity")
    private List<Long> quantity;

    @JsonProperty("contactPerson")
    private List<ContactPersonDetailsDTO> contactPerson;

    public FishingActivityReportDTO(){

    }

    @JsonProperty("from")
    public String getFrom() {
        return from;
    }

    @JsonProperty("from")
    public void setFrom(String from) {
        this.from = from;
    }

    @JsonProperty("vesselTransportMeansName")
    public String getVesselTransportMeansName() {
        return vesselTransportMeansName;
    }

    @JsonProperty("vesselTransportMeansName")
    public void setVesselTransportMeansName(String vesselTransportMeansName) {
        this.vesselTransportMeansName = vesselTransportMeansName;
    }

    @JsonProperty("vesselTransportMeansIdList")
    public List<String> getVesselTransportMeansIdList() {
        return vesselTransportMeansIdList;
    }

    @JsonProperty("vesselTransportMeansIdList")
    public void setVesselTransportMeansIdList(List<String> vesselTransportMeansIdList) {
        this.vesselTransportMeansIdList = vesselTransportMeansIdList;
    }

    @JsonProperty("purposeCode")
    public String getPurposeCode() {
        return purposeCode;
    }

    @JsonProperty("purposeCode")
    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    @JsonProperty("FAReportType")
    public String getFAReportType() {
        return FAReportType;
    }

    @JsonProperty("FAReportType")
    public void setFAReportType(String FAReportType) {
        this.FAReportType = FAReportType;
    }

    @JsonProperty("areas")
    public List<String> getAreas() {
        return areas;
    }

    @JsonProperty("areas")
    public void setAreas(List<String> areas) {
        this.areas = areas;
    }

    @JsonProperty("port")
    public List<String> getPort() {
        return port;
    }

    @JsonProperty("port")
    public void setPort(List<String> port) {
        this.port = port;
    }

    @JsonProperty("fishingGear")
    public List<String> getFishingGear() {
        return fishingGear;
    }

    @JsonProperty("fishingGear")
    public void setFishingGear(List<String> fishingGear) {
        this.fishingGear = fishingGear;
    }

    @JsonProperty("speciesCode")
    public List<String> getSpeciesCode() {
        return speciesCode;
    }

    @JsonProperty("speciesCode")
    public void setSpeciesCode(List<String> speciesCode) {
        this.speciesCode = speciesCode;
    }

    @JsonProperty("quantity")
    public List<Long> getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(List<Long> quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("contactPerson")
    public List<ContactPersonDetailsDTO> getContactPerson() {
        return contactPerson;
    }

    @JsonProperty("contactPerson")
    public void setContactPerson(List<ContactPersonDetailsDTO> contactPerson) {
        this.contactPerson = contactPerson;
    }


}
