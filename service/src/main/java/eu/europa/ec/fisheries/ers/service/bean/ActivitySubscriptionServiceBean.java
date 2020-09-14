package eu.europa.ec.fisheries.ers.service.bean;


import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import eu.europa.ec.fisheries.ers.service.mapper.SubscriptionMapper;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityEventQueueConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.MapToSubscriptionRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ReportingProducerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.SubscriptionDataProducerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.SubscriptionProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ForwardQueryToSubscriptionRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.MapToSubscriptionRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.MessageType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ReportToSubscription;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.constants.ConfigHelper;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataRequest;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionPermissionAnswer;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionPermissionResponse;
import eu.europa.fisheries.uvms.subscription.model.exceptions.ApplicationException;
import eu.europa.fisheries.uvms.subscription.model.mapper.SubscriptionModuleResponseMapper;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

@LocalBean
@Stateless
@Slf4j
public class ActivitySubscriptionServiceBean {

    @Inject
    @ActivityMessageErrorEvent
    private Event<EventMessage> errorEvent;

    @EJB
    private SubscriptionProducerBean subscriptionProducer;

    @EJB
    private SubscriptionDataProducerBean subscriptionDataProducerBean;

    @EJB
    private ActivityEventQueueConsumerBean activityEventQueueConsumerBean;


    @EJB
    private ActivityConsumerBean activityConsumerBean;

    @EJB
    private ReportingProducerBean reportingProducerBean;

    @EJB
    private ConfigHelper configHelper;

    public void getMapToSubscriptionMessage(@Observes @MapToSubscriptionRequestEvent EventMessage message) {
        log.info("Got JMS inside Activity to get MapToSubscriptionRequestEvent");
        try {
            TextMessage jmsMessage = message.getJmsMessage();
            String jmsCorrelationID = jmsMessage.getJMSMessageID();
            String messageReceived = jmsMessage.getText();
            boolean incoming = jmsMessage.getBooleanProperty("incoming");
            SubscriptionDataRequest subscriptionDataRequest = null;
            MapToSubscriptionRequest baseRequest = JAXBUtils.unMarshallMessage(messageReceived, MapToSubscriptionRequest.class);
            switch (baseRequest.getMessageType()){
                case FLUX_FA_QUERY_MESSAGE:
                    FLUXFAQueryMessage fluxfaQueryMessage = JAXBUtils.unMarshallMessage(baseRequest.getRequest(), FLUXFAQueryMessage.class);
                    subscriptionDataRequest = SubscriptionMapper.mapToSubscriptionDataRequest(fluxfaQueryMessage.getFAQuery(), incoming);
                    break;
                case FLUX_FA_REPORT_MESSAGE:
                    FLUXFAReportMessage fluxFAReportMessage = JAXBUtils.unMarshallMessage(baseRequest.getRequest(), FLUXFAReportMessage.class);
                    subscriptionDataRequest = SubscriptionMapper.mapToSubscriptionDataRequest(fluxFAReportMessage, incoming);
                    break;
                default:
                    sendError(message, new IllegalArgumentException("VERBODEN VRUCHT"));
            }
            subscriptionProducer.sendMessageWithSpecificIds(JAXBUtils.marshallJaxBObjectToString(subscriptionDataRequest),
                    subscriptionProducer.getDestination(), JMSUtils.lookupQueue(MessageConstants.QUEUE_RULES),null, jmsCorrelationID);
        } catch ( JAXBException | MessageException | JMSException e) {
            sendError(message, e);
        }
    }

    public void sendForwardReportToSubscriptionRequest(List<ReportToSubscription> faReports) {
        try {
            String request = ActivityModuleRequestMapper.mapToForwardReportToSubscriptionRequest(faReports);
            subscriptionDataProducerBean.sendModuleMessageWithProps(request, null,  Collections.singletonMap(MessageConstants.JMS_SUBSCRIPTION_SOURCE_PROPERTY, configHelper.getModuleName()));
        } catch (ActivityModelMarshallException | MessageException e) {
            e.printStackTrace();
        }
    }

    public void sendForwardReportToReportingRequest(List<ReportToSubscription> faReports) {
        try {
            String request = ActivityModuleRequestMapper.mapToForwardReportToSubscriptionRequest(faReports);
            Map<String, String> params = new HashMap<>();
            params.put("mainTopic", "reporting");
            params.put("subTopic", "activity");
            reportingProducerBean.sendMessageToSpecificQueueSameTx(request, reportingProducerBean.getDestination(), null,  params);
        } catch (ActivityModelMarshallException | MessageException e) {
            e.printStackTrace();
        }
    }

    public boolean requestSubscriptionForPermission(List<ReportToSubscription> faReports) {
        try {
            String request = ActivityModuleRequestMapper.mapToForwardReportToSubscriptionRequest(faReports, MessageType.FLUX_FA_REPORT_MESSAGE);
            String correlationId = subscriptionProducer.sendMessageToSpecificQueue(request, JMSUtils.lookupQueue("jms/queue/UVMSSubscriptionPermissionEvent"),
                    JMSUtils.lookupQueue(MessageConstants.QUEUE_ACTIVITY), Collections.singletonMap(MessageConstants.JMS_SUBSCRIPTION_SOURCE_PROPERTY, configHelper.getModuleName()));
            if (correlationId != null) {
                TextMessage message = activityConsumerBean.getMessage(correlationId, TextMessage.class);
                log.debug("Received response message from Subscription.");
                SubscriptionPermissionResponse subscriptionPermissionResponse = SubscriptionModuleResponseMapper.mapToSubscriptionPermissionResponse(message.getText());
                SubscriptionPermissionAnswer subscriptionCheck = subscriptionPermissionResponse.getSubscriptionCheck();
                return SubscriptionPermissionAnswer.YES.equals(subscriptionCheck);
            }
            return false;
        } catch (MessageException | JMSException | JAXBException | ActivityModelMarshallException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }

    public Optional<SubscriptionPermissionResponse> requestSubscriptionForPermission(ForwardQueryToSubscriptionRequest forwardQueryToSubscriptionRequest) {
        SubscriptionPermissionResponse subscriptionPermissionResponse = null;
        try {
            String request = ActivityModuleRequestMapper.mapToSubscriptionRequest(JAXBMarshaller.marshallJaxBObjectToString(forwardQueryToSubscriptionRequest),
                    MessageType.FLUX_FA_QUERY_MESSAGE);
            String correlationId = subscriptionProducer.sendMessageToSpecificQueue(request,
                    JMSUtils.lookupQueue("jms/queue/UVMSSubscriptionPermissionEvent"), JMSUtils.lookupQueue(MessageConstants.QUEUE_ACTIVITY),
                    Collections.singletonMap(MessageConstants.JMS_SUBSCRIPTION_SOURCE_PROPERTY, configHelper.getModuleName()));
            if (correlationId != null) {
                TextMessage message = activityConsumerBean.getMessage(correlationId, TextMessage.class);
                log.debug("Received response message from Subscription.");
                subscriptionPermissionResponse = SubscriptionModuleResponseMapper.mapToSubscriptionPermissionResponse(message.getText());
            }
            return Optional.ofNullable(subscriptionPermissionResponse);
        } catch (MessageException | JMSException | JAXBException | ActivityModelMarshallException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }

    private void sendError(EventMessage message, Exception e) {
        log.error("[ Error in activity module. ] ", e);
        errorEvent.fire(new EventMessage(message.getJmsMessage(), ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Exception in activity [ " + e.getMessage())));
    }
}
