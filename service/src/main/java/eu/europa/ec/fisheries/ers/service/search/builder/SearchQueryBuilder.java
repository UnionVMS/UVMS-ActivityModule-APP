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

import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import eu.europa.ec.fisheries.ers.service.search.FilterDetails;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.SortKey;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter.PURPOSE;

public abstract class SearchQueryBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SearchQueryBuilder.class);
    private static final String JOIN_FETCH = " JOIN FETCH ";
    static final String LEFT = " LEFT ";
    private static final String RIGHT = " RIGHT ";
    protected static final String JOIN = " JOIN ";

    private Map<SearchFilter, String> queryParameterMappings = FilterMap.getFilterQueryParameterMappings();
    private FilterMap filterMap = FilterMap.createFilterMap();

    // Assumption for the weight is, calculated_weight_measure is in Kg.
    // IF we get WEIGHT MEASURE as TON, we need to convert the input value to Kilograms.
    private static Double normalizeWeightValue(String value, String weightMeasure) {
        Double valueConverted = Double.parseDouble(value);
        if ("TNE".equals(weightMeasure)) {
            valueConverted = 1000D * valueConverted;
        }
        return valueConverted;
    }

    public FilterMap getFilterMap() {
        return filterMap;
    }

    void setFilterMap(FilterMap filterMap) {
        this.filterMap = filterMap;
    }

    /**
     * Create SQL dynamically based on Filter criteria
     *
     * @param query
     * @return
     * @throws ServiceException
     */
    public abstract StringBuilder createSQL(FishingActivityQuery query) throws ServiceException;

    /**
     * Create Table Joins based on Filters provided by user. Avoid joining unnecessary tables
     *
     * @param sql
     * @param query
     * @return
     */
    void createJoinTablesPartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Join Tables part of Query");
        Map<SearchFilter, FilterDetails> filterMappings = filterMap.getFilterMappings();
        Set<SearchFilter> keySet = new HashSet<>();
        if (MapUtils.isNotEmpty(query.getSearchCriteriaMap())) {
            keySet.addAll(query.getSearchCriteriaMap().keySet());
        }
        if (MapUtils.isNotEmpty(query.getSearchCriteriaMapMultipleValues())) {
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
    }

    private void completeQueryDependingOnKey(StringBuilder sql, SearchFilter key, String joinString) {
        switch (key) {
            case MASTER:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS);
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.CONTACT_PARTY_TABLE_ALIAS);
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
            case FLUX_FA_REPORT_ID:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
                appendJoinString(sql, joinString);
                break;
            case AREAS: /* We need to do Right join here as one activity can have multiple areas and in the resultDTO we want to show all the areas for the activity*/
                appendRightJoinString(sql, joinString);
                break;
            case SPECIES: /* We need to do Right join here as one activity can have multiple catches and in the resultDTO we want to show all the species for the activity*/
                appendRightJoinString(sql, joinString);
                break;
            case PERIOD_END:
                appendLeftJoinFetchString(sql, joinString);
                break;
            case CONTACT_ROLE_CODE:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS);
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.CONTACT_PARTY_TABLE_ALIAS);
                appendJoinFetchString(sql, joinString);
                break;
            default:
                appendJoinFetchString(sql, joinString);
                break;
        }
    }

    void appendJoinString(StringBuilder sql, String joinString) {
        sql.append(JOIN).append(joinString).append(StringUtils.SPACE);
    }

    protected void appendLeftJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(LEFT).append(JOIN_FETCH).append(joinString).append(StringUtils.SPACE);
    }

    private void appendRightJoinString(StringBuilder sql, String joinString) {
        sql.append(RIGHT).append(JOIN).append(joinString).append(StringUtils.SPACE);
    }

    protected void appendJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(JOIN_FETCH).append(joinString).append(StringUtils.SPACE);
    }

    /**
     * Add missing join for required table if doesn't already exist in the query;
     *
     * @param sql
     * @param valueToFindAndApply
     */
    protected void appendJoinFetchIfConditionDoesntExist(StringBuilder sql, String valueToFindAndApply) {
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
    private StringBuilder getJoinPartForSortingOptions(StringBuilder sql, FishingActivityQuery query) {
        SortKey sort = query.getSorting();
        // IF sorting has been requested and
        if (sort == null) {
            return sql;
        }
        SearchFilter field = sort.getSortBy();
        if (field == null) {
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
            case 9:
                appendLeftJoin(sql,filterMap.DELIMITED_PERIOD_TABLE_ALIAS );
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

    private int getFiledCase(StringBuilder sql, SearchFilter field) {
        if (SearchFilter.PERIOD_END.equals(field) && sql.indexOf(filterMap.DELIMITED_PERIOD_TABLE_ALIAS) == -1) {
            return 1;
        } else if (PURPOSE.equals(field) && sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1) {
            return 2;
        } else if (SearchFilter.PERIOD_END_TRIP.equals(field)){
            return 9;
        }

        return 0;
    }

    protected void appendLeftJoinFetch(StringBuilder sql, String delimitedPeriodTableAlias) {
        sql.append(LEFT).append(JOIN_FETCH).append(delimitedPeriodTableAlias);
    }

    protected void appendLeftJoin(StringBuilder sql, String delimitedPeriodTableAlias) {
        sql.append(LEFT).append(JOIN).append(delimitedPeriodTableAlias);
    }

    void createWherePartForQueryForFilters(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("[INFO] Creating Where part of Query");
        sql.append(" where ");
        Map<SearchFilter, FilterDetails> filterMappings = filterMap.getFilterMappings();
        Set<SearchFilter> keySet = new HashSet<>();
        if (MapUtils.isNotEmpty(query.getSearchCriteriaMap())) {
            keySet.addAll(query.getSearchCriteriaMap().keySet());
        }
        if (MapUtils.isNotEmpty(query.getSearchCriteriaMapMultipleValues())) {
            keySet.addAll(query.getSearchCriteriaMapMultipleValues().keySet());
        }
        // Create Where part of SQL Query
        if(query.getShowOnlyLatest() != null){
            sql.append(" a.latest=:latest ").append(" and ");
        }
        int i = 0;
        for (SearchFilter key : keySet) {
            if (!appendWhereQueryPart(sql, filterMappings, keySet, i, key)) {
                continue;
            }
            i++;
        }
        LOG.debug("[INFO] Generated Query After Where :" + sql);
    }

    private boolean appendWhereQueryPart(StringBuilder sql, Map<SearchFilter, FilterDetails> filterMappings, Set<SearchFilter> keySet, int i, SearchFilter key) {
        if ((SearchFilter.QUANTITY_MIN.equals(key) && keySet.contains(SearchFilter.QUANTITY_MAX)) || (filterMappings.get(key) == null)) { // skip this as MIN and MAX both are required to form where part. Treat it differently
            return false;
        }
        String mapping = filterMappings.get(key).getCondition();
        if (i != 0) {
            sql.append(" and ");
        }
        if (SearchFilter.QUANTITY_MIN.equals(key)) {
            sql.append("((faCatch.calculatedWeightMeasure >= :").append(FilterMap.QUANTITY_MIN).append(" OR aprod.calculatedWeightMeasure >= :")
                    .append(FilterMap.QUANTITY_MIN).append(" ))");

        } else if (SearchFilter.QUANTITY_MAX.equals(key)) {
            sql.append(" ( ");
            sql.append(filterMappings.get(SearchFilter.QUANTITY_MIN).getCondition()).append(" and ").append(mapping);
            sql.append(" OR (aprod.calculatedWeightMeasure BETWEEN :").append(FilterMap.QUANTITY_MIN).append(" and :").append(FilterMap.QUANTITY_MAX + ")");
            sql.append(" ) ");
        } else {
            sql.append(mapping);
        }
        return true;
    }

    /**
     * Build Where part of the query based on Filter criterias
     *
     * @param sql
     * @param query
     * @return
     */
    public abstract void createWherePartForQuery(StringBuilder sql, FishingActivityQuery query);

    /**
     * Create sorting part for the Query
     *
     * @param sql
     * @param query
     * @return
     * @throws ServiceException
     */
    void createSortPartForQuery(StringBuilder sql, FishingActivityQuery query, boolean isActivityTrip) throws ServiceException {
        LOG.debug("Create Sorting part of Query");
        SortKey sort = query.getSorting();
        if (sort != null && sort.getSortBy() != null) {
            SearchFilter field = sort.getSortBy();
            if (SearchFilter.PERIOD_END.equals(field)) {
                getSqlForStartAndEndDateSorting(sql, field, query);
            }
            if(SearchFilter.PERIOD_END_TRIP.equals(field)){
                getMaxEndDate(sql, field, query);
            }
            StringBuilder orderBy =new StringBuilder();
            if(isActivityTrip){
                orderBy.append( " timeout ");
            }
            if (sort.isReversed()) {
                orderBy.append(" DESC NULLS LAST");
            } else {
                orderBy.append(" ASC ");
            }
            String sortFieldMapping = FilterMap.getFilterSortMappings().get(field);
            if (sortFieldMapping == null) {
                throw new ServiceException("Information about which database field to be used for sorting is unavailable");
            }
            sql.append(" order by ");
            if(isActivityTrip){
                sql.append(orderBy);
            } else{
                sql.append(sortFieldMapping);
            }
        } else {
            sql.append(" order by fa.acceptedDatetime ASC ");
        }
    }
    private void getMaxEndDate(StringBuilder sql, SearchFilter filter, FishingActivityQuery query) {
        Map<SearchFilter, String> searchCriteriaMap = query.getSearchCriteriaMap();
        if (searchCriteriaMap == null) {
            return;
        }
        sql.append(" and a.typeCode = 'ARRIVAL'  ");
    }

    /**
     * Special treatment for date sorting . In the resultset, One record can have multiple dates. But We need to consider only one date from the list. and then sort that selected date across resultset
     *
     * @param sql
     * @param filter
     * @param query
     * @return
     */
    private void getSqlForStartAndEndDateSorting(StringBuilder sql, SearchFilter filter, FishingActivityQuery query) {
        Map<SearchFilter, String> searchCriteriaMap = query.getSearchCriteriaMap();
        if (searchCriteriaMap == null) {
            return;
        }
        sql.append(" and(  ");
        sql.append(FilterMap.getFilterSortMappings().get(filter));
        sql.append(" =(select max(").append(FilterMap.getFilterSortWhereMappings().get(filter)).append(") from a.delimitedPeriods dp1  ");
        sql.append(" ) ");
        sql.append(" OR dp is null ) ");
    }

    public Query fillInValuesForTypedQuery(FishingActivityQuery query, Query typedQuery) throws ServiceException {
        Map<SearchFilter, String> searchCriteriaMap = query.getSearchCriteriaMap();
        Map<SearchFilter, List<String>> searchForMultipleValues = query.getSearchCriteriaMapMultipleValues();
        if (MapUtils.isNotEmpty(searchCriteriaMap)) {
            applySingleValuesToQuery(searchCriteriaMap, typedQuery);
        }
        if (MapUtils.isNotEmpty(searchForMultipleValues)) {
            applyListValuesToQuery(searchForMultipleValues, typedQuery);
        }
        if(query.getShowOnlyLatest() != null){
            typedQuery.setParameter("latest",query.getShowOnlyLatest());
        }
        return typedQuery;
    }

    private void applySingleValuesToQuery(Map<SearchFilter, String> searchCriteriaMap, Query typedQuery) throws ServiceException {
        // Assign values to created SQL Query
        for (Map.Entry<SearchFilter, String> entry : searchCriteriaMap.entrySet()) {
            SearchFilter key = entry.getKey();
            String value = entry.getValue();
            //For WeightMeasure there is no mapping present, In that case
            if (queryParameterMappings.get(key) == null) {
                continue;
            }
            if (StringUtils.isEmpty(value)) {
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
                typedQuery.setParameter(queryParameterMappings.get(key), SearchQueryBuilder.normalizeWeightValue(value, searchCriteriaMap.get(SearchFilter.WEIGHT_MEASURE)));
                break;
            case QUANTITY_MAX:
                typedQuery.setParameter(queryParameterMappings.get(key), SearchQueryBuilder.normalizeWeightValue(value, searchCriteriaMap.get(SearchFilter.WEIGHT_MEASURE)));
                break;
            case MASTER:
            case SPECIES:
            case PORT:
            case OWNER:
                typedQuery.setParameter(queryParameterMappings.get(key), value.toUpperCase());
                break;
            case FA_REPORT_ID:
                typedQuery.setParameter(queryParameterMappings.get(key), Integer.parseInt(value));
                break;
            case FLUX_FA_REPORT_ID:
                typedQuery.setParameter(queryParameterMappings.get(key), value);
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
    private void applyListValuesToQuery(Map<SearchFilter, List<String>> searchCriteriaMap, Query typedQuery) throws ServiceException {
        // Assign values to created SQL Query
        for (Map.Entry<SearchFilter, List<String>> entry : searchCriteriaMap.entrySet()) {

            SearchFilter key = entry.getKey();
            List<String> valueList = entry.getValue();
            //For WeightMeasure there is no mapping present, In that case
            if (queryParameterMappings.get(key) == null) {
                continue;
            }
            if (valueList == null || valueList.isEmpty()) {
                throw new ServiceException("valueList for filter " + key + " is null or empty");
            }
            switch (key) {
                case MASTER:
                case SPECIES:
                case PORT:
                case OWNER:
                    List<String> uppperCaseValList = valueList.stream().map(String::toUpperCase).collect(Collectors.toList());
                    typedQuery.setParameter(queryParameterMappings.get(key), uppperCaseValList);
                    break;
                default:
                    typedQuery.setParameter(queryParameterMappings.get(key), valueList);
                    break;
            }
        }
    }
}
