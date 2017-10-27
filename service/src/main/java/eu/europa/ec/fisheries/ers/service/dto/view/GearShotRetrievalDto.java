/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.dto.view;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.CommonView;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.FishingOperation;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.GearShotAndRetrieval;

@JsonInclude(NON_NULL)
public class GearShotRetrievalDto {

    @JsonView(CommonView.class)
    private String type;

    @JsonView(CommonView.class)
    private String occurrence;

    @JsonView(CommonView.class)
    private IdentifierDto id;

    @JsonView(CommonView.class)
    private Double duration;

    @JsonView(CommonView.class)
    private Double calculatedDuration;

    @JsonView(CommonView.class)
    private String durationMeasure;

    @JsonView(CommonView.class)
    private GearDto gear;

    @JsonView(CommonView.class)
    private Map<String, String> characteristics;

    @JsonView({FishingOperation.class, GearShotAndRetrieval.class})
    private List<GearProblemDto> gearProblems;

    @JsonView(CommonView.class)
    private FluxLocationDto location;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(String occurrence) {
        this.occurrence = occurrence;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public GearDto getGear() {
        return gear;
    }

    public void setGear(GearDto gear) {
        this.gear = gear;
    }

    public Map<String, String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Map<String, String> characteristics) {
        this.characteristics = characteristics;
    }

    public FluxLocationDto getLocation() {
        return location;
    }

    public void setLocation(FluxLocationDto location) {
        this.location = location;
    }

    public List<GearProblemDto> getGearProblems() {
        return gearProblems;
    }

    public void setGearProblems(List<GearProblemDto> gearProblems) {
        this.gearProblems = gearProblems;
    }

    public IdentifierDto getId() {
        return id;
    }

    public void setId(IdentifierDto id) {
        this.id = id;
    }

    public Double getCalculatedDuration() {
        return calculatedDuration;
    }

    public void setCalculatedDuration(Double calculatedDuration) {
        this.calculatedDuration = calculatedDuration;
    }

    public String getDurationMeasure() {
        return durationMeasure;
    }

    public void setDurationMeasure(String durationMeasure) {
        this.durationMeasure = durationMeasure;
    }
}
