/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries  European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.dao;


import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.ers.service.search.*;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

/**
 * Created by padhyad on 5/3/2016.
 */

public class FishingActivityDao extends AbstractDAO<FishingActivityEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(FishingActivityDao.class);
    final static String FORMAT = "yyyy-MM-dd HH:mm:ss";


    private EntityManager em;

    public FishingActivityDao(EntityManager em) {
        this.em = em;
    }


    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<FishingActivityEntity> getFishingActivityList(){
        return getFishingActivityList(null);
    }



    public List<FishingActivityEntity> getFishingActivityListForFishingTrip(String fishingTripId,Pagination pagination) throws ServiceException {
        if(fishingTripId == null || fishingTripId.length() == 0)
            throw new ServiceException("fishing Trip Id is null or empty. ");
        Query query = getEntityManager().createNamedQuery(FishingActivityEntity.ACTIVITY_FOR_FISHING_TRIP);

        query.setParameter("fishingTripId", fishingTripId);
        if(pagination!=null) {
            query.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            query.setMaxResults(pagination.getListSize());
        }

        return query.getResultList();
    }

    public List<FishingActivityEntity> getFishingActivityList(Pagination pagination)  {
        String sql = "SELECT DISTINCT a  from FishingActivityEntity a JOIN FETCH a.faReportDocument fa JOIN FETCH fa.fluxReportDocument flux where fa.status = '"+ FaReportStatusEnum.NEW.getStatus() +"' order by flux.fluxReportDocumentId asc  ";

        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(sql, FishingActivityEntity.class);
        if(pagination!=null) {
            typedQuery.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            typedQuery.setMaxResults(pagination.getListSize());
        }

        return typedQuery.getResultList();
    }



    public List<FishingActivityEntity> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {

        Map<Filters,String> mappings =  FilterMap.getFilterQueryParameterMappings();
      StringBuilder sql =createSQL(query);

       LOG.info("sql :"+sql);
        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(sql.toString(), FishingActivityEntity.class);

          List<ListCriteria> criteriaList = query.getSearchCriteria();
            // Assign values to created SQL Query
            for (ListCriteria criteria : criteriaList) {
                Filters key = criteria.getKey();
                String value= criteria.getValue();

                    switch (key) {
                        case PERIOD_START:
                            typedQuery.setParameter(mappings.get(key), DateUtils.parseToUTCDate(value,FORMAT));
                            break;
                        case PERIOD_END:
                            typedQuery.setParameter(mappings.get(key), DateUtils.parseToUTCDate(value,FORMAT));
                            break;
                        case QUNTITY_MIN:
                            typedQuery.setParameter(mappings.get(key), Double.parseDouble(value));
                            break;
                        case QUNTITY_MAX:
                            typedQuery.setParameter(mappings.get(key), Double.parseDouble(value));
                            break;
                        case MASTER:
                            typedQuery.setParameter(mappings.get(key), value.toUpperCase());
                            break;
                        default:
                            typedQuery.setParameter(mappings.get(key), value);
                            break;
                    }

            }


       Pagination pagination= query.getPagination();

        if(pagination!=null) {
            typedQuery.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            typedQuery.setMaxResults(pagination.getListSize());
        }


        return typedQuery.getResultList();

    }

    private StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {

        List<ListCriteria> criteriaList=query.getSearchCriteria();
        if(criteriaList.isEmpty())
            throw new ServiceException("Fishing Activity Report Search Criteria is empty.");

        Map<Filters, FilterDetails> mappings= FilterMap.getFilterMappings();
        StringBuilder sql = new  StringBuilder("SELECT DISTINCT a from FishingActivityEntity a JOIN FETCH a.faReportDocument fa ");

        // Create join part of SQL query
       for(ListCriteria criteria :criteriaList){
           Filters key= criteria.getKey();

           FilterDetails details=mappings.get(key);
           String joinString = details.getJoinString();

           // Add join statement only if its not already been added
           if(sql.indexOf(joinString)==-1){

               //If table join is already present in Query, we want to reuse that join alias. so, treat it differently
               if(Filters.MASTER.equals(key) && sql.indexOf(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS)!=-1 ){
                   sql.append(" JOIN FETCH ").append(FilterMap.MASTER_MAPPING).append(" ");
               }// Add table alias if not already present
                else if( Filters.VESSEL_IDENTIFIRE.equals(key) && sql.indexOf(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS)==-1){
                   sql.append(" JOIN FETCH ").append(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS);
                   sql.append(" JOIN FETCH ").append(details.getJoinString()).append(" ");
               }else{
                   sql.append(" JOIN FETCH ").append(details.getJoinString()).append(" ");
               }
           }
       }

            sql.append("where ");

            // Create Where part of SQL Query
            int listSize = criteriaList.size();
            for (int i = 0; i < listSize; i++) {
                ListCriteria criteria = criteriaList.get(i);
                String mapping = mappings.get(criteria.getKey()).getCondition();
                if (i != 0) {
                    sql.append(" and ").append(mapping);
                } else {
                    sql.append(mapping);
                }
            }

      /*  SortKey sort=   query.getSortKey();
        if(sort!=null){
            sql.append(" order by " +FilterMap.getFilterSortMappings().get(sort.getField()) + " "+sort.getOrder());
        }else {
            sql.append(" order by a.faReportDocument.id ASC ");
        }*/

        return sql;
    }


}