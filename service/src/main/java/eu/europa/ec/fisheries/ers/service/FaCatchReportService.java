/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service;

import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

/**
 * Created by sanera on 17/01/2017.
 */
public interface FaCatchReportService {



   FACatchSummaryDTO getCatchSummaryReport(FishingActivityQuery query, boolean isLanding) throws ServiceException;

   FACatchSummaryReportResponse getFACatchSummaryReportResponse(FishingActivityQuery query) throws ServiceException;

   FACatchDetailsDTO getCatchDetailsScreen(String tripId) throws ServiceException;

   //FACatchSummaryDTO getCatchSummaryReportForLandingAndPresentation(FishingActivityQuery query) throws ServiceException;
}
