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

package eu.europa.ec.fisheries.uvms.activity.service.dto.fareport;

import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class FaReportCorrectionDTO implements Serializable, Comparable<FaReportCorrectionDTO> {

    @JsonbProperty("id")
    private String id;

    @JsonbProperty("correctionType")
    private String correctionType;

    @JsonbProperty("creationDate")
    private Instant creationDate;

    @JsonbProperty("acceptedDate")
    private Instant acceptedDate;

    @JsonbProperty("faReportIdentifiers")
    private Map<String, String> faReportIdentifiers;

    @JsonbProperty("ownerFluxPartyName")
    private String ownerFluxPartyName;

    @JsonbProperty("purposeCode")
    private Integer purposeCode;

    public FaReportCorrectionDTO() {}

    public FaReportCorrectionDTO(String id, String correctionType, Instant creationDate, Instant acceptedDate, Map<String, String> faReportIdentifiers, String ownerFluxPartyName, Integer purposeCode) {
        this.id = id;
        this.correctionType = correctionType;
        this.creationDate = creationDate;
        this.acceptedDate = acceptedDate;
        this.faReportIdentifiers = faReportIdentifiers;
        this.ownerFluxPartyName = ownerFluxPartyName;
        this.purposeCode = purposeCode;
    }

    @JsonbProperty("id")
    public String getId() {
        return id;
    }

    @JsonbProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonbProperty("correctionType")
    public String getCorrectionType() {
        return correctionType;
    }

    @JsonbProperty("correctionType")
    public void setCorrectionType(String correctionType) {
        this.correctionType = correctionType;
    }

    @JsonbProperty("creationDate")
    public Instant getCreationDate() {
        return creationDate;
    }

    @JsonbProperty("creationDate")
    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    @JsonbProperty("acceptedDate")
    public Instant getAcceptedDate() {
        return acceptedDate;
    }

    @JsonbProperty("acceptedDate")
    public void setAcceptedDate(Instant acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    @JsonbProperty("faReportIdentifiers")
    public Map<String, String> getFaReportIdentifiers() {
        return faReportIdentifiers;
    }

    @JsonbProperty("faReportIdentifiers")
    public void setFaReportIdentifiers(Map<String, String> faReportIdentifiers) {
        this.faReportIdentifiers = faReportIdentifiers;
    }

    @JsonbProperty("ownerFluxPartyName")
    public String getOwnerFluxPartyName() {
        return ownerFluxPartyName;
    }

    @JsonbProperty("ownerFluxPartyName")
    public void setOwnerFluxPartyName(String ownerFluxPartyName) {
        this.ownerFluxPartyName = ownerFluxPartyName;
    }

    @JsonbProperty("purposeCode")
    public Integer getPurposeCode() {
        return purposeCode;
    }

    @JsonbProperty("purposeCode")
    public void setPurposeCode(Integer purposeCode) {
        this.purposeCode = purposeCode;
    }

    @Override
    public int compareTo(FaReportCorrectionDTO o) {
        return acceptedDate.compareTo(o.getAcceptedDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FaReportCorrectionDTO)) return false;

        FaReportCorrectionDTO that = (FaReportCorrectionDTO) o;

        return getAcceptedDate() != null ? getAcceptedDate().equals(that.getAcceptedDate()) : that.getAcceptedDate() == null;

    }

    @Override
    public int hashCode() {
        return getAcceptedDate() != null ? getAcceptedDate().hashCode() : 0;
    }
}
