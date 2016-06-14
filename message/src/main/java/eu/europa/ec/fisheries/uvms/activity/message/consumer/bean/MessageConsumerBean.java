package eu.europa.ec.fisheries.uvms.activity.message.consumer.bean;


import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.activity.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFLUXFAReportMessageEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFLUXFMDRSyncMessageEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ModelMapperException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleRequest;

@MessageDriven(mappedName = MessageConstants.ACTIVITY_MESSAGE_IN_QUEUE, activationConfig = {
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = MessageConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = MessageConstants.COMPONENT_MESSAGE_IN_QUEUE_NAME)
})
public class MessageConsumerBean implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(MessageConsumerBean.class);

    @Inject
    @GetFLUXFAReportMessageEvent
    Event<EventMessage> getFLUXFAReportMessageEvent;
        
    @Inject
    @GetFLUXFMDRSyncMessageEvent
    Event<EventMessage> getFLUXFMDRSyncMessageEvent;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {

        TextMessage textMessage = null;

        try {

            textMessage = (TextMessage) message;

            LOG.info("Message received in activity");

            ActivityModuleRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, ActivityModuleRequest.class);
        //    SetFLUXFAReportMessageRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, SetFLUXFAReportMessageRequest.class);
            LOG.info("Message unmarshalled successfully in activity");

            if(request==null){
                LOG.error("[ Request is null ]");
                return;
            }

            if (request.getMethod() == null) {
                LOG.error("[ Request method is null ]");
             //   errorEvent.fire(new EventMessage(textMessage, "Error when receiving message in movement: "));
                return;
            }

            switch (request.getMethod()) {

                case GET_FLUX_FA_REPORT:
                    getFLUXFAReportMessageEvent.fire(new EventMessage(textMessage));
                    break;
                case GET_FLUX_MDR_ENTITY : 
                	 getFLUXFMDRSyncMessageEvent.fire(new EventMessage(textMessage));
                     break;

                default:
                    LOG.error("[ Request method {} is not implemented ]", request.getMethod().name());
                   // errorEvent.fire(new EventMessage(textMessage, "[ Request method " + request.getMethod().name() + "  is not implemented ]"));
            }

        } catch (ModelMapperException | NullPointerException | ClassCastException e) {
            LOG.error("[ Error when receiving message in activity: ] {}", e);
           // errorEvent.fire(new EventMessage(textMessage, "Error when receiving message in movement: " + e.getMessage()));
        }
    }

}
