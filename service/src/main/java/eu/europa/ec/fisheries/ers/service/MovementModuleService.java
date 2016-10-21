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

package eu.europa.ec.fisheries.ers.service;

import eu.europa.ec.fisheries.schema.movement.search.v1.MovementMapResponseType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 10/11/2016.
 */
public interface MovementModuleService {

    /**
     * Get Movements by Assets Ids, start date of movements and last date of movements
     *
     * @param vesselIds vessel guids
     * @param startDate start date
     * @param endDate end date
     * @return list of Movements
     * @throws ServiceException
     */
    List<MovementType> getMovement(List<String> vesselIds, Date startDate, Date endDate) throws ServiceException;
}
