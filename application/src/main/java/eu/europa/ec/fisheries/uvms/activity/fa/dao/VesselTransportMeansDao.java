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

package eu.europa.ec.fisheries.uvms.activity.fa.dao;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.commons.service.dao.AbstractDAO;
import eu.europa.ec.fisheries.uvms.commons.service.dao.QueryParameter;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.List;

public class VesselTransportMeansDao extends AbstractDAO<VesselTransportMeansEntity> {

    private EntityManager em;

    public VesselTransportMeansDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public VesselTransportMeansEntity findLatestVesselByTripId(String tripId) {
        VesselTransportMeansEntity vesselTransportMeansEntity = null;
        List<VesselTransportMeansEntity> byNamedQuery = findEntityByNamedQuery(
                VesselTransportMeansEntity.class,
                VesselTransportMeansEntity.FIND_LATEST_VESSEL_BY_TRIP_ID,
                QueryParameter.with("tripId", tripId).parameters(),
                1
        );
        if (!CollectionUtils.isEmpty(byNamedQuery)) {
            vesselTransportMeansEntity = byNamedQuery.get(0);
        }
        return vesselTransportMeansEntity;
    }

}
