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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.europa.ec.fisheries.uvms.commons.date.CustomDateSerializer;
import lombok.Data;

import java.util.Date;
import java.util.List;

@JsonInclude(Include.NON_NULL)
@Data
public class FishingActivityDTO {

    @JsonProperty("uniqueReportIdList")
    private List<FluxReportIdentifierDTO> uniqueFAReportId;

    @JsonProperty("fishingActivityId")
    private int fishingActivityId;

    @JsonProperty("faReportID")
    private int faReportID;

    @JsonIgnore
    private int cancelingReportID;

    @JsonIgnore
    private int deletingReportID;

    @JsonProperty("faUniqueReportID")
    private String faUniqueReportID;

    @JsonProperty("faUniqueReportSchemeID")
    private String faUniqueReportSchemeID;

    @JsonProperty("faReferenceID")
    private String faReferenceID;

    @JsonProperty("faReferenceSchemeID")
    private String faReferenceSchemeID;

    @JsonProperty("activityType")
    private String activityType;

    @JsonProperty("geometry")
    private String geometry;

    @JsonProperty("occurence")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date occurence;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("purposeCode")
    private String purposeCode;

    @JsonProperty("fishingGears")
    private List<FishingGearDTO> fishingGears;

    @JsonProperty("fluxCharacteristics")
    private List<FluxCharacteristicsDto> fluxCharacteristics;

    @JsonProperty("delimitedPeriod")
    private List<DelimitedPeriodDTO> delimitedPeriod;

}
