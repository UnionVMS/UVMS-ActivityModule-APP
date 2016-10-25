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

package eu.europa.ec.fisheries.ers.service.search;

import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.ers.fa.utils.WeightConversion;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Created by sanera on 28/09/2016.
 */
public class SearchQueryBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SearchQueryBuilder.class);
    private static final String JOIN = " JOIN FETCH ";
    private static final String LEFT = " LEFT ";
    private static final String FISHING_ACTIVITY_JOIN = "SELECT DISTINCT a from FishingActivityEntity a LEFT JOIN FETCH a.faReportDocument fa ";

    private SearchQueryBuilder() {
        super();
    }

    // Create SQL dynamically based on Filter criteria
    public static StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {
        LOG.debug("Start building SQL depending upon Filter Criterias");
        StringBuilder sql = new StringBuilder();
        sql.append(FISHING_ACTIVITY_JOIN); // Common Join for all filters

        // Create join part of SQL query
        SearchQueryBuilder.createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
        SearchQueryBuilder.createWherePartForQuery(sql, query);  // Add Where part associated with Filters
        SearchQueryBuilder.createSortPartForQuery(sql, query); // Add Order by clause for only requested Sort field
        LOG.info("sql :" + sql);

        return sql;
    }

    // Create Table Joins based on Filters provided by user. Avoid joining unnecessary tables
    public static StringBuilder createJoinTablesPartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Join Tables part of Query");
        Map<Filters, FilterDetails> mappings = FilterMap.getFilterMappings();
        // Create join part of SQL query
        if(query.getSearchCriteriaMap() !=null) {
            Set<Filters> keySet = query.getSearchCriteriaMap().keySet();
            for (Filters key : keySet) {
                FilterDetails details = mappings.get(key);
                String joinString = null;
                if (details != null) {
                    joinString = details.getJoinString();
                }
                if (joinString == null || sql.indexOf(joinString) != -1) // If the Table join for the Filter is already present in SQL, do not join the table again
                    continue;

                completeQueryDependingOnKey(sql, key, joinString);
            }
        }
        getJoinPartForSortingOptions(sql, query);

        LOG.debug("Generated SQL for JOIN Part :" + sql);
        return sql;
    }

    private static void completeQueryDependingOnKey(StringBuilder sql, Filters key, String joinString) {
        switch (key) {
            case MASTER:
                if (sql.indexOf(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS) != -1)  // If vesssel table is already joined, use join string accordingly
                    joinString = FilterMap.MASTER_MAPPING;
                appendJoinString(sql, joinString);
                break;
            case VESSEL_IDENTIFIRE:
                tryAppendIfConditionDoesntExist(sql, FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS);
                appendJoinString(sql, joinString);
                break;
            case FROM_ID:
                tryAppendIfConditionDoesntExist(sql, FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
                tryAppendIfConditionDoesntExist(sql, FilterMap.FLUX_PARTY_TABLE_ALIAS); // Add missing join for required table
                appendJoinString(sql, joinString);
                break;
            case FROM_NAME:
                tryAppendIfConditionDoesntExist(sql, FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
                if (sql.indexOf(FilterMap.FLUX_PARTY_TABLE_ALIAS) == -1) // Add missing join for required table
                    appendJoinString(sql, joinString);
                break;
            default:
                appendJoinString(sql, joinString);
                break;
        }
    }

    private static void appendJoinString(StringBuilder sql, String joinString) {
        sql.append(JOIN).append(joinString).append(StringUtils.SPACE);
    }

    /**
     * Add missing join for required table if doesn't already exist in the query;
     *
     * @param sql
     * @param valueToFindAndApply
     */
    private static void tryAppendIfConditionDoesntExist(StringBuilder sql, String valueToFindAndApply) {
        if (sql.indexOf(valueToFindAndApply) == -1) // Add missing join for required table
            sql.append(JOIN).append(valueToFindAndApply);
    }


    // This method makes sure that Table join is present for the Filter for which sorting has been requested.
    private static StringBuilder getJoinPartForSortingOptions(StringBuilder sql, FishingActivityQuery query) {
        SortKey sort = query.getSortKey();
        // IF sorting has been requested and
        if (sort == null) {
            return sql;
        }

        Filters field = sort.getField();

        // Make sure that the field which we want to sort, table Join is present for it.
        switch (getFiledCase(sql, field)) {
            case 1 :
                appendLeftJoin(sql, FilterMap.DELIMITED_PERIOD_TABLE_ALIAS);
                break;
            case 2 :
                appendLeftJoin(sql, FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
                break;
            case 3 :
                if (sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1)
                    appendLeftJoin(sql, FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
                if (sql.indexOf(FilterMap.FLUX_PARTY_TABLE_ALIAS) == -1)
                    appendLeftJoin(sql, FilterMap.FLUX_PARTY_TABLE_ALIAS);
                break;
            default:
                break;
        }

        return sql;
    }

    private static int getFiledCase(StringBuilder sql, Filters field) {
        if (Filters.PERIOD_START.equals(field) || Filters.PERIOD_END.equals(field) && sql.indexOf(FilterMap.DELIMITED_PERIOD_TABLE_ALIAS) == -1) {
            return 1;
        } else if (Filters.PURPOSE.equals(field) && sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1) {
            return 2;
        } else if (Filters.FROM_NAME.equals(field) && (sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1 || sql.indexOf(FilterMap.FLUX_PARTY_TABLE_ALIAS) == -1)) {
            return 3;
        }
        return 0;
    }

    private static void appendLeftJoin(StringBuilder sql, String delimitedPeriodTableAlias) {
        sql.append(LEFT).append(JOIN).append(delimitedPeriodTableAlias);
    }

    // Build Where part of the query based on Filter criterias
    public static StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Where part of Query");
        Map<Filters, FilterDetails> mappings = FilterMap.getFilterMappings();
        sql.append("where ");
        // Create join part of SQL query
        if(query.getSearchCriteriaMap() !=null) {
            Set<Filters> keySet = query.getSearchCriteriaMap().keySet();


            // Create Where part of SQL Query
            int i = 0;
            for (Filters key : keySet) {

                if ( (Filters.QUNTITY_MIN.equals(key) && keySet.contains(Filters.QUNTITY_MAX)) || mappings.get(key) == null) // skip this as MIN and MAX both are required to form where part. Treat it differently
                  continue;

                String mapping = mappings.get(key).getCondition();
                if (i != 0) {
                    sql.append(" and ");
                }

                if(Filters.QUNTITY_MIN.equals(key) ){
                     sql.append("(faCatch.calculatedWeightMeasure >= :").append(FilterMap.QUNTITY_MIN).append(" OR aprod.calculatedWeightMeasure >= :").append(FilterMap.QUNTITY_MIN).append(" )") ;

                }else if (Filters.QUNTITY_MAX.equals(key)) {
                    sql.append(mappings.get(Filters.QUNTITY_MIN).getCondition()).append(" and ").append(mapping);
                    sql.append(" OR (aprod.calculatedWeightMeasure  BETWEEN :").append(FilterMap.QUNTITY_MIN).append(" and :").append(FilterMap.QUNTITY_MAX + ")");
                } else {
                    sql.append(mapping);
                }
                i++;
            }
            sql.append(" and ");
        }
        sql.append("  fa.status = '" + FaReportStatusEnum.NEW.getStatus() + "'"); // get data from only new reports

        LOG.debug("Generated Query After Where :" + sql);
        return sql;
    }

    // Create sorting part for the Query
    public static StringBuilder createSortPartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Sorting part of Query");
        SortKey sort = query.getSortKey();

        if (sort != null) {
            Filters field = sort.getField();
            if (Filters.PERIOD_START.equals(field) || Filters.PERIOD_END.equals(field)) {
                getSqlForStartAndEndDateSorting(sql, field, query);
            }
            sql.append(" order by " + FilterMap.getFilterSortMappings().get(field) + " " + sort.getOrder());
        } else {
            sql.append(" order by fa.acceptedDatetime ASC ");
        }
        LOG.debug("Generated Query After Sort :" + sql);
        return sql;
    }

    // Special treatment for date sorting . In the resultset, One record can have multiple dates. But We need to consider only one date from the list. and then sort that selected date across resultset
    public static StringBuilder getSqlForStartAndEndDateSorting(StringBuilder sql, Filters filter, FishingActivityQuery query) {
        Map<Filters, String> searchCriteriaMap = query.getSearchCriteriaMap();
        sql.append(" and(  ");
        sql.append(FilterMap.getFilterSortMappings().get(filter));
        sql.append(" =(select max(").append(FilterMap.getFilterSortWhereMappings().get(filter)).append(") from a.delimitedPeriods dp1  ");

        if (searchCriteriaMap.containsKey(filter)) {
            sql.append(" where ");
            sql.append(" ( dp1.startDate >= :startDate  OR a.occurence  >= :startDate ) ");
            if (searchCriteriaMap.containsKey(Filters.PERIOD_END)) {
                sql.append(" and  dp1.endDate <= :endDate ");
            }
        }
        sql.append(" ) ");
        sql.append(" OR dp is null ) ");
        return sql;
    }

    public static Double normalizeWeightValue(String value, String weightMeasure) {
        Double valueConverted = Double.parseDouble(value);
        if (WeightConversion.TON.equals(weightMeasure))
            valueConverted = WeightConversion.convertToKiloGram(Double.parseDouble(value), WeightConversion.TON);

        return valueConverted;
    }

}
