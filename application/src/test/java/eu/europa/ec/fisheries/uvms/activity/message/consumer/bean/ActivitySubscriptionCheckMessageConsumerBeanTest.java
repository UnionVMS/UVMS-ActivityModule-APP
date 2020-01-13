package eu.europa.ec.fisheries.uvms.activity.message.consumer.bean;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivitySubscriptionCheckMessageConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.SubscriptionProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.MapToSubscriptionRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.MessageType;
import eu.europa.ec.fisheries.wsdl.subscription.module.CriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataCriteria;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataQuery;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataRequest;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQueryParameter;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.jms.Destination;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivitySubscriptionCheckMessageConsumerBeanTest {

    @InjectMocks
    private ActivitySubscriptionCheckMessageConsumerBean subscriptionConsumer;

    @Mock
    private SubscriptionProducerBean subscriptionProducer;

    @Test
    public void mapToSubscriptionRequest_fluxFaQueryMessage() throws Exception {
        // Given
        MapToSubscriptionRequest request = new MapToSubscriptionRequest();
        request.setMethod(ActivityModuleMethod.MAP_TO_SUBSCRIPTION_REQUEST);
        request.setMessageType(MessageType.FLUX_FA_QUERY_MESSAGE);

        FLUXParty fluxParty = new FLUXParty();
        IDType idType = new IDType();
        idType.setValue("SWE");
        fluxParty.setIDS(Lists.newArrayList(idType));

        FAQueryParameter faQueryParameter = new FAQueryParameter();
        List<FAQueryParameter> faQueryParamaters = new ArrayList<>();
        faQueryParamaters.add(faQueryParameter);

        FAQuery faQuery = new FAQuery();
        faQuery.setSubmitterFLUXParty(fluxParty);
        faQuery.setSimpleFAQueryParameters(faQueryParamaters);
        faQuery.setSpecifiedDelimitedPeriod(new DelimitedPeriod());

        FLUXFAQueryMessage fluxfaQueryMessage = new FLUXFAQueryMessage();
        fluxfaQueryMessage.setFAQuery(faQuery);

        String fluxFaQueryMessageString = JAXBMarshaller.marshallJaxBObjectToString(fluxfaQueryMessage);
        request.setRequest(fluxFaQueryMessageString);

        String requestString = JAXBMarshaller.marshallJaxBObjectToString(request);

        ActiveMQTextMessage activeMQTextMessage = mock(ActiveMQTextMessage.class);
        when(activeMQTextMessage.getText()).thenReturn(requestString);
        when(activeMQTextMessage.getJMSMessageID()).thenReturn("jms-message-id-1");

        // When
        subscriptionConsumer.onMessage(activeMQTextMessage);

        // Then
        ArgumentCaptor<String> subscriptionRequestCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Destination> destinationArgumentCaptor = ArgumentCaptor.forClass(Destination.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> jmsCorrelationIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(subscriptionProducer).sendMessageWithSpecificIds(subscriptionRequestCaptor.capture(), destinationArgumentCaptor.capture(), stringArgumentCaptor.capture(), jmsCorrelationIdCaptor.capture());

        String subscriptionRequestCaptorValue = subscriptionRequestCaptor.getValue();
        assertTrue(subscriptionRequestCaptorValue.contains(MessageType.FLUX_FA_QUERY_MESSAGE.toString()));

        String jmsCorrelationIdValue = jmsCorrelationIdCaptor.getValue();
        assertEquals("jms-message-id-1", jmsCorrelationIdValue);
    }

    @Test
    public void mapToSubscriptionRequest_fluxFaReportMessage() throws Exception {
        // Given
        MapToSubscriptionRequest request = new MapToSubscriptionRequest();
        request.setMethod(ActivityModuleMethod.MAP_TO_SUBSCRIPTION_REQUEST);
        request.setMessageType(MessageType.FLUX_FA_REPORT_MESSAGE);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        FLUXReportDocument fluxReportDocument = new FLUXReportDocument();
        fluxReportDocument.setCreationDateTime(new DateTimeType(xmlGregorianCalendar, new DateTimeType.DateTimeString()));

        FLUXFAReportMessage fluxfaReportMessage = new FLUXFAReportMessage();
        fluxfaReportMessage.setFLUXReportDocument(fluxReportDocument);

        String fluxFaReportMessageString = JAXBMarshaller.marshallJaxBObjectToString(fluxfaReportMessage);
        request.setRequest(fluxFaReportMessageString);

        String requestString = JAXBMarshaller.marshallJaxBObjectToString(request);

        ActiveMQTextMessage activeMQTextMessage = mock(ActiveMQTextMessage.class);
        when(activeMQTextMessage.getText()).thenReturn(requestString);
        when(activeMQTextMessage.getJMSMessageID()).thenReturn("jms-message-id-1");

        // When
        subscriptionConsumer.onMessage(activeMQTextMessage);

        // Then
        ArgumentCaptor<String> subscriptionRequestCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Destination> destinationArgumentCaptor = ArgumentCaptor.forClass(Destination.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> jmsCorrelationIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(subscriptionProducer).sendMessageWithSpecificIds(subscriptionRequestCaptor.capture(), destinationArgumentCaptor.capture(), stringArgumentCaptor.capture(), jmsCorrelationIdCaptor.capture());

        String subscriptionRequestCaptorValue = subscriptionRequestCaptor.getValue();
        SubscriptionDataRequest subscriptionDataRequest = JAXBMarshaller.unmarshallTextMessage(subscriptionRequestCaptorValue, SubscriptionDataRequest.class);

        SubscriptionDataQuery subscriptionDataQuery = subscriptionDataRequest.getQuery();
        List<SubscriptionDataCriteria> criteria = subscriptionDataQuery.getCriteria();
        SubscriptionDataCriteria subscriptionDataCriteria = criteria.get(0);

        assertEquals(eu.europa.ec.fisheries.wsdl.subscription.module.MessageType.FLUX_FA_REPORT_MESSAGE, subscriptionDataQuery.getMessageType());
        assertEquals(CriteriaType.VALIDITY_PERIOD, subscriptionDataCriteria.getCriteria());
        assertEquals(SubCriteriaType.START_DATE, subscriptionDataCriteria.getSubCriteria());
        assertEquals(xmlGregorianCalendar.toString(), subscriptionDataCriteria.getValue());

        String jmsCorrelationIdValue = jmsCorrelationIdCaptor.getValue();
        assertEquals("jms-message-id-1", jmsCorrelationIdValue);
    }
}
