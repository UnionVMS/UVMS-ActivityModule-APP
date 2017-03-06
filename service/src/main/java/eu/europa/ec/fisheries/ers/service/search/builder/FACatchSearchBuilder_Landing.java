package eu.europa.ec.fisheries.ers.service.search.builder;

import eu.europa.ec.fisheries.ers.fa.utils.ActivityConstants;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;

/**
 * Created by sanera on 01/03/2017.
 */
public class FACatchSearchBuilder_Landing extends FACatchSearchBuilder {

    protected String FA_CATCH_JOIN = " from FaCatchEntity faCatch JOIN faCatch.fishingActivity a " +
            "JOIN "+ FilterMap.AAP_PROCESS_TABLE_ALIAS +" JOIN  " +FilterMap.AAP_PRODUCT_TABLE_ALIAS+
            "  JOIN a.faReportDocument fa  " ;


    protected String SUM_WEIGHT = " SUM(aprod.calculatedWeightMeasure)  " ;

    protected void enrichWherePartOFQueryForDISOrDIM(StringBuilder sql){

        sql.append(" and ( a.typeCode ='").append(ActivityConstants.LANDING).append("' and faCatch.typeCode IN ('").append(FaCatchTypeEnum.DEMINIMIS).append("','").append(FaCatchTypeEnum.DISCARDED).append("')) ");
    }



    protected void conditionsForFACatchSummaryReport(StringBuilder sql){
        sql.append(" and ( a.typeCode ='").append(ActivityConstants.LANDING).append("') ");
    }

}
