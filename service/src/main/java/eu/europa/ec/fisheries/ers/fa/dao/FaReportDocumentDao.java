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
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.ers.service.util.Utils;
import eu.europa.ec.fisheries.uvms.commons.service.dao.AbstractDAO;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FaReportDocumentDao extends AbstractDAO<FaReportDocumentEntity> {

    private static final String REPORT_ID = "reportId";
    private static final String SCHEME_ID = "schemeId";
    private static final String REPORT_REF_ID = "reportRefId";
    private static final String SCHEME_REF_ID = "schemeRefId";
    private static final String TRIP_ID = "tripId";
    private static final String STATUSES = "statuses";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String AREA = "area";

    private EntityManager em;

    public FaReportDocumentDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<FaReportDocumentEntity> getHistoryOfFaReport(FaReportDocumentEntity faReportEntity, List<FaReportDocumentEntity> reportsList) {
        if(faReportEntity == null){
            return null;
        }
        reportsList.add(faReportEntity);

        // Find reports that refer to this report
        FluxReportIdentifierEntity repId = faReportEntity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next();
        FaReportDocumentEntity reportThatRefersToThisOne = findFaReportByRefIdAndRefScheme(repId.getFluxReportIdentifierId(), repId.getFluxReportIdentifierSchemeId());
        if(reportThatRefersToThisOne != null && !alreadyExistsInList(reportsList, reportThatRefersToThisOne)){
            getHistoryOfFaReport(reportThatRefersToThisOne, reportsList);
        }

        // Find reports that this report refers to
        String repRefId = faReportEntity.getFluxReportDocument().getReferenceId();
        String repRefSchemeId = faReportEntity.getFluxReportDocument().getReferenceSchemeId();
        if(StringUtils.isNotEmpty(repRefId) && StringUtils.isNotEmpty(repRefSchemeId)){
            FaReportDocumentEntity referredReport = findFaReportByIdAndScheme(repRefId, repRefSchemeId);
            if(referredReport != null && !alreadyExistsInList(reportsList, referredReport)){
                getHistoryOfFaReport(referredReport, reportsList);
            }
        }
        return reportsList;
    }

    private boolean alreadyExistsInList(List<FaReportDocumentEntity> reportsList, FaReportDocumentEntity reportThatRefersToThisOne) {
        for (FaReportDocumentEntity faReportDocumentEntity : reportsList) {
            if(faReportDocumentEntity.equals(reportThatRefersToThisOne)){
                return true;
            }
        }
        return false;
    }

    /**
     * Load FaReportDocument by one or more Report identifiers
     *
     * @param reportId
     * @param schemeId
     * @return FaReportDocumentEntity
     * @throws ServiceException
     */
    public FaReportDocumentEntity findFaReportByIdAndScheme(String reportId, String schemeId) {
        TypedQuery query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_FA_ID_AND_SCHEME, FaReportDocumentEntity.class);
        query.setParameter(REPORT_ID, reportId);
        query.setParameter(SCHEME_ID, schemeId);
        FaReportDocumentEntity singleResult;
        try {
            singleResult = (FaReportDocumentEntity) query.getSingleResult();
        } catch (NoResultException ex){
            singleResult = null; // no need to log this exception!
        }
        return singleResult;
    }

    /**
     * Load FaReportDocument by one or more Report identifiers
     *
     * @param reportRefId
     * @param refSchemId
     * @return FaReportDocumentEntity
     * @throws ServiceException
     */
    public FaReportDocumentEntity findFaReportByRefIdAndRefScheme(String reportRefId, String refSchemId) {
        TypedQuery query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_REF_FA_ID_AND_SCHEME, FaReportDocumentEntity.class);
        query.setParameter(REPORT_REF_ID, reportRefId);
        query.setParameter(SCHEME_REF_ID, refSchemId);
        FaReportDocumentEntity singleResult;
        try {
            singleResult = (FaReportDocumentEntity) query.getSingleResult();
        } catch (NoResultException ex) {
            singleResult = null; // no need to log this exception!
        }
        return singleResult;
    }

    public List<FaReportDocumentEntity> loadReports(String tripId, String consolidated) {
        return loadReports(tripId, consolidated, null, null);
    }

    public List<FaReportDocumentEntity> loadReports(String tripId, String consolidated, Instant startDate, Instant endDate) {

        Set<String> statuses = new HashSet<>();
        statuses.add(FaReportStatusType.NEW.name());
        if ("N".equals(consolidated) || consolidated == null) {
            statuses.add(FaReportStatusType.UPDATED.name());
            statuses.add(FaReportStatusType.CANCELED.name());
            statuses.add(FaReportStatusType.DELETED.name());
        }
        Query query = getEntityManager().createNamedQuery(FaReportDocumentEntity.LOAD_REPORTS, FaReportDocumentEntity.class);
        query.setParameter(TRIP_ID, tripId);
        query.setParameter(STATUSES, statuses);

        query.setParameter(START_DATE, Instant.ofEpochSecond(-30_610_224_000L)); // The year 1000
        query.setParameter(END_DATE, Instant.ofEpochSecond(32_503_680_000L)); // The year 3000

        if (startDate != null) {
            query.setParameter(START_DATE, startDate);
        }
        if (endDate != null) {
            query.setParameter(END_DATE, endDate);
        }

        return query.getResultList();
    }

    public List<FaReportDocumentEntity> findReportsByTripId(String tripId, Geometry multipolygon){
        Query query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_FA_DOCS_BY_TRIP_ID, FaReportDocumentEntity.class);
        query.setParameter(TRIP_ID, tripId);
        query.setParameter(AREA, multipolygon);
        return query.getResultList();
    }

    public List<FaReportDocumentEntity> findReportsByIdsList(List<Integer> ids){
        Query query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_FA_IDS_LIST, FaReportDocumentEntity.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    public List<FaReportDocumentEntity> loadCanceledAndDeletedReports(List<FaReportDocumentEntity> reports) {
        List<Integer> idsOfCancelledDeletedReports = new ArrayList<>();
        for (FaReportDocumentEntity report : reports) {
            populateDeletingAndCancellationIds(report.getFishingActivities(), idsOfCancelledDeletedReports);
        }
        if (CollectionUtils.isNotEmpty(idsOfCancelledDeletedReports)) {
            return findReportsByIdsList(idsOfCancelledDeletedReports);
        }
        return null;
    }

    private void populateDeletingAndCancellationIds(Set<FishingActivityEntity> fishingActivities, List<Integer> idsOfCancelledDeletedReports) {
        for (FishingActivityEntity fishingActivity : Utils.safeIterable(fishingActivities)) {
            if(fishingActivity.getCanceledBy() != null){
                idsOfCancelledDeletedReports.add(fishingActivity.getCanceledBy());
            }
            if(fishingActivity.getDeletedBy() != null){
                idsOfCancelledDeletedReports.add(fishingActivity.getDeletedBy());
            }
        }
    }
}