package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityIDType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityTableType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportOrQueryMessageRequest;
import eu.europa.ec.fisheries.uvms.activity.service.FluxMessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FluxReportMessageSaverTest {

    @Mock
    private FluxMessageService fluxMessageService;

    @Mock
    private ActivityMatchingIdsService matchingIdsService;

    @InjectMocks
    private FluxReportMessageSaver fluxReportMessageSaver;

    @Test
    public void handleFaReportSaving_removeReportThatAlreadyExistInTheDatabase() throws Exception {
        // Given
        String fluxfaReportMessageString = getFluxfaReportMessageFromFile("FaReportSaverBeanTest_flux_report_message_with_1_new_document_and_1_that_already_exists_in_db_id.xml");

        SetFLUXFAReportOrQueryMessageRequest reportOrQueryMessageRequest = new SetFLUXFAReportOrQueryMessageRequest();
        reportOrQueryMessageRequest.setRequest(fluxfaReportMessageString);

        List<ActivityIDType> databaseIds = new ArrayList<>();
        databaseIds.add(new ActivityIDType("related-flux-report-document-id-already-in-database", "UUID"));

        when(matchingIdsService.getMatchingIds(anyList(), eq(ActivityTableType.RELATED_FLUX_REPORT_DOCUMENT_ENTITY))).thenReturn(databaseIds);

        // When
        fluxReportMessageSaver.saveFluxReportMessage(reportOrQueryMessageRequest);

        // Then
        ArgumentCaptor<FLUXFAReportMessage> argumentCaptor = ArgumentCaptor.forClass(FLUXFAReportMessage.class);
        verify(fluxMessageService).saveFishingActivityReportDocuments(argumentCaptor.capture(), eq(FaReportSourceEnum.FLUX));

        FLUXFAReportMessage capturedFluxFaReportMessage = argumentCaptor.getValue();
        List<FAReportDocument> faReportDocuments = capturedFluxFaReportMessage.getFAReportDocuments();
        assertEquals(1, faReportDocuments.size());

        FAReportDocument faReportDocument = faReportDocuments.get(0);
        IDType relatedFluxReportDocumentId = faReportDocument.getRelatedFLUXReportDocument().getIDS().get(0);
        assertEquals("related-flux-report-document-id-1", relatedFluxReportDocumentId.getValue());
    }

    @Test
    public void handleFaReportSaving_onlyDuplicateDocument_expectNonSaved() throws Exception {
        // Given
        String fluxfaReportMessageString = getFluxfaReportMessageFromFile("FaReportSaverBeanTest_flux_report_message_1_document_that_already_exists_in_db_id.xml");

        SetFLUXFAReportOrQueryMessageRequest reportOrQueryMessageRequest = new SetFLUXFAReportOrQueryMessageRequest();
        reportOrQueryMessageRequest.setRequest(fluxfaReportMessageString);

        List<ActivityIDType> databaseIds = new ArrayList<>();
        databaseIds.add(new ActivityIDType("related-flux-report-document-id-already-in-database", "UUID"));

        when(matchingIdsService.getMatchingIds(anyList(), eq(ActivityTableType.RELATED_FLUX_REPORT_DOCUMENT_ENTITY))).thenReturn(databaseIds);

        // When
        fluxReportMessageSaver.saveFluxReportMessage(reportOrQueryMessageRequest);

        // Then
        verify(fluxMessageService, never()).saveFishingActivityReportDocuments(any(), any());
    }

    private String getFluxfaReportMessageFromFile(String filename) throws IOException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(filename);
        URI uri = resource.toURI();
        byte[] bytes = Files.readAllBytes(Paths.get(uri));
        return new String(bytes);
    }
}
