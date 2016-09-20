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
    final static String JOIN =" JOIN FETCH ";
    final static String FISHING_ACTIVITY_JOIN=" from FishingActivityEntity a JOIN FETCH a.faReportDocument fa ";
    final static String FISHING_ACTIVITY_LIST_ALL_DATA="SELECT DISTINCT a  from FishingActivityEntity a JOIN FETCH a.faReportDocument fa JOIN FETCH fa.fluxReportDocument flux where fa.status = '"+ FaReportStatusEnum.NEW.getStatus() +"' order by flux.fluxReportDocumentId asc ";
    final static String FISHING_ACTIVITY_LIST_ALL_DATA_COUNT="SELECT DISTINCT a from FishingActivityEntity a JOIN FETCH a.faReportDocument fa JOIN FETCH fa.fluxReportDocument flux where fa.status = '"+ FaReportStatusEnum.NEW.getStatus() +"' order by flux.fluxReportDocumentId asc ";



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

        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(FISHING_ACTIVITY_LIST_ALL_DATA, FishingActivityEntity.class);
        if(pagination!=null) {
            typedQuery.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            typedQuery.setMaxResults(pagination.getListSize());
        }

        return typedQuery.getResultList();
    }

    public Integer getCountForFishingActivityList(Pagination pagination)  {

        TypedQuery<Integer> typedQuery = em.createQuery(FISHING_ACTIVITY_LIST_ALL_DATA_COUNT, Integer.class);
        return typedQuery.getResultList().size();
    }


    public Integer getCountForFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        StringBuilder sqlToGetActivityListCount =createSQL(query,true);
        Query countQuery= getTypedQueryForFishingActivityFilter(sqlToGetActivityListCount,query);
        LOG.info("countQuery :"+countQuery);
        return countQuery.getResultList().size();
    }

    public List<FishingActivityEntity> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {


        StringBuilder sqlToGetActivityList =createSQL(query,false);
        Query listQuery= getTypedQueryForFishingActivityFilter(sqlToGetActivityList,query);
        LOG.info("listQuery :"+listQuery);
        Pagination pagination= query.getPagination();


        if(pagination!=null) {
            listQuery.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            listQuery.setMaxResults(pagination.getListSize());
        }

        return listQuery.getResultList();
    }

    private Query getTypedQueryForFishingActivityFilter(StringBuilder sql,FishingActivityQuery query){
        Map<Filters,String> mappings =  FilterMap.getFilterQueryParameterMappings();
        Query typedQuery = em.createQuery(sql.toString());

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
         return typedQuery;
    }

    private StringBuilder createSQL(FishingActivityQuery query, boolean skipSort) throws ServiceException {

        List<ListCriteria> criteriaList=query.getSearchCriteria();
        if(criteriaList.isEmpty())
            throw new ServiceException("Fishing Activity Report Search Criteria is empty.");

        Map<Filters, FilterDetails> mappings= FilterMap.getFilterMappings();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT a ");
        sql.append(FISHING_ACTIVITY_JOIN);

        // Create join part of SQL query
       for(ListCriteria criteria :criteriaList){
           Filters key= criteria.getKey();

           FilterDetails details=mappings.get(key);
           String joinString = details.getJoinString();

           // Add join statement only if its not already been added
           if(sql.indexOf(joinString)==-1){

               //If table join is already present in Query, we want to reuse that join alias. so, treat it differently
               if(Filters.MASTER.equals(key) && sql.indexOf(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS)!=-1 ){
                   sql.append(JOIN).append(FilterMap.MASTER_MAPPING).append(" ");
               }// Add table alias if not already present
                else if( Filters.VESSEL_IDENTIFIRE.equals(key) && sql.indexOf(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS)==-1){
                   sql.append(JOIN).append(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS);
                   sql.append(JOIN).append(details.getJoinString()).append(" ");
               } else if( Filters.SPECIES.equals(key) && sql.indexOf(FilterMap.FA_CATCH_TABLE_ALIAS)==-1){
                   sql.append(JOIN).append(FilterMap.FA_CATCH_TABLE_ALIAS);
                   sql.append(JOIN).append(details.getJoinString()).append(" ");
               } else{
                   sql.append(JOIN).append(details.getJoinString()).append(" ");
               }
           }
       }


        SortKey sort = query.getSortKey();

          if (sort != null && !skipSort) {
              Filters field = sort.getField();
              if (Filters.PERIOD_START.equals(field) || Filters.PERIOD_END.equals(field) && sql.indexOf(FilterMap.DELIMITED_PERIOD_TABLE_ALIAS) == -1) {
                  sql.append(JOIN).append(FilterMap.DELIMITED_PERIOD_TABLE_ALIAS);
              } else if (Filters.PURPOSE.equals(field) || Filters.FROM_NAME.equals(field) && sql.indexOf(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS) == -1) {
                  sql.append(JOIN).append(FilterMap.FLUX_REPORT_DOC_TABLE_ALIAS);
              }
          }


            sql.append("where ");

            // Create Where part of SQL Query
            int listSize = criteriaList.size();
            for (int i = 0; i < listSize; i++) {
                ListCriteria criteria = criteriaList.get(i);
                String mapping = mappings.get(criteria.getKey()).getCondition();
                if(Filters.QUNTITY_MAX.equals(criteria.getKey())){
                    sql.append(" and ").append(mapping);
                    sql.append(" OR (aprod.weightMeasure  BETWEEN :").append(FilterMap.QUNTITY_MIN).append(" and :").append(FilterMap.QUNTITY_MAX+")");
                }else if (i != 0) {
                    sql.append(" and ").append(mapping);
                }
                else {
                    sql.append(mapping);
                }
            }

             sql.append(" and fa.status = '"+ FaReportStatusEnum.NEW.getStatus() +"'");

         if(!skipSort) {
                if (sort != null) {
                    sql.append(" order by " + FilterMap.getFilterSortMappings().get(sort.getField()) + " " + sort.getOrder());
                } else {
                    sql.append(" order by fa.acceptedDatetime ASC ");
                }
         }

        return sql;
    }




}