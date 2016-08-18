/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FaReportDocumentDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.ReportDTO;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

import java.util.List;

/**
 * Created by sanera on 29/06/2016.
 */

public interface ActivityService {
     List<FishingActivityReportDTO> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException;

     List<ReportDTO> getFishingActivityReportForFishingTrip(String fishingTripId) throws ServiceException;


     /**
      * <p>
      *   This service returns the list of corrections (e.g. deletes, cancels, updates) received previously for a Fishing Activity report
      *   Corrections are identified by the <code>referenceId</code> of the selected <code>FaReportDocumentId</code> recursively.
      * </p>
      *
      * @param selectedFaReportId selected FA report Id
      * @return List<FaReportCorrectionDTO> list of corrections made
      * @throws ServiceException Exception
      */
     List<FaReportCorrectionDTO> getFaReportCorrections(String selectedFaReportId) throws ServiceException;

     /**
      * <p>
      *     This service returns the Fishing activity report details. It contains the complete detail of the fishing activity
      *     along with catches, flux location, trips, vessels identity, contact address Etc.
      * </p>
      * @param faReportDocumentId unique identifier of the FA Report
      * @return FaReportDocumentDetailsDTO
      * @throws ServiceException Exception
      */
     FaReportDocumentDetailsDTO getFaReportDocumentDetails(String faReportDocumentId) throws ServiceException;

}