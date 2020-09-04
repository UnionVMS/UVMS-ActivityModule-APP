package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ExchangeProducerBean;
import eu.europa.ec.fisheries.uvms.activity.service.FluxMessageService;
import eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend.FishingReport;
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
import static org.mockito.Mockito.verifyNoInteractions;
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
        when(efrToFluxMapper.efrFishingReportToFluxMessageEntity(any(FishingReport.class))).thenReturn(new FluxFaReportMessageEntity());
    }

    @Test
    public void saveEfrFishingReport_success() throws JMSException {
        // Given
        FishingReport efrFishingReport = new FishingReport();
        efrFishingReport.setFishingReportId(UUID.randomUUID());
        String efrFishingReportAsString = JsonbBuilder.create().toJson(efrFishingReport);

        // When
        efrMessageSaver.saveEfrReport(efrFishingReportAsString);

        // Then
        verify(efrToFluxMapper, times(1)).efrFishingReportToFluxMessageEntity(any(FishingReport.class));
        verify(fluxMessageService, times(1)).saveFishingActivityReportDocuments(any(FluxFaReportMessageEntity.class));
        verify(exchangeProducerBean, times(1)).sendEfrReportSavedAckToExchange(efrFishingReport.getFishingReportId());
    }

    @Test
    public void saveEfrFishingReport_notAValidFishingReport() {
        // Given

        // When
        efrMessageSaver.saveEfrReport("{\"blaha\": \"blaha\"}");

        // Then
        verifyNoInteractions(efrToFluxMapper);
        verifyNoInteractions(fluxMessageService);
        verifyNoInteractions(exchangeProducerBean);
    }
}
