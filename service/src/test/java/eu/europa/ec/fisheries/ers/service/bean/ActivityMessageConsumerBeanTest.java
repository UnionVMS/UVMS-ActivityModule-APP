/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.enterprise.event.Event;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveSalesReportRequest;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityMessageConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import lombok.SneakyThrows;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by kovian on 17/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivityMessageConsumerBeanTest {

    @InjectMocks
    ActivityMessageConsumerBean consumer;

    @Mock
    ClientSession session;

    @Mock
    Event<EventMessage> mapToSubscriptionRequest;

    @Mock
    Event<EventMessage> getFLUXFAReportMessageEvent;

    @Mock
    Event<EventMessage> getFishingTripListEvent;

    @Mock
    Event<EventMessage> getFACatchSummaryReportEvent;

    @Mock
    Event<EventMessage> getNonUniqueIdsRequest;

    @Mock
    Event<EventMessage> getFishingActivityForTrips;

    @Mock
    Event<EventMessage> errorEvent;


    @Test
    @SneakyThrows
    public void testOnMessageMethod(){
        for(ActivityModuleMethod moduleMethod : ActivityModuleMethod.values()){
            GetNonUniqueIdsRequest request = new GetNonUniqueIdsRequest();
            request.setMethod(moduleMethod);
            ActiveMQTextMessage textMessage = new ActiveMQTextMessage(session);
            final String strReq = JAXBMarshaller.marshallJaxBObjectToString(request);
            Whitebox.setInternalState(textMessage, "text", new SimpleString(strReq));
            consumer.onMessage(textMessage);
        }
        verify(getFLUXFAReportMessageEvent,times(1)).fire(Mockito.any(EventMessage.class));
        verify(getFishingTripListEvent,times(1)).fire(Mockito.any(EventMessage.class));
        verify(getFACatchSummaryReportEvent,times(1)).fire(Mockito.any(EventMessage.class));
        verify(getNonUniqueIdsRequest,times(1)).fire(Mockito.any(EventMessage.class));
    }

    @Test
    @SneakyThrows
    public void testThrowing(){
        ReceiveSalesReportRequest request = new ReceiveSalesReportRequest();
        request.setMethod(ExchangeModuleMethod.RECEIVE_SALES_REPORT);
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage(session);
        final String strReq = JAXBMarshaller.marshallJaxBObjectToString(request);
        Whitebox.setInternalState(textMessage, "text", new SimpleString(strReq));
        consumer.onMessage(textMessage);
        verify(errorEvent,times(1)).fire(Mockito.any(EventMessage.class));
    }

}
