/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries  European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.search;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;

/**
 * Created by sanera on 01/07/2016.
 */
public class SortKey {

    private SearchFilter sortBy;
    private boolean isReversed;

    public SortKey(){
        super();
    }

    public SearchFilter getSortBy() {
        return sortBy;
    }

    public void setSortBy(SearchFilter sortBy) {
        this.sortBy = sortBy;
    }

    public SortKey(SearchFilter field, boolean order) {
        this.sortBy = field;
        this.isReversed = order;
    }

    public boolean isReversed() {
        return isReversed;
    }

    public void setReversed(boolean reversed) {
        isReversed = reversed;
    }

    @Override
    public String toString() {
        return "SortKey{" +
                "field=" + sortBy +

                '}';
    }
}