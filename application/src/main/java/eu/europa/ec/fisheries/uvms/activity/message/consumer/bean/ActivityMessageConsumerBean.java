/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.message.consumer.bean;


import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportOrQueryMessageRequest;
import eu.europa.ec.fisheries.uvms.activity.service.bean.EfrMessageSaver;
import eu.europa.ec.fisheries.uvms.activity.service.bean.FluxReportMessageSaver;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import lombok.extern.slf4j.Slf4j;

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
    private EfrMessageSaver efrMessageSaver;

    @EJB
    private FluxReportMessageSaver fluxReportMessageSaver;

    @Override
    public void onMessage(Message message) {
        log.debug("Received message");

        TextMessage textMessage = (TextMessage) message;
        try {
            String function = textMessage.getStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY);
            ExchangeModuleMethod exchangeMethod = (function != null) ? ExchangeModuleMethod.valueOf(function) : null;
            if (exchangeMethod == ExchangeModuleMethod.EFR_SAVE_REPORT) {
                log.trace("Received EFR message: {}", textMessage);
                String shouldBeReportClass = textMessage.getText();
                efrMessageSaver.saveEfrReport(shouldBeReportClass);
            } else {
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

                if (method == ActivityModuleMethod.GET_FLUX_FA_REPORT) {
                    saveFluxReport(textMessage);
                } else {
                    log.error("Request method {} is not implemented", method.name());
                    errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Request method " + method.name() + " is not implemented")));
                }
            }
        } catch (ActivityModelMarshallException | ClassCastException | JMSException e) {
            log.error("Error when receiving message in activity", e);
            errorEvent.fire(new EventMessage(textMessage, ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Error when receiving message: " + e.getMessage())));
        }
    }

    private void saveFluxReport(TextMessage textMessage) throws ActivityModelMarshallException {
        SetFLUXFAReportOrQueryMessageRequest saveReportRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, SetFLUXFAReportOrQueryMessageRequest.class);
        fluxReportMessageSaver.saveFluxReportMessage(saveReportRequest);
    }
}
