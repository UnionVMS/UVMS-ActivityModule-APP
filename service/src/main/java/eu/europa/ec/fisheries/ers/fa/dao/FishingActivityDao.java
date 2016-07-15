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
import eu.europa.ec.fisheries.ers.fa.utils.DateUtil;
import eu.europa.ec.fisheries.ers.service.search.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import org.hibernate.Session;
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



    private EntityManager em;

    public FishingActivityDao(EntityManager em) {
        this.em = em;
    }


    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<FishingActivityEntity> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {

        Map<SearchKey, FilterDetails> mappings= FilterMap.getFilterMappings();
        StringBuffer sql =createSQL(query);
        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(sql.toString(), FishingActivityEntity.class);
       List<ListCriteria> criteriaList=query.getSearchCriteria();
        // Assign values to created SQL Query
        for(ListCriteria criteria :criteriaList){
            SearchKey key= criteria.getKey();
            List<SearchValue> valueList= criteria.getValue();
           for(SearchValue searchValue:valueList) {
               String parameterName= searchValue.getParameterName();
               String parameterValue= searchValue.getParameterValue();
               switch(key){
                   case PERIOD :
                       typedQuery.setParameter(parameterName, DateUtil.parseToUTCDate(parameterValue));
                       break;
                   case QUNTITIES :
                       typedQuery.setParameter(parameterName, Long.parseLong(parameterValue));
                       break;
                   default :
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
        return resultList;

    }

    private StringBuffer createSQL(FishingActivityQuery query) throws ServiceException {

        List<ListCriteria> criteriaList=query.getSearchCriteria();
        if(criteriaList.size()==0)
            throw new ServiceException("Fishing Activity Report Search Criteria is empty.");

        Map<SearchKey, FilterDetails> mappings= FilterMap.getFilterMappings();
        StringBuffer sql = new  StringBuffer("SELECT DISTINCT a from FishingActivityEntity a ");

        // Create join part of SQL query
       for(ListCriteria criteria :criteriaList){
           SearchKey key= criteria.getKey();

           FilterDetails details=mappings.get(key);
           String joinString = details.getJoinString();

           // Add join statement only if its not already been added
           if(sql.indexOf(joinString)==-1){

               //If table join is already present in Query, we want to reuse that join alias. so, treat it differently
               if(SearchKey.MASTER.equals(key) && sql.indexOf(FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS)!=-1 ){
                   sql.append(" JOIN FETCH "+FilterMap.MASTER_MAPPING);
               }// Add table alias if not already present
               else if(sql.indexOf(FilterMap.REPORT_DOCUMENT_TABLE_ALIAS)==-1 && (SearchKey.FROM.equals(key) || SearchKey.VESSEL_IDENTIFIES.equals(key) || SearchKey.PURPOSE.equals(key))) {
                   sql.append(" JOIN FETCH "+FilterMap.REPORT_DOCUMENT_TABLE_ALIAS+" ");
                   sql.append(" JOIN FETCH "+details.getJoinString()+" ");
               }else{
                   sql.append(" JOIN FETCH "+details.getJoinString()+" ");
               }
           }
       }

        sql.append("where ");

        // Create Where part of SQL Query
        int listSize =criteriaList.size();
        for(int i=0;i<listSize;i++){
            ListCriteria criteria=criteriaList.get(i);
            String mapping= mappings.get(criteria.getKey()).getCondition();
            if(i!=0){
                sql.append(" and "+mapping);
            }else{
                sql.append(mapping);
            }
        }

        return sql;
    }

    private Session getSession() {
        return em.unwrap(Session.class);
    }
}