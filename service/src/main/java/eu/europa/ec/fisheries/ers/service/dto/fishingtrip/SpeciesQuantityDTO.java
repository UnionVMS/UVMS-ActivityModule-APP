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
package eu.europa.ec.fisheries.ers.service.dto.fishingtrip;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovian on 10/10/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SpeciesQuantityDTO {

    @JsonProperty("speciesCode")
    private String speciesCode;

    @JsonProperty("weight")
    private Double weight=0.0;

    private List<SpeciesAreaDTO> areaInfo;

    public SpeciesQuantityDTO() {
        super();
    }

    public SpeciesQuantityDTO(String code) {
        areaInfo = new ArrayList<>();
        setSpeciesCode(code);

    }

    public void addToAreaInfo(Geometry geom, Double measure){
        areaInfo.add(new SpeciesAreaDTO(geom, measure));
        weight =weight+measure;
    }

    public String getSpeciesCode() {
        return speciesCode;
    }

    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public List<SpeciesAreaDTO> getAreaInfo() {
        return areaInfo;
    }

    public void setAreaInfo(List<SpeciesAreaDTO> areaInfo) {
        this.areaInfo = areaInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpeciesQuantityDTO)) return false;

        SpeciesQuantityDTO that = (SpeciesQuantityDTO) o;

        return getSpeciesCode() != null ? getSpeciesCode().equals(that.getSpeciesCode()) : that.getSpeciesCode() == null;

    }

    @Override
    public int hashCode() {
        return getSpeciesCode() != null ? getSpeciesCode().hashCode() : 0;
    }
}
