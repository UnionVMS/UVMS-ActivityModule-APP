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
 * Created by padhyad on 8/5/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FaReportCorrectionDTO implements Serializable, Comparable {

    @JsonProperty("correctionType")
    private String correctionType;

    @JsonProperty("correctionDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date correctionDate;

    @JsonProperty("faReportIdentifier")
    private String faReportIdentifier;

    @JsonProperty("ownerFluxParty")
    private String ownerFluxParty;

    public FaReportCorrectionDTO() {}

    public FaReportCorrectionDTO(String correctionType, Date correctionDate, String faReportIdentifier, String ownerFluxParty) {
        this.correctionType = correctionType;
        this.correctionDate = correctionDate;
        this.faReportIdentifier = faReportIdentifier;
        this.ownerFluxParty = ownerFluxParty;
    }

    @JsonProperty("correctionType")
    public String getCorrectionType() {
        return correctionType;
    }

    @JsonProperty("correctionType")
    public void setCorrectionType(String correctionType) {
        this.correctionType = correctionType;
    }

    @JsonProperty("correctionDate")
    public Date getCorrectionDate() {
        return correctionDate;
    }

    @JsonProperty("correctionDate")
    public void setCorrectionDate(Date correctionDate) {
        this.correctionDate = correctionDate;
    }

    @JsonProperty("faReportIdentifier")
    public String getFaReportIdentifier() {
        return faReportIdentifier;
    }

    @JsonProperty("faReportIdentifier")
    public void setFaReportIdentifier(String faReportIdentifier) {
        this.faReportIdentifier = faReportIdentifier;
    }

    @JsonProperty("ownerFluxParty")
    public String getOwnerFluxParty() {
        return ownerFluxParty;
    }

    @JsonProperty("ownerFluxParty")
    public void setOwnerFluxParty(String ownerFluxParty) {
        this.ownerFluxParty = ownerFluxParty;
    }

    @Override
    public int compareTo(Object o) {
        return correctionDate.compareTo(((FaReportCorrectionDTO) o).getCorrectionDate());
    }
}
