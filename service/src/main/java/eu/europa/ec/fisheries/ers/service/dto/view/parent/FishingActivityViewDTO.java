/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.dto.view.parent;

import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.facatch.FaCatchGroupDto;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.*;
import lombok.Data;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.*;

/**
 * This DTO will be returned to the requester for every request of activity views.
 * It will serialize only the properties that are configured to be present for the specific view Eg : @JsonView(FishingActivityView.Arrival.class).
 */
@JsonInclude(Include.NON_NULL)
@Data
public class FishingActivityViewDTO {

    @JsonView(CommonView.class)
    private ActivityDetailsDto activityDetails;

    @JsonView({FishingOperation.class, NotificationOfArrival.class, Departure.class, Landing.class, Arrival.class, Transhipment.class, Relocation.class, JointFishingOperation.class, AreaEntry.class,  AreaExit.class, })
    private Set<FluxLocationDto> locations;

    @JsonView({Arrival.class, Departure.class, JointFishingOperation.class, FishingOperation.class})
    private List<GearDto> gears;

    @JsonView({CommonView.class})
    private ReportDocumentDto reportDetails;

    @JsonView(CommonView.class)
    private Set<FaCatchGroupDto> catches;

    @JsonView({FishingOperation.class, GearShotAndRetrieval.class})
    private List<GearShotRetrievalDto> gearShotRetrievalList;

    @JsonView(CommonView.class)
    private List<ProcessingProductsDto> processingProducts;

    @JsonView({AreaEntry.class, AreaExit.class})
    private AreaDto areas;

    @JsonView(CommonView.class)
    private TripWidgetDto tripDetails;

    @JsonView({Relocation.class, Transhipment.class, JointFishingOperation.class,FishingOperation.class})
    private List<VesselDetailsDTO> vesselDetails;

    @JsonView({JointFishingOperation.class, FishingOperation.class})
    private List<GearProblemDto> gearProblems;

    @JsonView(JointFishingOperation.class)
    private List<RelocationDto> relocations;

    @JsonView(CommonView.class)
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
