/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.activity.message.consumer.bean.ActivityErrorMessageServiceBean;
import eu.europa.ec.fisheries.ers.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
import lombok.SneakyThrows;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by kovian on 17/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivityEventServiceBeanTest {

    @InjectMocks
    ActivityEventServiceBean activityEventBean;

    @Mock
    ActivityServiceBean activityServiceBean;

    @Mock
    ActivityMatchingIdsServiceBean matchingIdsService;

    @Mock
    ActivityErrorMessageServiceBean producer;

    @Mock
    ActiveMQTextMessage nonUniqueIdsMessage;

    @Mock
    ActiveMQTextMessage faAndTripIdsFromTripIdsMessage;

    @Mock
    ClientSession session;

    private EventMessage nonUniqueIdsMessageEventMessage;
    private EventMessage faAndTripIdsFromTripIdsEventMessage;

    private GetNonUniqueIdsResponse getNonUniqueIdsResponse;

    private GetFishingActivitiesForTripResponse getFishingActivitiesForTripResponse;

    @Before
    @SneakyThrows
    public void setUp() {
        nonUniqueIdsMessage = new ActiveMQTextMessage(session);
        Whitebox.setInternalState(nonUniqueIdsMessage, "text", new SimpleString(getStrRequest1()));
        Whitebox.setInternalState(nonUniqueIdsMessage, "jmsCorrelationID", "SomeCorrId");
        nonUniqueIdsMessageEventMessage = new EventMessage(nonUniqueIdsMessage);

        faAndTripIdsFromTripIdsMessage = new ActiveMQTextMessage(session);
        Whitebox.setInternalState(faAndTripIdsFromTripIdsMessage, "text", new SimpleString(getStrRequest2()));
        Whitebox.setInternalState(faAndTripIdsFromTripIdsMessage, "jmsCorrelationID", "SomeCorrId");
        faAndTripIdsFromTripIdsEventMessage = new EventMessage(faAndTripIdsFromTripIdsMessage);

        getNonUniqueIdsResponse = JAXBMarshaller.unmarshallTextMessage(getResponseStr1(), GetNonUniqueIdsResponse.class);
        getFishingActivitiesForTripResponse = JAXBMarshaller.unmarshallTextMessage(getResponseStr2(), GetFishingActivitiesForTripResponse.class);

    }

    @Test
    public void getNonUniqueIds() throws JMSException {
        // Given
        when(matchingIdsService.getMatchingIdsResponse(any())).thenReturn(getNonUniqueIdsResponse);

        // When
        activityEventBean.getNonUniqueIdsRequest(nonUniqueIdsMessageEventMessage);

        // Then
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(producer, times(1)).sendResponseMessageToSender(messageCaptor.capture(), textCaptor.capture());

        TextMessage message = messageCaptor.getValue();
        String text = textCaptor.getValue();

        assertTrue(message.getText().contains("46DCC44C-0AE2-434C-BC14-B85D86B29512"));
        assertTrue(text.contains("46DCC44C-0AE2-434C-BC14-B85D86B29512bbbbb"));
    }

    @Test
    @SneakyThrows
    public void getFishingActivityForTripsRequest() {
        // Given
        when(activityServiceBean.getFaAndTripIdsFromTripIds(any())).thenReturn(getFishingActivitiesForTripResponse);

        // When
        activityEventBean.getFishingActivityForTripsRequest(faAndTripIdsFromTripIdsEventMessage);

        // Then
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(producer, times(1)).sendResponseMessageToSender(messageCaptor.capture(), textCaptor.capture());

        TextMessage message = messageCaptor.getValue();
        String text = textCaptor.getValue();

        assertTrue(message.getText().contains("AUT-TRP-38778a5888837-000000"));
        assertTrue(text.contains("GetFishingActivitiesForTripResponse"));
    }

    private String getStrRequest1() {
        return "<ns2:GetNonUniqueIdsRequest xmlns:ns2=\"http://europa.eu/ec/fisheries/uvms/activity/model/schemas\">\n" +
                "    <method>GET_NON_UNIQUE_IDS</method>\n" +
                "    <activityUniquinessList>\n" +
                "        <activityTableType>RELATED_FLUX_REPORT_DOCUMENT_ENTITY</activityTableType>\n" +
                "        <ids>\n" +
                "            <value>46DCC44C-0AE2-434C-BC14-B85D86B29512</value>\n" +
                "            <identifierSchemeId>UUID</identifierSchemeId>\n" +
                "        </ids>\n" +
                "    </activityUniquinessList>\n" +
                "    <activityUniquinessList>\n" +
                "        <activityTableType>FLUX_REPORT_DOCUMENT_ENTITY</activityTableType>\n" +
                "        <ids>\n" +
                "            <value>46DCC44C-0AE2-434C-BC14-B85D86B29512</value>\n" +
                "            <identifierSchemeId>UUID</identifierSchemeId>\n" +
                "        </ids>\n" +
                "    </activityUniquinessList>\n" +
                "</ns2:GetNonUniqueIdsRequest>";
    }

    private String getResponseStr1() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns2:GetNonUniqueIdsResponse xmlns:ns2=\"http://europa.eu/ec/fisheries/uvms/activity/model/schemas\">\n" +
                "    <method>GET_NON_UNIQUE_IDS</method>\n" +
                "    <activityUniquinessList>\n" +
                "        <activityTableType>RELATED_FLUX_REPORT_DOCUMENT_ENTITY</activityTableType>\n" +
                "        <ids>\n" +
                "            <value>46DCC44C-0AE2-434C-BC14-B85D86B29512bbbbb</value>\n" +
                "            <identifierSchemeId>scheme-idqq</identifierSchemeId>\n" +
                "        </ids>\n" +
                "        <ids/>\n" +
                "    </activityUniquinessList>\n" +
                "    <activityUniquinessList>\n" +
                "        <activityTableType>FLUX_REPORT_DOCUMENT_ENTITY</activityTableType>\n" +
                "        <ids>\n" +
                "            <value>46DCC44C-0AE2-434C-BC14-B85D86B29512bbbbb</value>\n" +
                "            <identifierSchemeId>scheme-idqq</identifierSchemeId>\n" +
                "        </ids>\n" +
                "        <ids/>\n" +
                "    </activityUniquinessList>\n" +
                "</ns2:GetNonUniqueIdsResponse>";
    }

    public String getStrRequest2() {
        return "<ns2:GetFishingActivitiesForTripRequest xmlns:ns2=\"http://europa.eu/ec/fisheries/uvms/activity/model/schemas\">\n" +
                "    <method>GET_FISHING_ACTIVITY_FOR_TRIPS</method>\n" +
                "    <faAndTripIds>\n" +
                "        <fishActTypeCode>LANDING</fishActTypeCode>\n" +
                "        <tripId>AUT-TRP-38778a5888837-000000</tripId>\n" +
                "        <tripSchemeId>EU_TRIP_ID</tripSchemeId>\n" +
                "        <fluxRepDocPurposeCodes>9</fluxRepDocPurposeCodes>\n" +
                "    </faAndTripIds>\n" +
                "</ns2:GetFishingActivitiesForTripRequest>";
    }

    public String getResponseStr2() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns2:GetFishingActivitiesForTripResponse xmlns:ns2=\"http://europa.eu/ec/fisheries/uvms/activity/model/schemas\"/>\n";
    }
}
