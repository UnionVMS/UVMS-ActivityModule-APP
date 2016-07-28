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
import eu.europa.ec.fisheries.ers.service.search.*;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
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

    public List<FishingActivityEntity> getFishingActivityList(Pagination pagination)  {
        StringBuilder sql = new  StringBuilder("SELECT DISTINCT a  from FishingActivityEntity a order by a.faReportDocument.id");
        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(sql.toString(), FishingActivityEntity.class);
        if(pagination!=null) {
            typedQuery.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            typedQuery.setMaxResults(pagination.getListSize());
        }
        List<FishingActivityEntity> resultList = typedQuery.getResultList();
        return resultList;
    }

    public List<FishingActivityEntity> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {


        StringBuilder sql =createSQL(query);
        LOG.info("sql :"+sql);
        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(sql.toString(), FishingActivityEntity.class);


            List<ListCriteria> criteriaList = query.getSearchCriteria();
            // Assign values to created SQL Query
            for (ListCriteria criteria : criteriaList) {
                Filters key = criteria.getKey();
                List<SearchValue> valueList = criteria.getValue();
                for (SearchValue searchValue : valueList) {
                    String parameterName = searchValue.getParameterName();
                    String parameterValue = searchValue.getParameterValue();
                    switch (key) {
                        case PERIOD:
                            typedQuery.setParameter(parameterName, DateUtils.parseToUTCDate(parameterValue,FORMAT));
                            break;
                        case QUNTITIES:
                            typedQuery.setParameter(parameterName, Long.parseLong(parameterValue));
                            break;
                        default:
                            typedQuery.setParameter(parameterName, parameterValue);
                            break;
                    }
                }
            }


       Pagination pagination= query.getPagination();

        if(pagination!=null) {
            typedQuery.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            typedQuery.setMaxResults(pagination.getListSize());
        }

        List<FishingActivityEntity> resultList = typedQuery.getResultList();
        LOG.info("resultList size :"+resultList.size());
        return resultList;

    }

    private StringBuilder createSQL(FishingActivityQuery query) throws ServiceException {

        List<ListCriteria> criteriaList=query.getSearchCriteria();
        if(criteriaList.isEmpty())
            throw new ServiceException("Fishing Activity Report Search Criteria is empty.");

        Map<Filters, FilterDetails> mappings= FilterMap.getFilterMappings();
        StringBuilder sql = new  StringBuilder("SELECT DISTINCT a from FishingActivityEntity a ");

        // Create join part of SQL query
       for(ListCriteria criteria :criteriaList){
           Filters key= criteria.getKey();

           FilterDetails details=mappings.get(key);
           String joinString = details.getJoinString();

           // Add join statement only if its not already been added
           if(sql.indexOf(joinString)==-1){

               //If table join is already present in Query, we want to reuse that join alias. so, treat it differently
               if(Filters.MASTER.equals(key) && sql.indexOf(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS)!=-1 ){
                   sql.append(" JOIN FETCH "+FilterMap.MASTER_MAPPING);
               }// Add table alias if not already present
               else if(sql.indexOf(FilterMap.REPORT_DOCUMENT_TABLE_ALIAS)==-1 && (Filters.FROM.equals(key) || Filters.VESSEL_IDENTIFIES.equals(key) || Filters.PURPOSE.equals(key))) {
                   sql.append(" JOIN FETCH "+FilterMap.REPORT_DOCUMENT_TABLE_ALIAS+" ");
                   sql.append(" JOIN FETCH "+details.getJoinString()+" ");
               }else{
                   sql.append(" JOIN FETCH "+details.getJoinString()+" ");
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
                    sql.append(" and " + mapping);
                } else {
                    sql.append(mapping);
                }
            }

            sql.append(" order by a.faReportDocument.id ");

        return sql;
    }


}