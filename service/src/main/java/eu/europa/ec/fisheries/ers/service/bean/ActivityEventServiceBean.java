/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.activity.message.consumer.bean.ActivityErrorMessageServiceBean;
import eu.europa.ec.fisheries.ers.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.ers.activity.message.event.GetFishingActivityForTripsRequestEvent;
import eu.europa.ec.fisheries.ers.activity.message.event.GetNonUniqueIdsRequestEvent;
import eu.europa.ec.fisheries.ers.activity.message.event.MapToSubscriptionRequestEvent;
import eu.europa.ec.fisheries.ers.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.ers.activity.message.producer.SubscriptionProducerBean;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.EventService;
import eu.europa.ec.fisheries.ers.service.mapper.SubscriptionMapper;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.MapToSubscriptionRequest;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataRequest;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

@LocalBean
@Stateless
@Slf4j
public class ActivityEventServiceBean implements EventService {

    private static final String GOT_JMS_INSIDE_ACTIVITY_TO_GET = "Got JMS inside Activity to get ";

    @Inject
    @ActivityMessageErrorEvent
    private Event<EventMessage> errorEvent;

    @EJB
    private ActivityMatchingIdsServiceBean matchingIdsService;

    @EJB
    private ActivityService activityServiceBean;

    @EJB
    private ActivityErrorMessageServiceBean producer;

    @EJB
    private SubscriptionProducerBean subscriptionProducer;


    @Resource(mappedName =  "java:/" + MessageConstants.QUEUE_RULES)
    private Destination rulesQueue;

    @Override
    public void getMapToSubscriptionMessage(@Observes @MapToSubscriptionRequestEvent EventMessage message) {
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "MapToSubscriptionRequestEvent");
        try {
            TextMessage jmsMessage = message.getJmsMessage();
            String jmsCorrelationID = jmsMessage.getJMSMessageID();
            String messageReceived = jmsMessage.getText();
            SubscriptionDataRequest subscriptionDataRequest = null;
            MapToSubscriptionRequest baseRequest = JAXBUtils.unMarshallMessage(messageReceived, MapToSubscriptionRequest.class);
            switch (baseRequest.getMessageType()){
                case FLUX_FA_QUERY_MESSAGE:
                    FLUXFAQueryMessage fluxfaQueryMessage = JAXBUtils.unMarshallMessage(baseRequest.getRequest(), FLUXFAQueryMessage.class);
                    subscriptionDataRequest = SubscriptionMapper.mapToSubscriptionDataRequest(fluxfaQueryMessage.getFAQuery());
                    break;
                case FLUX_FA_REPORT_MESSAGE:
                    FLUXFAReportMessage fluxFAReportMessage = JAXBUtils.unMarshallMessage(baseRequest.getRequest(), FLUXFAReportMessage.class);
                    subscriptionDataRequest = SubscriptionMapper.mapToSubscriptionDataRequest(fluxFAReportMessage);
                    break;
                    default:
                        sendError(message, new IllegalArgumentException("VERBODEN VRUCHT"));
            }
            subscriptionProducer.sendMessageWithSpecificIds(JAXBUtils.marshallJaxBObjectToString(subscriptionDataRequest), rulesQueue , null, jmsCorrelationID);
        } catch ( JAXBException | JMSException e) {
            sendError(message, e);
        }
    }

    @Override
    public void getNonUniqueIdsRequest(@Observes @GetNonUniqueIdsRequestEvent EventMessage message){
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "Non Matching Ids of different Tables : ");
        try {
            log.debug(message.getJmsMessage().getText());
            GetNonUniqueIdsRequest getNonUniqueIdsRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetNonUniqueIdsRequest.class);
            GetNonUniqueIdsResponse faCatchSummaryReportResponse = matchingIdsService.getMatchingIdsResponse(getNonUniqueIdsRequest.getActivityUniquinessLists());
            String response = JAXBMarshaller.marshallJaxBObjectToString(faCatchSummaryReportResponse);
            producer.sendResponseMessageToSender(message.getJmsMessage(), response);
        } catch (ActivityModelMarshallException | JMSException e) {
            sendError(message, e);
        }
    }

    @Override
    public void getFishingActivityForTripsRequest(@Observes @GetFishingActivityForTripsRequestEvent EventMessage message){
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + " Fishing activities related to trips.");
        try {
            log.debug(message.getJmsMessage().getText());
            GetFishingActivitiesForTripRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetFishingActivitiesForTripRequest.class);
            GetFishingActivitiesForTripResponse response = activityServiceBean.getFaAndTripIdsFromTripIds(request.getFaAndTripIds());
            String responseStr = JAXBMarshaller.marshallJaxBObjectToString(response);
            producer.sendResponseMessageToSender(message.getJmsMessage(), responseStr);
        } catch (ActivityModelMarshallException | JMSException | ServiceException e) {
            sendError(message, e);
        }
    }

    private void sendError(EventMessage message, Exception e) {
        log.error("[ Error in activity module. ] ", e);
        errorEvent.fire(new EventMessage(message.getJmsMessage(), ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Exception in activity [ " + e.getMessage())));
    }

}
