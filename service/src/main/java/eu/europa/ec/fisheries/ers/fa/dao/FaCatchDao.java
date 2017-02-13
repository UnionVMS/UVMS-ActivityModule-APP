/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.dao;


import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.helper.FACatchSummaryHelper;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.builder.FACatchSearchBuilder;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by padhyad on 5/3/2016.
 */
@Slf4j
public class FaCatchDao extends AbstractDAO<FaCatchEntity> {

    private EntityManager em;
    private static final String TRIP_ID = "tripId";

    public FaCatchDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Object[]> findFaCatchesByFishingTrip(String fTripID){
        TypedQuery<Object[]> query = getEntityManager().createNamedQuery(FaCatchEntity.CATCHES_FOR_FISHING_TRIP, Object[].class);
        query.setParameter(TRIP_ID, fTripID);
        return query.getResultList();
    }


    public StringBuilder getFACatchSummaryReportString(FishingActivityQuery query) throws ServiceException {

        StringBuilder str =new StringBuilder("test");
        FACatchSummaryHelper faCatchSummaryHelper = new FACatchSummaryHelper();
        List<GroupCriteria> groupByFieldList = query.getGroupByFields();
        if (groupByFieldList == null || Collections.isEmpty(groupByFieldList))
            throw new ServiceException(" No Group information present to aggregate report.");

        faCatchSummaryHelper.enrichGroupCriteriaWithFishSizeAndSpecies(groupByFieldList);
        query.setGroupByFields(faCatchSummaryHelper.mapAreaGroupCriteriaToAllSchemeIdTypes(groupByFieldList));

        List<FaCatchSummaryCustomEntity> customEntities=  getRecordsForFishClassOrFACatchType(query);

        faCatchSummaryHelper.enrichGroupCriteriaWithFACatchType(query.getGroupByFields());

        customEntities.addAll(getRecordsForFishClassOrFACatchType(query));

        Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap = faCatchSummaryHelper.groupByFACatchCustomEntities(customEntities);

        faCatchSummaryHelper.buildFaCatchSummaryTable(groupedMap);

        return str;
    }

     private   List<FaCatchSummaryCustomEntity> getRecordsForFishClassOrFACatchType(FishingActivityQuery query) throws ServiceException {
         FACatchSearchBuilder faCatchSearchBuilder = new FACatchSearchBuilder();
         FACatchSummaryHelper faCatchSummaryHelper = new FACatchSummaryHelper();
         StringBuilder sql= faCatchSearchBuilder.createSQL(query);
         System.out.println("sql :: "+sql);
         TypedQuery<Object[]> typedQuery = em.createQuery(sql.toString(), Object[].class);
         typedQuery = (TypedQuery<Object[]>) faCatchSearchBuilder.fillInValuesForTypedQuery(query,typedQuery);

         List<Object[]> list=  typedQuery.getResultList();
         List<FaCatchSummaryCustomEntity> customEntities= new ArrayList<>();
         log.debug("size of records :"+list.size());
         for(Object[] objArr :list){
             try {
                 FaCatchSummaryCustomEntity entity= faCatchSummaryHelper.mapObjectArrayToFaCatchSummaryCustomEntity(objArr,query);
                 customEntities.add(entity);
               //  log.debug(""+entity);

             } catch (Exception e) {
                 log.error("Could map sql selection to FaCatchSummaryCustomEntity object", Arrays.toString(objArr));
             }
         }
         log.debug("----------------------------------------------------------------------------------------------------");
         return customEntities;
     }

     public List<Object[]> getCatchSummary(){
  //  public List<FaCatchSummaryCustomEntity> getCatchSummary(){

      /*  String queryStr= "SELECT NEW eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity(activity.occurence as date ," +
                "faCatch.territory as territory,faCatch.faoArea as faoArea,faCatch.icesStatRectangle as icesStatRectangle,faCatch.effortZone as effortZone," +
                "faCatch.rfmo as rfmo,faCatch.gfcmGsa as gfcmGsa, faCatch.gfcmStatRectangle as gfcmStatRectangle," +
                " sdClassCode.classCode as fishClass ,faCatch.speciesCode as species ,  count(faCatch.id) as count ) " +
                "FROM FishingActivityEntity activity " +
                "JOIN activity.faReportDocument fa " +
                "JOIN activity.faCatchs faCatch " +
                "JOIN faCatch.sizeDistribution sizeDistribution " +
                "JOIN sizeDistribution.sizeDistributionClassCode  sdClassCode " +
                "WHERE activity.typeCode ='FISHING_OPERATION' " +
                "GROUP BY activity.occurence, faCatch.territory,faCatch.faoArea,faCatch.icesStatRectangle,faCatch.effortZone,faCatch.rfmo,faCatch.gfcmGsa," +
                " faCatch.gfcmStatRectangle, sdClassCode.classCode,faCatch.speciesCode " ;*/

   /* String queryStr= "SELECT activity.occurence, faCatch.speciesCode, sdClassCode.classCode ,vt.country ,vt.name, fg.typeCode, aapProcessCode.typeCode, count(faCatch.id), faCatch " +
                "FROM FishingActivityEntity activity " +
                "JOIN activity.faReportDocument fa " +
                 "JOIN activity.relatedFishingActivity relatedActivity " +
                 "JOIN relatedActivity.faCatchs relatedfaCatchs " +
                "JOIN fa.vesselTransportMeans vt " +
                "JOIN activity.faCatchs faCatch " +
                "JOIN faCatch.sizeDistribution sizeDistribution " +
                "JOIN faCatch.aapProcesses aprocess "+
                "JOIN aprocess.aapProcessCode aapProcessCode "+
                "JOIN sizeDistribution.sizeDistributionClassCode  sdClassCode " +
                "JOIN activity.fishingGears fg "+
                "WHERE activity.typeCode ='FISHING_OPERATION' or (activity.typeCode='JOINT_FISHING_OPERATION' and relatedActivity.typeCode='RELOCATION' " +
            "and  relatedActivity.vesselTransportMeans.guid = activity.vesselTransportMeans.guid and  relatedfaCatchs.typeCode IN ('ALLOCATED_TO_QUOTA','TAKEN_ON_BOARD','ONBOARD' ) )" +
                "GROUP BY activity.occurence, faCatch.speciesCode, sdClassCode.classCode, vt.country, vt.name ,fg.typeCode, aapProcessCode.typeCode " ;
        String queryStr= "SELECT activity.occurence, faCatch.speciesCode, count(faCatch.id) " +
                "FROM FishingActivityEntity activity " +
                "JOIN activity.relatedFishingActivity relatedActivity " +
                "JOIN relatedActivity.faCatchs relatedfaCatchs " +
                "JOIN activity.faCatchs faCatch " +
                "WHERE " +
             //   "activity.typeCode ='FISHING_OPERATION' or " +
                "(activity.typeCode='JOINT_FISHING_OPERATION' and relatedActivity.typeCode='RELOCATION' " +
                "and  relatedActivity.vesselTransportMeans.guid = activity.vesselTransportMeans.guid and  relatedfaCatchs.typeCode IN ('ALLOCATED_TO_QUOTA','TAKEN_ON_BOARD','ONBOARD' ) )" +
                "GROUP BY activity.occurence, faCatch.speciesCode " ;*/



               String queryStr= "SELECT  a.occurence,  faCatch.speciesCode,  count(faCatch.id)   from  FishingActivityEntity a   \n" +
                       "LEFT JOIN   a.relatedFishingActivity relatedActivity   \n" +
                       "LEFT JOIN  relatedActivity.faCatchs relatedfaCatchs  \n" +
                       "JOIN  a.faCatchs faCatch     \n" +
                       "where  (a.typeCode ='FISHING_OPERATION')  \n" +
                       "\tOR (a.typeCode='JOINT_FISHING_OPERATION' \n" +
                      "\t\tand relatedActivity.typeCode='RELOCATION' \n" +
                       "\t\tand  relatedActivity.vesselTransportMeans.guid = a.vesselTransportMeans.guid \n" +
                       "\t\tand  relatedfaCatchs.typeCode IN ('ALLOCATED_TO_QUOTA','TAKEN_ON_BOARD','ONBOARD' )" +
                       ")\n" +
                       "GROUP BY a.occurence, faCatch.speciesCode " ;


      /*   String queryStr= "SELECT a.occurence, faCatch.speciesCode, faCatch.fluxLocations, count(faCatch.id) from " +
                "FishingActivityEntity a  JOIN  " +
                "a.faReportDocument fa JOIN  " +
                "a.faCatchs faCatch JOIN   " +
                "faCatch.fluxLocations fluxLoc JOIN   " +
                "faCatch.sizeDistribution sizeDistribution JOIN  " +
                "sizeDistribution.sizeDistributionClassCode  sdClassCode   " +
                "where " +
                "fa.source =:dataSource GROUP BY " +
                "a.occurence ,faCatch.speciesCode, faCatch.fluxLocations";*/

      System.out.println(queryStr);
        // TypedQuery<Object[]> query = em.createQuery(queryStr, Object[].class);
        Query query=  em.createQuery(queryStr);

      //   query.setParameter("dataSource","FLUX");
        return query.getResultList();
    }
}