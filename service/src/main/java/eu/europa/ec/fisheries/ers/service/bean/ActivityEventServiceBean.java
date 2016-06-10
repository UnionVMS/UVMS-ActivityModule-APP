package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.service.EventService;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFLUXFAReportMessageEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportMessageRequest;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import un.unece.uncefact.data.standard.fluxfareportmessage._1.FLUXFAReportMessage;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;


@LocalBean
@Stateless
public class ActivityEventServiceBean implements EventService {
    final static Logger LOG = LoggerFactory.getLogger(ActivityEventServiceBean.class);

    private @EJB  FluxResponseMessageService repository;

    @Override
    public void GetFLUXFAReportMessage(@Observes @GetFLUXFAReportMessageEvent EventMessage message) {
        LOG.info("inside Activity module GetFLUXFAReportMessage");
        try {
            SetFLUXFAReportMessageRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetFLUXFAReportMessageRequest.class);
            LOG.info("ActivityModuleRequest unmarshalled");
            if(baseRequest==null){
                LOG.error("Unmarshalled SetFLUXFAReportMessageRequest is null. Something went wrong");
                return;
            }

            FLUXFAReportMessage fluxFAReportMessage =extractFLUXFAReportMessage(baseRequest.getRequest());

            repository.saveFishingActivityReportDocuments(fluxFAReportMessage.getFAReportDocuments());

        } catch (ModelMarshallException e) {
            LOG.error("xception while trying to unmarshall SetFLUXFAReportMessageRequest in Activity");
            e.printStackTrace();
        } catch (ServiceException e) {
            LOG.error("xception while trying to saveFishingActivityReportDocuments in Activity");
            e.printStackTrace();
        }
    }

    public FLUXFAReportMessage extractFLUXFAReportMessage(String request) throws ModelMarshallException{
        JAXBContext jc = null;
        FLUXFAReportMessage fluxFAReportMessage = null;
        try {
            jc = JAXBContext.newInstance(FLUXFAReportMessage.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StringReader sr = new StringReader(request);
            StreamSource source = new StreamSource(sr);
            long before = System.currentTimeMillis();
             fluxFAReportMessage = (FLUXFAReportMessage) unmarshaller.unmarshal(source);
        } catch (JAXBException | NullPointerException e) {
            throw new ModelMarshallException("[Exception while trying to unmarshall FLUXFAReportMessage in Activity ]", e);
        }
       return fluxFAReportMessage;
    }
}
