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
package eu.europa.ec.fisheries.ers.service;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.service.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.ActivityViewEnum;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivityForTripIds;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripResponse;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;

import java.util.List;

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
    FilterFishingActivityReportResultDTO getFishingActivityListByQuery(FishingActivityQuery query, List<Dataset> datasets) throws ServiceException;

    /**
     * returns fishing Activity reports list based on filters as a List of strings.
     * 
     * @param query
     * @param datasets
     * @return
     * @throws ServiceException
     */
    List<String> getFishingActivityListByQueryAsStringArray(FishingActivityQuery query, List<Dataset> datasets) throws ServiceException;


    /**
     * <p>
     * This service returns the list of corrections (e.g. deletes, cancels, updates) received previously for a Fishing Activity report
     * Corrections are identified by the <code>referenceId</code> of the selected <code>FluxReportDocument.FluxReportIdentifier</code> recursively.
     * </p>
     *
     * @param refReportId selected FA report Reference Id
     * @param refSchemeId selected FA scheme Reference Id
     * @throws ServiceException Exception
     */
    List<FaReportCorrectionDTO> getFaReportHistory(String refReportId, String refSchemeId) throws ServiceException;

    boolean checkAndEnrichIfVesselFiltersArePresent(FishingActivityQuery query) throws ServiceException;

    FishingActivityViewDTO getFishingActivityForView(Integer activityId, String tripId, Integer reportId, List<Dataset> datasets, ActivityViewEnum view, boolean withHistory) throws ServiceException;

    GetFishingActivitiesForTripResponse getFaAndTripIdsFromTripIds(List<FishingActivityForTripIds> faAndTripIds) throws ServiceException;

    int getPreviousFishingActivity(int fishingActivityId);

    int getNextFishingActivity(int fishingActivityId);

    FaReportDocumentEntity findFaReportByFluxReportIdentifierRefIdAndRefScheme(String reportRefId, String schemeId);

    List<FaReportDocumentEntity> findFaReportDocumentsByTripId(String tripId, Boolean consolidated);
}