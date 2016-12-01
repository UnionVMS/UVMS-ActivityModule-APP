package eu.europa.ec.fisheries.ers.service.search;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sanera on 16/11/2016.
 */
public class FishingTripSearch extends SearchQueryBuilder{
   // public static final FishingTripSearch INSTANCE = new FishingTripSearch();

    private static final Logger LOG = LoggerFactory.getLogger(FishingActivitySearch.class);
    private static final String FISHING_TRIP_JOIN = "SELECT DISTINCT ft from FishingTripEntity ft LEFT JOIN FETCH ft.fishingActivity a LEFT JOIN FETCH a.faReportDocument fa ";

    public   StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {
        LOG.debug("Start building SQL depending upon Filter Criterias");
        StringBuilder sql = new StringBuilder();


        sql.append(FISHING_TRIP_JOIN); // Common Join for all filters

        // Create join part of SQL query
         createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
         createWherePartForQuery(sql, query);  // Add Where part associated with Filters
      //  SearchQueryBuilder.createSortPartForQuery(sql, query); // Add Order by clause for only requested Sort field
        LOG.info("sql :" + sql);

        return sql;
    }

    // Build Where part of the query based on Filter criterias
    public  StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Where part of Query");

        sql.append(" where ");

        createWherePartForQueryForFilters(sql,query);

       LOG.debug("Generated Query After Where :" + sql);
        return sql;
    }




}
