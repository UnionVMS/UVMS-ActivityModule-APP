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

package eu.europa.ec.fisheries.uvms.activity.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchEvolutionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.FishingActivityTypeDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.FishingTripSummaryViewDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.MessageCountDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.ReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripWidgetDto;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import org.locationtech.jts.geom.Geometry;

import java.util.List;
import java.util.Map;

public interface FishingTripService {

    /**
     * Return FishingTripSummary view screen data for specified Fishing Trip ID
     *
     * @param fishingTripId
     * @return FishingTripSummaryViewDTO All of summary view data
     * @throws ServiceException
     */
    FishingTripSummaryViewDTO getFishingTripSummaryAndReports(String fishingTripId, List<Dataset> datasets) throws ServiceException;


    /**
     * get Vessel Details for Perticular fishing trip (this is for fishing trip summary view)
     *
     * @param fishingTripId
     */
    VesselDetailsDTO getVesselDetailsForFishingTrip(String fishingTripId) throws ServiceException;


    /**
     * Service that given a trip-id collects all the message summs for it and returns a MessageCountDTO object;
     *
     * @param tripId
     * @return MessageCountDTO
     */
    MessageCountDTO getMessageCountersForTripId(String tripId);

    /**
     * Retrieves all the catches for the given fishing trip;
     *
     * @param fishingTripId
     */
    Map<String, CatchSummaryListDTO> retrieveFaCatchesForFishingTrip(String fishingTripId);

    /**
     * Retrieve GEO data for Fishing Trip MAp
     *
     * @param tripId
     */
    ObjectNode getTripMapDetailsForTripId(String tripId);

    /**
     * This method will Return filtered FishingTrips which match with provided filter criterias
     *
     * @param query Filter criterias
     * @return List of unique Fishing tripIds with their Geometries
     * List of Fishing Activities which happened duriong those fishing trips
     * @throws ServiceException
     */

    FishingTripResponse filterFishingTripsForReporting(FishingActivityQuery query) throws ServiceException;


    Map<String, FishingActivityTypeDTO> populateFishingActivityReportListAndFishingTripSummary(String fishingTripId, List<ReportDTO> reportDTOList,
                                                                                               Geometry multipolygon, boolean isOnlyTripSummary) throws ServiceException;

    /**
     * Returns TripWidgetDto based on the tripId and activityId
     *
     * @param activityEntity
     * @param tripId
     * @return
     */
    TripWidgetDto getTripWidgetDto(FishingActivityEntity activityEntity, String tripId);

    FishingTripResponse filterFishingTrips(FishingActivityQuery query) throws ServiceException;

    CatchEvolutionDTO retrieveCatchEvolutionForFishingTrip(String fishingTripId) throws ServiceException;

    String getOwnerFluxPartyFromTripId(String tripId);
}
