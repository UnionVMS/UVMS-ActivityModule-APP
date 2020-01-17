/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveSalesReportRequest;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityMessageConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFault;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.enterprise.event.Event;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivityMessageConsumerBeanTest {

    @InjectMocks
    private ActivityMessageConsumerBean consumer;

    @Mock
    private Event<EventMessage> errorEvent;

    @Test
    public void wrongRequestType_expectError() throws Exception {
        // Given
        ReceiveSalesReportRequest request = new ReceiveSalesReportRequest();
        request.setMethod(ExchangeModuleMethod.RECEIVE_SALES_REPORT);

        String strReq = JAXBMarshaller.marshallJaxBObjectToString(request);

        ActiveMQTextMessage activeMQTextMessage = mock(ActiveMQTextMessage.class);
        when(activeMQTextMessage.getText()).thenReturn(strReq);

        // When
        consumer.onMessage(activeMQTextMessage);

        // Then
        ArgumentCaptor<EventMessage> argumentCaptor = ArgumentCaptor.forClass(EventMessage.class);
        verify(errorEvent, times(1)).fire(argumentCaptor.capture());

        EventMessage eventMessage = argumentCaptor.getValue();
        ActivityFault fault = eventMessage.getFault();

        assertEquals("Error when receiving message: [Error when unmarshalling response in ResponseMapper ]", fault.getFault());
        assertEquals(1700, fault.getCode());
    }
}
