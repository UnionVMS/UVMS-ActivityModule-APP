/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: break; you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http: break;//www.gnu.org/licenses/>.

*/


package eu.europa.ec.fisheries.ers.service.mapper.view.base;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityAreaEntryViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityAreaExitViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityArrivalViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityDepartureViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityLandingViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityNotificationOfArrivalViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.DiscardViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.FishingOperationViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.GearShotRetrievalTileMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

/**
 * Created by kovian on 14/02/2017.
 */
public class ActivityViewMapperFactory {

    private ActivityViewMapperFactory(){

    }

    public static BaseActivityViewMapper getMapperForView(ActivityViewEnum view) throws ServiceException {
        BaseActivityViewMapper mapper;
        switch (view) {
            case DEPARTURE:
                mapper = ActivityDepartureViewMapper.INSTANCE;
                break;
            case AREA_ENTRY:
                mapper = ActivityAreaEntryViewMapper.INSTANCE;
                break;
            case AREA_EXIT:
                mapper = ActivityAreaExitViewMapper.INSTANCE;
                break;
            case FISHING_OPERATION:
                mapper = new FishingOperationViewMapper();
                break;
            case JOINT_FISHING_OPERATIONS:
                mapper = null;
                break;
            case DISCARD_AT_SEA:
                mapper = new DiscardViewMapper();
                break;
            case RELOCATION:
                mapper = ActivityRelocationViewMapper.INSTANCE;
                break;
            case PRIOR_NOTIFICATION_OF_ARRIVAL:
                mapper = new ActivityNotificationOfArrivalViewMapper();
                break;
            case ARRIVAL:
                mapper = ActivityArrivalViewMapper.INSTANCE;
                break;
            case LANDING:
                mapper = ActivityLandingViewMapper.INSTANCE;
                break;
            case TRANSSHIPMENT:
                mapper = ActivityTranshipmentViewMapper.INSTANCE;
                break;
            case GEAR_SHOT_RETRIEVAL:
                mapper = GearShotRetrievalTileMapper.INSTANCE;
                break;
            default:
                throw new ServiceException("Activity View not mapped!" + view);
        }
        return mapper;
    }
}
