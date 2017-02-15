package eu.europa.ec.fisheries.ers.service;

import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

/**
 * Created by sanera on 17/01/2017.
 */
public interface FaCatchReportService {

   void getCatchSummaryReport(FishingActivityQuery query) throws ServiceException;
}
