package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginatedResponse;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class FishingActivityResourceTest extends BaseActivityArquillianTest {

    private String authToken;

    @Before
    public void setUp() {
        authToken = getToken();
    }

    @Test
    public void getCommunicationChannels_OK() {
        // Given

        // When
        Response response = getWebTarget()
                .path("fa")
                .path("commChannels")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get();

        // Then
        assertEquals(200, response.getStatus());
        FaReportSourceEnum[] faReportSourceEnums = response.readEntity(FaReportSourceEnum[].class);
        FaReportSourceEnum[] faReportSourceEnumsExpected = {FaReportSourceEnum.FLUX, FaReportSourceEnum.MANUAL};
        assertArrayEquals(faReportSourceEnumsExpected, faReportSourceEnums);
    }

    @Test
    public void listActivityReportsByQuery_noSearchCriteria() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        query.setSearchCriteriaMap(new HashMap<>());

        Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues = new HashMap<>();
        searchCriteriaMapMultipleValues.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValues);

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null)
                .header("roleName", "myRole")
                .post(Entity.json(query), String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        List<FishingActivityReportDTO> resultList = responseDto.getResultList();

        assertEquals(59, responseDto.getTotalItemsCount());
        assertEquals(59, resultList.size());

        for (FishingActivityReportDTO fishingActivityReportDTO : resultList) {
            assertEquals("FLUX", fishingActivityReportDTO.getDataSource());
            assertEquals(1, fishingActivityReportDTO.getFromId().size());
            assertTrue(fishingActivityReportDTO.getFromId().get(0).equals("MLT") || fishingActivityReportDTO.getFromId().get(0).equals("FRA"));
            assertNull(fishingActivityReportDTO.getFromName());
            assertEquals("9", fishingActivityReportDTO.getPurposeCode());
            assertEquals(1, fishingActivityReportDTO.getUniqueFAReportId().size());
            assertNotNull(fishingActivityReportDTO.getUniqueFAReportId().get(0).getFluxReportId());
            assertEquals("UUID", fishingActivityReportDTO.getUniqueFAReportId().get(0).getFluxReportSchemeId());
            assertEquals(0, fishingActivityReportDTO.getCancelingReportID());
            assertEquals(0, fishingActivityReportDTO.getDeletingReportID());
            assertNull(fishingActivityReportDTO.getFaUniqueReportID());
            assertNull(fishingActivityReportDTO.getFaUniqueReportSchemeID());
            assertNull(fishingActivityReportDTO.getFaReferenceID());
            assertNull(fishingActivityReportDTO.getFaReferenceSchemeID());
            assertNotNull(fishingActivityReportDTO.getActivityType());
            assertNull(fishingActivityReportDTO.getGeometry());
            assertNull(fishingActivityReportDTO.getReason());
            assertNull(fishingActivityReportDTO.getFluxLocations());
            assertNull(fishingActivityReportDTO.getFishingGears());
            assertNull(fishingActivityReportDTO.getFluxCharacteristics());
            assertNull(fishingActivityReportDTO.getDelimitedPeriod());
        }
    }

    /**
     * This test currently asserts that we get zero results back when we query with a valid vessel ID.
     * This is because this test is 50% complete: when AssetModuleServiceBean is updated to actually make
     * calls to the asset module this test should fail because we will get more than zero results back.
     * The intent is to have this test ready and for the asserts to be updated when AssetModuleServiceBean
     * is functioning again.
     */
    @Test
    public void listActivityReportsByQuery_withVesselId() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.VESSEL, "SWE000004540");
        query.setSearchCriteriaMap(searchCriteriaMap);

        Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues = new HashMap<>();
        searchCriteriaMapMultipleValues.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValues);

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null)
                .header("roleName", "myRole")
                .post(Entity.json(query), String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        assertTrue(responseDto.getResultList().isEmpty());
    }

    @Test
    public void listFishingTripsByQuery() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        query.setSearchCriteriaMap(new HashMap<>());

        Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues = new HashMap<>();
        searchCriteriaMapMultipleValues.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValues);

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("listTrips")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .post(Entity.json(query), String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));

        ResponseDto<FishingTripResponse> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FishingTripResponse>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingTripResponse fishingTripResponse = responseDto.getData();

        assertEquals(0, fishingTripResponse.getFishingActivityLists().size());
        assertEquals(BigInteger.valueOf(57L), fishingTripResponse.getTotalCountOfRecords());
        assertNull(fishingTripResponse.getMethod());
        assertNull(fishingTripResponse.getPluginType());

        List<FishingTripIdWithGeometry> fishingTripIds = fishingTripResponse.getFishingTripIdLists();
        assertEquals(3, fishingTripIds.size());

        assertEquals("ESP-TRP-20160630000003", fishingTripIds.get(0).getTripId());
        assertEquals("FRA-TRP-2016122102030", fishingTripIds.get(1).getTripId());
        assertEquals("MLT-TRP-20160630000001", fishingTripIds.get(2).getTripId());
    }

    @Test
    public void getAllCorrections() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("history")
                .path("C8471F99-FB55-4B05-9AAA-7E08738C92B2")
                .path("UUID")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseDto<List<FaReportCorrectionDTO>> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<List<FaReportCorrectionDTO>>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        List<FaReportCorrectionDTO> data = responseDto.getData();
        assertEquals(1, data.size());
        FaReportCorrectionDTO faReportCorrectionDTO = data.get(0);
        assertEquals("58", faReportCorrectionDTO.getId());
        assertEquals("NEW", faReportCorrectionDTO.getCorrectionType());
        assertEquals(1_467_954_000_000L, faReportCorrectionDTO.getCreationDate().getTime());
        assertEquals(1_483_931_160_000L, faReportCorrectionDTO.getAcceptedDate().getTime());
        assertNull(faReportCorrectionDTO.getOwnerFluxPartyName());
        assertEquals(9, faReportCorrectionDTO.getPurposeCode().intValue());
        assertEquals(1, faReportCorrectionDTO.getFaReportIdentifiers().size());
        assertEquals("UUID", faReportCorrectionDTO.getFaReportIdentifiers().get("C8471F99-FB55-4B05-9AAA-7E08738C92B2"));
    }

    @Test
    public void getPreviousFishingActivity() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("previous")
                .path("103")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseDto<Integer> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<Integer>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        assertEquals(102, responseDto.getData().intValue());
    }

    @Test
    public void getNextFishingActivity() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("next")
                .path("102")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseDto<Integer> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<Integer>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        assertEquals(103, responseDto.getData().intValue());
    }
}
