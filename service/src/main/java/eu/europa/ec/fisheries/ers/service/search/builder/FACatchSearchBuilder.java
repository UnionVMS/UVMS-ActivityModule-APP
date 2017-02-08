package eu.europa.ec.fisheries.ers.service.search.builder;

import eu.europa.ec.fisheries.ers.fa.utils.ActivityConstants;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.GroupCriteriaMapper;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FaCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 20/01/2017.
 */
@Slf4j
public class FACatchSearchBuilder extends SearchQueryBuilder {
  //  private static final String FA_CATCH_JOIN = " from FishingActivityEntity a LEFT JOIN FETCH a.faReportDocument fa JOIN FETCH " + FilterMap.FA_CATCH_TABLE_ALIAS;
   // private static final String FA_CATCH_JOIN = " from FishingActivityEntity a LEFT JOIN a.relatedFishingActivity relatedActivity LEFT JOIN relatedActivity.faCatchs relatedfaCatchs JOIN a.faReportDocument fa JOIN  " + FilterMap.FA_CATCH_TABLE_ALIAS;

   // private static final String FA_CATCH_JOIN = " from FaCatchEntity faCatch JOIN faCatch.fishingActivity a LEFT JOIN a.relatedFishingActivity relatedActivity LEFT JOIN relatedActivity.faCatchs relatedfaCatchs JOIN a.faReportDocument fa  " ;
   private static final String FA_CATCH_JOIN = " from FaCatchEntity faCatch JOIN faCatch.fishingActivity a LEFT JOIN a.relatedFishingActivity relatedActivity JOIN a.faReportDocument fa  " ;

    private static final String FISHING_OPERATION="FISHING_OPERATION";


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

     //   augmentJoinForFACatchSummaryReport(sql);

        // Create join part of SQL query
        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria

        for (GroupCriteria criteria : groupByFieldList) {

            GroupCriteriaMapper mapper = groupMAppings.get(criteria);
            if (sql.indexOf(mapper.getTableJoin()) == -1) {
                appendJoinString(sql, mapper.getTableJoin());
            }
        }


        createWherePartForQuery(sql, query);  // Add Where part associated with Filters

        if(groupByFieldList.indexOf(GroupCriteria.CATCH_TYPE)!=-1){
            enrichWherePartOFQueryForDISOrDIM(sql);
        }

      //  conditionsForFACatchSummaryReport(sql);
        sql.append(" and  "
              //  "(a.typeCode ='FISHING_OPERATION')  "
         //        "OR " +
            //    "(a.typeCode='JOINT_FISHING_OPERATION' " +
           //      " and a.relatedFishingActivity.typeCode='RELOCATION' " +
           //     " and  a.relatedFishingActivity.vesselTransportMeans.guid = a.vesselTransportMeans.guid " +
             //   " and  a.relatedFishingActivity.typeCode IN ('ALLOCATED_TO_QUOTA','TAKEN_ON_BOARD','ONBOARD' )" +
              //  " ) "
                );
      /*  sql.append(" faCatch.id IN ( select catch.id from FaCatchEntity catch " +
                  "JOIN catch.fishingActivity activity " +
                " JOIN catch.fishingActivity.relatedFishingActivity relatedActivity where " +
                "  catch.fishingActivity.typeCode='RELOCATION' "  +
                "and catch.fishingActivity.relatedFishingActivity.typeCode='JOINT_FISHING_OPERATION') "
             );*/
        sql.append(" (a.typeCode ='DEPARTURE') OR (a.typeCode ='RELOCATION' and a.relatedFishingActivity.typeCode='JOINT_FISHING_OPERATION') ");

        sql.append(" GROUP BY  ");

        int i = 0;
        for (GroupCriteria criteria : groupByFieldList) {

            if (i != 0)
                sql.append(", ");

            GroupCriteriaMapper mapper = groupMAppings.get(criteria);
            sql.append(mapper.getColumnName());
            i++;
        }


        sql.append(" ORDER BY a.occurence ");
        log.debug("sql :" + sql);

        return sql;
    }

    private void enrichWherePartOFQueryForDISOrDIM(StringBuilder sql){
        sql.append(" and faCatch.typeCode IN ('").append(FaCatchTypeEnum.DEMINIMIS).append("','").append(FaCatchTypeEnum.DISCARDED).append("') ");
    }



    private void conditionsForFACatchSummaryReport(StringBuilder sql){
        sql.append(" and (( a.typeCode ='").append(ActivityConstants.FISHING_OPERATION).append("' and faCatch.typeCode IN ('")
                .append(FaCatchTypeEnum.ONBOARD).append("','").append(FaCatchTypeEnum.KEPT_IN_NET).append("','").append(FaCatchTypeEnum.BY_CATCH).append("'))  ")
                .append(" OR ")
                .append(" ( a.typeCode ='").append(ActivityConstants.JOINT_FISHING_OPERATION).append("' and relatedActivity.typeCode='")
                .append(ActivityConstants.RELOCATION).append("' ").append(" and relatedActivity.vesselTransportMeans.guid = a.vesselTransportMeans.guid and ").append(" relatedfaCatchs.typeCode IN ('")
                .append(FaCatchTypeEnum.ALLOCATED_TO_QUOTA).append("','").append(FaCatchTypeEnum.TAKEN_ON_BOARD).append("','").append(FaCatchTypeEnum.ONBOARD).append("')))");
    }

    private void augmentJoinForFACatchSummaryReport(StringBuilder sql){
        sql.append(StringUtils.SPACE);
        sql.append("LEFT ");
        appendJoinString(sql,"a.relatedFishingActivity relatedActivity ");
        sql.append("LEFT ");
        appendJoinString(sql,"relatedActivity.faCatchs relatedfaCatchs ");
    }

    @Override
    public StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        sql.append(" where ");
        createWherePartForQueryForFilters(sql, query);
        log.debug("Generated Query After Where :" + sql);
        return sql;
    }

}
