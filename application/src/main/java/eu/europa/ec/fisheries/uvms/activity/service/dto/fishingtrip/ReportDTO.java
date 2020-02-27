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

package eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip;

import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityDTO;

import javax.json.bind.annotation.JsonbProperty;
import java.util.Date;
import java.util.List;

public class ReportDTO extends FishingActivityDTO {

    @JsonbProperty("faReportAcceptedDateTime")
    private Date faReportAcceptedDateTime;

    @JsonbProperty("faReportDocumentType")
    private String faReportDocumentType;

    @JsonbProperty("locations")
    private List<String> locations;

    @JsonbProperty("correction")
    private boolean correction;


    @JsonbProperty("faReportDocumentType")
    public String getFaReportDocumentType() {
        return faReportDocumentType;
    }

    @JsonbProperty("faReportDocumentType")
    public void setFaReportDocumentType(String faReportDocumentType) {
        this.faReportDocumentType = faReportDocumentType;
    }

    @JsonbProperty("faReportAcceptedDateTime")
    public Date getFaReportAcceptedDateTime() {
        return faReportAcceptedDateTime;
    }

    @JsonbProperty("faReportAcceptedDateTime")
    public void setFaReportAcceptedDateTime(Date faReportAcceptedDateTime) {
        this.faReportAcceptedDateTime = faReportAcceptedDateTime;
    }

    @JsonbProperty("correction")
    public boolean isCorrection() {
        return correction;
    }

    @JsonbProperty("correction")
    public void setCorrection(boolean correction) {
        this.correction = correction;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
