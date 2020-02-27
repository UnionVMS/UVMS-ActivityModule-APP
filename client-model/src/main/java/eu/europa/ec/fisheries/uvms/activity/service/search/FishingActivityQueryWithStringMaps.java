/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries  European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.service.search;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to define Filters to be applied on Fishing Activities
 * It also contains list of grouping Criteria to be applied
 */
public class FishingActivityQueryWithStringMaps {

    private Map<String, String> searchCriteriaMap = new HashMap<>();
    private Map<String, List<String>> searchCriteriaMapMultipleValues = new HashMap<>();
    private SortKey sorting;
    private List<GroupCriteria> groupByFields;
    private Boolean showOnlyLatest;
    private Boolean useStatusInsteadOfPurposeCode;
    private Integer offset;
    private Integer pageSize;

    public FishingActivityQuery convert() {
        FishingActivityQuery fishingActivityQuery = new FishingActivityQuery();
        fishingActivityQuery.setSorting(sorting);
        fishingActivityQuery.setGroupByFields(groupByFields);
        fishingActivityQuery.setShowOnlyLatest(showOnlyLatest);
        fishingActivityQuery.setUseStatusInsteadOfPurposeCode(useStatusInsteadOfPurposeCode);
        fishingActivityQuery.setOffset(offset);
        fishingActivityQuery.setPageSize(pageSize);

        Map<SearchFilter, String> enumSearchCriteriaMap = new HashMap<>();
        searchCriteriaMap.forEach((s, s2) -> enumSearchCriteriaMap.put(SearchFilter.fromValue(s), s2));
        fishingActivityQuery.setSearchCriteriaMap(enumSearchCriteriaMap);

        Map<SearchFilter, List<String>> enumSearchCriteriaMapMultipleValues = new HashMap<>();
        searchCriteriaMapMultipleValues.forEach((s, s2) -> enumSearchCriteriaMapMultipleValues.put(SearchFilter.fromValue(s), s2));
        fishingActivityQuery.setSearchCriteriaMapMultipleValues(enumSearchCriteriaMapMultipleValues);

        return fishingActivityQuery;
    }

    public Map<String, String> getSearchCriteriaMap() {
        return searchCriteriaMap;
    }

    public void setSearchCriteriaMap(Map<String, String> searchCriteriaMap) {
        this.searchCriteriaMap = searchCriteriaMap;
    }

    public Map<String, List<String>> getSearchCriteriaMapMultipleValues() {
        return searchCriteriaMapMultipleValues;
    }

    public void setSearchCriteriaMapMultipleValues(Map<String, List<String>> searchCriteriaMapMultipleValues) {
        this.searchCriteriaMapMultipleValues = searchCriteriaMapMultipleValues;
    }

    public SortKey getSorting() {
        return sorting;
    }

    public void setSorting(SortKey sorting) {
        this.sorting = sorting;
    }

    public List<GroupCriteria> getGroupByFields() {
        return groupByFields;
    }

    public void setGroupByFields(List<GroupCriteria> groupByFields) {
        this.groupByFields = groupByFields;
    }

    public Boolean getShowOnlyLatest() {
        return showOnlyLatest;
    }

    public void setShowOnlyLatest(Boolean showOnlyLatest) {
        this.showOnlyLatest = showOnlyLatest;
    }

    public Boolean getUseStatusInsteadOfPurposeCode() {
        return useStatusInsteadOfPurposeCode;
    }

    public void setUseStatusInsteadOfPurposeCode(Boolean useStatusInsteadOfPurposeCode) {
        this.useStatusInsteadOfPurposeCode = useStatusInsteadOfPurposeCode;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
