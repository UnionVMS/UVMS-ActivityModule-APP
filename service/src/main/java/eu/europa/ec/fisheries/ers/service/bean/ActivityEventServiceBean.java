package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.uvms.activity.message.event.GetFLUXFAReportMessageEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportMessageRequest;
import eu.europa.ec.fisheries.ers.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;


@LocalBean
@Stateless
public class ActivityEventServiceBean implements EventService {
    final static Logger LOG = LoggerFactory.getLogger(ActivityEventServiceBean.class);
    @Override
    public void GetFLUXFAReportMessage(@Observes @GetFLUXFAReportMessageEvent EventMessage message) {
        LOG.info("inside Activity module GetFLUXFAReportMessage");
        try {
            SetFLUXFAReportMessageRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetFLUXFAReportMessageRequest.class);
            LOG.info("ActivityModuleRequest unmarshalled");
        } catch (ModelMarshallException e) {
            e.printStackTrace();
        }
    }
}
