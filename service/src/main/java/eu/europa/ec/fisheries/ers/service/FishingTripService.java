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

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.CatchEvolutionDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.ChronologyTripDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.FishingActivityTypeDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.FishingTripSummaryViewDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.MessageCountDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.ReportDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.TripWidgetDto;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityReportGenerationResults;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.AttachmentResponseObject;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ForwardFAReportFromPositionRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ForwardFAReportWithLogbookRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ForwardMultipleFAReportsRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetAttachmentsForGuidAndQueryPeriod;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;

/**
 * Created by padhyad on 9/22/2016.
 */
public interface FishingTripService {

    /**
     * <p>
     * This API returns the list of chronology of selected fishing trip,
     * Additionally it also return the current trip for the vessel.
     * <p>
     * <code>if (Count == 0)</code> Then return all the previous and next
     * </p>
     *
     * @param tripId currently selected
     * @param count  number of trip Id to view
     * @return list of fishing trips
     */
    ChronologyTripDTO getChronologyOfFishingTrip(String tripId, Integer count) throws ServiceException;


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
     * @return
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
     * @return
     */
    Map<String, CatchSummaryListDTO> retrieveFaCatchesForFishingTrip(String fishingTripId);

    /**
     * Retrieve GEO data for Fishing Trip MAp
     *
     * @param tripId
     * @return
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

    TripWidgetDto getTripWidgetDto(FishingActivityEntity activityEntity, String tripId);

    /**
     * Returns list of FishingActivities for provided tripId
     *
     * @param tripId
     * @return List<FishingActivityEntity> list of activities for the trip
     * @throws ServiceException
     */
    List<FishingActivityEntity> getAllFishingActivitiesForTrip(String tripId) throws ServiceException;

    FishingTripResponse filterFishingTrips(FishingActivityQuery query) throws ServiceException;

    CatchEvolutionDTO retrieveCatchEvolutionForFishingTrip(String fishingTripId) throws ServiceException;

    String getOwnerFluxPartyFromTripId(String tripId);

    void generateLogBookReport(String tripId, String consolidated, OutputStream destination) throws ServiceException;

    List<AttachmentResponseObject> getAttachmentsForGuidAndPeriod(GetAttachmentsForGuidAndQueryPeriod query) throws ServiceException;

    ActivityReportGenerationResults forwardMultipleFaReports(ForwardMultipleFAReportsRequest request) throws ServiceException;

    ActivityReportGenerationResults forwardFaReportWithLogbook(ForwardFAReportWithLogbookRequest request) throws ServiceException;

    ActivityReportGenerationResults forwardFAReportFromPosition(ForwardFAReportFromPositionRequest request) throws ServiceException;
}
