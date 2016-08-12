/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.dao;


import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.service.search.Pagination;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 5/3/2016.
 */
public class FaReportDocumentDao extends AbstractFaDao<FaReportDocumentEntity> {

    private EntityManager em;

    public FaReportDocumentDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    private static final String REPORT_IDS = "reportIds";

    /**
     * Get FaReportDocument by one or more Report identifiers
     *
     * @param strings
     * @return List<FaReportDocumentEntity>
     * @throws ServiceException
     */
    public List<FaReportDocumentEntity> findFaReportsByIds(Set<String> strings) throws ServiceException {
        TypedQuery query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_FA_ID, FaReportDocumentEntity.class);
        query.setParameter(REPORT_IDS, strings);
        return query.getResultList();
    }

    /**
     * Find Fa Report document by reference id of a Fishing activity Report
     *
     * @param referenceId Reference Id
     * @return List<FaReportDocumentEntity>
     * @throws ServiceException
     */
    public List<FaReportDocumentEntity> findFaReportsByReferenceId(String referenceId) throws ServiceException {
        TypedQuery query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_FA_REF_ID, FaReportDocumentEntity.class);
        query.setParameter(REPORT_IDS, referenceId);
        return query.getResultList();
    }

    public List<FaReportDocumentEntity> getFishingActivityListForFishingTrip(String fishingTripId, Pagination pagination) throws ServiceException {
        if(fishingTripId == null || fishingTripId.length() == 0)
            throw new ServiceException("fishing Trip Id is null or empty. ");

        String sql = "SELECT DISTINCT fa  from FaReportDocumentEntity fa JOIN FETCH  fa.fishingActivities a JOIN FETCH a.fishingTrips ft JOIN FETCH ft.fishingTripIdentifiers fi where fi.tripId =:fishingTripId order by fa.fluxReportDocument.fluxReportDocumentId";

        TypedQuery<FaReportDocumentEntity> typedQuery = em.createQuery(sql, FaReportDocumentEntity.class);
        typedQuery.setParameter("fishingTripId", fishingTripId);
        if(pagination!=null) {
            typedQuery.setFirstResult(pagination.getListSize() * (pagination.getPage() - 1));
            typedQuery.setMaxResults(pagination.getListSize());
        }
        List<FaReportDocumentEntity> resultList = typedQuery.getResultList();
        return resultList;
    }
}