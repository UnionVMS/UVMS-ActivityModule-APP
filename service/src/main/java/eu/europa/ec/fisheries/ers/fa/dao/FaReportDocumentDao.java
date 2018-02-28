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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class FaReportDocumentDao extends AbstractFaDao<FaReportDocumentEntity> {

    private static final String REPORT_ID = "reportId";
    private static final String SCHEME_ID = "schemeId";
    private static final String TRIP_ID = "tripId";
    private static final String VESSEL_ID = "vesselId";
    private static final String STATUSES = "statuses";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    private EntityManager em;

    public FaReportDocumentDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Load FaReportDocument by one or more Report identifiers
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

    public List<FaReportDocumentEntity> loadReports(String tripId, String consolidated) {
        return loadReports(tripId, consolidated, null, null, null, null);
    }

    public List<FaReportDocumentEntity> loadReports(String tripId, String consolidated, String vesselId, String schemeId, String startDate, String endDate){

        Set<String> statuses = new HashSet<>();
        statuses.add(FaReportStatusType.NEW.getStatus());
        if ("N".equals(consolidated) || consolidated == null){
            statuses.add(FaReportStatusType.UPDATED.getStatus());
            statuses.add(FaReportStatusType.CANCELED.getStatus());
            statuses.add(FaReportStatusType.DELETED.getStatus());
        }

        Query query = getEntityManager().createNamedQuery(FaReportDocumentEntity.LOAD_REPORTS, FaReportDocumentEntity.class);
        query.setParameter(TRIP_ID, tripId);
        query.setParameter(STATUSES, statuses);
        query.setParameter(VESSEL_ID, vesselId);
        query.setParameter(SCHEME_ID, schemeId);
        query.setParameter(START_DATE, DateUtils.START_OF_TIME.toDate());
        query.setParameter(END_DATE, DateUtils.END_OF_TIME.toDate());

        if (startDate != null){
            query.setParameter(START_DATE, DateTime.parse(startDate, ISODateTimeFormat.dateTimeParser()).toDate());
        }

        if (endDate != null){
            query.setParameter(END_DATE,  DateTime.parse(endDate, ISODateTimeFormat.dateTimeParser()).toDate());

        }
        return query.getResultList();
    }

}