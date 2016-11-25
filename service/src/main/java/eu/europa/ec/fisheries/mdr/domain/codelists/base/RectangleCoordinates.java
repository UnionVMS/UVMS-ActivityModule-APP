/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.mdr.domain.codelists.base;

import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Embeddable
@EqualsAndHashCode
@ToString
@Indexed
public class RectangleCoordinates implements Serializable {

    @Column(name = "south")
    @Field(name="south", analyze= Analyze.NO, store = Store.YES)
    private double south;

    @Column(name = "west")
    @Field(name="west", analyze= Analyze.NO, store = Store.YES)
    private double west;

    @Column(name = "north")
    @Field(name="north", analyze= Analyze.NO, store = Store.YES)
    private double north;

    @Column(name = "east")
    @Field(name="east", analyze= Analyze.NO, store = Store.YES)
    private double east;

    public RectangleCoordinates() {
        super();
    }

    public RectangleCoordinates(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateRectangle(mdrDataType);
    }

    public void populateRectangle(MDRDataNodeType mdrDataType) {
        List<MDRElementDataNodeType> fieldsToRemove  = new ArrayList<>();
        for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
            String fieldName  = field.getName().getValue();
            String fieldValue = field.getName().getValue();
            if(StringUtils.equalsIgnoreCase("WEST", fieldName)){
                this.setWest(Double.parseDouble(fieldValue));
                fieldsToRemove.add(field);
            } else if(StringUtils.equalsIgnoreCase("EAST", fieldName)){
                this.setEast(Double.parseDouble(fieldValue));
                fieldsToRemove.add(field);
            } else if(StringUtils.equalsIgnoreCase("NORTH", fieldName)){
                this.setNorth(Double.parseDouble(fieldValue));
                fieldsToRemove.add(field);
            } else if(StringUtils.equalsIgnoreCase("SOUTH", fieldName)){
                this.setSouth(Double.parseDouble(fieldValue));
                fieldsToRemove.add(field);
            }
        }
        mdrDataType.getSubordinateMDRElementDataNodes().removeAll(fieldsToRemove);
    }


    public double getSouth() {
        return south;
    }
    public void setSouth(double south) {
        this.south = south;
    }
    public double getWest() {
        return west;
    }
    public void setWest(double west) {
        this.west = west;
    }
    public double getNorth() {
        return north;
    }
    public void setNorth(double north) {
        this.north = north;
    }
    public double getEast() {
        return east;
    }
    public void setEast(double east) {
        this.east = east;
    }

}