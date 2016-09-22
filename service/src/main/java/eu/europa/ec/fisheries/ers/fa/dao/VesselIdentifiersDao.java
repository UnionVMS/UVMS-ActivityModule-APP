/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
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

import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by padhyad on 9/22/2016.
 */
public class VesselIdentifiersDao extends AbstractDAO<VesselIdentifierEntity> {

    private EntityManager em;

    private static String TRIP_ID = "tripId";

    public VesselIdentifiersDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<VesselIdentifierEntity> getLatestVesselIdByTrip(String tripId) {
        TypedQuery query = getEntityManager().createNamedQuery(VesselIdentifierEntity.FIND_LATEST_VESSEL_BY_TRIP_ID, VesselIdentifierEntity.class);
        query.setParameter(TRIP_ID, tripId);
        query.setMaxResults(1);
        return query.getResultList();
    }
}
