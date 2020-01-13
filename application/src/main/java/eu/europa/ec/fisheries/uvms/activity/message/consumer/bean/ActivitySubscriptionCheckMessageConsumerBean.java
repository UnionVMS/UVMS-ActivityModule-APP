package eu.europa.ec.fisheries.uvms.activity.message.consumer.bean;


import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.message.producer.SubscriptionProducerBean;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.SubscriptionMapper;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.MapToSubscriptionRequest;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataRequest;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

@MessageDriven(mappedName = MessageConstants.QUEUE_MODULE_ACTIVITY, activationConfig = {
        @ActivationConfigProperty(propertyName = MessageConstants.MESSAGING_TYPE_STR, propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_TYPE_STR, propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_STR, propertyValue = MessageConstants.QUEUE_MODULE_ACTIVITY_NAME),
        @ActivationConfigProperty(propertyName = MessageConstants.MESSAGE_SELECTOR_STR, propertyValue = "messageSelector = 'SubscriptionCheck'"),
})
@Slf4j
public class ActivitySubscriptionCheckMessageConsumerBean implements MessageListener {

    @Inject
    @ActivityMessageErrorEvent
    private Event<EventMessage> errorEvent;

    @EJB
    private SubscriptionProducerBean subscriptionProducer;

    @Resource(mappedName =  "java:/" + MessageConstants.QUEUE_RULES)
    private Destination rulesQueue;

    @Override
    public void onMessage(Message message) {
        log.debug("Received subscription message");
        TextMessage textMessage = (TextMessage) message;
        try {
            String jmsMessageID = message.getJMSMessageID();
            MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
            MapToSubscriptionRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, MapToSubscriptionRequest.class);

            log.debug("Successfully parsed message to {}", MapToSubscriptionRequest.class.getName());

            if (request == null) {
                log.error("Request is null");
                return;
            }

            ActivityModuleMethod method = request.getMethod();
            if (method == null) {
                log.error("Request method is null");
                return;
            }

            if (method == ActivityModuleMethod.MAP_TO_SUBSCRIPTION_REQUEST) {
                mapToSubscriptionRequest(request, jmsMessageID, textMessage);
            } else {
                log.error("Request method {} is not implemented", method.name());
                errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Request method " + method.name() + "  is not implemented")));
            }
        } catch (ActivityModelMarshallException | ClassCastException | JMSException | JAXBException e) {
            log.error("Error when receiving message", e);
            errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Error when receiving message: " + e.getMessage())));
        }
    }

    private void mapToSubscriptionRequest(MapToSubscriptionRequest request, String jmsCorrelationId, TextMessage textMessage) throws JAXBException, JMSException {
        SubscriptionDataRequest subscriptionDataRequest = null;
        switch (request.getMessageType()) {
            case FLUX_FA_QUERY_MESSAGE:
                FLUXFAQueryMessage fluxfaQueryMessage = JAXBUtils.unMarshallMessage(request.getRequest(), FLUXFAQueryMessage.class);
                subscriptionDataRequest = SubscriptionMapper.mapToSubscriptionDataRequest(fluxfaQueryMessage.getFAQuery());
                break;
            case FLUX_FA_REPORT_MESSAGE:
                FLUXFAReportMessage fluxFAReportMessage = JAXBUtils.unMarshallMessage(request.getRequest(), FLUXFAReportMessage.class);
                subscriptionDataRequest = SubscriptionMapper.mapToSubscriptionDataRequest(fluxFAReportMessage);
                break;
            default:
                errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Message has incorrect message type " + request.getMessageType())));
        }
        String messageToSend = JAXBUtils.marshallJaxBObjectToString(subscriptionDataRequest);
        subscriptionProducer.sendMessageWithSpecificIds(messageToSend, rulesQueue , null, jmsCorrelationId);
    }

}
