/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.dto.view;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView;

import java.util.List;

/**
 * Created by kovian on 09/02/2017.
 */
public class ReportDocumentDto {

    @JsonView(FishingActivityView.CommonView.class)
    private String type;

    @JsonView(FishingActivityView.CommonView.class)
    private String creationDate;

    @JsonView(FishingActivityView.CommonView.class)
    private String purposeCode;

    @JsonView(FishingActivityView.CommonView.class)
    private String owner;

    @JsonView(FishingActivityView.CommonView.class)
    private String id;

    @JsonView(FishingActivityView.CommonView.class)
    private String refId;

    @JsonView(FishingActivityView.CommonView.class)
    private String acceptedDate;

    @JsonView(FishingActivityView.CommonView.class)
    private String fmcMark;

    @JsonView(FishingActivityView.CommonView.class)
    private List<RelatedReportDto> relatedReports;


    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getAcceptedDate() {
        return acceptedDate;
    }
    public void setAcceptedDate(String acceptedDate) {
        this.acceptedDate = acceptedDate;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRefId() {
        return refId;
    }
    public void setRefId(String refId) {
        this.refId = refId;
    }
    public String getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    public String getPurposeCode() {
        return purposeCode;
    }
    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFmcMark() {
        return fmcMark;
    }

    public void setFmcMark(String fmcMark) {
        this.fmcMark = fmcMark;
    }

    public List<RelatedReportDto> getRelatedReports() {
        return relatedReports;
    }

    public void setRelatedReports(List<RelatedReportDto> relatedReports) {
        this.relatedReports = relatedReports;
    }
}
