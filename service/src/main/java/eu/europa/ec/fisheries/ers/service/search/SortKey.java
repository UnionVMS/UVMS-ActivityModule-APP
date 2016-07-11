/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.search;

/**
 * Created by sanera on 01/07/2016.
 */
public class SortKey {

    private SearchKey field;
    private SortOrder order;


    public SearchKey getField() {
        return field;
    }

    public void setField(SearchKey field) {
        this.field = field;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public SortKey(SearchKey field, SortOrder order) {
        this.field = field;
        this.order = order;
    }
    public SortKey(){

    }

    @Override
    public String toString() {
        return "SortKey{" +
                "field=" + field +
                ", order=" + order +
                '}';
    }
}