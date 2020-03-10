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

import eu.europa.ec.fisheries.uvms.activity.fa.entities.LocationEntity;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class FluxLocationDao {

    private final EntityManager em;

    public FluxLocationDao(EntityManager em) {
        this.em = em;
    }

    public LocationEntity findLocation(IDType idType) {
        if(idType == null) {
            return null;
        }
        TypedQuery<LocationEntity> query = em.createNamedQuery(LocationEntity.LOOKUP_LOCATION, LocationEntity.class);
        query.setParameter("identifier", idType.getValue());
        query.setParameter("schemeId", idType.getSchemeID());
        LocationEntity locationEntity = null;
        try {
            locationEntity = query.getSingleResult();
        } catch (NoResultException ignore) {}
        return locationEntity;
    }

}
