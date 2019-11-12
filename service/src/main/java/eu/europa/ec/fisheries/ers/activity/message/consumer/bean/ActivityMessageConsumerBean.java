/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.activity.message.consumer.bean;


import eu.europa.ec.fisheries.ers.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.ers.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.ers.activity.service.ActivityRulesModuleService;
import eu.europa.ec.fisheries.ers.activity.service.ActivityService;
import eu.europa.ec.fisheries.ers.activity.service.FaCatchReportService;
import eu.europa.ec.fisheries.ers.activity.service.FishingTripService;
import eu.europa.ec.fisheries.ers.activity.service.bean.ActivityMatchingIdsServiceBean;
import eu.europa.ec.fisheries.ers.activity.service.bean.FaReportSaverBean;
import eu.europa.ec.fisheries.ers.activity.service.exception.ActivityModuleException;
import eu.europa.ec.fisheries.ers.activity.service.mapper.FishingActivityRequestMapper;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportOrQueryMessageRequest;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(mappedName = MessageConstants.QUEUE_MODULE_ACTIVITY, activationConfig = {
        @ActivationConfigProperty(propertyName = MessageConstants.MESSAGING_TYPE_STR, propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_TYPE_STR, propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_STR, propertyValue = MessageConstants.QUEUE_MODULE_ACTIVITY_NAME),
        @ActivationConfigProperty(propertyName = MessageConstants.MESSAGE_SELECTOR_STR, propertyValue = "messageSelector IS NULL"),
        @ActivationConfigProperty(propertyName = "maxMessagesPerSessions", propertyValue = "100"),
        @ActivationConfigProperty(propertyName = "initialRedeliveryDelay", propertyValue = "60000"),
        @ActivationConfigProperty(propertyName = "maximumRedeliveries", propertyValue = "3"),
        @ActivationConfigProperty(propertyName = "maxSessions", propertyValue = "10")
})
@Slf4j
public class ActivityMessageConsumerBean implements MessageListener {

    @Inject
    @ActivityMessageErrorEvent
    private Event<EventMessage> errorEvent;

    @EJB
    private FaReportSaverBean saveReportBean;

    @EJB
    private ActivityRulesModuleService activityRulesModuleServiceBean;

    @EJB
    private FishingTripService fishingTripService;

    @EJB
    private ActivityErrorMessageServiceBean producer;

    @EJB
    private FaCatchReportService faCatchReportService;

    @EJB
    private ActivityMatchingIdsServiceBean matchingIdsService;

    @EJB
    private ActivityService activityServiceBean;

    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = null;
        try {
            textMessage = (TextMessage) message;
            MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
            ActivityModuleRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, ActivityModuleRequest.class);
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
                case GET_FLUX_FA_REPORT:
                    SetFLUXFAReportOrQueryMessageRequest saveReportRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, SetFLUXFAReportOrQueryMessageRequest.class);
                    saveReportBean.handleFaReportSaving(saveReportRequest);
                    break;
                case GET_FLUX_FA_QUERY:
                    SetFLUXFAReportOrQueryMessageRequest getReportRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, SetFLUXFAReportOrQueryMessageRequest.class);
                    FLUXFAReportMessage faRepQueryResponseAfterMapping = new FLUXFAReportMessage();
                    activityRulesModuleServiceBean.sendSyncAsyncFaReportToRules(faRepQueryResponseAfterMapping, "getTheOnValueFromSomewhere", getReportRequest.getRequestType(), textMessage.getJMSMessageID());
                    break;
                case GET_FISHING_TRIPS:
                    FishingTripRequest fishingTripRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, FishingTripRequest.class);
                    FishingTripResponse fishingTripResponse = fishingTripService.filterFishingTripsForReporting(FishingActivityRequestMapper.buildFishingActivityQueryFromRequest(fishingTripRequest));
                    String fishingTripResponseString = JAXBMarshaller.marshallJaxBObjectToString(fishingTripResponse);
                    producer.sendResponseMessageToSender(textMessage, fishingTripResponseString, 3_600_000L);
                    break;
                case GET_FA_CATCH_SUMMARY_REPORT:
                    FACatchSummaryReportRequest faCatchSummaryReportRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, FACatchSummaryReportRequest.class);
                    FACatchSummaryReportResponse faCatchSummaryReportResponse = faCatchReportService.getFACatchSummaryReportResponse(FishingActivityRequestMapper.buildFishingActivityQueryFromRequest(faCatchSummaryReportRequest));
                    String faCatchSummaryReportResponseString = JAXBMarshaller.marshallJaxBObjectToString(faCatchSummaryReportResponse);
                    producer.sendResponseMessageToSender(textMessage, faCatchSummaryReportResponseString);
                    break;
                case GET_NON_UNIQUE_IDS:
                    GetNonUniqueIdsRequest getNonUniqueIdsRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetNonUniqueIdsRequest.class);
                    GetNonUniqueIdsResponse getNonUniqueIdsResponse = matchingIdsService.getMatchingIdsResponse(getNonUniqueIdsRequest.getActivityUniquinessLists());
                    String getNonUniqueIdsResponseString = JAXBMarshaller.marshallJaxBObjectToString(getNonUniqueIdsResponse);
                    producer.sendResponseMessageToSender(textMessage, getNonUniqueIdsResponseString);
                    break;
                case GET_FISHING_ACTIVITY_FOR_TRIPS:
                    GetFishingActivitiesForTripRequest getFishingActivitiesForTripRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetFishingActivitiesForTripRequest.class);
                    GetFishingActivitiesForTripResponse getFishingActivitiesForTripResponse = activityServiceBean.getFaAndTripIdsFromTripIds(getFishingActivitiesForTripRequest.getFaAndTripIds());
                    String responseStr = JAXBMarshaller.marshallJaxBObjectToString(getFishingActivitiesForTripResponse);
                    producer.sendResponseMessageToSender(textMessage, responseStr);
                    break;
                default:
                    log.error("[ Request method {} is not implemented ]", method.name());
                    errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "[ Request method " + method.name() + "  is not implemented ]")));
            }
        } catch (ActivityModelMarshallException | ClassCastException | JMSException | ActivityModuleException | ServiceException e) {
            log.error("[ Error when receiving message in activity: ] {}", e);
            errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Error when receiving message")));
        }
    }

}
