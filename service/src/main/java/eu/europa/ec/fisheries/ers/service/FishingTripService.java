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

import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * Created by padhyad on 9/22/2016.
 */
public interface FishingTripService {

    /**
     * <p>
     * This API returns the list of cronology of selected fishing trip,
     * Additionally it also return the current trip for the vessel.
     *
     * <code>if (Count == 0)</code> Then return all the previous and next
     *</p>
     * @param tripId currently selected
     * @param count number of trip Id to view
     * @return list of fishing trips
     */
    CronologyTripDTO getCronologyOfFishingTrip(String tripId, Integer count);



    /**
     * Return FishingTripSummary view screen data for specified Fishing Trip ID
     *
     * @param fishingTripId
     * @return FishingTripSummaryViewDTO All of summary view data
     * @throws ServiceException
     */
    FishingTripSummaryViewDTO getFishingTripSummary(String fishingTripId) throws ServiceException;


    /**
     * get Vessel Details for Perticular fishing trip (this is for fishing trip summary view)
     *
     * @param fishingTripId
     * @return
     */
    VesselDetailsTripDTO getVesselDetailsForFishingTrip(String fishingTripId);

    /**
     * This service will return various data for specified fishing Trip ID
     *
     * @param fishingTripId         ID for which data will be returned
     * @param reportDTOList         Refernce variable to store Fishing Activity Reports for specified fishingTripId
     * @param summary Refernce variable to store short FishingTripSummary for specified fishingTripId
     * @param messagesCount         Refernce variable to store various message counts for specified fishingTripId
     * @throws ServiceException
     */
    void getFishingActivityReportAndRelatedDataForFishingTrip(String fishingTripId, List<ReportDTO> reportDTOList, Map<String,FishingActivityTypeDTO> summary, MessageCountDTO messagesCount) throws ServiceException;

    /**
     * Service that given a trip-id collects all the message summs for it and returns a MessageCountDTO object;
     *
     * @param tripId
     * @return MessageCountDTO
     */
    MessageCountDTO getMessageCountersForTripId(String tripId);
}
