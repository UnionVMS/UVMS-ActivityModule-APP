package eu.europa.ec.fisheries.ers.service.search.builder;

import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.GroupCriteriaMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 20/01/2017.
 */
@Slf4j
public class FACatchSearchBuilder extends SearchQueryBuilder {
  //  private static final String FA_CATCH_JOIN = " from FishingActivityEntity a LEFT JOIN FETCH a.faReportDocument fa JOIN FETCH " + FilterMap.FA_CATCH_TABLE_ALIAS;
  private static final String FA_CATCH_JOIN = " from FishingActivityEntity a  JOIN  a.faReportDocument fa JOIN  " + FilterMap.FA_CATCH_TABLE_ALIAS;


    @Override
    public StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {
        StringBuilder sql = new StringBuilder();
        Map<GroupCriteria, GroupCriteriaMapper> groupMAppings = FilterMap.getGroupByMapping();
        sql.append("SELECT  "); // Common Join for all filters
        List<GroupCriteria> groupByFieldList = query.getGroupByFields();
        if (groupByFieldList == null || Collections.isEmpty(groupByFieldList))
            throw new ServiceException(" No Group information present to aggregate report.");

        for (GroupCriteria criteria : groupByFieldList) {

            GroupCriteriaMapper mapper = groupMAppings.get(criteria);
            sql.append(mapper.getColumnName());
            sql.append(", ");
        }
        sql.append(" count(faCatch.id)  ");
        sql.append(FA_CATCH_JOIN);

        // Create join part of SQL query
        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria

        for (GroupCriteria criteria : groupByFieldList) {

            GroupCriteriaMapper mapper = groupMAppings.get(criteria);
            if (sql.indexOf(mapper.getTableJoin()) == -1) {
                appendOnlyJoinString(sql, mapper.getTableJoin());
            }
        }


        createWherePartForQuery(sql, query);  // Add Where part associated with Filters

        sql.append(" GROUP BY  ");

        int i = 0;
        for (GroupCriteria criteria : groupByFieldList) {

            if (i != 0)
                sql.append(", ");

            GroupCriteriaMapper mapper = groupMAppings.get(criteria);
            sql.append(mapper.getColumnName());
            i++;
        }

        log.debug("sql :" + sql);

        return sql;
    }

    @Override
    public StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        sql.append(" where ");
        createWherePartForQueryForFilters(sql, query);
        log.debug("Generated Query After Where :" + sql);
        return sql;
    }

}
