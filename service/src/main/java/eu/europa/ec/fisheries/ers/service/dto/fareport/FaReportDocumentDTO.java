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

package eu.europa.ec.fisheries.ers.service.dto.fareport;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static eu.europa.ec.fisheries.uvms.commons.date.DateUtils.DATE_TIME_UI_FORMAT;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FaReportDocumentDTO {

    @JsonProperty("typeCode")
    private String typeCode;

    @JsonProperty("fmcMarker")
    private String fmcMarker;

    @JsonProperty("acceptedDateTime")
    @JsonFormat(shape = STRING, pattern = DATE_TIME_UI_FORMAT)
    private Date acceptedDateTime;

    @JsonProperty("creationDateTime")
    @JsonFormat(shape = STRING, pattern = DATE_TIME_UI_FORMAT)
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

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getFmcMarker() {
        return fmcMarker;
    }

    public void setFmcMarker(String fmcMarker) {
        this.fmcMarker = fmcMarker;
    }

    public Date getAcceptedDateTime() {
        return acceptedDateTime;
    }

    public void setAcceptedDateTime(Date acceptedDateTime) {
        this.acceptedDateTime = acceptedDateTime;
    }

    public Date getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Date creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public String getFluxReportDocumentId() {
        return fluxReportDocumentId;
    }

    public void setFluxReportDocumentId(String fluxReportDocumentId) {
        this.fluxReportDocumentId = fluxReportDocumentId;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getOwnerFluxPartyId() {
        return ownerFluxPartyId;
    }

    public void setOwnerFluxPartyId(String ownerFluxPartyId) {
        this.ownerFluxPartyId = ownerFluxPartyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
