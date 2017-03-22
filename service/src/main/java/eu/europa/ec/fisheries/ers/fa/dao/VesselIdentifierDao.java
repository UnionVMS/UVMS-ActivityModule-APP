/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.fa.dao;

import static eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity.FIND_LATEST_VESSEL_BY_TRIP_ID;

import javax.persistence.EntityManager;
import java.util.List;

import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import eu.europa.ec.fisheries.uvms.service.QueryParameter;

/**
 * TODO create test
 */
public class VesselIdentifierDao extends AbstractDAO<VesselIdentifierEntity> {

    private static final String TRIP_ID = "tripId";
    private EntityManager em;

    public VesselIdentifierDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<VesselIdentifierEntity> getLatestVesselIdByTrip(String tripId) throws ServiceException {
        return findEntityByNamedQuery(VesselIdentifierEntity.class,
                FIND_LATEST_VESSEL_BY_TRIP_ID, QueryParameter.with(TRIP_ID, tripId).parameters(), 1);
    }

}
