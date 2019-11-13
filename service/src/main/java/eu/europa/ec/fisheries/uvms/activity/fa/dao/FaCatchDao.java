
/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.fa.dao;


import eu.europa.ec.fisheries.uvms.activity.fa.dao.proxy.FaCatchSummaryCustomProxy;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.service.facatch.FACatchSummaryHelper;
import eu.europa.ec.fisheries.uvms.activity.service.facatch.FACatchSummaryHelperFactory;
import eu.europa.ec.fisheries.uvms.activity.service.search.builder.FACatchSearchBuilder;
import eu.europa.ec.fisheries.uvms.activity.service.search.builder.FACatchSearchBuilder_Landing;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.commons.service.dao.AbstractDAO;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by padhyad on 5/3/2016.
 */
@Slf4j
public class FaCatchDao extends AbstractDAO<FaCatchEntity> {

    private static final String TRIP_ID = "tripId";
    private EntityManager em;

    public FaCatchDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Object[]> findFaCatchesByFishingTrip(String fTripID) {
        TypedQuery<Object[]> query = getEntityManager().createNamedQuery(FaCatchEntity.CATCHES_FOR_FISHING_TRIP, Object[].class);
        query.setParameter(TRIP_ID, fTripID);
        return query.getResultList();
    }

    /**
     * This method gets data from database and groups data as per various aggregation factors.
     * If isLanding flag is true, then we need to gather information for Landing table as well.
     *
     * @param query
     * @return Map<FaCatchSummaryCustomEntity   ,   List   <   FaCatchSummaryCustomEntity>> key = object represnting common group, value is list of different objects which belong to  that group
     * @throws ServiceException
     */
    public Map<FaCatchSummaryCustomProxy, List<FaCatchSummaryCustomProxy>> getGroupedFaCatchData(FishingActivityQuery query, boolean isLanding) throws ServiceException {
        List<GroupCriteria> groupByFieldList = query.getGroupByFields();
        if (groupByFieldList == null || Collections.isEmpty(groupByFieldList))
            throw new ServiceException(" No Group information present to aggregate report.");
        FACatchSummaryHelper faCatchSummaryHelper = isLanding ? FACatchSummaryHelperFactory.getFACatchSummaryHelper(FACatchSummaryHelperFactory.PRESENTATION) : FACatchSummaryHelperFactory.getFACatchSummaryHelper(FACatchSummaryHelperFactory.STANDARD);
        // By default FishSize(LSC/BMS etc) and FACatch(DIS/DIM etc) type should be present in the summary table. First Query db with group FishClass
        faCatchSummaryHelper.enrichGroupCriteriaWithFishSizeAndSpecies(groupByFieldList);
        List<FaCatchSummaryCustomProxy> customEntities = getRecordsForFishClassOrFACatchType(query, isLanding); // get data with FishClass grouping factor
        faCatchSummaryHelper.enrichGroupCriteriaWithFACatchType(query.getGroupByFields());
        customEntities.addAll(getRecordsForFishClassOrFACatchType(query, isLanding)); // Query database again to get records for FACatchType and combine it with previous result
        return faCatchSummaryHelper.groupByFACatchCustomEntities(customEntities);

    }


    /**
     * Get list of records from FACatch table grouped by certain aggregation criterias. Also, Activity Filtering will be applied before getting data
     *
     * @param query
     * @return List<FaCatchSummaryCustomEntity> custom object represnting aggregation factors and its count
     * @throws ServiceException
     */
    private List<FaCatchSummaryCustomProxy> getRecordsForFishClassOrFACatchType(FishingActivityQuery query, boolean isLanding) throws ServiceException {

        // create Query to get grouped data from FACatch table, also combine query to filter records as per filters provided by users
        FACatchSearchBuilder faCatchSearchBuilder = createBuilderForFACatch(isLanding);
        StringBuilder sql = faCatchSearchBuilder.createSQL(query);
        TypedQuery<Object[]> typedQuery = em.createQuery(sql.toString(), Object[].class);
        typedQuery = (TypedQuery<Object[]>) faCatchSearchBuilder.fillInValuesForTypedQuery(query, typedQuery);
        List<Object[]> list = typedQuery.getResultList();
        log.debug("size of records received from DB :" + list.size());

        // Map Raw data received from database to custom entity which will help identifing correct groups
        List<FaCatchSummaryCustomProxy> customEntities = new ArrayList<>();
        FACatchSummaryHelper faCatchSummaryHelper = isLanding ? FACatchSummaryHelperFactory.getFACatchSummaryHelper(FACatchSummaryHelperFactory.PRESENTATION) : FACatchSummaryHelperFactory.getFACatchSummaryHelper(FACatchSummaryHelperFactory.STANDARD);
        List<GroupCriteria> groupCriterias = query.getGroupByFields();
        for (Object[] objArr : list) {
            try {
                FaCatchSummaryCustomProxy entity = faCatchSummaryHelper.mapObjectArrayToFaCatchSummaryCustomEntity(objArr, groupCriterias, isLanding);
                if (entity != null) {
                    customEntities.add(entity);
                }
            } catch (Exception e) {
                log.error("Could not map sql selection to FaCatchSummaryCustomProxy object", e);
            }
        }
        return customEntities;
    }

    private FACatchSearchBuilder createBuilderForFACatch(boolean isLanding) {
        if (isLanding) {
            return new FACatchSearchBuilder_Landing();// This is for landing table
        } else {
            return new FACatchSearchBuilder();
        }
    }


}
