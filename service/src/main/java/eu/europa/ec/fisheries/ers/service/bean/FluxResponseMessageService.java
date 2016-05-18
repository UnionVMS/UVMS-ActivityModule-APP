package eu.europa.ec.fisheries.ers.service.bean;

import un.unece.uncefact.data.standard.fluxfareportmessage._1.FLUXFAReportMessage;

/**
 * Created by padhyad on 5/13/2016.
 */
public interface FluxResponseMessageService {

    void saveFluxFishingActivityReport(FLUXFAReportMessage fluxFaReportMessage);
}
