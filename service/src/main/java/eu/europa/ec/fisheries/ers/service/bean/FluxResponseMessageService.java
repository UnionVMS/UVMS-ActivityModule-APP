package eu.europa.ec.fisheries.ers.service.bean;

import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;

import java.util.List;

/**
 * Created by padhyad on 5/13/2016.
 */
public interface FluxResponseMessageService {

    void saveFluxFishingActivityReport(List<FAReportDocument> faReportDocuments) ;
}
