package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ListValueTypeFilter;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SingleValueTypeFilter;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class FishingActivityRequestMapper {

    public static FishingActivityRequestMapper INSTANCE = Mappers.getMapper(FishingActivityRequestMapper.class);

    /**
     *
     * @param baseRequest
     * @return
     * @throws ServiceException
     */
    public static FishingActivityQuery buildFishingActivityQueryFromRequest(FACatchSummaryReportRequest baseRequest) throws ServiceException {
        FishingActivityQuery query = new FishingActivityQuery();
        query.setSearchCriteriaMap(extractFiltersAsMap(baseRequest.getSingleValueFilters()));
        query.setSearchCriteriaMapMultipleValues(extractFiltersAsMapWithMultipleValues(baseRequest.getListValueFilters()));
        query.setGroupByFields(baseRequest.getGroupCriterias());
        return query;
    }


    public static FishingActivityQuery buildFishingActivityQueryFromRequest(FishingTripRequest baseRequest) throws ServiceException {
        FishingActivityQuery query = new FishingActivityQuery();
        query.setSearchCriteriaMap(extractFiltersAsMap(baseRequest.getSingleValueFilters()));
        query.setSearchCriteriaMapMultipleValues(extractFiltersAsMapWithMultipleValues(baseRequest.getListValueFilters()));
        return query;
    }

    private static Map<SearchFilter,String> extractFiltersAsMap(List<SingleValueTypeFilter> filterTypes) throws ServiceException {
        Set<SearchFilter> filtersWithMultipleValues = FilterMap.getFiltersWhichSupportMultipleValues();
        Map<SearchFilter,String> searchMap          = new EnumMap<>(SearchFilter.class);

        for(SingleValueTypeFilter filterType : filterTypes) {
            SearchFilter filter = filterType.getKey();
            if (filtersWithMultipleValues.contains(filter)) {
                throw new ServiceException("Filter provided with Single Value. Application Expects values as List for the Filter :" + filter);
            }
            searchMap.put(filterType.getKey(),filterType.getValue());
        }

        return searchMap;
    }

    private static Map<SearchFilter,List<String>>  extractFiltersAsMapWithMultipleValues(List<ListValueTypeFilter> filterTypes) throws ServiceException {
        Set<SearchFilter> filtersWithMultipleValues = FilterMap.getFiltersWhichSupportMultipleValues();
        Map<SearchFilter,List<String>> searchMap = new EnumMap<>(SearchFilter.class);

        for(ListValueTypeFilter filterType : filterTypes){
            SearchFilter filter = filterType.getKey();
            if(!filtersWithMultipleValues.contains(filter)) {
                throw new ServiceException("Filter provided with multiple Values do not support Multiple Values. Filter name is:" + filter);
            }
            searchMap.put(filterType.getKey(),filterType.getValues());
        }
        return searchMap;
    }

}
