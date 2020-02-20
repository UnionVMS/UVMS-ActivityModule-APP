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

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FishingActivityReportDTO extends FishingActivityDTO implements Serializable  {

    @JsonbProperty("dataSource")
    private String dataSource;

    @JsonbProperty("fromId")
    private List<String> fromId;

    @JsonbProperty("fromName")
    private String fromName;

    @JsonbProperty("vesselIdTypes")
    private Map<String,String> vesselIdTypes;

    @JsonbProperty("vesselTransportMeansName")
    private String vesselTransportMeansName;

    @JsonbProperty("FAReportType")
    private String faReportType;

    @JsonbProperty("areas")
    private List<String> areas;

    @JsonbProperty("port")
    private List<String> port;

    @JsonbProperty("fishingGear")
    private Set<String> fishingGear;

    @JsonbProperty("speciesCode")
    private List<String> speciesCode;

    @JsonbProperty("quantity")
    private Double quantity;

    @JsonbProperty("startDate")
    private Date startDate;

    @JsonbProperty("endDate")
    private Date endDate;

    @JsonbProperty("hasCorrection")
    private boolean hasCorrection;

    @JsonbProperty("fluxReportReferenceId")
    private String fluxReportReferenceId;

    @JsonbProperty("fluxReportReferenceSchemeId")
    private String fluxReportReferenceSchemeId;

    public FishingActivityReportDTO() {
        // Assuming jackson needs this when serializing/deserializing
    }

    @JsonbProperty("vesselTransportMeansName")
    public String getVesselTransportMeansName() {
        return vesselTransportMeansName;
    }

    @JsonbProperty("vesselTransportMeansName")
    public void setVesselTransportMeansName(String vesselTransportMeansName) {
        this.vesselTransportMeansName = vesselTransportMeansName;
    }

    @JsonbProperty("FAReportType")
    public String getFaReportType() {
        return faReportType;
    }

    @JsonbProperty("FAReportType")
    public void setFaReportType(String faReportType) {
        this.faReportType = faReportType;
    }

    @JsonbProperty("areas")
    public List<String> getAreas() {
        return areas;
    }

    @JsonbProperty("areas")
    public void setAreas(List<String> areas) {
        this.areas = areas;
    }

    @JsonbProperty("port")
    public List<String> getPort() {
        return port;
    }

    @JsonbProperty("port")
    public void setPort(List<String> port) {
        this.port = port;
    }

    @JsonbProperty("fishingGear")
    public Set<String> getFishingGear() {
        return fishingGear;
    }

    @JsonbProperty("fishingGear")
    public void setFishingGear(Set<String> fishingGear) {
        this.fishingGear = fishingGear;
    }

    @JsonbProperty("speciesCode")
    public List<String> getSpeciesCode() {
        return speciesCode;
    }

    @JsonbProperty("speciesCode")
    public void setSpeciesCode(List<String> speciesCode) {
        this.speciesCode = speciesCode;
    }

    @JsonbProperty("quantity")
    public Double getQuantity() {
        return quantity;
    }

    @JsonbProperty("quantity")
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @JsonbProperty("dataSource")
    public String getDataSource() {
        return dataSource;
    }

    @JsonbProperty("dataSource")
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @JsonbProperty("fromId")
    public List<String> getFromId() {
        return fromId;
    }

    @JsonbProperty("fromId")
    public void setFromId(List<String> fromId) {
        this.fromId = fromId;
    }

    @JsonbProperty("fromName")
    public String getFromName() {
        return fromName;
    }

    @JsonbProperty("fromName")
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public Map<String, String> getVesselIdTypes() {
        return vesselIdTypes;
    }

    public void setVesselIdTypes(Map<String, String> vesselIdTypes) {
        this.vesselIdTypes = vesselIdTypes;
    }

    @JsonbProperty("startDate")
    public Date getStartDate() {
        return startDate;
    }

    @JsonbProperty("startDate")
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @JsonbProperty("endDate")
    public Date getEndDate() {
        return endDate;
    }

    @JsonbProperty("endDate")
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
