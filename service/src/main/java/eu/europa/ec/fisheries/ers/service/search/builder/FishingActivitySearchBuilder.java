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

import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sanera on 16/11/2016.
 */
public class FishingActivitySearchBuilder extends SearchQueryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FishingActivitySearchBuilder.class);
    private static final String FISHING_ACTIVITY_COUNT_JOIN = "SELECT COUNT(DISTINCT a) from FishingActivityEntity a LEFT JOIN FETCH a.faReportDocument fa ";
    private static final String FISHING_ACTIVITY_JOIN = "SELECT DISTINCT a from FishingActivityEntity a LEFT JOIN FETCH a.faReportDocument fa ";


    {
        FilterMap filterMap=FilterMap.createFilterMap();
        filterMap.populateFilterMappings();
        setFilterMap(filterMap);
    }


    /**
     * Creates an SQL String query parsing all the entries found in FishingActivityQuery query parameter.
     *
     * @param query
     * @return
     * @throws ServiceException
     */
    @Override
    public StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {
        LOG.debug("Start building SQL depending upon Filter Criterias");
        StringBuilder sql = new StringBuilder();
        sql.append(FISHING_ACTIVITY_JOIN); // Common Join for all filters
        // Create join part of SQL query
        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
        createWherePartForQuery(sql, query);  // Add Where part associated with Filters
        createSortPartForQuery(sql, query,false); // Add Order by clause for only requested Sort field
        LOG.debug("sql :" + sql);
        return sql;
    }

    public StringBuilder createCountingSql(FishingActivityQuery query) throws ServiceException {
        LOG.debug("Start building SQL depending upon Filter Criterias");
        StringBuilder sql = new StringBuilder();
        sql.append(FISHING_ACTIVITY_COUNT_JOIN); // Common Join for all filters
        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
        createWherePartForQuery(sql, query);  // Add Where part associated with Filters
        LOG.debug("sql :" + sql);
        return sql;
    }

    /**
     * Build Where part of the query based on Filter criterias
     */
    @Override
    public void createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        createWherePartForQueryForFilters(sql, query);
        sql.append(" and a.relatedFishingActivity IS NULL ");
    }
}
