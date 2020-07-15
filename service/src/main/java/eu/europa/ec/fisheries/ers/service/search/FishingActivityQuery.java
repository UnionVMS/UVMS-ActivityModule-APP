/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries  European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.search;

import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used to define Filters to be applied on Fishing Activities
 * It also contains list of grouping Criteria to be applied
 * Created by sanera on 24/06/2016.
 */
@NoArgsConstructor
@Data
public class FishingActivityQuery {

    private PaginationDto pagination;
    private Map<SearchFilter, String> searchCriteriaMap;
    private Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues;
    private SortKey sorting;
    private List<GroupCriteria> groupByFields;
    private Boolean showOnlyLatest;
    private Boolean useStatusInsteadOfPurposeCode;

}