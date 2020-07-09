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

import eu.europa.ec.fisheries.ers.fa.entities.ChronologyData;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.service.dao.AbstractDAO;
import eu.europa.ec.fisheries.uvms.commons.service.dao.QueryParameter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.stream.Stream;

/**
 * Created by sanera on 25/08/2016.
 */
public class FishingTripIdentifierDao extends AbstractDAO<FishingTripIdentifierEntity> {

    private EntityManager em;

    private static final String VESSEL_GUID = "vesselGuid";
    private static final String TRIP_ID = "tripId";
    private static final String SELECTED_TRIP_START_DATE = "selectedTripStartDate";
    private static final String SELECTED_TRIP_END_DATE = "selectedTripEndDate";

    public FishingTripIdentifierDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Date getSelectedTripStartDate(String tripId) {
        TypedQuery<Date> query = getEntityManager().createNamedQuery(FishingTripIdentifierEntity.FIND_SELECTED_TRIP_START_DATE, Date.class);
        query.setParameter(TRIP_ID, tripId);
        return query.getSingleResult();
    }

    public Stream<ChronologyData> getPreviousTrips(String vesselGuid, Date startDate, Integer limit) {
        return getTrips(FishingTripIdentifierEntity.FIND_PREVIOUS_TRIPS, QueryParameter.with(VESSEL_GUID, vesselGuid).and(SELECTED_TRIP_START_DATE, startDate), limit);
    }

    public Stream<ChronologyData> getNextTrips(String vesselGuid, Date startDate, Integer limit) {
        return getTrips(FishingTripIdentifierEntity.FIND_NEXT_TRIPS, QueryParameter.with(VESSEL_GUID, vesselGuid).and(SELECTED_TRIP_START_DATE, startDate), limit);
    }

    public Stream<ChronologyData> getTripsBetween(String connectId, Date startDate,Date endDate, Integer limit) {
        return getTrips(FishingTripIdentifierEntity.FIND_TRIPS_BETWEEN, QueryParameter.with(VESSEL_GUID, connectId).and(SELECTED_TRIP_START_DATE, startDate).and(SELECTED_TRIP_END_DATE, endDate), limit);
    }

    public Stream<ChronologyData> getNextConcurrentTrips(String tripId, String vesselGuid, Date startDate, Integer limit) {
        return getTrips(FishingTripIdentifierEntity.FIND_NEXT_CONCURRENT_TRIPS, QueryParameter.with(VESSEL_GUID, vesselGuid).and(SELECTED_TRIP_START_DATE, startDate).and(TRIP_ID, tripId), limit);
    }

    public Stream<ChronologyData> getPreviousConcurrentTrips(String tripId, String vesselGuid, Date startDate, Integer limit) {
        return getTrips(FishingTripIdentifierEntity.FIND_PREVIOUS_CONCURRENT_TRIPS, QueryParameter.with(VESSEL_GUID, vesselGuid).and(SELECTED_TRIP_START_DATE, startDate).and(TRIP_ID, tripId), limit);
    }

    private Stream<ChronologyData> getTrips(String queryName, QueryParameter params, int limit) {
        TypedQuery<Object[]> query = getEntityManager().createNamedQuery(queryName, Object[].class);
        params.parameters().forEach(query::setParameter);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(obj -> new ChronologyData(obj[0].toString(), DateUtils.dateToString((Date) obj[1])));
    }
}
