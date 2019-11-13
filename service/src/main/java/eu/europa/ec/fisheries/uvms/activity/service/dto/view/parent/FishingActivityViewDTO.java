/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.uvms.activity.service.dto.view.parent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.uvms.activity.service.dto.facatch.FaCatchGroupDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityHistoryDtoElement;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.AreaDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.GearDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.GearProblemDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.GearShotRetrievalDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ProcessingProductsDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.RelocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripWidgetDto;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * This DTO will be returned to the requester for every request of activity views.
 * It will serialize only the properties that are configured to be present for the specific view Eg : @JsonView(FishingActivityView.Arrival.class).
 */
@JsonInclude(Include.NON_NULL)
@Data
public class FishingActivityViewDTO {

    @JsonView(FishingActivityView.CommonView.class)
    private ActivityDetailsDto activityDetails;

    @JsonView({FishingActivityView.FishingOperation.class, FishingActivityView.NotificationOfArrival.class, FishingActivityView.Departure.class, FishingActivityView.Landing.class, FishingActivityView.Arrival.class, FishingActivityView.Transhipment.class, FishingActivityView.Relocation.class, FishingActivityView.JointFishingOperation.class, FishingActivityView.AreaEntry.class,  FishingActivityView.AreaExit.class, })
    private Set<FluxLocationDto> locations;

    @JsonView({FishingActivityView.Arrival.class, FishingActivityView.Departure.class, FishingActivityView.JointFishingOperation.class, FishingActivityView.FishingOperation.class})
    private List<GearDto> gears;

    @JsonView({FishingActivityView.CommonView.class})
    private ReportDocumentDto reportDetails;

    @JsonView(FishingActivityView.CommonView.class)
    private Set<FaCatchGroupDto> catches;

    @JsonView({FishingActivityView.FishingOperation.class, FishingActivityView.GearShotAndRetrieval.class})
    private List<GearShotRetrievalDto> gearShotRetrievalList;

    @JsonView(FishingActivityView.CommonView.class)
    private List<ProcessingProductsDto> processingProducts;

    @JsonView({FishingActivityView.AreaEntry.class, FishingActivityView.AreaExit.class})
    private AreaDto areas;

    @JsonView(FishingActivityView.CommonView.class)
    private TripWidgetDto tripDetails;

    @JsonView({FishingActivityView.Relocation.class, FishingActivityView.Transhipment.class, FishingActivityView.JointFishingOperation.class, FishingActivityView.FishingOperation.class})
    private List<VesselDetailsDTO> vesselDetails;

    @JsonView({FishingActivityView.JointFishingOperation.class, FishingActivityView.FishingOperation.class})
    private List<GearProblemDto> gearProblems;

    @JsonView(FishingActivityView.JointFishingOperation.class)
    private List<RelocationDto> relocations;

    @JsonView(FishingActivityView.CommonView.class)
    private List<ActivityHistoryDtoElement> history;

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
