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

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView;
import eu.europa.ec.fisheries.uvms.rest.serializer.CustomDateSerializer;

@JsonInclude(NON_NULL)
public class PositionDto {

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonView(FishingActivityView.CommonView.class)
    private Date occurence;

    @JsonView(FishingActivityView.CommonView.class)
    private String geometry;

    public Date getOccurence() {
        return occurence;
    }

    public void setOccurence(Date occurence) {
        this.occurence = occurence;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }
}
