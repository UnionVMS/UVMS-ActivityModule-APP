/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.fa.dao;

import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by sanera on 25/08/2016.
 */
public class FishingTripIdentifierDao extends AbstractDAO<FishingTripIdentifierEntity> {

    private EntityManager em;

    private static final String VESSEL_ID = "vesselId";
    private static final String VESSEL_SCHEME_ID = "vesselSchemeId";
    private static final String TRIP_ID = "tripId";

    public FishingTripIdentifierDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public FishingTripIdentifierEntity getCurrentTrip(String vesselId, String vesselSchemeId) {
        TypedQuery query = getEntityManager().createNamedQuery(FishingTripIdentifierEntity.FIND_CURRENT_TRIP, FishingTripIdentifierEntity.class);
        query.setParameter(VESSEL_ID, vesselId);
        query.setParameter(VESSEL_SCHEME_ID, vesselSchemeId);
        query.setMaxResults(1);
        List<FishingTripIdentifierEntity> fishingTripIdentifies = query.getResultList();
        if (fishingTripIdentifies != null && !fishingTripIdentifies.isEmpty()) {
            return fishingTripIdentifies.get(0);
        } else {
            return null;
        }
    }

    public List<FishingTripIdentifierEntity> getPreviousTrips(String vesselId, String vesselSchemeId, String tripId, Integer limit) {
        TypedQuery query = getEntityManager().createNamedQuery(FishingTripIdentifierEntity.FIND_PREVIOUS_TRIP, FishingTripIdentifierEntity.class);
        query.setParameter(VESSEL_ID, vesselId);
        query.setParameter(VESSEL_SCHEME_ID, vesselSchemeId);
        query.setParameter(TRIP_ID, tripId);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<FishingTripIdentifierEntity> getNextTrips(String vesselId, String vesselSchemeId, String tripId, Integer limit) {
        TypedQuery query = getEntityManager().createNamedQuery(FishingTripIdentifierEntity.FIND_NEXT_TRIP, FishingTripIdentifierEntity.class);
        query.setParameter(VESSEL_ID, vesselId);
        query.setParameter(VESSEL_SCHEME_ID, vesselSchemeId);
        query.setParameter(TRIP_ID, tripId);
        query.setMaxResults(limit);
        return query.getResultList();
    }


    public String getCurrentTrip(){
        Query query =  getEntityManager().createNamedQuery(FishingTripIdentifierEntity.FIND_CURRENT_TRTIPID);
       return (String) query.getSingleResult();
    }


    public List<Object[]> getFishingTripsBefore(String tripID,int numberOfTripsBeforeAndAfter){
        Query query =  getEntityManager().createNamedQuery(FishingTripIdentifierEntity.FIND_LESS_THAN_TRIPID);
        query.setParameter("fishingTripId",tripID);
        query.setMaxResults(numberOfTripsBeforeAndAfter);

        return query.getResultList();
    }

    public List<Object[]> getFishingTripsAfter(String tripID,int numberOfTripsBeforeAndAfter){
        Query query = getEntityManager().createNamedQuery(FishingTripIdentifierEntity.FIND_GREATER_THAN_TRTIPID);
        query.setParameter("fishingTripId",tripID);
        query.setMaxResults(numberOfTripsBeforeAndAfter+1); // Add one because you need to include the referenced tripId as well

        return query.getResultList();
    }

}
