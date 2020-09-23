package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ExchangeProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.efr.activities.PriorNotificationEfrActivity;
import eu.europa.ec.fisheries.uvms.activity.service.FluxMessageService;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.EfrToFluxMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.JMSException;
import javax.json.bind.JsonbBuilder;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EfrMessageSaverTest {

    @Mock
    private EfrToFluxMapper efrToFluxMapper;

    @Mock
    private FluxMessageService fluxMessageService;

    @Mock
    private ExchangeProducerBean exchangeProducerBean;

    @InjectMocks
    private EfrMessageSaver efrMessageSaver;

    @Before
    public void setUp() {
        when(efrToFluxMapper.map(any(PriorNotificationEfrActivity.class))).thenReturn(new FluxFaReportMessageEntity());
    }

    @Test
    public void handlePriorNotificationEfrActivity_success() throws JMSException {
        // Given
        PriorNotificationEfrActivity efrActivity = new PriorNotificationEfrActivity();
        efrActivity.setActivityMessageId(UUID.randomUUID());
        efrActivity.setFishingReportId(UUID.randomUUID());
        String efrActivityAsString = JsonbBuilder.create().toJson(efrActivity);

        // When
        efrMessageSaver.handleEfrActivity(efrActivityAsString);

        // Then
        verify(efrToFluxMapper, times(1)).map(any(PriorNotificationEfrActivity.class));
        verify(fluxMessageService, times(1)).saveFishingActivityReportDocuments(any(FluxFaReportMessageEntity.class));
        verify(exchangeProducerBean, times(1)).sendEfrActivitySavedAckToExchange(efrActivity.getActivityMessageId());
    }
}
