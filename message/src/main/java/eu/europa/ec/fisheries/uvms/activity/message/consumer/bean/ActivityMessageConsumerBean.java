/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.message.consumer.bean;


import static eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod.GET_FLUX_FA_QUERY;

import eu.europa.ec.fisheries.uvms.activity.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.activity.message.event.*;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.uvms.activity.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFACatchSummaryReportEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFLUXFAReportMessageEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFishingActivityForTripsRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFishingTripListEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetNonUniqueIdsRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.MapToSubscriptionRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageDriven(mappedName = MessageConstants.ACTIVITY_MESSAGE_IN_QUEUE, activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = MessageConstants.COMPONENT_MESSAGE_IN_QUEUE_NAME)
})
@Slf4j
public class ActivityMessageConsumerBean implements MessageListener {

    static final Logger LOG = LoggerFactory.getLogger(ActivityMessageConsumerBean.class);

    @Inject
    @ReceiveFishingActivityRequestEvent
    private Event<EventMessage> receiveFishingActivityEvent;

    @Inject
    @MapToSubscriptionRequestEvent
    private Event<EventMessage> mapToSubscriptionRequest;

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
    @ActivityMessageErrorEvent
    private Event<EventMessage> errorEvent;


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {

        TextMessage textMessage = null;
        try {
            textMessage = (TextMessage) message;
            ActivityModuleRequest request;
            request = JAXBMarshaller.unmarshallTextMessage(textMessage, ActivityModuleRequest.class);
            LOG.debug("Message unmarshalled successfully in activity");
            if (request==null) {
                LOG.error("[ Request is null ]");
                return;
            }
            if (request.getMethod() == null) {
                LOG.error("[ Request method is null ]");
                return;
            }
            switch (request.getMethod()) {

                case GET_FLUX_FA_REPORT :
                case GET_FLUX_FA_QUERY  :
                    receiveFishingActivityEvent.fire(new EventMessage(textMessage, GET_FLUX_FA_QUERY));
                case MAP_TO_SUBSCRIPTION_REQUEST:
                    mapToSubscriptionRequest.fire(new EventMessage(textMessage));
                    break;
                case GET_FISHING_TRIPS :
                    getFishingTripListEvent.fire(new EventMessage(textMessage));
                    break;
                case GET_FA_CATCH_SUMMARY_REPORT :
                    getFACatchSummaryReportEvent.fire(new EventMessage(textMessage));
                    break;
                case GET_NON_UNIQUE_IDS :
                    getNonUniqueIdsRequest.fire(new EventMessage(textMessage));
                    break;
                case GET_FISHING_ACTIVITY_FOR_TRIPS:
                    getFishingActivityForTrips.fire(new EventMessage(textMessage));
                    break;
                default:
                    LOG.error("[ Request method {} is not implemented ]", request.getMethod().name());
                    errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "[ Request method " + request.getMethod().name() + "  is not implemented ]")));
            }

        } catch ( ActivityModelMarshallException | ClassCastException e) {
            LOG.error("[ Error when receiving message in activity: ] {}", e);
            errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Error when receiving message")));
        }
    }

}
