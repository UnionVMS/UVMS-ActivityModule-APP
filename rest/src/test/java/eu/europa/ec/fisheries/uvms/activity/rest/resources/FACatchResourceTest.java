package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryRecord;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishSizeClassEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SummaryFishSize;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SummaryTable;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class FACatchResourceTest extends BaseActivityArquillianTest {
    private String authToken;

    @Before
    public void setUp() {
        authToken = getToken();
    }

    @Test
    public void getFACatchSummaryReport_byYear() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        query.setSearchCriteriaMap(new HashMap<>());
        query.setSearchCriteriaMapMultipleValues(new HashMap<>());

        List<GroupCriteria> groupByFields = new ArrayList<>();
        groupByFields.add(GroupCriteria.DATE_YEAR);
        query.setGroupByFields(groupByFields);

        // When
        String responseAsString = getWebTarget()
                .path("catch")
                .path("summary")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .post(Entity.json(query), String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));

        ResponseDto<FACatchSummaryReportResponse> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FACatchSummaryReportResponse>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FACatchSummaryReportResponse faCatchSummaryReportResponse = responseDto.getData();

        SummaryTable total = faCatchSummaryReportResponse.getTotal();
        assertTrue(total.getFaCatchTypeSummaries().isEmpty());
        assertEquals(1, total.getFishSizeSummaries().size());

        SummaryFishSize totalFishSizeSummary = total.getFishSizeSummaries().get(0);
        assertEquals(FishSizeClassEnum.LSC, totalFishSizeSummary.getFishSize());
        assertTrue(totalFishSizeSummary.getSpecies().isEmpty());
        assertEquals(152454.01999999996, totalFishSizeSummary.getFishSizeCount(), 0.01d);

        List<FACatchSummaryRecord> summaryRecords = faCatchSummaryReportResponse.getSummaryRecords();
        assertEquals(1, summaryRecords.size());

        FACatchSummaryRecord faCatchSummaryRecord = summaryRecords.get(0);
        assertEquals(1, faCatchSummaryRecord.getGroups().size());
        assertEquals(GroupCriteria.DATE_YEAR, faCatchSummaryRecord.getGroups().get(0).getKey());
        assertEquals("2017", faCatchSummaryRecord.getGroups().get(0).getValue());

        SummaryTable summaryTable = faCatchSummaryRecord.getSummary();
        assertTrue(summaryTable.getFaCatchTypeSummaries().isEmpty());

        List<SummaryFishSize> fishSizeSummaries = summaryTable.getFishSizeSummaries();
        assertEquals(1, fishSizeSummaries.size());
        assertEquals(FishSizeClassEnum.LSC, fishSizeSummaries.get(0).getFishSize());
        assertTrue(fishSizeSummaries.get(0).getSpecies().isEmpty());
        assertEquals(152454.01999999996, fishSizeSummaries.get(0).getFishSizeCount(), 0.01d);
    }
}
