/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries  European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.dao;


import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.utils.DaoHelper;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.builder.FACatchSearchBuilder;
import eu.europa.ec.fisheries.ers.service.search.builder.FishingActivitySearchBuilder;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Created by padhyad on 5/3/2016.
 */
@Slf4j
public class FishingActivityDao extends AbstractDAO<FishingActivityEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(FishingActivityDao.class);

    private EntityManager em;

    public FishingActivityDao(EntityManager em) {
        this.em = em;
    }

   // public List<Object[]> getCatchSummary(){
   public List<FaCatchSummaryCustomEntity> getCatchSummary(){

         String queryStr= "SELECT NEW eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity(activity.occurence as date ," +
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
                 " faCatch.gfcmStatRectangle, sdClassCode.classCode,faCatch.speciesCode " ;

   /* String queryStr= "SELECT activity.occurence, faCatch.speciesCode, sdClassCode.classCode ,vt.country ,vt.name, fg.typeCode, aapProcessCode.typeCode, count(faCatch.id), faCatch " +
                "FROM FishingActivityEntity activity " +
                "JOIN activity.faReportDocument fa " +
                "JOIN fa.vesselTransportMeans vt " +
                "JOIN activity.faCatchs faCatch " +
                "JOIN faCatch.sizeDistribution sizeDistribution " +
                "JOIN faCatch.aapProcesses aprocess "+
                "JOIN aprocess.aapProcessCode aapProcessCode "+
                "JOIN sizeDistribution.sizeDistributionClassCode  sdClassCode " +
                "JOIN activity.fishingGears fg "+
                "WHERE activity.typeCode ='FISHING_OPERATION' " +
                "GROUP BY activity.occurence, faCatch.speciesCode, sdClassCode.classCode, vt.country, vt.name ,fg.typeCode, aapProcessCode.typeCode " ;*/
      /*    String queryStr= "SELECT a.occurence, faCatch.speciesCode, faCatch.fluxLocations, count(faCatch.id) from " +
                "FishingActivityEntity a  JOIN  " +
                "a.faReportDocument fa JOIN  " +
                "a.faCatchs faCatch JOIN   " +
                "faCatch.fluxLocations fluxLoc JOIN   " +
                "faCatch.sizeDistribution sizeDistribution JOIN  " +
                "sizeDistribution.sizeDistributionClassCode  sdClassCode   " +
                "where " +
                "fa.source =:dataSource GROUP BY " +
                "a.occurence ,faCatch.speciesCode, faCatch.fluxLocations";*/


       // TypedQuery<Object[]> query = em.createQuery(queryStr, Object[].class);
        Query query=  em.createQuery(queryStr);

       // query.setParameter("dataSource","FLUX");
        return query.getResultList();
    }


    public StringBuilder getFACatchSummaryReportString(FishingActivityQuery query) throws ServiceException {
        FACatchSearchBuilder faCatchSearchBuilder = new FACatchSearchBuilder();
        StringBuilder str =null;
        List<GroupCriteria> groupByFieldList = query.getGroupByFields();
        if (groupByFieldList == null || Collections.isEmpty(groupByFieldList))
            throw new ServiceException(" No Group information present to aggregate report.");

        query.setGroupByFields(DaoHelper.mapAreaGroupCriteriaToAllSchemeIdTypes(groupByFieldList));

        str= faCatchSearchBuilder.createSQL(query);

         TypedQuery<Object[]> typedQuery = em.createQuery(str.toString(), Object[].class);
            typedQuery = (TypedQuery<Object[]>) faCatchSearchBuilder.fillInValuesForTypedQuery(query,typedQuery);

            List<Object[]> list=  typedQuery.getResultList();
            List<FaCatchSummaryCustomEntity> customEntities= new ArrayList<>();

            for(Object[] objArr :list){
                try {
                    FaCatchSummaryCustomEntity entity= DaoHelper.mapObjectArrayToFaCatchSummaryCustomEntity(objArr,query);
                    customEntities.add(entity);
                    System.out.println(entity);

                } catch (Exception e) {
                    log.error("Could map sql selection to FaCatchSummaryCustomEntity object", Arrays.toString(objArr));
                }
            }
           log.debug("----------------------------------------------------------------------------------------------------");

           Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap= new HashMap<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>>();
           for(FaCatchSummaryCustomEntity summaryObj: customEntities){
               List<FaCatchSummaryCustomEntity> tempList=groupedMap.get(summaryObj);
              if( Collections.isEmpty(tempList)){
                  tempList = new ArrayList<>();
                  tempList.add(summaryObj);
                  groupedMap.put(summaryObj,tempList);
               }else{
                  tempList.add(summaryObj);
              }

           }

        for (Map.Entry<FaCatchSummaryCustomEntity, List<FaCatchSummaryCustomEntity>> entry : groupedMap.entrySet()) {

            System.out.println("Key:"+entry.getKey()+":::"+entry.getValue().size());
            System.out.println(entry.getValue());
            System.out.println("**********************************************************");
        }

       //   System.out.println(Arrays.asList(groupedMap));
           /* for(Object[] objArr :list){
                System.out.println(Arrays.toString(objArr));
            }*/


       return str;
    }


    public void buildFaCatchSummaryTable(){

    }



    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<FishingActivityEntity> getFishingActivityListForFishingTrip(String fishingTripId, Geometry multipolgon) throws ServiceException {
        if(fishingTripId == null || fishingTripId.length() == 0)
            throw new ServiceException("fishing Trip Id is null or empty. ");

        String queryName = FishingActivityEntity.ACTIVITY_FOR_FISHING_TRIP;
        if(multipolgon == null)
            queryName = FishingActivityEntity.FIND_FA_DOCS_BY_TRIP_ID_WITHOUT_GEOM;

        Query query = getEntityManager().createNamedQuery(queryName);

        query.setParameter("fishingTripId", fishingTripId);

        if(multipolgon != null)
            query.setParameter("area", multipolgon);

        return query.getResultList();
    }

    public Integer getCountForFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        FishingActivitySearchBuilder search = new FishingActivitySearchBuilder();
           LOG.info("Get Total Count for Fishing Activities When filter criteria is present");
           StringBuilder sqlToGetActivityListCount =search.createSQL(query);

           Query countQuery= getTypedQueryForFishingActivityFilter(sqlToGetActivityListCount, query, search);

           return countQuery.getResultList().size();
       }


       /**
        * Set typed values for Dynamically generated Query
        */
    private Query getTypedQueryForFishingActivityFilter(StringBuilder sql, FishingActivityQuery query, FishingActivitySearchBuilder search) throws ServiceException {
        LOG.debug("Set Typed Parameters to Query");

        Query typedQuery = em.createQuery(sql.toString());
        return search.fillInValuesForTypedQuery(query,typedQuery);

    }


    /*
     Get all the Fishing Activities which match Filter criterias mentioned in the Input. Also, provide the sorted data based on what user has requested.
     Provide paginated data if user has asked for it
     */
    public List<FishingActivityEntity> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        LOG.info("Get Fishing Activity Report list by Query.");
        FishingActivitySearchBuilder search = new FishingActivitySearchBuilder();

        // Create Query dynamically based on filter and Sort criteria
        StringBuilder sqlToGetActivityList = search.createSQL(query);

        // Apply real values to Query built
        Query listQuery = getTypedQueryForFishingActivityFilter(sqlToGetActivityList,query, search);

        PaginationDto pagination= query.getPagination();
        if(pagination!=null) {
            listQuery.setFirstResult(pagination.getOffset());
            listQuery.setMaxResults(pagination.getPageSize());
        }

        return listQuery.getResultList();
    }
}