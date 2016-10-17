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

package eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovian on 10/10/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CatchSummaryListDTO {

    @JsonProperty("speciesList")
    private List<SpeciesQuantityDTO> speciesList;

    @JsonProperty("total")
    private Double total;

    public CatchSummaryListDTO(){
        this.speciesList = new ArrayList<>();
        total = 0.0;
    }

    @JsonIgnore
    public void addSpecieAndQuantity(String speciesCode, Double weight){
        speciesList.add(new SpeciesQuantityDTO(speciesCode, weight));
        this.setTotal(total + weight);
    }

    public List<SpeciesQuantityDTO> getSpeciesList() {
        return speciesList;
    }
    public void setSpeciesList(List<SpeciesQuantityDTO> speciesList) {
        this.speciesList = speciesList;
    }
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
}
