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

import lombok.Data;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.util.Date;
import java.util.List;

@Data
public class FishingActivityDTO {

    @JsonbProperty("uniqueReportIdList")
    private List<FluxReportIdentifierDTO> uniqueFAReportId;

    @JsonbProperty("fishingActivityId")
    private int fishingActivityId;

    @JsonbProperty("faReportID")
    private int faReportID;

    @JsonbTransient
    private int cancelingReportID;

    @JsonbTransient
    private int deletingReportID;

    @JsonbProperty("faUniqueReportID")
    private String faUniqueReportID;

    @JsonbProperty("faUniqueReportSchemeID")
    private String faUniqueReportSchemeID;

    @JsonbProperty("faReferenceID")
    private String faReferenceID;

    @JsonbProperty("faReferenceSchemeID")
    private String faReferenceSchemeID;

    @JsonbProperty("activityType")
    private String activityType;

    @JsonbProperty("geometry")
    private String geometry;

    @JsonbProperty("occurence")
    private Date occurence;

    @JsonbProperty("reason")
    private String reason;

    @JsonbProperty("purposeCode")
    private String purposeCode;

    @JsonbProperty("fishingGears")
    private List<FishingGearDTO> fishingGears;

    @JsonbProperty("fluxCharacteristics")
    private List<FluxCharacteristicsDto> fluxCharacteristics;

    @JsonbProperty("delimitedPeriod")
    private List<DelimitedPeriodDTO> delimitedPeriod;

}
