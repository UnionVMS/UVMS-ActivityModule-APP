/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.ers.activity.message.consumer.bean.ActivityErrorMessageServiceBean;
import eu.europa.ec.fisheries.ers.activity.message.consumer.bean.ActivityMessageConsumerBean;
import eu.europa.ec.fisheries.ers.activity.message.consumer.bean.ActivitySubscriptionCheckMessageConsumerBean;
import eu.europa.ec.fisheries.ers.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveSalesReportRequest;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityIDType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityTableType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityUniquinessList;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivityForTripIds;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.SneakyThrows;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.enterprise.event.Event;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by kovian on 17/07/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({MappedDiagnosticContext.class})
@PowerMockIgnore({"javax.management.*"})
// TODO: Rewrite this test without powermock
public class ActivityMessageConsumerBeanTest {

    @InjectMocks
    private ActivityMessageConsumerBean consumer;

    @InjectMocks
    private ActivitySubscriptionCheckMessageConsumerBean subscriptionConsumer;

    @Mock
    private ClientSession session;

    @Mock
    private Event<EventMessage> mapToSubscriptionRequest;

    @Mock
    private Event<EventMessage> errorEvent;

    @Mock
    private ActivityMatchingIdsServiceBean matchingIdsService;

    @Mock
    private ActivityErrorMessageServiceBean producer;

    @Mock
    private ActivityService activityService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @SneakyThrows
    @Ignore("Doesn't really test anything, it just used to send in messages for each method and expected it to fire off a certain number of internal messages.")
    public void testOnMessageMethod() {
        mockStatic(MappedDiagnosticContext.class);
        PowerMockito.doNothing().when(MappedDiagnosticContext.class, "addMessagePropertiesToThreadMappedDiagnosticContext", Mockito.any(TextMessage.class));
        for (ActivityModuleMethod moduleMethod : ActivityModuleMethod.values()) {

            GetNonUniqueIdsRequest request = new GetNonUniqueIdsRequest();
            request.setMethod(moduleMethod);

            final String strReq = JAXBMarshaller.marshallJaxBObjectToString(request);
            ActiveMQTextMessage activeMQTextMessage = mock(ActiveMQTextMessage.class);
            when(activeMQTextMessage.getText()).thenReturn(strReq);

            GetNonUniqueIdsResponse getNonUniqueIdsResponse = new GetNonUniqueIdsResponse();
            when(matchingIdsService.getMatchingIdsResponse(any())).thenReturn(getNonUniqueIdsResponse);

            consumer.onMessage(activeMQTextMessage);

            PowerMockito.verifyStatic();
            MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(activeMQTextMessage);
        }
//        verify(receiveFishingActivityEvent, times(2)).fire(any(EventMessage.class));
//        verify(getFishingTripListEvent, times(1)).fire(any(EventMessage.class));
//        verify(getFACatchSummaryReportEvent, times(1)).fire(any(EventMessage.class));
//        verify(getNonUniqueIdsRequest, times(1)).fire(any(EventMessage.class));
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

    @Test
    public void getNonUniqueIds() throws ActivityModelMarshallException, JMSException {
        // Given
        GetNonUniqueIdsRequest getNonUniqueIdsRequest = new GetNonUniqueIdsRequest();
        getNonUniqueIdsRequest.setMethod(ActivityModuleMethod.GET_NON_UNIQUE_IDS);
        String requestString = JAXBMarshaller.marshallJaxBObjectToString(getNonUniqueIdsRequest);

        ActiveMQTextMessage activeMQTextMessage = mock(ActiveMQTextMessage.class);
        when(activeMQTextMessage.getText()).thenReturn(requestString);

        List<ActivityIDType> idList = new ArrayList<>();
        idList.add(new ActivityIDType("46DCC44C-0AE2-434C-BC14-B85D86B29512bbbbb", "scheme-idqq"));

        ActivityUniquinessList activityUniquinessList1 = new ActivityUniquinessList();
        activityUniquinessList1.setActivityTableType(ActivityTableType.RELATED_FLUX_REPORT_DOCUMENT_ENTITY);
        activityUniquinessList1.setIds(idList);

        ActivityUniquinessList activityUniquinessList2 = new ActivityUniquinessList();
        activityUniquinessList2.setActivityTableType(ActivityTableType.FLUX_REPORT_DOCUMENT_ENTITY);
        activityUniquinessList2.setIds(idList);

        List<ActivityUniquinessList> list = new ArrayList<>();
        list.add(activityUniquinessList1);
        list.add(activityUniquinessList2);

        GetNonUniqueIdsResponse getNonUniqueIdsResponse = new GetNonUniqueIdsResponse();
        getNonUniqueIdsResponse.setMethod(ActivityModuleMethod.GET_NON_UNIQUE_IDS);
        getNonUniqueIdsResponse.setActivityUniquinessLists(list);

        when(matchingIdsService.getMatchingIdsResponse(any())).thenReturn(getNonUniqueIdsResponse);

        // When
        consumer.onMessage(activeMQTextMessage);

        // Then
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(producer, times(1)).sendResponseMessageToSender(messageCaptor.capture(), textCaptor.capture());

        String text = textCaptor.getValue();

        assertTrue(text.contains("46DCC44C-0AE2-434C-BC14-B85D86B29512bbbbb"));
    }

    @Test
    public void getFishingActivityForTrips() throws ActivityModelMarshallException, ServiceException, JMSException {
        // Given
        FishingActivityForTripIds fishingActivityForTripIds = new FishingActivityForTripIds();
        fishingActivityForTripIds.setTripId("AUT-TRP-38778a5888837-000000");
        fishingActivityForTripIds.setTripSchemeId("EU_TRIP_ID");
        fishingActivityForTripIds.setFishActTypeCode("LANDING");
        fishingActivityForTripIds.setFluxRepDocPurposeCodes(Lists.newArrayList("9"));

        List<FishingActivityForTripIds> tripIds = new ArrayList<>();
        tripIds.add(fishingActivityForTripIds);

        GetFishingActivitiesForTripRequest getFishingActivitiesForTripRequest = new GetFishingActivitiesForTripRequest();
        getFishingActivitiesForTripRequest.setMethod(ActivityModuleMethod.GET_FISHING_ACTIVITY_FOR_TRIPS);
        getFishingActivitiesForTripRequest.setFaAndTripIds(tripIds);

        String requestString = JAXBMarshaller.marshallJaxBObjectToString(getFishingActivitiesForTripRequest);

        ActiveMQTextMessage activeMQTextMessage = mock(ActiveMQTextMessage.class);
        when(activeMQTextMessage.getText()).thenReturn(requestString);

        GetFishingActivitiesForTripResponse getFishingActivitiesForTripResponse = new GetFishingActivitiesForTripResponse();

        when(activityService.getFaAndTripIdsFromTripIds(any())).thenReturn(getFishingActivitiesForTripResponse);

        // When
        consumer.onMessage(activeMQTextMessage);

        // Then
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(producer, times(1)).sendResponseMessageToSender(messageCaptor.capture(), textCaptor.capture());

        TextMessage message = messageCaptor.getValue();
        String text = textCaptor.getValue();

        assertTrue(message.getText().contains("AUT-TRP-38778a5888837-000000"));
        assertTrue(text.contains("GetFishingActivitiesForTripResponse"));
    }
}
