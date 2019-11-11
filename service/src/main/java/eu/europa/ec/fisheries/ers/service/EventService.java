/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service;

import eu.europa.ec.fisheries.ers.activity.message.event.GetFACatchSummaryReportEvent;
import eu.europa.ec.fisheries.ers.activity.message.event.GetFishingActivityForTripsRequestEvent;
import eu.europa.ec.fisheries.ers.activity.message.event.GetNonUniqueIdsRequestEvent;
import eu.europa.ec.fisheries.ers.activity.message.event.MapToSubscriptionRequestEvent;
import eu.europa.ec.fisheries.ers.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;

import javax.ejb.Local;
import javax.enterprise.event.Observes;


/**
 * Created by sanera on 07/06/2016.
 */
@Local
public interface EventService {

    void getMapToSubscriptionMessage(@Observes @MapToSubscriptionRequestEvent EventMessage message);

    void getFACatchSummaryReport(@Observes @GetFACatchSummaryReportEvent EventMessage message) throws ServiceException;

    void getNonUniqueIdsRequest(@Observes @GetNonUniqueIdsRequestEvent EventMessage message);

    void getFishingActivityForTripsRequest(@Observes @GetFishingActivityForTripsRequestEvent EventMessage message);
}
