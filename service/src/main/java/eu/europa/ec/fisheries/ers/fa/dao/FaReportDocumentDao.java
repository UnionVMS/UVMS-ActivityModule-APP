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
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

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

    private static final String REPORT_ID = "reportId";
    private static final String SCHEME_ID = "schemeId";
    private static final String TRIP_ID = "tripId";

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
        //TypedQuery query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_TRIP_ID, FaReportDocumentEntity.class);
        //query.setParameter(TRIP_ID, tripId);
        //List<FaReportDocumentEntity> entities = query.getResultList();

        /*criteria.createAlias("fishingActivities", "fishActivities");
        criteria.createAlias("fishActivities.fishingTrips", "fishTrips");
        criteria.createAlias("fishTrips.fishingTripIdentifiers", "fishTripsIdentifier");
        //criteria.createAlias("fluxReportDocument.fluxReportIdentifiers", "fluxIdentifierList");
        criteria.add(Restrictions.and(
                        Restrictions.eq("fishTripsIdentifier.tripId", tripId)));
        //criteria.setProjection(Projections.projectionList()
        //                .add(Projections.distinct(Projections.property("fluxIdentifierList"))));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        /*Criteria fluxReportRootedCriteria   = criteria.createCriteria("fluxReportDocument");
        fluxReportRootedCriteria.setProjection(Projections.projectionList()
                .add(Projections.distinct(Projections.property("fluxReportIdentifiers"))));*/


        /*DetachedCriteria subcriteria_1 = DetachedCriteria.forClass(FluxReportDocumentEntity.class);
        subcriteria_1.setProjection(Projections.projectionList()
                //.add(Projections.distinct(Projections.property("fluxReportDoc.fluxReportIdentifiers")))
                .add(Projections.property("fluxReportIdentifiers")));

        DetachedCriteria subcriteria_2 = DetachedCriteria.forClass(FluxReportIdentifierEntity.class);
        subcriteria_2.setProjection(Projections.projectionList()
                //.add(Projections.distinct(Projections.property("fluxReportDoc.fluxReportIdentifiers")))
                .add(Projections.property("fluxReportDocument")));

        criteria.add(Subqueries.propertyIn("fluxReportDocument", subcriteria_1));
        criteria.add(Subqueries.propertyIn("fluxReportIdentifiers", subcriteria_2));

        criteria.createAlias("fishingActivities", "fishActivities");
        criteria.createAlias("fishActivities.fishingTrips", "fishTrips");
        criteria.createAlias("fishTrips.fishingTripIdentifiers", "fishTripsIdentifier");
        criteria.add(Restrictions.and(Restrictions.eq("fishTripsIdentifier.tripId", tripId)));

        List<FaReportDocumentEntity> entities = criteria.list();*/

        Session session = (getEntityManager().unwrap(Session.class)).getSessionFactory().getCurrentSession();
        //Criteria criteria = session.createCriteria(FaReportDocumentEntity.class);

        String identifiersSelectorQuery =  "SELECT DISTINCT fluxIdentifier from FluxReportIdentifierEntity fluxIdentifier " +
                "JOIN fluxIdentifier.fluxReportDocument fluxRepDoc "  +
                "JOIN fluxRepDoc.faReportDocument faRepDocument " +
                "JOIN faRepDocument.fishingActivities factivity " +
                "JOIN factivity.fishingTrips fishingtrip " +
                "JOIN fishingtrip.fishingTripIdentifiers ftidentifier " +
                "WHERE ftidentifier.tripId = 'NOR-TRP-20160517234053706' ";

        String faDocumentQuery = "SELECT DISTINCT fareport FROM FaReportDocumentEntity fareport " +
                "JOIN FETCH fareport.fishingActivities factivity " +
                "JOIN FETCH factivity.fishingTrips fishingtrip " +
                "JOIN FETCH fishingtrip.fishingTripIdentifiers ftidentifier " +
                "JOIN FETCH fareport.fluxReportDocument fluxreport " +
                "JOIN FETCH fluxreport.fluxReportIdentifiers fluxIdentifier " +
                "WHERE ftidentifier.tripId = 'NOR-TRP-20160517234053706' " +
                "AND fluxIdentifier IN "+"("+ identifiersSelectorQuery +")";

        List<FaReportDocumentEntity> entities = session.createQuery(faDocumentQuery).list();


        return entities;
    }
}