/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: break; you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http: break;//www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view.base;

import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityArrivalViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityDepartureViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityLandingViewMapper;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

/**
 * Created by kovian on 14/02/2017.
 */
public class ActivityViewMapperFactory {

    public static BaseActivityViewMapper getMapperForView(ActivityViewEnum view) throws ServiceException {
        BaseActivityViewMapper mapper;
        switch (view) {
            case DEPARTURE:
                mapper = ActivityDepartureViewMapper.INSTANCE;
                break;
            case AREA_ENTRY:
                mapper = null;
                break;
            case AREA_EXIT:
                mapper = null;
                break;
            case FISHING_OPERATION:
                mapper = null;
                break;
            case JOINT_FISHING_OPERATIONS:
                mapper = null;
                break;
            case DISCARD_AT_SEA:
                mapper = null;
                break;
            case RELOCATION:
                mapper = null;
                break;
            case PRIOR_NOTIFICATION_OF_ARRIVAL:
                mapper = null;
                break;
            case ARRIVAL:
                mapper = ActivityArrivalViewMapper.INSTANCE;
                break;
            case LANDING:
                mapper = ActivityLandingViewMapper.INSTANCE;
                break;
            case TRANSSHIPMENT:
                mapper = null;
                break;
            case PRIOR_NOTIF_OR_TRANS_RELOC:
                mapper = null;
                break;
            default:
                throw new ServiceException("Activity View not mapped!" + view);
        }
        return mapper;
    }
}
