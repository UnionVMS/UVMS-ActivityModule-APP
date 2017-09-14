/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.search.builder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import eu.europa.ec.fisheries.ers.fa.utils.WeightConversion;
import eu.europa.ec.fisheries.ers.service.search.FilterDetails;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.SortKey;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.common.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sanera on 28/09/2016.
 */
public abstract class SearchQueryBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SearchQueryBuilder.class);
    protected static final String JOIN_FETCH = " JOIN FETCH ";
    protected static final String LEFT = " LEFT ";
    protected static final String RIGHT = " RIGHT ";
    protected static final String JOIN =  " JOIN ";

    private Map<SearchFilter,String> queryParameterMappings =  FilterMap.getFilterQueryParameterMappings();
    private FilterMap  filterMap = FilterMap.createFilterMap();



    protected SearchQueryBuilder(){

    }


    protected SearchQueryBuilder(FilterMap  filterMap){
      this.filterMap = filterMap;
    }

    // Assumption for the weight is, calculated_weight_measure is in Kg.
    // IF we get WEIGHT MEASURE as TON, we need to convert the input value to Kilograms.
    public static Double normalizeWeightValue(String value, String weightMeasure) {
        Double valueConverted = Double.parseDouble(value);
        if (WeightConversion.TON.equals(weightMeasure)) {
            valueConverted = WeightConversion.convertToKiloGram(Double.parseDouble(value), WeightConversion.TON);
        }
        return valueConverted;
    }

    public FilterMap getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(FilterMap filterMap) {
        this.filterMap = filterMap;
    }

    /**
     * Create SQL dynamically based on Filter criteria
     *
     * @param query
     * @return
     * @throws ServiceException
     */
    public  abstract StringBuilder createSQL(FishingActivityQuery query) throws ServiceException ;

    /**
     * Create Table Joins based on Filters provided by user. Avoid joining unnecessary tables
     *
     * @param sql
     * @param query
     * @return
     */
    public  StringBuilder createJoinTablesPartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Join Tables part of Query");
        Map<SearchFilter, FilterDetails> filterMappings = filterMap.getFilterMappings();
        Set<SearchFilter> keySet = new HashSet<>();
        if (MapUtils.isNotEmpty(query.getSearchCriteriaMap())) {
            keySet.addAll(query.getSearchCriteriaMap().keySet());
        }
        if(MapUtils.isNotEmpty(query.getSearchCriteriaMapMultipleValues())) {
            keySet.addAll(query.getSearchCriteriaMapMultipleValues().keySet());
        }
        for (SearchFilter key : keySet) {
            FilterDetails details = filterMappings.get(key);
            String joinString = null;
            if (details != null) {
                joinString = details.getJoinString();
            }
            if (joinString == null || sql.indexOf(joinString) != -1) {// If the Table join for the Filter is already present in SQL, do not join the table again
                continue;
            }
            completeQueryDependingOnKey(sql, key, joinString);
        }
        getJoinPartForSortingOptions(sql, query);

     //   LOG.debug("Generated SQL for JOIN Part :" + sql);
        return sql;
    }

    private  void completeQueryDependingOnKey(StringBuilder sql, SearchFilter key, String joinString) {
        switch (key) {
            case MASTER:
                if (sql.indexOf(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS) != -1) {  // If vesssel table is already joined, use join string accordingly
                    joinString = FilterMap.MASTER_MAPPING;
                }
                appendJoinFetchString(sql, joinString);
                break;
            case VESSEL_IDENTIFIRE:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS);
                appendJoinString(sql, joinString);
                break;
            case OWNER:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.FLUX_PARTY_TABLE_ALIAS); // Add missing join for required table
                appendJoinFetchString(sql, joinString);
                break;
            case FROM:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.FLUX_REP_MESSAGE_FROM_FA_REP);
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.FLUX_REP_DOC_FROM_MESSAGE);
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.FLUX_PARTY_FOR_MESSAGE);
                appendJoinFetchString(sql, joinString);
                break;
            case AREAS: /* We need to do Right join here as one activity can have multiple areas and in the resultDTO we want to show all the areas for the activity*/
                appendRightJoinString(sql, joinString);
                break;
            case SPECIES: /* We need to do Right join here as one activity can have multiple catches and in the resultDTO we want to show all the species for the activity*/
                appendRightJoinString(sql, joinString);
                break;
            case PERIOD_END:
                appendLeftJoinString(sql, joinString);
                break;
            default:
                appendJoinFetchString(sql, joinString);
                break;
        }
    }

    protected void appendJoinString(StringBuilder sql, String joinString) {
        sql.append(JOIN).append(joinString).append(StringUtils.SPACE);
    }

    protected void appendLeftJoinString(StringBuilder sql, String joinString) {
        sql.append(LEFT).append(JOIN).append(joinString).append(StringUtils.SPACE);
    }

    protected void appendRightJoinString(StringBuilder sql, String joinString) {
        sql.append(RIGHT).append(JOIN).append(joinString).append(StringUtils.SPACE);
    }


    protected  void appendJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(JOIN_FETCH).append(joinString).append(StringUtils.SPACE);
    }

    /**
     * Add missing join for required table if doesn't already exist in the query;
     *
     * @param sql
     * @param valueToFindAndApply
     */
    protected  void appendJoinFetchIfConditionDoesntExist(StringBuilder sql, String valueToFindAndApply) {
        if (sql.indexOf(valueToFindAndApply) == -1) { // Add missing join for required table
            sql.append(JOIN_FETCH).append(valueToFindAndApply);
        }
    }

    /**
     * This method makes sure that Table join is present for the Filter for which sorting has been requested.
     *
     * @param sql
     * @param query
     * @return
     */
    private  StringBuilder getJoinPartForSortingOptions(StringBuilder sql, FishingActivityQuery query) {
        SortKey sort = query.getSorting();
        // IF sorting has been requested and
        if (sort == null) {
            return sql;
        }

        SearchFilter field = sort.getSortBy();

        if(field == null) {
            return sql;
        }

        // Make sure that the field which we want to sort, table Join is present for it.
        switch (getFiledCase(sql, field)) {
            case 1:
                appendLeftJoinFetch(sql, filterMap.DELIMITED_PERIOD_TABLE_ALIAS);
                break;
            case 2:
                appendLeftJoinFetch(sql, FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
                break;
            case 3:
                checkAndAppendIfNeededFluxReportDocTable(sql);
                break;
            default:
                break;
        }

        return sql;
    }

    private void checkAndAppendIfNeededFluxReportDocTable(StringBuilder sql) {
        if (sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1) {
            appendLeftJoinFetch(sql, FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
        }
        if (sql.indexOf(FilterMap.FLUX_PARTY_TABLE_ALIAS) == -1) {
            appendLeftJoinFetch(sql, FilterMap.FLUX_PARTY_TABLE_ALIAS);
        }
    }

    private  int getFiledCase(StringBuilder sql, SearchFilter field) {
        //      if (SearchFilter.PERIOD_START.equals(field) || SearchFilter.PERIOD_END.equals(field) && sql.indexOf(filterMap.DELIMITED_PERIOD_TABLE_ALIAS) == -1) {
        if (SearchFilter.PERIOD_END.equals(field) && sql.indexOf(filterMap.DELIMITED_PERIOD_TABLE_ALIAS) == -1) {
            return 1;
        } else if (SearchFilter.PURPOSE.equals(field) && sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1) {
            return 2;
        }
        return 0;
    }

    protected  void appendLeftJoinFetch(StringBuilder sql, String delimitedPeriodTableAlias) {
        sql.append(LEFT).append(JOIN_FETCH).append(delimitedPeriodTableAlias);
    }

    public StringBuilder createWherePartForQueryForFilters(StringBuilder sql,FishingActivityQuery query){
        Map<SearchFilter, FilterDetails> filterMappings = filterMap.getFilterMappings();
        Set<SearchFilter> keySet = new HashSet<>();
        if(MapUtils.isNotEmpty(query.getSearchCriteriaMap())){
            keySet.addAll(query.getSearchCriteriaMap().keySet());
        }

        if(MapUtils.isNotEmpty(query.getSearchCriteriaMapMultipleValues())){
            keySet.addAll(query.getSearchCriteriaMapMultipleValues().keySet());
        }

        // Create Where part of SQL Query
        int i = 0;
        for (SearchFilter key : keySet) {
            if (!appendWhereQueryPart(sql, filterMappings, keySet, i, key)) {
                continue;
            }
            i++;
        }
        return sql;
    }

    private boolean appendWhereQueryPart(StringBuilder sql, Map<SearchFilter, FilterDetails> filterMappings, Set<SearchFilter> keySet, int i, SearchFilter key) {
        if ((SearchFilter.QUANTITY_MIN.equals(key) && keySet.contains(SearchFilter.QUANTITY_MAX)) ||
                (filterMappings.get(key) == null )) { // skip this as MIN and MAX both are required to form where part. Treat it differently
            return false;
        }

        String mapping = filterMappings.get(key).getCondition();
        if (i != 0) {
            sql.append(" and ");
        }

        if(SearchFilter.QUANTITY_MIN.equals(key) ){
            sql.append("((faCatch.calculatedWeightMeasure >= :").append(FilterMap.QUANTITY_MIN).append(" OR aprod.calculatedWeightMeasure >= :").append(FilterMap.QUANTITY_MIN).append(" ))") ;

        } else if (SearchFilter.QUANTITY_MAX.equals(key)) {
            sql.append(" ( ");
            sql.append(filterMappings.get(SearchFilter.QUANTITY_MIN).getCondition()).append(" and ").append(mapping);
            sql.append(" OR (aprod.calculatedWeightMeasure BETWEEN :").append(FilterMap.QUANTITY_MIN).append(" and :").append(FilterMap.QUANTITY_MAX + ")");
            sql.append(" ) ");
        } else if(SearchFilter.PERIOD_END.equals(key) && !keySet.contains(SearchFilter.PERIOD_START)){
            sql.append(" ( ");
            sql.append(filterMappings.get(SearchFilter.PERIOD_END).getCondition()).append(" OR ");
            sql.append(" a.calculatedStartTime <= :").append(FilterMap.OCCURENCE_END_DATE).append(" ");
            sql.append(" ) ");
        }else {
            sql.append(mapping);
        }
        return true;
    }

    /**
     * Build Where part of the query based on Filter criterias
     * @param sql
     * @param query
     * @return
     */
    public abstract StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) ;

    /**
     * Create sorting part for the Query
     * @param sql
     * @param query
     * @return
     * @throws ServiceException
     */
    public StringBuilder createSortPartForQuery(StringBuilder sql, FishingActivityQuery query) throws ServiceException {
        LOG.debug("Create Sorting part of Query");
        SortKey sort = query.getSorting();
        if (sort != null && sort.getSortBy() !=null) {
            SearchFilter field = sort.getSortBy();
            //  if (SearchFilter.PERIOD_START.equals(field) || SearchFilter.PERIOD_END.equals(field)) {
            if (SearchFilter.PERIOD_END.equals(field)) {
                getSqlForStartAndEndDateSorting(sql, field, query);
            }
            String orderby =" ASC ";
            if(sort.isReversed()) {
                orderby = " DESC ";
            }
            String sortFieldMapping = FilterMap.getFilterSortMappings().get(field);
            if(sortFieldMapping ==null) {
                throw new ServiceException("Information about which database field to be used for sorting is unavailable");
            }
            sql.append(" order by ").append(sortFieldMapping).append(orderby);
        } else {
            sql.append(" order by fa.acceptedDatetime ASC ");
        }
      //  LOG.debug("Generated Query After Sort :" + sql);
        return sql;
    }

    /**
     * Special treatment for date sorting . In the resultset, One record can have multiple dates. But We need to consider only one date from the list. and then sort that selected date across resultset
     *
     * @param sql
     * @param filter
     * @param query
     * @return
     */



    public  StringBuilder getSqlForStartAndEndDateSorting(StringBuilder sql, SearchFilter filter, FishingActivityQuery query) {
        Map<SearchFilter, String> searchCriteriaMap = query.getSearchCriteriaMap();
        if(searchCriteriaMap == null){
            return sql;
        }
        sql.append(" and(  ");
        sql.append(FilterMap.getFilterSortMappings().get(filter));
        sql.append(" =(select max(").append(FilterMap.getFilterSortWhereMappings().get(filter)).append(") from a.delimitedPeriods dp1  ");

        if (searchCriteriaMap.containsKey(filter)) {
            sql.append(" where ");
            sql.append(" ( dp1.startDate >= :startDate  OR a.occurence  >= :startDate ) ");
            if (searchCriteriaMap.containsKey(SearchFilter.PERIOD_END)) {
                sql.append(" and  dp1.endDate <= :endDate ");
            }
        }
        sql.append(" ) ");
        sql.append(" OR dp is null ) ");
        return sql;
    }

    public Query fillInValuesForTypedQuery(FishingActivityQuery query, Query typedQuery) throws ServiceException {
        Map<SearchFilter, String> searchCriteriaMap             = query.getSearchCriteriaMap();
        Map<SearchFilter, List<String>> searchForMultipleValues = query.getSearchCriteriaMapMultipleValues();
        if (MapUtils.isNotEmpty(searchCriteriaMap)) {
            applySingleValuesToQuery(searchCriteriaMap, typedQuery);
        }
        if(MapUtils.isNotEmpty(searchForMultipleValues)) {
            applyListValuesToQuery(searchForMultipleValues, typedQuery);
        }
        return typedQuery;
    }


    private void applySingleValuesToQuery(Map<SearchFilter, String> searchCriteriaMap, Query typedQuery) throws ServiceException {

        // Assign values to created SQL Query
        for (Map.Entry<SearchFilter, String> entry : searchCriteriaMap.entrySet()){

            SearchFilter key =  entry.getKey();
            String value     =  entry.getValue();

            //For WeightMeasure there is no mapping present, In that case
            if(queryParameterMappings.get(key) == null) {
                continue;
            }

            if(StringUtils.isEmpty(value)) {
                throw new ServiceException("Value for filter " + key + " is null or empty");
            }

            applyValueDependingOnKey(searchCriteriaMap, typedQuery, key, value);

        }
    }

    private void applyValueDependingOnKey(Map<SearchFilter, String> searchCriteriaMap, Query typedQuery, SearchFilter key, String value) throws ServiceException {
        switch (key) {
            case PERIOD_START:
                typedQuery.setParameter(queryParameterMappings.get(key), DateUtils.parseToUTCDate(value, DateUtils.DATE_TIME_UI_FORMAT));
                break;
            case PERIOD_END:
                typedQuery.setParameter(queryParameterMappings.get(key), DateUtils.parseToUTCDate(value, DateUtils.DATE_TIME_UI_FORMAT));
                break;
            case QUANTITY_MIN:
                typedQuery.setParameter(queryParameterMappings.get(key), SearchQueryBuilder.normalizeWeightValue(value,searchCriteriaMap.get(SearchFilter.WEIGHT_MEASURE)));
                break;
            case QUANTITY_MAX:
                typedQuery.setParameter(queryParameterMappings.get(key), SearchQueryBuilder.normalizeWeightValue(value,searchCriteriaMap.get(SearchFilter.WEIGHT_MEASURE)));
                break;
            case MASTER:
                typedQuery.setParameter(queryParameterMappings.get(key), value.toUpperCase());
                break;
            case FA_REPORT_ID:
                typedQuery.setParameter(queryParameterMappings.get(key), Integer.parseInt(value));
                break;
        
            case AREA_GEOM:
                Geometry geom;
                try {
                    geom = GeometryMapper.INSTANCE.wktToGeometry(value).getValue();
                    geom.setSRID(GeometryUtils.DEFAULT_EPSG_SRID);
                } catch (ParseException e) {
                    throw new ServiceException(e.getMessage(), e);
                }
                typedQuery.setParameter(queryParameterMappings.get(key), geom);
                break;
            default:
                typedQuery.setParameter(queryParameterMappings.get(key), value);
                break;
        }
    }


    /**
     * Applies the values stored in the searchCriteriaMapMultipleValues map to the typedQuery
     *
     * @param searchCriteriaMap
     * @param typedQuery
     * @return
     * @throws ServiceException
     */
    private void applyListValuesToQuery(Map<SearchFilter,List<String>> searchCriteriaMap, Query typedQuery) throws ServiceException {

        // Assign values to created SQL Query
        for (Map.Entry<SearchFilter,List<String>> entry : searchCriteriaMap.entrySet()){

            SearchFilter key =  entry.getKey();
            List<String> valueList=  entry.getValue();
            //For WeightMeasure there is no mapping present, In that case
            if(queryParameterMappings.get(key) ==null) {
                continue;
            }

            if(valueList ==null || valueList.isEmpty()) {
                throw new ServiceException("valueList for filter " + key + " is null or empty");
            }

            switch(key){

                case MASTER:
                    List<String> uppperCaseValList=new ArrayList<>();
                    for(String val:valueList){
                        uppperCaseValList.add(val.toUpperCase());
                    }
                    typedQuery.setParameter(queryParameterMappings.get(key), uppperCaseValList);
                    break;
                default:
                    typedQuery.setParameter(queryParameterMappings.get(key), valueList);
                    break;
            }


        }
    }
}
