/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityMessageServiceBean;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityUniquinessList;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
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
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Created by kovian on 17/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivityEventServiceBeanTest {

    @InjectMocks
    ActivityEventServiceBean activityEventBean;

    @Mock
    ActivityMatchingIdsServiceBean matchingIdsService;

    @Mock
    ActivityMessageServiceBean producer;

    @Mock
    ActiveMQTextMessage textMessage;

    @Mock
    ClientSession session;

    EventMessage eventMessage;

    GetNonUniqueIdsResponse response;

    @Before
    @SneakyThrows
    public void setUp() {
        textMessage        = new ActiveMQTextMessage(session);
        Whitebox.setInternalState(textMessage, "text", new SimpleString(getStrRequest()));
        Whitebox.setInternalState(textMessage, "jmsCorrelationID", "SomeCorrId");
        eventMessage= new EventMessage(textMessage);
        response = JAXBMarshaller.unmarshallTextMessage(getResponseStr(), GetNonUniqueIdsResponse.class);

    }

    @Test
    public void testGeNonUniqueIds(){
        when(matchingIdsService.getMatchingIdsResponse((List<ActivityUniquinessList>) Mockito.any(Collection.class))).thenReturn(response);
        activityEventBean.getNonUniqueIdsRequest(eventMessage);
    }

    private String getStrRequest(){
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

    private String getResponseStr(){
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

}
