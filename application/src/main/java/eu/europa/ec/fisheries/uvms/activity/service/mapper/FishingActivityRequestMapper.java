/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ListValueTypeFilter;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SingleValueTypeFilter;
import eu.europa.ec.fisheries.uvms.activity.service.search.FilterMap;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.mapstruct.Mapper;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public abstract class FishingActivityRequestMapper {

    public static FishingActivityQuery buildFishingActivityQueryFromRequest(FishingTripRequest baseRequest) throws ServiceException {
        FishingActivityQuery query = new FishingActivityQuery();
        query.setSearchCriteriaMap(extractFiltersAsMap(baseRequest.getSingleValueFilters()));
        query.setSearchCriteriaMapMultipleValues(extractFiltersAsMapWithMultipleValues(baseRequest.getListValueFilters()));
        return query;
    }

    /**
     * Some search Filters expect only single value. Others support multiple values for search.
     * This method sorts Filter list and separates filters with single values and return the map with its value.
     *
     * @param filterTypes
     * @return Map<SearchFilter   ,   String> Map of SearchFilter and its value
     * @throws ServiceException
     */
    private static Map<SearchFilter, String> extractFiltersAsMap(List<SingleValueTypeFilter> filterTypes) throws ServiceException {
        Set<SearchFilter> filtersWithMultipleValues = FilterMap.getFiltersWhichSupportMultipleValues();
        Map<SearchFilter, String> searchMap = new EnumMap<>(SearchFilter.class);
        for (SingleValueTypeFilter filterType : filterTypes) {
            SearchFilter filter = filterType.getKey();
            if (filtersWithMultipleValues.contains(filter)) {
                throw new ServiceException("Filter provided with Single Value. Application Expects values as List for the Filter :" + filter);
            }
            searchMap.put(filterType.getKey(), filterType.getValue());
        }
        return searchMap;
    }

    /**
     * This method sorts incoming list and separates Filters with multiple values and put it into Map.
     *
     * @param filterTypes List of searchFilters
     * @return Map<SearchFilter   ,   List   <   String>> Map of SearchFilter and list of values for the filter
     * @throws ServiceException
     */
    private static Map<SearchFilter, List<String>> extractFiltersAsMapWithMultipleValues(List<ListValueTypeFilter> filterTypes) throws ServiceException {
        Set<SearchFilter> filtersWithMultipleValues = FilterMap.getFiltersWhichSupportMultipleValues();
        Map<SearchFilter, List<String>> searchMap = new EnumMap<>(SearchFilter.class);
        for (ListValueTypeFilter filterType : filterTypes) {
            SearchFilter filter = filterType.getKey();
            if (!filtersWithMultipleValues.contains(filter)) {
                throw new ServiceException("Filter provided with multiple Values do not support Multiple Values. Filter name is:" + filter);
            }
            searchMap.put(filterType.getKey(), filterType.getValues());
        }
        return searchMap;
    }

}
