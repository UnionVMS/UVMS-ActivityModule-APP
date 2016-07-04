package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;

import java.util.List;

/**
 * Created by padhyad on 5/13/2016.
 */
public interface FluxMessageService {

    void saveFishingActivityReportDocuments(List<FAReportDocument> faReportDocuments) throws ServiceException;
}
