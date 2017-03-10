/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.dto.view.parent;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.view.*;
import eu.europa.ec.fisheries.ers.service.dto.view.facatch.FaCatchGroupDto;

import java.util.List;

/**
 * Created by kovian on 07/02/2017.
 *
 * This DTO will be returned to the requestor for every request of activity views.
 * It will serialize only the properties that are configured to be present for the specific view Eg : @JsonView(FishingActivityView.Arrival.class).
 *
 */
public class FishingActivityViewDTO {

    @JsonView(FishingActivityView.CommonView.class)
    private ActivityDetailsDto activityDetails;

    @JsonView({FishingActivityView.Arrival.class, FishingActivityView.Landing.class, FishingActivityView.Departure.class})
    private List<FluxLocationDto> ports;

    @JsonView({FishingActivityView.Arrival.class, FishingActivityView.Departure.class})
    private List<GearDto> gears;

    @JsonView({FishingActivityView.Arrival.class, FishingActivityView.Landing.class, FishingActivityView.Departure.class})
    private ReportDocumentDto reportDetails;

    @JsonView(FishingActivityView.CommonView.class)
    private List<FaCatchGroupDto> catches;

    @JsonView(FishingActivityView.CommonView.class)
    private List<ProcessingProductsDto> processingProducts;

    public List<FluxLocationDto> getPorts() {
        return ports;
    }
    public void setPorts(List<FluxLocationDto> ports) {
        this.ports = ports;
    }
    public ActivityDetailsDto getActivityDetails() {
        return activityDetails;
    }
    public void setActivityDetails(ActivityDetailsDto activityDetails) {
        this.activityDetails = activityDetails;
    }
    public List<GearDto> getGears() {
        return gears;
    }
    public void setGears(List<GearDto> gears) {
        this.gears = gears;
    }
    public ReportDocumentDto getReportDetails() {
        return reportDetails;
    }
    public void setReportDetails(ReportDocumentDto reportDetails) {
        this.reportDetails = reportDetails;
    }
    public List<FaCatchGroupDto> getCatches() {
        return catches;
    }
    public void setCatches(List<FaCatchGroupDto> catches) {
        this.catches = catches;
    }

    public List<ProcessingProductsDto> getProcessingProducts() {
        return processingProducts;
    }

    public void setProcessingProducts(List<ProcessingProductsDto> processingProducts) {
        this.processingProducts = processingProducts;
    }
}
