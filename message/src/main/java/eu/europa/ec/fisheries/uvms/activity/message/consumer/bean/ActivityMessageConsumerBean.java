/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.message.consumer.bean;


import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.CreateAndSendFAQueryForTripEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.CreateAndSendFAQueryForVesselEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFACatchSummaryReportEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFishingActivityForTripsRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFishingTripListEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetNonUniqueIdsRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.ReceiveFishingActivityRequestEvent;
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

@MessageDriven(mappedName = MessageConstants.QUEUE_MODULE_ACTIVITY, activationConfig = {
        @ActivationConfigProperty(propertyName = MessageConstants.MESSAGING_TYPE_STR, propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_TYPE_STR, propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_STR, propertyValue = MessageConstants.QUEUE_MODULE_ACTIVITY_NAME),
        @ActivationConfigProperty(propertyName = "maxMessagesPerSessions", propertyValue = "3"),
        @ActivationConfigProperty(propertyName = "initialRedeliveryDelay", propertyValue = "120000"),
        @ActivationConfigProperty(propertyName = "maximumRedeliveries", propertyValue = "3"),
        @ActivationConfigProperty(propertyName = "maxSessions", propertyValue = "5")
})
@Slf4j
public class ActivityMessageConsumerBean implements MessageListener {

    @Inject
    @ReceiveFishingActivityRequestEvent
    private Event<EventMessage> receiveFishingActivityEvent;

    @Inject
    @GetFishingTripListEvent
    private Event<EventMessage> getFishingTripListEvent;

    @Inject
    @GetFACatchSummaryReportEvent
    private Event<EventMessage> getFACatchSummaryReportEvent;

    @Inject
    @GetNonUniqueIdsRequestEvent
    private Event<EventMessage> getNonUniqueIdsRequest;

    @Inject
    @GetFishingActivityForTripsRequestEvent
    private Event<EventMessage> getFishingActivityForTrips;

    @Inject
    @CreateAndSendFAQueryForVesselEvent
    private Event<EventMessage> createAndSendFAQueryForVessel;

    @Inject
    @CreateAndSendFAQueryForTripEvent
    private Event<EventMessage> createAndSendFAQueryForTrip;

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
                case GET_FLUX_FA_REPORT:
                case GET_FLUX_FA_QUERY:
                    receiveFishingActivityEvent.fire(new EventMessage(textMessage, method));
                    break;
                case GET_FISHING_TRIPS:
                    getFishingTripListEvent.fire(new EventMessage(textMessage));
                    break;
                case GET_FA_CATCH_SUMMARY_REPORT:
                    getFACatchSummaryReportEvent.fire(new EventMessage(textMessage));
                    break;
                case GET_NON_UNIQUE_IDS:
                    getNonUniqueIdsRequest.fire(new EventMessage(textMessage));
                    break;
                case GET_FISHING_ACTIVITY_FOR_TRIPS:
                    getFishingActivityForTrips.fire(new EventMessage(textMessage));
                    break;
                case CREATE_AND_SEND_FA_QUERY_FOR_VESSEL:
                    createAndSendFAQueryForVessel.fire(new EventMessage(textMessage));
                    break;
                case CREATE_AND_SEND_FA_QUERY_FOR_TRIP:
                    createAndSendFAQueryForTrip.fire(new EventMessage(textMessage));
                    break;
                default:
                    log.error("[ Request method {} is not implemented ]", method.name());
                    errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "[ Request method " + method.name() + "  is not implemented ]")));
            }
        } catch (ActivityModelMarshallException | ClassCastException e) {
            log.error("[ Error when receiving message in activity: ]", e);
            errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Error when receiving message")));
        }
    }

}
