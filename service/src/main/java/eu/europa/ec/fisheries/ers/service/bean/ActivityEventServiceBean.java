/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.service.*;
import eu.europa.ec.fisheries.ers.service.exception.ActivityModuleException;
import eu.europa.ec.fisheries.ers.service.facatch.FACatchSummaryHelper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityRequestMapper;
import eu.europa.ec.fisheries.ers.service.mapper.SubscriptionMapper;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityErrorMessageServiceBean;
import eu.europa.ec.fisheries.uvms.activity.message.event.*;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.message.producer.SubscriptionProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataRequest;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
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
    private FluxMessageService fluxMessageService;

    @EJB
    private FishingTripService fishingTripService;

    @EJB
    private FaCatchReportService faCatchReportService;

    @EJB
    private ActivityMatchingIdsServiceBean matchingIdsService;

    @EJB
    private ActivityService activityServiceBean;

    @EJB
    private ActivityErrorMessageServiceBean producer;

    @EJB
    private ActivityRulesModuleService activityRulesModuleServiceBean;

    @EJB
    private SubscriptionProducerBean subscriptionProducer;

    @EJB
    private ConcurrentFaReportSaverBean saveReportBean;

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
            subscriptionProducer.sendMessageWithSpecificIds(JAXBUtils.marshallJaxBObjectToString(subscriptionDataRequest),
                    subscriptionProducer.getDestination(), JMSUtils.lookupQueue(MessageConstants.QUEUE_RULES),null, jmsCorrelationID);
        } catch ( JAXBException | MessageException | JMSException e) {
            sendError(message, e);
        }
    }

    @Override
    public void getFishingActivityMessage(@Observes @ReceiveFishingActivityRequestEvent EventMessage eventMessage) {
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "SetFLUXFAReportOrQueryMessageRequest");
        try {
            TextMessage jmsMessage = eventMessage.getJmsMessage();
            SetFLUXFAReportOrQueryMessageRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, SetFLUXFAReportOrQueryMessageRequest.class);
            log.info("SetFLUXFAReportOrQueryMessageRequest unmarshalled");
            if(request == null){
                log.error("Unmarshalled SetFLUXFAReportOrQueryMessageRequest is null. Something went wrong during jms comm?!");
                return;
            }
            switch (eventMessage.getMethod()) {
                case GET_FLUX_FA_REPORT :
                    saveReportBean.handleFaReportSaving(request);
                    break;
                case GET_FLUX_FA_QUERY:
                    log.error("TODO : FAQUERY mappers NOT implemented yet....");
                    FLUXFAQueryMessage fluxFAQueryMessage = JAXBMarshaller.unmarshallTextMessage(request.getRequest(), FLUXFAQueryMessage.class);
                    // TODO : Implement me... Map tp real HQl/SQL query and run the query and map the results to FLUXFAReportMessage and send it to
                    FLUXFAReportMessage faRepQueryResponseAfterMapping = new FLUXFAReportMessage();
                    activityRulesModuleServiceBean.sendSyncAsyncFaReportToRules(faRepQueryResponseAfterMapping, "getTheOnValueFromSomewahre", request.getRequestType(), jmsMessage.getJMSMessageID());
                    break;
            }
        } catch (ActivityModelMarshallException | ServiceException | ActivityModuleException | JMSException e) {
            sendError(eventMessage, e);
        }
    }

    @Override
    public void getFishingTripList(@Observes @GetFishingTripListEvent EventMessage message) {
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "FishingTripIds:");
        try {
            log.debug("JMS Incoming text message: {}", message.getJmsMessage().getText());
            FishingTripRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), FishingTripRequest.class);
            log.debug("FishingTriId Request Unmarshalled");
            FishingTripResponse baseResponse = fishingTripService.filterFishingTripsForReporting(FishingActivityRequestMapper.buildFishingActivityQueryFromRequest(baseRequest));
            log.debug("FishingTripResponse ::: "+FACatchSummaryHelper.printJsonstructure(baseResponse));
            String response = JAXBMarshaller.marshallJaxBObjectToString(baseResponse);
            log.debug("FishingTriId response marshalled");
            producer.sendResponseMessageToSender(message.getJmsMessage(), response, 3_600_000L);
            log.debug("Response sent back.");
        } catch (ActivityModelMarshallException | JMSException | ServiceException | MessageException e) {
            sendError(message, e);
        }
    }


    @Override
    public void getFACatchSummaryReport(@Observes @GetFACatchSummaryReportEvent EventMessage message)  {
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "FACatchSummaryReport:");
        try {
            log.debug("JMS Incoming text message: {}", message.getJmsMessage().getText());
            FACatchSummaryReportRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), FACatchSummaryReportRequest.class);
            FACatchSummaryReportResponse faCatchSummaryReportResponse= faCatchReportService.getFACatchSummaryReportResponse(FishingActivityRequestMapper.buildFishingActivityQueryFromRequest(baseRequest));
            String response = JAXBMarshaller.marshallJaxBObjectToString(faCatchSummaryReportResponse);
            producer.sendResponseMessageToSender(message.getJmsMessage(), response);
        } catch (ActivityModelMarshallException | JMSException | ServiceException | MessageException e) {
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
        } catch (ActivityModelMarshallException | JMSException | MessageException e) {
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
        } catch (ActivityModelMarshallException | JMSException | ServiceException | MessageException e) {
            sendError(message, e);
        }
    }

    private void sendError(EventMessage message, Exception e) {
        log.error("[ Error in activity module. ] ", e);
        errorEvent.fire(new EventMessage(message.getJmsMessage(), ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Exception in activity [ " + e.getMessage())));
    }

}