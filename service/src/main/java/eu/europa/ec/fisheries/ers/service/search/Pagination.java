/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries  European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.search;

/**
 * Created by sanera on 24/06/2016.
 */
public class Pagination {

    private int page;
    private int listSize;
    private int totalPages;

    public Pagination(int page, int listSize) {
        this.page = page;
        this.listSize = listSize;
    }

    public Pagination(){
        super();
    }

    public void setPage(int page) {
        this.page = page;
    }
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }
    public int getPage() {
        return page;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    public int getListSize() {
        return listSize;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "page=" + page +
                ", listSize=" + listSize +
                '}';
    }
}