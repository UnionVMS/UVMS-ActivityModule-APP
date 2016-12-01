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
  //  public static final FishingActivitySearch INSTANCE = new FishingActivitySearch();
    private static final Logger LOG = LoggerFactory.getLogger(FishingActivitySearch.class);
    private static final String FISHING_ACTIVITY_JOIN = "SELECT DISTINCT a from FishingActivityEntity a LEFT JOIN FETCH a.faReportDocument fa ";

    public   StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {
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

    // Build Where part of the query based on Filter criterias
    public  StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Where part of Query");

   //     sql.append(" where ");
        sql.append(" where intersects(fa.geom, :area) = true and "); // fa is alias for FaReportDocument, fa must be defined in main query
        createWherePartForQueryForFilters(sql,query);

        if((query.getSearchCriteriaMap() !=null && !query.getSearchCriteriaMap().isEmpty())
                || (query.getSearchCriteriaMapMultipleValues() !=null && !query.getSearchCriteriaMapMultipleValues().isEmpty()))
                    sql.append(" and ");


        sql.append("  fa.status = '" + FaReportStatusEnum.NEW.getStatus() + "'"); // get data from only new reports

        LOG.debug("Generated Query After Where :" + sql);
        return sql;
    }

    public Query getTypedQueryForFishingActivityFilter(StringBuilder sql, FishingActivityQuery query, Geometry multipolygon, Query typedQuery){



        LOG.info("Area intersection is the minimum default condition to find the fishing activities");
        typedQuery.setParameter("area", multipolygon); // parameter name area is specified in create SQL

 //       if(query.getSearchCriteriaMap() ==null) {
   //         return typedQuery;
     //   }
        return fillInValuesForTypedQuery(query,typedQuery);



    }

}
