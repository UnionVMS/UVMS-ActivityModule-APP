package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
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
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@Ignore
public class FACatchResourceTest extends BaseActivityArquillianTest {

    @Before
    public void setUp() throws NamingException, IOException, JAXBException, ServiceException, NotSupportedException, SystemException {
        super.setUp();
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
        assertEquals(706823.8700000001, totalFishSizeSummary.getFishSizeCount(), 0.01d);

        // Summary records
        List<FACatchSummaryRecord> summaryRecords = faCatchSummaryReportResponse.getSummaryRecords();
        assertEquals(2, summaryRecords.size());

        FACatchSummaryRecord faCatchSummaryRecord2013 = summaryRecords.get(0);
        assertEquals(1, faCatchSummaryRecord2013.getGroups().size());
        assertEquals(GroupCriteria.DATE_YEAR, faCatchSummaryRecord2013.getGroups().get(0).getKey());
        assertEquals("2013", faCatchSummaryRecord2013.getGroups().get(0).getValue());

        SummaryTable summaryTable2013 = faCatchSummaryRecord2013.getSummary();
        assertTrue(summaryTable2013.getFaCatchTypeSummaries().isEmpty());

        List<SummaryFishSize> fishSizeSummaries2013 = summaryTable2013.getFishSizeSummaries();
        assertEquals(1, fishSizeSummaries2013.size());
        assertEquals(FishSizeClassEnum.LSC, fishSizeSummaries2013.get(0).getFishSize());
        assertTrue(fishSizeSummaries2013.get(0).getSpecies().isEmpty());
        assertEquals(700522.4700000001, fishSizeSummaries2013.get(0).getFishSizeCount(), 0.01d);

        FACatchSummaryRecord faCatchSummaryRecord2012 = summaryRecords.get(1);
        assertEquals(1, faCatchSummaryRecord2012.getGroups().size());
        assertEquals(GroupCriteria.DATE_YEAR, faCatchSummaryRecord2012.getGroups().get(0).getKey());
        assertEquals("2012", faCatchSummaryRecord2012.getGroups().get(0).getValue());

        SummaryTable summaryTable2012 = faCatchSummaryRecord2012.getSummary();
        assertTrue(summaryTable2012.getFaCatchTypeSummaries().isEmpty());

        List<SummaryFishSize> fishSizeSummaries2012 = summaryTable2012.getFishSizeSummaries();
        assertEquals(1, fishSizeSummaries2012.size());
        assertEquals(FishSizeClassEnum.LSC, fishSizeSummaries2012.get(0).getFishSize());
        assertTrue(fishSizeSummaries2012.get(0).getSpecies().isEmpty());
        assertEquals(6301.4, fishSizeSummaries2012.get(0).getFishSizeCount(), 0.01d);
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
        assertEquals(706823.8700000001, totalFishSizeSummary.getFishSizeCount(), 0.01d);

        // Summary records
        List<FACatchSummaryRecord> summaryRecords = faCatchSummaryReportResponse.getSummaryRecords();
        assertEquals(3, summaryRecords.size());

        FACatchSummaryRecord faCatchSummaryRecord = summaryRecords.get(2); // Get the last record, that one contains both YEAR and TERRITORY

        List<GroupCriteriaWithValue> groups = faCatchSummaryRecord.getGroups();
        GroupCriteriaWithValue yearGroup = groups.get(0);
        GroupCriteriaWithValue territoryGroup = groups.get(1);

        assertEquals("2013", yearGroup.getValue());
        assertEquals("KEF", territoryGroup.getValue());

        SummaryTable summary = faCatchSummaryRecord.getSummary();

        List<SummaryFishSize> fishSizeSummaries2012 = summary.getFishSizeSummaries();
        assertEquals(1, fishSizeSummaries2012.size());
        assertEquals(FishSizeClassEnum.LSC, fishSizeSummaries2012.get(0).getFishSize());
        assertTrue(fishSizeSummaries2012.get(0).getSpecies().isEmpty());
        assertEquals(692325.1699999999, fishSizeSummaries2012.get(0).getFishSizeCount(), 0.01d);
    }

    /*
        Uses fishing trip from flux001_anonymized.xml
     */
    @Test
    public void getFACatchSummaryDetails() throws JsonProcessingException {
        // When
        String responseAsString = getWebTarget()
                .path("catch")
                .path("details")
                .path("ICV-MOM-83R964412B3")
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
        assertEquals(3, lscFishMap.size());

        assertEquals(lscFishMap.get("HER"), 114923.9, 0.0);
        assertEquals(lscFishMap.get("SPR"), 617.84, 0.0);
        assertEquals(lscFishMap.get("MXV"), 1442.54, 0.0);
    }
}
