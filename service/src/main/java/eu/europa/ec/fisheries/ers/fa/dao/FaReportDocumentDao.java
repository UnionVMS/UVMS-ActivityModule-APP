/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.dao;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;

/**
 * Created by padhyad on 5/3/2016.
 */
public class FaReportDocumentDao extends AbstractFaDao<FaReportDocumentEntity> {

    private static final String REPORT_ID = "reportId";
    private static final String SCHEME_ID = "schemeId";
    private static final String TRIP_ID = "tripId";

    private EntityManager em;

    public FaReportDocumentDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Get FaReportDocument by one or more Report identifiers
     *
     * @param reportId
     * @param schemeId
     * @return FaReportDocumentEntity
     * @throws ServiceException
     */
    public FaReportDocumentEntity findFaReportByIdAndScheme(String reportId, String schemeId) throws ServiceException {
        TypedQuery query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_FA_ID_AND_SCHEME, FaReportDocumentEntity.class);
        query.setParameter(REPORT_ID, reportId);
        query.setParameter(SCHEME_ID, schemeId);
        query.setMaxResults(1);
        List<FaReportDocumentEntity> entities = query.getResultList();
        if (!entities.isEmpty()) {
            return entities.get(0);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<FaReportDocumentEntity> getFaReportDocumentsForTrip(String tripId){
        TypedQuery query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_FA_DOCS_BY_TRIP_ID, FaReportDocumentEntity.class);
        query.setParameter(TRIP_ID, tripId);
        return query.getResultList();
    }

    public List<FaReportDocumentEntity> getFaReportDocumentsForFaQuery(String tripId){
        TypedQuery query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FA_QUERY, FaReportDocumentEntity.class);
        query.setParameter(TRIP_ID, tripId);
        return query.getResultList();
    }

    public List<FaReportDocumentEntity> getLatestFaReportDocumentsForTrip(String tripId){
        TypedQuery query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_LATEST_FA_DOCS_BY_TRIP_ID, FaReportDocumentEntity.class);
        query.setParameter(TRIP_ID, tripId);
        return query.getResultList();
    }
}