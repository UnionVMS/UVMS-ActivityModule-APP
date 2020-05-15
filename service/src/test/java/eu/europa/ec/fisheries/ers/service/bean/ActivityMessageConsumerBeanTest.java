/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.enterprise.event.Event;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveSalesReportRequest;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityMessageConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivitySubscriptionCheckMessageConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import lombok.SneakyThrows;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by kovian on 17/07/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({MappedDiagnosticContext.class})
@PowerMockIgnore({"javax.management.*"})
public class ActivityMessageConsumerBeanTest {

    @InjectMocks
    ActivityMessageConsumerBean consumer;

    @InjectMocks
    ActivitySubscriptionCheckMessageConsumerBean subscriptionConsumer;

    @Mock
    ClientSession session;

    @Mock
    Event<EventMessage> mapToSubscriptionRequest;

    @Mock
    Event<EventMessage> receiveFishingActivityEvent;

    @Mock
    Event<EventMessage> getFishingTripListEvent;

    @Mock
    Event<EventMessage> getFACatchSummaryReportEvent;

    @Mock
    Event<EventMessage> getNonUniqueIdsRequest;

    @Mock @SuppressWarnings("unused")
    Event<EventMessage> getFishingActivityForTrips;

    @Mock @SuppressWarnings("unused")
    private Event<EventMessage> createAndSendFAQueryForVessel;

    @Mock @SuppressWarnings("unused")
    private Event<EventMessage> createAndSendFAQueryForTrip;

    @Mock @SuppressWarnings("unused")
    private Event<EventMessage> forwardMultipleFAReports;

    @Mock @SuppressWarnings("unused")
    private Event<EventMessage> forwardFAReportWithLogbook;

    @Mock @SuppressWarnings("unused")
    private Event<EventMessage> forwardFAReportFromPosition;

    @Mock
    Event<EventMessage> errorEvent;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @SneakyThrows
    public void testOnMessageMethod() {
        mockStatic(MappedDiagnosticContext.class);
        PowerMockito.doNothing().when(MappedDiagnosticContext.class, "addMessagePropertiesToThreadMappedDiagnosticContext", Mockito.any(TextMessage.class));
        for (ActivityModuleMethod moduleMethod : ActivityModuleMethod.values()) {

            GetNonUniqueIdsRequest request = new GetNonUniqueIdsRequest();
            request.setMethod(moduleMethod);
            ActiveMQTextMessage textMessage = new ActiveMQTextMessage(session);
            final String strReq = JAXBMarshaller.marshallJaxBObjectToString(request);
            Whitebox.setInternalState(textMessage, "text", new SimpleString(strReq));

            consumer.onMessage(textMessage);

            PowerMockito.verifyStatic();
            MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
        }
        verify(receiveFishingActivityEvent, times(2)).fire(any(EventMessage.class));
        verify(getFishingTripListEvent, times(1)).fire(any(EventMessage.class));
        verify(getFACatchSummaryReportEvent, times(1)).fire(any(EventMessage.class));
        verify(getNonUniqueIdsRequest, times(1)).fire(any(EventMessage.class));
    }

    @Test
    @SneakyThrows
    public void testOnMessageMethodForSubscriptionRequest() {
        mockStatic(MappedDiagnosticContext.class);
        PowerMockito.doNothing().when(MappedDiagnosticContext.class, "addMessagePropertiesToThreadMappedDiagnosticContext", Mockito.any(TextMessage.class));
        GetNonUniqueIdsRequest request = new GetNonUniqueIdsRequest();
        request.setMethod(ActivityModuleMethod.MAP_TO_SUBSCRIPTION_REQUEST);
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage(session);
        final String strReq = JAXBMarshaller.marshallJaxBObjectToString(request);
        Whitebox.setInternalState(textMessage, "text", new SimpleString(strReq));
        subscriptionConsumer.onMessage(textMessage);
        PowerMockito.verifyStatic();
        MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
        verify(mapToSubscriptionRequest, times(1)).fire(any(EventMessage.class));
    }

    @Test
    @SneakyThrows
    public void testThrowing() {
        mockStatic(MappedDiagnosticContext.class);
        PowerMockito.doNothing().when(MappedDiagnosticContext.class, "addMessagePropertiesToThreadMappedDiagnosticContext", Mockito.any(TextMessage.class));
        ReceiveSalesReportRequest request = new ReceiveSalesReportRequest();
        request.setMethod(ExchangeModuleMethod.RECEIVE_SALES_REPORT);
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage(session);
        final String strReq = JAXBMarshaller.marshallJaxBObjectToString(request);
        Whitebox.setInternalState(textMessage, "text", new SimpleString(strReq));
        consumer.onMessage(textMessage);
        PowerMockito.verifyStatic();
        MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
        verify(errorEvent, times(1)).fire(any(EventMessage.class));
    }

}
