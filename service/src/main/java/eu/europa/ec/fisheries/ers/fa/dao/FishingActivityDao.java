/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.dao;


import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.Pagination;
import eu.europa.ec.fisheries.ers.service.search.SortKey;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by padhyad on 5/3/2016.
 */

public class FishingActivityDao extends AbstractDAO<FishingActivityEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(FishingActivityDao.class);

    final static String FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    private EntityManager em;

    public FishingActivityDao(EntityManager em) {
        this.em = em;
    }





    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<FishingActivityEntity> getFishingActivityListByQuery(FishingActivityQuery query){
        LOG.info("INSIDE getFishingActivityListByQuery:"+query);
        StringBuffer sql =createSQL(query);
        TypedQuery<FishingActivityEntity> typedQuery = em.createQuery(sql.toString(), FishingActivityEntity.class);
        List<eu.europa.ec.fisheries.ers.service.search.ListCriteria> criteriaList=query.getSearchCriteria();
        int criteriaListSize =criteriaList.size();
        for(int i=0;i<criteriaListSize;i++) {
            eu.europa.ec.fisheries.ers.service.search.ListCriteria criteria= criteriaList.get(i);
            typedQuery.setParameter(criteria.getKey().toString(), criteria.getValue());

        }
        Pagination pagination= query.getPagination();

        if(pagination!=null) {
            typedQuery.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            typedQuery.setMaxResults(pagination.getListSize());
        }

        List<FishingActivityEntity> resultList = typedQuery.getResultList();
        for(FishingActivityEntity fishingActivityEntity:resultList){
            LOG.info("fishingActivityEntity:"+fishingActivityEntity.toString());
        }

        return resultList;

    }

    private StringBuffer createSQL(FishingActivityQuery query){

        StringBuffer sql = new  StringBuffer("SELECT DISTINCT a from FishingActivityEntity a where ");

        List<eu.europa.ec.fisheries.ers.service.search.ListCriteria> criteriaList=query.getSearchCriteria();
        int criteriaListSize =criteriaList.size();
       for(int i=0;i<criteriaListSize;i++) {
           eu.europa.ec.fisheries.ers.service.search.ListCriteria criteria= criteriaList.get(i);
           sql.append("a."+criteria.getKey()+ " = :"+criteria.getKey());
           if(i != (criteriaListSize-1)){
               sql.append(" and ");
           }
       }

        SortKey sortKey = query.getSortKey();
        if(sortKey!=null){
            sql.append(" order by a."+sortKey.getField()+" "+sortKey.getOrder());
        }else {
            sql.append(" order by a.id ASC");
        }

        return sql;
    }



     private Session getSession() {
        return em.unwrap(Session.class);
    }
}