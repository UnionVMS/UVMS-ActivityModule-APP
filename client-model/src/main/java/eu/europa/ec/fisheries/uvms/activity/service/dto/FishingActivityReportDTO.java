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

package eu.europa.ec.fisheries.uvms.activity.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class FishingActivityReportDTO extends FishingActivityDTO implements Serializable  {

    @JsonProperty("dataSource")
    private String dataSource;

    @JsonProperty("fromId")
    private List<String> fromId;

    @JsonProperty("fromName")
    private String fromName;

    @JsonProperty("vesselIdTypes")
    private Map<String,String> vesselIdTypes;

    @JsonProperty("vesselTransportMeansName")
    private String vesselTransportMeansName;

    @JsonProperty("FAReportType")
    private String faReportType;

    @JsonProperty("areas")
    private List<String> areas;

    @JsonProperty("port")
    private List<String> port;

    @JsonProperty("fishingGear")
    private Set<String> fishingGear;

    @JsonProperty("speciesCode")
    private List<String> speciesCode;

    @JsonProperty("quantity")
    private Double quantity;

    @JsonProperty("startDate")
    private Date startDate;

    @JsonProperty("endDate")
    private Date endDate;

    @JsonProperty("hasCorrection")
    private boolean hasCorrection;

    @JsonProperty("fluxReportReferenceId")
    private String fluxReportReferenceId;

    @JsonProperty("fluxReportReferenceSchemeId")
    private String fluxReportReferenceSchemeId;

    public FishingActivityReportDTO() {
        // Assuming jackson needs this when serializing/deserializing
    }

    @JsonProperty("vesselTransportMeansName")
    public String getVesselTransportMeansName() {
        return vesselTransportMeansName;
    }

    @JsonProperty("vesselTransportMeansName")
    public void setVesselTransportMeansName(String vesselTransportMeansName) {
        this.vesselTransportMeansName = vesselTransportMeansName;
    }

    @JsonProperty("FAReportType")
    public String getFaReportType() {
        return faReportType;
    }

    @JsonProperty("FAReportType")
    public void setFaReportType(String faReportType) {
        this.faReportType = faReportType;
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
    public Set<String> getFishingGear() {
        return fishingGear;
    }

    @JsonProperty("fishingGear")
    public void setFishingGear(Set<String> fishingGear) {
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
    public Double getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("dataSource")
    public String getDataSource() {
        return dataSource;
    }

    @JsonProperty("dataSource")
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @JsonProperty("fromId")
    public List<String> getFromId() {
        return fromId;
    }

    @JsonProperty("fromId")
    public void setFromId(List<String> fromId) {
        this.fromId = fromId;
    }

    @JsonProperty("fromName")
    public String getFromName() {
        return fromName;
    }

    @JsonProperty("fromName")
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public Map<String, String> getVesselIdTypes() {
        return vesselIdTypes;
    }

    public void setVesselIdTypes(Map<String, String> vesselIdTypes) {
        this.vesselIdTypes = vesselIdTypes;
    }

    @JsonProperty("startDate")
    public Date getStartDate() {
        return startDate;
    }

    @JsonProperty("startDate")
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("endDate")
    public Date getEndDate() {
        return endDate;
    }

    @JsonProperty("endDate")
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isHasCorrection() {
        return hasCorrection;
    }

    public void setHasCorrection(boolean hasCorrection) {
        this.hasCorrection = hasCorrection;
    }

    public String getFluxReportReferenceId() {
        return fluxReportReferenceId;
    }

    public void setFluxReportReferenceId(String fluxReportReferenceId) {
        this.fluxReportReferenceId = fluxReportReferenceId;
    }

    public String getFluxReportReferenceSchemeId() {
        return fluxReportReferenceSchemeId;
    }

    public void setFluxReportReferenceSchemeId(String fluxReportReferenceSchemeId) {
        this.fluxReportReferenceSchemeId = fluxReportReferenceSchemeId;
    }
}
