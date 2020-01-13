/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.message.consumer.bean;


import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityUniquinessList;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportOrQueryMessageRequest;
import eu.europa.ec.fisheries.uvms.activity.service.ActivityRulesModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.FishingTripService;
import eu.europa.ec.fisheries.uvms.activity.service.bean.ActivityMatchingIdsService;
import eu.europa.ec.fisheries.uvms.activity.service.bean.FluxReportMessageSaver;
import eu.europa.ec.fisheries.uvms.activity.service.exception.ActivityModuleException;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FishingActivityRequestMapper;
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
import java.util.List;

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

    private static final long RESPONSE_TTL = 3_600_000L;

    @Inject
    @ActivityMessageErrorEvent
    private Event<EventMessage> errorEvent;

    @EJB
    private FluxReportMessageSaver fluxReportMessageSaver;

    @EJB
    private ActivityRulesModuleService activityRulesModuleServiceBean;

    @EJB
    private FishingTripService fishingTripService;

    @EJB
    private ActivityErrorMessageServiceBean producer;

    @EJB
    private ActivityMatchingIdsService matchingIdsService;

    @Override
    public void onMessage(Message message) {
        log.debug("Received subscription message");

        TextMessage textMessage = (TextMessage) message;
        try {
            MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
            ActivityModuleRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, ActivityModuleRequest.class);

            log.debug("Successfully parsed message to {}", ActivityModuleRequest.class.getName());

            if (request == null) {
                log.error("Request is null");
                return;
            }

            ActivityModuleMethod method = request.getMethod();
            if (method == null) {
                log.error("Request method is null");
                return;
            }

            switch (method) {
                case GET_FLUX_FA_REPORT:
                    saveReport(textMessage);
                    break;
                case GET_FLUX_FA_QUERY:
                    getReport(textMessage);
                    break;
                case GET_FISHING_TRIPS:
                    getFishingTrips(textMessage);
                    break;
                case GET_NON_UNIQUE_IDS:
                    getNonUniqueIds(textMessage);
                    break;
                case GET_FA_CATCH_SUMMARY_REPORT:
                case GET_FISHING_ACTIVITY_FOR_TRIPS:
                default:
                    log.error("Request method {} is not implemented", method.name());
                    errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Request method " + method.name() + " is not implemented")));
            }
        } catch (ActivityModelMarshallException | ClassCastException | JMSException | ActivityModuleException | ServiceException e) {
            log.error("Error when receiving message in activity", e);
            errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Error when receiving message: " + e.getMessage())));
        }
    }

    private void getNonUniqueIds(TextMessage textMessage) throws ActivityModelMarshallException, JMSException {
        GetNonUniqueIdsRequest getNonUniqueIdsRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetNonUniqueIdsRequest.class);
        List<ActivityUniquinessList> matchingIds = matchingIdsService.getMatchingIds(getNonUniqueIdsRequest.getActivityUniquinessLists());

        GetNonUniqueIdsResponse getNonUniqueIdsResponse = new GetNonUniqueIdsResponse();
        getNonUniqueIdsResponse.setMethod(ActivityModuleMethod.GET_NON_UNIQUE_IDS);
        getNonUniqueIdsResponse.setActivityUniquinessLists(matchingIds);
        String getNonUniqueIdsResponseString = JAXBMarshaller.marshallJaxBObjectToString(getNonUniqueIdsResponse);

        producer.sendResponseMessageToSender(textMessage, getNonUniqueIdsResponseString);
    }

    private void getFishingTrips(TextMessage textMessage) throws ActivityModelMarshallException, ServiceException, JMSException {
        FishingTripRequest fishingTripRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, FishingTripRequest.class);
        FishingTripResponse fishingTripResponse = fishingTripService.filterFishingTripsForReporting(FishingActivityRequestMapper.buildFishingActivityQueryFromRequest(fishingTripRequest));
        String fishingTripResponseString = JAXBMarshaller.marshallJaxBObjectToString(fishingTripResponse);
        producer.sendResponseMessageToSender(textMessage, fishingTripResponseString, RESPONSE_TTL);
    }

    private void getReport(TextMessage textMessage) throws ActivityModelMarshallException, ActivityModuleException, JMSException {
        SetFLUXFAReportOrQueryMessageRequest getReportRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, SetFLUXFAReportOrQueryMessageRequest.class);
        FLUXFAReportMessage faRepQueryResponseAfterMapping = new FLUXFAReportMessage();
        activityRulesModuleServiceBean.sendSyncAsyncFaReportToRules(faRepQueryResponseAfterMapping, "getTheOnValueFromSomewhere", getReportRequest.getRequestType(), textMessage.getJMSMessageID());
    }

    private void saveReport(TextMessage textMessage) throws ActivityModelMarshallException {
        SetFLUXFAReportOrQueryMessageRequest saveReportRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, SetFLUXFAReportOrQueryMessageRequest.class);
        fluxReportMessageSaver.saveFluxReportMessage(saveReportRequest);
    }
}
