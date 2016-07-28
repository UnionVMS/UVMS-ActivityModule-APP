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

import java.io.Serializable;
import java.util.List;

/**
 * Created by sanera on 19/07/2016.
 */
public class FishingActivityReportDTO  implements Serializable  {

    private String uniqueId;
    private String from;
    private String vesselTransportMeansName;
    private List<String> vesselTransportMeansIdList;
    private String purposeCode;
    private String FAReportType;
    private String activityType;
    private List<String> areas;
    private List<String> port;
    private List<String> fishingGear;
    private List<String> speciesCode;
    private List<Long> quantity;
    private List<ContactPersonDTO> contactPerson;

    public FishingActivityReportDTO(){

    }

    public FishingActivityReportDTO(String uniqueId, String from, String vesselTransportMeansName, List<String> vesselTransportMeansIdList, String purposeCode, String FAReportType, List<String> areas, List<String> fishingGear, String activityType, List<String> port, List<String> speciesCode, List<Long> quantity, List<ContactPersonDTO> contactPerson) {
        this.uniqueId = uniqueId;
        this.from = from;
        this.vesselTransportMeansName = vesselTransportMeansName;
        this.vesselTransportMeansIdList = vesselTransportMeansIdList;
        this.purposeCode = purposeCode;
        this.FAReportType = FAReportType;
        this.areas = areas;
        this.fishingGear = fishingGear;
        this.activityType = activityType;
        this.port = port;
        this.speciesCode = speciesCode;
        this.quantity = quantity;
        this.contactPerson = contactPerson;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getVesselTransportMeansName() {
        return vesselTransportMeansName;
    }

    public void setVesselTransportMeansName(String vesselTransportMeansName) {
        this.vesselTransportMeansName = vesselTransportMeansName;
    }

    public List<String> getVesselTransportMeansIdList() {
        return vesselTransportMeansIdList;
    }

    public void setVesselTransportMeansIdList(List<String> vesselTransportMeansIdList) {
        this.vesselTransportMeansIdList = vesselTransportMeansIdList;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getFAReportType() {
        return FAReportType;
    }

    public void setFAReportType(String FAReportType) {
        this.FAReportType = FAReportType;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public List<String> getAreas() {
        return areas;
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
    }

    public List<String> getPort() {
        return port;
    }

    public void setPort(List<String> port) {
        this.port = port;
    }

    public List<String> getFishingGear() {
        return fishingGear;
    }

    public void setFishingGear(List<String> fishingGear) {
        this.fishingGear = fishingGear;
    }

    public List<String> getSpeciesCode() {
        return speciesCode;
    }

    public void setSpeciesCode(List<String> speciesCode) {
        this.speciesCode = speciesCode;
    }

    public List<Long> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<Long> quantity) {
        this.quantity = quantity;
    }

    public List<ContactPersonDTO> getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(List<ContactPersonDTO> contactPerson) {
        this.contactPerson = contactPerson;
    }


}
