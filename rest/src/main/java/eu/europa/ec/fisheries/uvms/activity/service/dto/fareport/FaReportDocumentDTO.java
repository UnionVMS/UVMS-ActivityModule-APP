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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static eu.europa.ec.fisheries.uvms.commons.date.DateUtils.DATE_TIME_UI_FORMAT;

@JsonInclude(Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
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

}
