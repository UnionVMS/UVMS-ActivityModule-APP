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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class GearDto {

    private String type;

    private String role;

    private String meshSize;

    private String lengthWidth;

    private Integer numberOfGears;

    private String height;

    private String nrOfLines;

    private String nrOfNets;

    private String nominalLengthOfNet;

    private String quantity;

    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMeshSize() {
        return meshSize;
    }

    public void setMeshSize(String meshSize) {
        this.meshSize = meshSize;
    }

    public String getLengthWidth() {
        return lengthWidth;
    }

    public void setLengthWidth(String lengthWidth) {
        this.lengthWidth = lengthWidth;
    }

    public Integer getNumberOfGears() {
        return numberOfGears;
    }

    public void setNumberOfGears(Integer numberOfGears) {
        this.numberOfGears = numberOfGears;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getNrOfLines() {
        return nrOfLines;
    }

    public void setNrOfLines(String nrOfLines) {
        this.nrOfLines = nrOfLines;
    }

    public String getNrOfNets() {
        return nrOfNets;
    }

    public void setNrOfNets(String nrOfNets) {
        this.nrOfNets = nrOfNets;
    }

    public String getNominalLengthOfNet() {
        return nominalLengthOfNet;
    }

    public void setNominalLengthOfNet(String nominalLengthOfNet) {
        this.nominalLengthOfNet = nominalLengthOfNet;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
