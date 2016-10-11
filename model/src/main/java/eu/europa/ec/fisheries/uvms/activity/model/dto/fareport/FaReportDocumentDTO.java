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

package eu.europa.ec.fisheries.uvms.activity.model.dto.fareport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by padhyad on 8/9/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FaReportDocumentDTO implements Serializable {

    @JsonProperty("typeCode")
    private String typeCode;

    @JsonProperty("fmcMarker")
    private String fmcMarker;

    @JsonProperty("acceptedDateTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd\'T\'HH:mm:ss")
    private Date acceptedDateTime;

    @JsonProperty("creationDateTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd\'T\'HH:mm:ss")
    private Date creationDateTime;

    @JsonProperty("fluxReportDocumentId")
    private String fluxReportDocumentId;

    @JsonProperty("purposeCode")
    private String purposeCode;

    @JsonProperty("referenceId")
    private String referenceId;

    @JsonProperty("ownerFluxPartyId")
    private String ownerFluxPartyId;

    @JsonProperty("status")
    private String status;

    public FaReportDocumentDTO() {
    }

    public FaReportDocumentDTO(String typeCode, String fmcMarker, Date acceptedDateTime, Date creationDateTime, String fluxReportDocumentId, String purposeCode, String referenceId, String ownerFluxPartyId, String status) {
        this.typeCode = typeCode;
        this.fmcMarker = fmcMarker;
        this.acceptedDateTime = acceptedDateTime;
        this.creationDateTime = creationDateTime;
        this.fluxReportDocumentId = fluxReportDocumentId;
        this.purposeCode = purposeCode;
        this.referenceId = referenceId;
        this.ownerFluxPartyId = ownerFluxPartyId;
        this.status = status;
    }

    @JsonProperty("typeCode")
    public String getTypeCode() {
        return typeCode;
    }

    @JsonProperty("typeCode")
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @JsonProperty("fmcMarker")
    public String getFmcMarker() {
        return fmcMarker;
    }

    @JsonProperty("fmcMarker")
    public void setFmcMarker(String fmcMarker) {
        this.fmcMarker = fmcMarker;
    }

    @JsonProperty("acceptedDateTime")
    public Date getAcceptedDateTime() {
        return acceptedDateTime;
    }

    @JsonProperty("acceptedDateTime")
    public void setAcceptedDateTime(Date acceptedDateTime) {
        this.acceptedDateTime = acceptedDateTime;
    }

    @JsonProperty("creationDateTime")
    public Date getCreationDateTime() {
        return creationDateTime;
    }

    @JsonProperty("creationDateTime")
    public void setCreationDateTime(Date creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    @JsonProperty("fluxReportDocumentId")
    public String getFluxReportDocumentId() {
        return fluxReportDocumentId;
    }

    @JsonProperty("fluxReportDocumentId")
    public void setFluxReportDocumentId(String fluxReportDocumentId) {
        this.fluxReportDocumentId = fluxReportDocumentId;
    }

    @JsonProperty("purposeCode")
    public String getPurposeCode() {
        return purposeCode;
    }

    @JsonProperty("purposeCode")
    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    @JsonProperty("referenceId")
    public String getReferenceId() {
        return referenceId;
    }

    @JsonProperty("referenceId")
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    @JsonProperty("ownerFluxPartyId")
    public String getOwnerFluxPartyId() {
        return ownerFluxPartyId;
    }

    @JsonProperty("ownerFluxPartyId")
    public void setOwnerFluxPartyId(String ownerFluxPartyId) {
        this.ownerFluxPartyId = ownerFluxPartyId;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }
}
