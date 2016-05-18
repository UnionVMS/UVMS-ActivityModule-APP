package eu.europa.ec.fisheries.ers.service.bean;

import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;

import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by padhyad on 5/13/2016.
 */
@Stateless
@Transactional
@Slf4j
public class FluxResponseMessageServiceBean implements FluxResponseMessageService {

    @Override
    public void saveFluxFishingActivityReport(List<FAReportDocument> faReportDocuments) {

    }
}
