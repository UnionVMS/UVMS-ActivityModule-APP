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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;

/**
 * Created by sanera on 01/09/2017.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SpeciesAreaDTO {


    @JsonIgnore
    private Geometry geom;

    @JsonProperty("weight")
    private Double weight;

    public SpeciesAreaDTO(){

    }

    public SpeciesAreaDTO(Geometry geom, Double weight) {
        this.geom = geom;
        this.weight = weight;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @JsonProperty("geom")
    public String getWkt() {
        String wkt = null;
        if (geom != null) {
            wkt = GeometryMapper.INSTANCE.geometryToWkt(geom).getValue();
        }
        return wkt;
    }
}
