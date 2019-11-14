package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.google.common.collect.Sets;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryRecord;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishSizeClassEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteriaWithValue;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SummaryFishSize;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SummaryTable;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.FACatchDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

        // Total summary table
        SummaryTable total = faCatchSummaryReportResponse.getTotal();
        assertTrue(total.getFaCatchTypeSummaries().isEmpty());
        assertEquals(1, total.getFishSizeSummaries().size());

        SummaryFishSize totalFishSizeSummary = total.getFishSizeSummaries().get(0);
        assertEquals(FishSizeClassEnum.LSC, totalFishSizeSummary.getFishSize());
        assertTrue(totalFishSizeSummary.getSpecies().isEmpty());
        assertEquals(152454.01999999996, totalFishSizeSummary.getFishSizeCount(), 0.01d);

        // Summary records
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
        assertEquals(152454.02d, fishSizeSummaries.get(0).getFishSizeCount(), 0.01d);
    }

    @Test
    public void getFACatchSummaryReport_byYearAndTerritory() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        query.setSearchCriteriaMap(new HashMap<>());
        query.setSearchCriteriaMapMultipleValues(new HashMap<>());

        List<GroupCriteria> groupByFields = new ArrayList<>();
        groupByFields.add(GroupCriteria.DATE_YEAR);
        groupByFields.add(GroupCriteria.TERRITORY);
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

        // Total summary table
        SummaryTable total = faCatchSummaryReportResponse.getTotal();
        assertTrue(total.getFaCatchTypeSummaries().isEmpty());
        assertEquals(1, total.getFishSizeSummaries().size());

        SummaryFishSize totalFishSizeSummary = total.getFishSizeSummaries().get(0);
        assertEquals(FishSizeClassEnum.LSC, totalFishSizeSummary.getFishSize());
        assertTrue(totalFishSizeSummary.getSpecies().isEmpty());
        assertEquals(152454.02d, totalFishSizeSummary.getFishSizeCount(), 0.01d);

        // Summary records
        List<FACatchSummaryRecord> summaryRecords = faCatchSummaryReportResponse.getSummaryRecords();
        assertEquals(2, summaryRecords.size());

        for (FACatchSummaryRecord summaryRecord : summaryRecords) {
            List<GroupCriteriaWithValue> summaryRecordGroups = summaryRecord.getGroups();
            assertEquals(2, summaryRecordGroups.size());

            if (summaryRecordGroups.get(0).getKey() == GroupCriteria.DATE_YEAR) {
                assertEquals("2017", summaryRecordGroups.get(0).getValue());
                assertEquals(GroupCriteria.TERRITORY, summaryRecordGroups.get(1).getKey());
                assertTrue(summaryRecordGroups.get(1).getValue().equals("IRL") || summaryRecordGroups.get(1).getValue().equals("SYC"));
            } else if (summaryRecordGroups.get(0).getKey() == GroupCriteria.TERRITORY) {
                assertTrue(summaryRecordGroups.get(0).getValue().equals("IRL") || summaryRecordGroups.get(1).getValue().equals("SYC"));
                assertEquals(GroupCriteria.DATE_YEAR, summaryRecordGroups.get(1).getKey());
                assertEquals("2017", summaryRecordGroups.get(1).getValue());
            } else {
                fail();
            }

            SummaryTable summaryTable = summaryRecord.getSummary();
            assertTrue(summaryTable.getFaCatchTypeSummaries().isEmpty());

            List<SummaryFishSize> fishSizeSummaries = summaryTable.getFishSizeSummaries();
            assertEquals(1, fishSizeSummaries.size());
            assertEquals(FishSizeClassEnum.LSC, fishSizeSummaries.get(0).getFishSize());
            assertTrue(fishSizeSummaries.get(0).getSpecies().isEmpty());
            assertTrue(fishSizeSummaries.get(0).getFishSizeCount() > 10_000.0d);
        }
    }

    @Test
    public void getFACatchSummaryDetails() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("catch")
                .path("details")
                .path("FRA-TRP-2016122102030")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));

        ResponseDto<FACatchDetailsDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FACatchDetailsDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FACatchDetailsDTO faCatchDetailsDTO = responseDto.getData();

        FACatchSummaryDTO landing = faCatchDetailsDTO.getLanding();
        assertNull(landing.getTotal().getSummaryFishSize());
        assertNull(landing.getTotal().getSummaryFaCatchType());
        assertTrue(landing.getRecordDTOs().isEmpty());

        FACatchSummaryDTO catches = faCatchDetailsDTO.getCatches();
        SummaryTableDTO catchesTotal = catches.getTotal();
        Map<String, Double> lscFishMap = (Map<String, Double>) catchesTotal.getSummaryFishSize().get(FishSizeClassEnum.LSC);
        assertEquals(16, lscFishMap.size());
        assertNull(catchesTotal.getSummaryFaCatchType());
        assertEquals(1, catchesTotal.getSummaryFishSize().size());

        for (FACatchSummaryRecordDTO recordDTO : catches.getRecordDTOs()) {
            assertRecordDto(recordDTO);
        }
    }

    private void assertRecordDto(FACatchSummaryRecordDTO dto) {

        List<GroupCriteriaWithValue> criterias = dto.getGroups();
        Set<GroupCriteria> actualCriterias = criterias.stream().map(c -> c.getKey()).collect(Collectors.toSet());
        HashSet<GroupCriteria> expectedCriterias = Sets.newHashSet(GroupCriteria.DATE, GroupCriteria.EFFORT_ZONE, GroupCriteria.FAO_AREA, GroupCriteria.ICES_STAT_RECTANGLE, GroupCriteria.TERRITORY);

        assertEquals(expectedCriterias, actualCriterias);
        assertNotNull(criterias.get(0).getKey());
        assertNotNull(criterias.get(1).getKey());
        assertNotNull(criterias.get(2).getKey());
        assertNotNull(criterias.get(3).getKey());
        assertNotNull(criterias.get(4).getKey());

        SummaryTableDTO summaryTable = dto.getSummaryTable();
        assertNull(summaryTable.getSummaryFaCatchType());
        Map<String, Double> lscFishes = (Map<String, Double>)summaryTable.getSummaryFishSize().get(FishSizeClassEnum.LSC);
        assertFalse(lscFishes.isEmpty());
    }
}
