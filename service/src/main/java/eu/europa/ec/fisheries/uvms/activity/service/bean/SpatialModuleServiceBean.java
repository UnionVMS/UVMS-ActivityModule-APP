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

package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.service.ModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.SpatialModuleService;
import eu.europa.ec.fisheries.uvms.spatial.client.SpatialRestClient;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.SpatialModuleMethod;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.Collection;

@Stateless
@Transactional
@Slf4j
public class SpatialModuleServiceBean extends ModuleService implements SpatialModuleService {

    @EJB
    private SpatialRestClient spatialRestClient;

    @Override
    public String getFilteredAreaGeom(Collection<AreaIdentifierType> areaIdentifiers) {
        return spatialRestClient.getFilteredAreaGeometry(areaIdentifiers);
    }

    @Override
    public String getGeometryForPortCode(String portCode) {
        return spatialRestClient.getGeometryByPortCode(portCode, SpatialModuleMethod.GET_GEOMETRY_BY_PORT_CODE);
    }
}
