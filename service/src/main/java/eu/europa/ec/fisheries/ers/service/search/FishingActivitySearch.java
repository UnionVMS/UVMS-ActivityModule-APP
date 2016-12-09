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

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;

/**
 * Created by sanera on 16/11/2016.
 */
public class FishingActivitySearch extends SearchQueryBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(FishingActivitySearch.class);
    private static final String FISHING_ACTIVITY_JOIN = "SELECT DISTINCT a from FishingActivityEntity a LEFT JOIN FETCH a.faReportDocument fa ";

    public StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {
        LOG.debug("Start building SQL depending upon Filter Criterias");
        StringBuilder sql = new StringBuilder();

        sql.append(FISHING_ACTIVITY_JOIN); // Common Join for all filters

        // Create join part of SQL query
        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
        createWherePartForQuery(sql, query);  // Add Where part associated with Filters
        createSortPartForQuery(sql, query); // Add Order by clause for only requested Sort field
        LOG.info("sql :" + sql);

        return sql;
    }

    /**
     * Build Where part of the query based on Filter criterias
     */
    public StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Where part of Query");

        sql.append(" where intersects(fa.geom, :area) = true and "); // fa is alias for FaReportDocument, fa must be defined in main query
        createWherePartForQueryForFilters(sql,query);

        if((query.getSearchCriteriaMap() !=null && !query.getSearchCriteriaMap().isEmpty())
                || (query.getSearchCriteriaMapMultipleValues() !=null && !query.getSearchCriteriaMapMultipleValues().isEmpty()))
                    sql.append(" and ");
        sql.append("  fa.status = '" + FaReportStatusEnum.NEW.getStatus() + "'"); // get data from only new reports
        LOG.debug("Generated Query After Where :" + sql);
        return sql;
    }

    public Query getTypedQueryForFishingActivityFilter(StringBuilder sql, FishingActivityQuery query, Geometry multipolygon, Query typedQuery) throws ServiceException {
        LOG.debug("Area intersection is the minimum default condition to find the fishing activities");
        typedQuery.setParameter("area", multipolygon); // parameter name area is specified in create SQL
        return fillInValuesForTypedQuery(query,typedQuery);
    }
}
