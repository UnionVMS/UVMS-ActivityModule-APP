/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.search.builder;

import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.GroupCriteriaMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 20/01/2017.
 */
@Slf4j
public class FACatchSearchBuilder extends SearchQueryBuilder {

  protected String FA_CATCH_JOIN = " from FaCatchEntity faCatch JOIN faCatch.fishingActivity a " +
          "LEFT JOIN a.relatedFishingActivity relatedActivity  " +
           "  JOIN a.faReportDocument fa  " ;

    protected String SUM_WEIGHT = " SUM(faCatch.calculatedWeightMeasure)  " ;

   public FACatchSearchBuilder() {
        super();
        FilterMap filterMap=FilterMap.createFilterMap();
        filterMap.populateFilterMAppingsWithChangeForFACatchReport();
        setFilterMap(filterMap);
    }


    @Override
    /**
     * Remember that not all the catches should be considered for the catchSummary. There are specific conditions mentioned in the requirement. Only those catches should be selected for the summary which match the criteria.
     */
    public StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {
        StringBuilder sql = new StringBuilder();
     //   FilterMap.populateFilterMAppingsWithChangeForFACatchReport();
        Map<GroupCriteria, GroupCriteriaMapper> groupMappings = FilterMap.getGroupByMapping();
        List<GroupCriteria> groupByFieldList= query.getGroupByFields();
        sql.append("SELECT  "); // Common Join for all filters

        appendSelectGroupColumns(groupByFieldList, sql, groupMappings);

        createJoinPartOfTheQuery(query, sql, groupMappings, groupByFieldList);

        createWherePartOfQuery(query, sql, groupByFieldList);

        createGroupByPartOfTheQuery(sql, groupMappings, groupByFieldList);

        log.debug("sql :" + sql);

        return sql;
    }

    protected void createGroupByPartOfTheQuery(StringBuilder sql, Map<GroupCriteria, GroupCriteriaMapper> groupMappings, List<GroupCriteria> groupByFieldList) {
        sql.append(" GROUP BY  ");

        // Add group by statement based on grouping factors
        int i = 0;
        for (GroupCriteria criteria : groupByFieldList) {

            if (i != 0)
                sql.append(", ");

            GroupCriteriaMapper mapper = groupMappings.get(criteria);
            sql.append(mapper.getColumnName());
            i++;
        }
    }

    protected void createWherePartOfQuery(FishingActivityQuery query, StringBuilder sql, List<GroupCriteria> groupByFieldList) {
        createWherePartForQuery(sql, query);  // Add Where part associated with Filters

        if(groupByFieldList.indexOf(GroupCriteria.CATCH_TYPE)!=-1){
              enrichWherePartOFQueryForDISOrDIM(sql);
          }else{
              conditionsForFACatchSummaryReport(sql);
          }
    }

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

    /**
     *  Special condition to get data for DIM/DIS
     * @param sql
     */
    protected void enrichWherePartOFQueryForDISOrDIM(StringBuilder sql){

        sql.append(" and ( a.typeCode ='").append(FishingActivityTypeEnum.FISHING_OPERATION.toString()).append("' and faCatch.typeCode IN ('").append(FaCatchTypeEnum.DEMINIMIS).append("','").append(FaCatchTypeEnum.DISCARDED).append("')) ");
    }



    protected void conditionsForFACatchSummaryReport(StringBuilder sql){
        sql.append(" and (" +
                "(a.typeCode ='").append(FishingActivityTypeEnum.FISHING_OPERATION.toString()).append("' and faCatch.typeCode IN('") .append(FaCatchTypeEnum.ONBOARD)
                 .append("','").append(FaCatchTypeEnum.KEPT_IN_NET).append("','").append(FaCatchTypeEnum.BY_CATCH).append("'))  OR (a.typeCode ='").append(FishingActivityTypeEnum.RELOCATION.toString())
                .append("' and a.relatedFishingActivity.typeCode='").append(FishingActivityTypeEnum.JOINED_FISHING_OPERATION.toString()).append("' and a.vesselTransportGuid = a.relatedFishingActivity.vesselTransportGuid  ")
                 .append(" and faCatch.typeCode IN('").append(FaCatchTypeEnum.ONBOARD).append("','").append(FaCatchTypeEnum.TAKEN_ON_BOARD)
                .append("','").append(FaCatchTypeEnum.ALLOCATED_TO_QUOTA).append("')))");
    }



    @Override
    public StringBuilder createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        sql.append(" where ");
        createWherePartForQueryForFilters(sql, query);
        return sql;
    }

    @Override
    public  void appendJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(JOIN).append(joinString).append(StringUtils.SPACE);
    }

   
    @Override
    protected  void appendLeftJoinFetch(StringBuilder sql, String delimitedPeriodTableAlias) {
        sql.append(LEFT).append(JOIN).append(delimitedPeriodTableAlias);
    }

    @Override
    protected  void appendJoinFetchIfConditionDoesntExist(StringBuilder sql, String valueToFindAndApply) {
        if (sql.indexOf(valueToFindAndApply) == -1) { // Add missing join for required table
            sql.append(JOIN).append(valueToFindAndApply);
        }
    }

    protected void appendLeftJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(LEFT).append(JOIN).append(joinString).append(StringUtils.SPACE);
    }


}
