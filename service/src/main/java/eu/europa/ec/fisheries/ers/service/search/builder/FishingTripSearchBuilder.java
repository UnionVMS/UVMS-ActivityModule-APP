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
//import static eu.europa.ec.fisheries.ers.service.search.FilterMap.populateFilterMappingsWithChangedDelimitedPeriodTable;

/**
 * Created by sanera on 16/11/2016.
 */
public class FishingTripSearchBuilder extends SearchQueryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FishingTripSearchBuilder.class);
    private static final String FISHING_TRIP_JOIN = "SELECT DISTINCT ft from FishingTripEntity ft JOIN FETCH ft.fishingTripIdentifiers ftripId LEFT JOIN FETCH ft.fishingActivity a LEFT JOIN FETCH a.faReportDocument fa ";


    /**
     *For some usecases we need different database column mappings for same filters.
     * We can call method which sets these specific mappings for certain business requirements while calling constructor     *
     */
   public FishingTripSearchBuilder(){
        super();
        FilterMap filterMap=FilterMap.createFilterMap();
        filterMap.populateFilterMappingsForFilterFishingTrips();
        setFilterMap(filterMap);
    }

    @Override
    public StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {

        LOG.debug("Start building SQL depending upon Filter Criterias");

        StringBuilder sql = new StringBuilder();


        sql.append(FISHING_TRIP_JOIN); // Common Join for all filters

        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
        createWherePartForQuery(sql, query);  // Add Where part associated with Filters
        LOG.info("sql :" + sql);

        return sql;
    }

    // Build Where part of the query based on Filter criterias
    @Override
    public StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Where part of Query");

        sql.append(" where ");
        createWherePartForQueryForFilters(sql, query);

        return sql;
    }

}
