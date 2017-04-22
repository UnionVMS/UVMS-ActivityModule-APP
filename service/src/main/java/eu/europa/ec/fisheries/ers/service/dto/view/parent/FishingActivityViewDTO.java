/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.dto.view.parent;

import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.AreaEntry;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.AreaExit;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Arrival;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.CommonView;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Departure;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.FishingOperation;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.GearShotAndRetrieval;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.JointFishingOperation;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Landing;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.NotificationOfArrival;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Relocation;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Transhipment;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.facatch.FaCatchGroupDto;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.AreaDto;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.GearDto;
import eu.europa.ec.fisheries.ers.service.dto.view.GearProblemDto;
import eu.europa.ec.fisheries.ers.service.dto.view.GearShotRetrievalDto;
import eu.europa.ec.fisheries.ers.service.dto.view.ProcessingProductsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.RelocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.ers.service.dto.view.TripWidgetDto;

/**
 * This DTO will be returned to the requester for every request of activity views.
 * It will serialize only the properties that are configured to be present for the specific view Eg : @JsonView(FishingActivityView.Arrival.class).
 */
@JsonInclude(Include.NON_NULL)
public class FishingActivityViewDTO {

    @JsonView(CommonView.class)
    private ActivityDetailsDto activityDetails;

    @JsonView({FishingOperation.class, NotificationOfArrival.class, Departure.class, Landing.class, Arrival.class, Transhipment.class, Relocation.class, JointFishingOperation.class})
    private Set<FluxLocationDto> locations;

    @JsonView({Arrival.class, Departure.class, JointFishingOperation.class, FishingOperation.class})
    private List<GearDto> gears;

    @JsonView({FishingOperation.class, NotificationOfArrival.class, Arrival.class, Landing.class, Departure.class, AreaEntry.class, FishingActivityView.AreaExit.class, Transhipment.class, Relocation.class})
    private ReportDocumentDto reportDetails;

    @JsonView(CommonView.class)
    private List<FaCatchGroupDto> catches;

    @JsonView({FishingOperation.class, GearShotAndRetrieval.class})
    private List<GearShotRetrievalDto> gearShotRetrievalList;

    @JsonView(CommonView.class)
    private List<ProcessingProductsDto> processingProducts;

    @JsonView({AreaEntry.class, AreaExit.class})
    private AreaDto areas;

    @JsonView(CommonView.class)
    private TripWidgetDto tripDetails;

    @JsonView({Relocation.class, Transhipment.class, JointFishingOperation.class})
    private VesselDetailsDTO vesselDetails;

    @JsonView(JointFishingOperation.class)
    private List<GearProblemDto> gearProblems;

    @JsonView(JointFishingOperation.class)
    private List<RelocationDto> relocations;

    public Set<FluxLocationDto> getLocations() {
        return locations;
    }

    public void setLocations(Set<FluxLocationDto> locations) {
        this.locations = locations;
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

    public List<GearShotRetrievalDto> getGearShotRetrievalList() {
        return gearShotRetrievalList;
    }

    public void setGearShotRetrievalList(List<GearShotRetrievalDto> gearShotRetrievalList) {
        this.gearShotRetrievalList = gearShotRetrievalList;
    }

    public AreaDto getAreas() {
        return areas;
    }

    public void setAreas(AreaDto areas) {
        this.areas = areas;
    }

    public TripWidgetDto getTripDetails() {
        return tripDetails;
    }

    public void setTripDetails(TripWidgetDto tripDetails) {
        this.tripDetails = tripDetails;
    }

    public VesselDetailsDTO getVesselDetails() {
        return vesselDetails;
    }

    public void setVesselDetails(VesselDetailsDTO vesselDetails) {
        this.vesselDetails = vesselDetails;
    }

    public List<GearProblemDto> getGearProblems() {
        return gearProblems;
    }

    public void setGearProblems(List<GearProblemDto> gearProblems) {
        this.gearProblems = gearProblems;
    }

    public List<RelocationDto> getRelocations() {
        return relocations;
    }

    public void setRelocations(List<RelocationDto> relocations) {
        this.relocations = relocations;
    }

    @Override
    public String toString() {
        return "FishingActivityViewDTO{" +
                "activityDetails=" + activityDetails +
                ", locations=" + locations +
                ", gears=" + gears +
                ", reportDetails=" + reportDetails +
                ", catches=" + catches +
                ", gearShotRetrievalList=" + gearShotRetrievalList +
                ", processingProducts=" + processingProducts +
                ", areas=" + areas +
                ", vesselDetails=" + vesselDetails +
                ", gearProblems=" + gearProblems +
                ", relocations=" + relocations +
                '}';
    }
}
