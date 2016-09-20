/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FaReportDocumentDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 29/06/2016.
 */

public interface ActivityService {

    /**
     * returns fishing Activity reports list based on Filters
     *
     * @param query
     * @return
     * @throws ServiceException
     */
    FilterFishingActivityReportResultDTO getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException;


    /**
     * Get Current FishingTrip
     *
     * @return
     */
    String getCurrentTripId();

    /**
     * <p>
     * This service returns the list of corrections (e.g. deletes, cancels, updates) received previously for a Fishing Activity report
     * Corrections are identified by the <code>referenceId</code> of the selected <code>FaReportDocumentId</code> recursively.
     * </p>
     *
     * @param selectedFaReportId selected FA report Id
     * @return List<FaReportCorrectionDTO> list of corrections made
     * @throws ServiceException Exception
     */
    List<FaReportCorrectionDTO> getFaReportCorrections(String selectedFaReportId) throws ServiceException;


    /**
     * Return FishingTripSummary view screen data for specified Fishing Trip ID
     *
     * @param fishingTripId
     * @return FishingTripSummaryViewDTO All of summary view data
     * @throws ServiceException
     */
    FishingTripSummaryViewDTO getFishingTripSummary(String fishingTripId) throws ServiceException;


    /**
     * <p>
     * This service returns the Fishing activity report details. It contains the complete detail of the fishing activity
     * along with catches, flux location, trips, vessels identity, contact address Etc.
     * </p>
     *
     * @param faReportDocumentId unique identifier of the FA Report
     * @return FaReportDocumentDetailsDTO
     * @throws ServiceException Exception
     */
    FaReportDocumentDetailsDTO getFaReportDocumentDetails(String faReportDocumentId) throws ServiceException;

    /**
     * This service returns cronological order of Fishing Trip Ids.
     *
     * @param tripID                      TripID which will be used as a reference. Trips After and before this Id will be returned
     * @param numberOfTripsBeforeAndAfter These many trips before and after mentioned tripId will be returned
     * @return List<CronologyDTO>  List of TripIds and dates associated with the trip
     */
    List<CronologyDTO> getCronologyForTripIds(String tripID, int numberOfTripsBeforeAndAfter);


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

}