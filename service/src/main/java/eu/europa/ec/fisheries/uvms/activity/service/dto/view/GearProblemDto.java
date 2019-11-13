/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.uvms.activity.service.dto.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.parent.FishingActivityView;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class GearProblemDto {

    @JsonView(FishingActivityView.CommonView.class)
    private String type;

    @JsonView(FishingActivityView.CommonView.class)
    private Integer nrOfGears;

    @JsonView(FishingActivityView.CommonView.class)
    private String recoveryMeasure;

    @JsonView(FishingActivityView.CommonView.class)
    private FluxLocationDto location;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNrOfGears() {
        return nrOfGears;
    }

    public void setNrOfGears(Integer nrOfGears) {
        this.nrOfGears = nrOfGears;
    }

    public String getRecoveryMeasure() {
        return recoveryMeasure;
    }

    public void setRecoveryMeasure(String recoveryMeasure) {
        this.recoveryMeasure = recoveryMeasure;
    }

    public FluxLocationDto getLocation() {
        return location;
    }

    public void setLocation(FluxLocationDto location) {
        this.location = location;
    }
}
