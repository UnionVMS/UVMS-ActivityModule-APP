package eu.europa.ec.fisheries.ers.service;

import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

/**
 * Created by sanera on 17/01/2017.
 */
public interface FaCatchReportService {

   FACatchSummaryDTO getCatchSummaryReportForWeb(FishingActivityQuery query) throws ServiceException;

   FACatchSummaryReportResponse getFACatchSummaryReportResponse(FishingActivityQuery query) throws ServiceException;
}
