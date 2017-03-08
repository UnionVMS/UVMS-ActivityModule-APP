package eu.europa.ec.fisheries.ers.service.search.builder;

import eu.europa.ec.fisheries.ers.fa.utils.ActivityConstants;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.GroupCriteriaMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import io.jsonwebtoken.lang.Collections;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 01/03/2017.
 */
public class FACatchSearchBuilder_Landing extends FACatchSearchBuilder {

    protected String FA_CATCH_JOIN = " from FaCatchEntity faCatch JOIN faCatch.fishingActivity a " +
            "JOIN "+ FilterMap.AAP_PROCESS_TABLE_ALIAS +" JOIN  " +FilterMap.AAP_PRODUCT_TABLE_ALIAS+
            "  JOIN a.faReportDocument fa  " ;


    protected String SUM_WEIGHT = " SUM(aprod.calculatedWeightMeasure)  " ;

    protected void createJoinPartOfTheQuery(FishingActivityQuery query, StringBuilder sql, Map<GroupCriteria, GroupCriteriaMapper> groupMAppings, List<GroupCriteria> groupByFieldList) {
        // Below is default JOIN for the query
        sql.append(FA_CATCH_JOIN);

        // Create join part of SQL query
        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria

        // Add joins if not added by activity filtering . Below code will add joins required by FA Catch report joins
        for (GroupCriteria criteria : groupByFieldList) {

            GroupCriteriaMapper mapper = groupMAppings.get(criteria);
            if (sql.indexOf(mapper.getTableJoin()) == -1) {
                appendJoinString(sql, mapper.getTableJoin());
            }
        }
    }

    @NotNull
    protected void appendSelectGroupColumns(List<GroupCriteria> groupByFieldList, StringBuilder sql, Map<GroupCriteria, GroupCriteriaMapper> groupMAppings) throws ServiceException {

        if (groupByFieldList == null || Collections.isEmpty(groupByFieldList))
            throw new ServiceException(" No Group information present to aggregate report.");

        // Build SELECT part of query.
        for (GroupCriteria criteria : groupByFieldList) {

            GroupCriteriaMapper mapper = groupMAppings.get(criteria);
            sql.append(mapper.getColumnName());
            sql.append(", ");
        }

        sql.append(SUM_WEIGHT);
    }

    protected void enrichWherePartOFQueryForDISOrDIM(StringBuilder sql){

        sql.append(" and ( a.typeCode ='").append(ActivityConstants.LANDING).append("' and faCatch.typeCode IN ('").append(FaCatchTypeEnum.DEMINIMIS).append("','").append(FaCatchTypeEnum.DISCARDED).append("')) ");
    }



    protected void conditionsForFACatchSummaryReport(StringBuilder sql){
        sql.append(" and ( a.typeCode ='").append(ActivityConstants.LANDING).append("') and faCatch.typeCode NOT IN ('").append(FaCatchTypeEnum.DEMINIMIS).append("','").append(FaCatchTypeEnum.DISCARDED).append("') ");
    }

}
