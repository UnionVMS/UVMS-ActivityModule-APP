/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.dto.view.facatch;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView;

/**
 * Created by kovian on 01/03/2017.
 */
public class DestinationLocationDto {

    @JsonView(FishingActivityView.CommonView.class)
    private String id;

    @JsonView(FishingActivityView.CommonView.class)
    private String name;

    @JsonView(FishingActivityView.CommonView.class)
    private String countryId;

    public DestinationLocationDto() {
        super();
    }

    public DestinationLocationDto(String id, String countryId, String name) {
        this.id = id;
        this.countryId = countryId;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCountryId() {
        return countryId;
    }
    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }
}
