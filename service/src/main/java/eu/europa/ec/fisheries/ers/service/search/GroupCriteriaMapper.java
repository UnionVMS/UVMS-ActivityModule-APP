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
 * Created by sanera on 20/01/2017.
 */
public class GroupCriteriaMapper {

    private String tableJoin;
    private String columnName;
    private String methodName; // This is a method name which is used to map criteria to FaCatchSummaryCustomEntity object


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public GroupCriteriaMapper(String tableJoin, String columnName, String methodName) {
        this.tableJoin = tableJoin;
        this.columnName = columnName;
        this.methodName = methodName;
    }

    public GroupCriteriaMapper() {
        super();
    }

    public String getTableJoin() {
        return tableJoin;
    }

    public void setTableJoin(String tableJoin) {
        this.tableJoin = tableJoin;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
