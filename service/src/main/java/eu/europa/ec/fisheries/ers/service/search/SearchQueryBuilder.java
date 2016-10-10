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
    private static final String FISHING_ACTIVITY_JOIN = "SELECT DISTINCT a from FishingActivityEntity a JOIN FETCH a.faReportDocument fa ";

    private SearchQueryBuilder() {

    }

    // Create SQL dynamically based on Filter criteria
    public static StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {
        LOG.debug("Start building SQL depending upon Filter Criterias");
        StringBuilder sql = new StringBuilder();
        sql.append(FISHING_ACTIVITY_JOIN);

        // Create join part of SQL query
        SearchQueryBuilder.createJoinTablesPartForQuery(sql, query);
        SearchQueryBuilder.createWherePartForQuery(sql, query);
        SearchQueryBuilder.createSortPartForQuery(sql, query);
        LOG.info("sql :" + sql);

        return sql;
    }

    // Create Table Joins based on Filters provided by user. Avoid joining unnecessary tables
    public static StringBuilder createJoinTablesPartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Join Tables part of Query");
        Map<Filters, FilterDetails> mappings = FilterMap.getFilterMappings();
        // Create join part of SQL query
        Set<Filters> keySet = query.getSearchCriteriaMap().keySet();
        for (Filters key : keySet) {
            FilterDetails details = mappings.get(key);
            if (details == null)
                continue;
            String joinString = details.getJoinString();


            if (sql.indexOf(joinString) != -1)
                continue;

            switch (key) {
                case MASTER:
                    if (sql.indexOf(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS) != -1)
                        joinString = FilterMap.MASTER_MAPPING;

                    sql.append(JOIN).append(joinString).append(" ");
                    break;
                case VESSEL_IDENTIFIRE:
                    if (sql.indexOf(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS) == -1)
                        sql.append(JOIN).append(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS);
                    sql.append(JOIN).append(joinString).append(" ");
                    break;
                case FROM_ID:
                    if (sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1)
                        sql.append(JOIN).append(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
                    if (sql.indexOf(FilterMap.FLUX_PARTY_TABLE_ALIAS) == -1)
                        sql.append(JOIN).append(FilterMap.FLUX_PARTY_TABLE_ALIAS);
                    sql.append(JOIN).append(joinString).append(" ");

                    break;
                case FROM_NAME:
                    if (sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1)
                        sql.append(JOIN).append(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
                    if (sql.indexOf(FilterMap.FLUX_PARTY_TABLE_ALIAS) == -1)
                        sql.append(JOIN).append(joinString).append(" ");
                    break;

                default:
                    sql.append(JOIN).append(joinString).append(" ");
                    break;
            }

        }

        getJoinPartForSortingOptions(sql, query);

        LOG.debug("Generated SQL for JOIN Part :" + sql);
        return sql;
    }

    private static StringBuilder getJoinPartForSortingOptions(StringBuilder sql, FishingActivityQuery query) {
        SortKey sort = query.getSortKey();
        // IF sorting has been requested and
        if (sort != null) {
            Filters field = sort.getField();
            if (Filters.PERIOD_START.equals(field) || Filters.PERIOD_END.equals(field) && sql.indexOf(FilterMap.DELIMITED_PERIOD_TABLE_ALIAS) == -1) {
                sql.append(LEFT).append(JOIN).append(FilterMap.DELIMITED_PERIOD_TABLE_ALIAS);
            } else if (Filters.PURPOSE.equals(field) && sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1) {
                sql.append(LEFT).append(JOIN).append(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
            } else if (Filters.FROM_NAME.equals(field) && (sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1 || sql.indexOf(FilterMap.FLUX_PARTY_TABLE_ALIAS) == -1)) {
                if (sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1)
                    sql.append(LEFT).append(JOIN).append(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
                if (sql.indexOf(FilterMap.FLUX_PARTY_TABLE_ALIAS) == -1)
                    sql.append(LEFT).append(JOIN).append(FilterMap.FLUX_PARTY_TABLE_ALIAS);
            }
        }
        return sql;
    }

    public static StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Where part of Query");
        Map<Filters, FilterDetails> mappings = FilterMap.getFilterMappings();
        // Create join part of SQL query
        Set<Filters> keySet = query.getSearchCriteriaMap().keySet();

        sql.append("where ");
        // Create Where part of SQL Query
        int i = 0;
        for (Filters key : keySet) {

            if (Filters.QUNTITY_MIN.equals(key) || mappings.get(key) == null)
                continue;

            String mapping = mappings.get(key).getCondition();
            if (i != 0) {
                sql.append(" and ");
            }

            if (Filters.QUNTITY_MAX.equals(key)) {
                sql.append(mappings.get(Filters.QUNTITY_MIN).getCondition()).append(" and ").append(mapping);
                sql.append(" OR (aprod.calculated_weight_measure  BETWEEN :").append(FilterMap.QUNTITY_MIN).append(" and :").append(FilterMap.QUNTITY_MAX + ")");
            } else {
                sql.append(mapping);
            }
            i++;
        }

        sql.append(" and fa.status = '" + FaReportStatusEnum.NEW.getStatus() + "'");

        LOG.debug("Generated Query After Where :" + sql);
        return sql;
    }

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
