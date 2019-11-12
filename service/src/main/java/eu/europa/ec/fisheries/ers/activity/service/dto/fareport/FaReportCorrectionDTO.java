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

package eu.europa.ec.fisheries.ers.activity.service.dto.fareport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape;
import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Created by padhyad on 8/5/2016.
 */
@JsonInclude(Include.NON_EMPTY)
public class FaReportCorrectionDTO implements Serializable, Comparable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("correctionType")
    private String correctionType;

    @JsonProperty("creationDate")
    @JsonFormat(shape = Shape.STRING, pattern = DateUtils.DATE_TIME_UI_FORMAT)
    private Date creationDate;

    @JsonProperty("acceptedDate")
    @JsonFormat(shape = Shape.STRING, pattern = DateUtils.DATE_TIME_UI_FORMAT)
    private Date acceptedDate;

    @JsonProperty("faReportIdentifiers")
    private Map<String, String> faReportIdentifiers;

    @JsonProperty("ownerFluxPartyName")
    private String ownerFluxPartyName;

    @JsonProperty("purposeCode")
    private Integer purposeCode;

    public FaReportCorrectionDTO() {}

    public FaReportCorrectionDTO(String id, String correctionType, Date creationDate, Date acceptedDate, Map<String, String> faReportIdentifiers, String ownerFluxPartyName, Integer purposeCode) {
        this.id = id;
        this.correctionType = correctionType;
        this.creationDate = creationDate;
        this.acceptedDate = acceptedDate;
        this.faReportIdentifiers = faReportIdentifiers;
        this.ownerFluxPartyName = ownerFluxPartyName;
        this.purposeCode = purposeCode;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("correctionType")
    public String getCorrectionType() {
        return correctionType;
    }

    @JsonProperty("correctionType")
    public void setCorrectionType(String correctionType) {
        this.correctionType = correctionType;
    }

    @JsonProperty("creationDate")
    public Date getCreationDate() {
        return creationDate;
    }

    @JsonProperty("creationDate")
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @JsonProperty("acceptedDate")
    public Date getAcceptedDate() {
        return acceptedDate;
    }

    @JsonProperty("acceptedDate")
    public void setAcceptedDate(Date acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    @JsonProperty("faReportIdentifiers")
    public Map<String, String> getFaReportIdentifiers() {
        return faReportIdentifiers;
    }

    @JsonProperty("faReportIdentifiers")
    public void setFaReportIdentifiers(Map<String, String> faReportIdentifiers) {
        this.faReportIdentifiers = faReportIdentifiers;
    }

    @JsonProperty("ownerFluxPartyName")
    public String getOwnerFluxPartyName() {
        return ownerFluxPartyName;
    }

    @JsonProperty("ownerFluxPartyName")
    public void setOwnerFluxPartyName(String ownerFluxPartyName) {
        this.ownerFluxPartyName = ownerFluxPartyName;
    }

    @JsonProperty("purposeCode")
    public Integer getPurposeCode() {
        return purposeCode;
    }

    @JsonProperty("purposeCode")
    public void setPurposeCode(Integer purposeCode) {
        this.purposeCode = purposeCode;
    }

    @Override
    public int compareTo(Object o) {
        return acceptedDate.compareTo(((FaReportCorrectionDTO) o).getAcceptedDate());
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
