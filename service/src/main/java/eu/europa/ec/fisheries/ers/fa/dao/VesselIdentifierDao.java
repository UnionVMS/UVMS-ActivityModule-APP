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

import static eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity.FIND_TRIP_VESSEL_BY_TRIP_ID;

/**
 * TODO create test
 */
public class VesselIdentifierDao{

    private static final String TRIP_ID = "tripId";
    private EntityManager em;

    public VesselIdentifierDao(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public String getLatestVesselIdByTrip(String tripId) {
        TypedQuery<String> typedQuery = getEntityManager().createNamedQuery(FIND_TRIP_VESSEL_BY_TRIP_ID, String.class);
        typedQuery.setParameter(TRIP_ID, tripId);
        return typedQuery.getResultList().stream().findFirst().orElse(null);
    }
}
