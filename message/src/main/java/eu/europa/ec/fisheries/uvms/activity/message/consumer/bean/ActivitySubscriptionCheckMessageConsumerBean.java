package eu.europa.ec.fisheries.uvms.activity.message.consumer.bean;


import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.MapToSubscriptionRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleRequest;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(mappedName = MessageConstants.QUEUE_MODULE_ACTIVITY, activationConfig = {
        @ActivationConfigProperty(propertyName = MessageConstants.MESSAGING_TYPE_STR, propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_TYPE_STR, propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_STR, propertyValue = MessageConstants.QUEUE_MODULE_ACTIVITY_NAME),
        @ActivationConfigProperty(propertyName = MessageConstants.MESSAGE_SELECTOR_STR, propertyValue = "messageSelector = 'SubscriptionCheck'"),
})
@Slf4j
public class ActivitySubscriptionCheckMessageConsumerBean implements MessageListener {

    @Inject
    @MapToSubscriptionRequestEvent
    private Event<EventMessage> mapToSubscriptionRequest;

    @Inject
    @ActivityMessageErrorEvent
    private Event<EventMessage> errorEvent;

    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = null;
        try {
            textMessage = (TextMessage) message;
            MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
            ActivityModuleRequest request;
            request = JAXBMarshaller.unmarshallTextMessage(textMessage, ActivityModuleRequest.class);
            log.debug("Message unmarshalled successfully in activity");
            if (request == null) {
                log.error("[ Request is null ]");
                return;
            }
            ActivityModuleMethod method = request.getMethod();
            if (method == null) {
                log.error("[ Request method is null ]");
                return;
            }
            switch (method) {
                case MAP_TO_SUBSCRIPTION_REQUEST:
                    mapToSubscriptionRequest.fire(new EventMessage(textMessage));
                    break;
                default:
                    log.error("[ Request method {} is not implemented ]", method.name());
                    errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "[ Request method " + method.name() + "  is not implemented ]")));
            }

        } catch (ActivityModelMarshallException | ClassCastException e) {
            log.error("[ Error when receiving message in activity: ] {}", e);
            errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Error when receiving message")));
        }
    }

}
