package eu.europa.ec.fisheries.ers.activity.message.consumer.bean;

import eu.europa.ec.fisheries.ers.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.enterprise.event.Event;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivitySubscriptionCheckMessageConsumerBeanTest {

    @InjectMocks
    private ActivitySubscriptionCheckMessageConsumerBean subscriptionConsumer;

    @Mock
    private Event<EventMessage> mapToSubscriptionRequest;

    @Mock
    private Event<EventMessage> errorEvent; // Needed otherwise mockito will choose the wrong member to inject into

    @Test
    public void testOnMessageMethodForSubscriptionRequest() throws Exception {
        // Given
        GetNonUniqueIdsRequest request = new GetNonUniqueIdsRequest();
        request.setMethod(ActivityModuleMethod.MAP_TO_SUBSCRIPTION_REQUEST);

        String requestString = JAXBMarshaller.marshallJaxBObjectToString(request);

        ActiveMQTextMessage activeMQTextMessage = mock(ActiveMQTextMessage.class);
        when(activeMQTextMessage.getText()).thenReturn(requestString);

        subscriptionConsumer.onMessage(activeMQTextMessage);

        verify(mapToSubscriptionRequest, times(1)).fire(any(EventMessage.class));
    }
}
