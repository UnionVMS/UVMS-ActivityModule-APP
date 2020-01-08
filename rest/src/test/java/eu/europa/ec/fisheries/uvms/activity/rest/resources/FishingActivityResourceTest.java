package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.service.search.SortKey;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginatedResponse;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class FishingActivityResourceTest extends BaseActivityArquillianTest {

    @Before
    public void setUp() throws NamingException, IOException, JAXBException, ServiceException, NotSupportedException, SystemException {
        super.setUp();
    }

    @Test
    public void getCommunicationChannels_OK() throws JsonProcessingException {
        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("commChannel")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));

        ResponseDto<FaReportSourceEnum[]> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FaReportSourceEnum[]>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FaReportSourceEnum[] faReportSourceEnumsExpected = {FaReportSourceEnum.FLUX, FaReportSourceEnum.MANUAL};
        assertArrayEquals(faReportSourceEnumsExpected, responseDto.getData());
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
        String responseAsString = list(query);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        List<FishingActivityReportDTO> resultList = responseDto.getResultList();

        assertEquals(54, responseDto.getTotalItemsCount());
        assertEquals(54, resultList.size());

        for (FishingActivityReportDTO fishingActivityReportDTO : resultList) {
            assertEquals("FLUX", fishingActivityReportDTO.getDataSource());
            assertEquals(1, fishingActivityReportDTO.getFromId().size());
            assertTrue(fishingActivityReportDTO.getFromId().get(0).equals("WVO") || fishingActivityReportDTO.getFromId().get(0).equals("UVH"));
            assertNull(fishingActivityReportDTO.getFromName());
            assertTrue("9".equals(fishingActivityReportDTO.getPurposeCode()) || "5".equals(fishingActivityReportDTO.getPurposeCode()));
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

    @Test
    public void listActivityReportsByQuery_multipleActivityTypes() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> activityTypeValues = new ArrayList<>();
        activityTypeValues.add("FISHING_OPERATION");
        activityTypeValues.add("DEPARTURE");
        searchCriteriaMapMultiVal.put(SearchFilter.ACTIVITY_TYPE, activityTypeValues);
        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        // When
        String responseAsString = list(query);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        List<FishingActivityReportDTO> resultList = responseDto.getResultList();

        assertEquals(41, responseDto.getTotalItemsCount());
        assertEquals(41, resultList.size());

        for (FishingActivityReportDTO fishingActivityReportDTO : resultList) {
            String activityType = fishingActivityReportDTO.getActivityType();
            assertTrue(activityType.equals("FISHING_OPERATION") || activityType.equals("DEPARTURE"));
        }
    }

    @Test
    public void listActivityReportsByQuery_byReportId() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        // When
        String responseAsString = list(query);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        List<FishingActivityReportDTO> resultList = responseDto.getResultList();

        FishingActivityReportDTO fishingActivityReportDTO = resultList.get(0);
        int faReportID = fishingActivityReportDTO.getFaReportID();

        FishingActivityQuery query2 = new FishingActivityQuery();

        query2.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.FA_REPORT_ID, Integer.toString(faReportID));
        query2.setSearchCriteriaMap(searchCriteriaMap);

        String responseString2 = list(query2);

        PaginatedResponse<FishingActivityReportDTO> response2 =
                objectMapper.readValue(responseString2, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        assertEquals(200, response2.getCode());

        List<FishingActivityReportDTO> resultList2 = response2.getResultList();

        assertEquals(1, response2.getTotalItemsCount());
        assertEquals(1, resultList2.size());

        FishingActivityReportDTO dto = resultList2.get(0);
        assertEquals(faReportID, dto.getFaReportID());
    }

    @Test
    public void listActivityReportsByQuery_byReportType() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        query.setSearchCriteriaMap(searchCriteriaMap);

        // When
        String responseAsString = list(query);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        List<FishingActivityReportDTO> resultList = responseDto.getResultList();

        assertEquals(49, responseDto.getTotalItemsCount());
        assertEquals(49, resultList.size());

        for (FishingActivityReportDTO fishingActivityReportDTO : resultList) {
            assertEquals("DECLARATION", fishingActivityReportDTO.getFAReportType());
        }
    }

    @Test
    public void listActivityReportsByQuery_byReportType_withPagination() throws JsonProcessingException {
        // Given
        FishingActivityQuery query1 = new FishingActivityQuery();
        FishingActivityQuery query2 = new FishingActivityQuery();

        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query1.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);
        query2.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        query1.setSearchCriteriaMap(searchCriteriaMap);
        query2.setSearchCriteriaMap(searchCriteriaMap);

        PaginationDto pagination1 = new PaginationDto();
        pagination1.setPageSize(2);
        pagination1.setOffset(0);
        query1.setPagination(pagination1);

        PaginationDto pagination2 = new PaginationDto();
        pagination2.setPageSize(2);
        pagination2.setOffset(1); // note: offset is 1 item, not 1 page
        query2.setPagination(pagination2);

        // make sorting deterministic
        SortKey sortingDto = new SortKey();
        sortingDto.setSortBy(SearchFilter.OCCURRENCE);
        sortingDto.setReversed(false);
        query1.setSorting(sortingDto);
        query2.setSorting(sortingDto);

        // When
        String query1Response = list(query1);

        String query2Response = list(query2);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> response1 =
                objectMapper.readValue(query1Response, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        PaginatedResponse<FishingActivityReportDTO> response2 =
                objectMapper.readValue(query2Response, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        assertEquals(200, response1.getCode());
        assertEquals(200, response2.getCode());

        List<FishingActivityReportDTO> resultList1 = response1.getResultList();
        List<FishingActivityReportDTO> resultList2 = response2.getResultList();

        assertEquals(49, response1.getTotalItemsCount());
        assertEquals(2, resultList1.size());

        assertEquals(49, response2.getTotalItemsCount());
        assertEquals(2, resultList2.size());

        assertEquals(resultList2.get(0).getFishingActivityId(), resultList1.get(1).getFishingActivityId());
    }

    @Test
    public void listActivityReportsByQuery_byPort() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.PORT, "PAZIM");
        query.setSearchCriteriaMap(searchCriteriaMap);

        // When
        String responseAsString = list(query);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        List<FishingActivityReportDTO> resultList = responseDto.getResultList();

        assertEquals(4, responseDto.getTotalItemsCount());
        assertEquals(4, resultList.size());

        for (FishingActivityReportDTO fishingActivityReportDTO : resultList) {
            List<String> portList = fishingActivityReportDTO.getPort();
            assertEquals(1, portList.size());

            String port = portList.get(0);
            assertEquals("PAZIM", port);
        }
    }

    @Test
    public void listActivityReportsByQuery_incorrectPurposeCode_expectNoResults() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, Lists.newArrayList("929"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        // When
        String responseAsString = list(query);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        List<FishingActivityReportDTO> resultList = responseDto.getResultList();

        assertEquals(0, responseDto.getTotalItemsCount());
        assertEquals(0, resultList.size());
    }

    /**
     * This test currently asserts that we get zero results back when we query with a valid vessel ID.
     * This is because this test is 50% complete: when AssetModuleServiceBean is updated to actually make
     * calls to the asset module this test should fail because we will get more than zero results back.
     * The intent is to have this test ready and for the asserts to be updated when AssetModuleServiceBean
     * is functioning again.
     *
     * TODO: still not working properly even though mock is working
     */
    @Test
    public void listActivityReportsByQuery_withVesselId() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.VESSEL, "BZR755583695");
        query.setSearchCriteriaMap(searchCriteriaMap);

        Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues = new HashMap<>();
        searchCriteriaMapMultipleValues.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValues);

        // When
        String responseAsString = list(query);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

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
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FishingTripResponse>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingTripResponse fishingTripResponse = responseDto.getData();

        assertEquals(0, fishingTripResponse.getFishingActivityLists().size());
        assertEquals(BigInteger.valueOf(6L), fishingTripResponse.getTotalCountOfRecords());
        assertNull(fishingTripResponse.getMethod());
        assertNull(fishingTripResponse.getPluginType());

        List<FishingTripIdWithGeometry> fishingTripIds = fishingTripResponse.getFishingTripIdLists();
        assertEquals(6, fishingTripIds.size());

        assertEquals("RNX-BVF-14E728224N4", fishingTripIds.get(0).getTripId());
        assertEquals("SLT-YEZ-83772353", fishingTripIds.get(1).getTripId());
        assertEquals("OFL-QEB-11354164", fishingTripIds.get(2).getTripId());
        assertEquals("UUR-XSM-45913768", fishingTripIds.get(3).getTripId());
        assertEquals("XQF-NYK-8726D815443D8", fishingTripIds.get(4).getTripId());
        assertEquals("ICV-MOM-83R964412B3", fishingTripIds.get(5).getTripId());
    }

    @Test
    public void getAllCorrections_reportNotFound() throws JsonProcessingException {
        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("history")
                .path("unrecognised-report-id")
                .path("UUID")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseDto<List<FaReportCorrectionDTO>> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<List<FaReportCorrectionDTO>>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        List<FaReportCorrectionDTO> data = responseDto.getData();
        assertEquals(0, data.size());
    }

    @Test
    public void getAllCorrections() throws JsonProcessingException {
        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("history")
                .path("8c90fa8b-9778-4b08-811b-050608589590")
                .path("UUID")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseDto<List<FaReportCorrectionDTO>> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<List<FaReportCorrectionDTO>>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        List<FaReportCorrectionDTO> data = responseDto.getData();
        assertEquals(2, data.size());

        FaReportCorrectionDTO originalReport = data.get(0);
        assertEquals("UPDATED", originalReport.getCorrectionType());
        assertEquals(1365175257000L, originalReport.getCreationDate().getTime());
        assertEquals(1365115483000L, originalReport.getAcceptedDate().getTime());
        assertNull(originalReport.getOwnerFluxPartyName());
        assertEquals(9, originalReport.getPurposeCode().intValue());
        assertEquals(1, originalReport.getFaReportIdentifiers().size());
        assertEquals("UUID", originalReport.getFaReportIdentifiers().get("8c90fa8b-9778-4b08-811b-050608589590"));

        FaReportCorrectionDTO correctionReport = data.get(1);
        assertEquals("NEW", correctionReport.getCorrectionType());
        assertEquals(1365175257000L, correctionReport.getCreationDate().getTime());
        assertEquals(1365175252000L, correctionReport.getAcceptedDate().getTime());
        assertNull(correctionReport.getOwnerFluxPartyName());
        assertEquals(5, correctionReport.getPurposeCode().intValue());
        assertEquals(1, correctionReport.getFaReportIdentifiers().size());
        assertEquals("UUID", correctionReport.getFaReportIdentifiers().get("27d8c389-c792-4271-90d4-e7108d6f5f79"));
    }

    @Test
    public void getPreviousFishingActivity() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        HashMap<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.TRIP_ID, "ICV-MOM-83R964412B3");
        query.setSearchCriteriaMap(searchCriteriaMap);

        Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues = new HashMap<>();
        searchCriteriaMapMultipleValues.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValues);

        String activityListResponseAsString = list(query);

        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> activityListResponse =
                objectMapper.readValue(activityListResponseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        List<FishingActivityReportDTO> resultList = activityListResponse.getResultList();
        resultList.sort(Comparator.comparing(FishingActivityDTO::getOccurence));

        int activityListSize = resultList.size();
        FishingActivityReportDTO lastActivity = resultList.get(activityListSize - 1);
        FishingActivityReportDTO secondToLastActivity = resultList.get(activityListSize - 2);

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("previous")
                .path(Integer.toString(lastActivity.getFishingActivityId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ResponseDto<Integer> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<Integer>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        assertEquals(secondToLastActivity.getFishingActivityId(), responseDto.getData().intValue());
    }

    @Test
    public void getNextFishingActivity() throws JsonProcessingException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        HashMap<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.TRIP_ID, "ICV-MOM-83R964412B3");
        query.setSearchCriteriaMap(searchCriteriaMap);

        Map<SearchFilter, List<String>> searchCriteriaMapMultipleValues = new HashMap<>();
        searchCriteriaMapMultipleValues.put(SearchFilter.PURPOSE, Lists.newArrayList("9"));
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValues);

        String activityListResponseAsString = list(query);

        ObjectMapper objectMapper = new ObjectMapper();

        PaginatedResponse<FishingActivityReportDTO> activityListResponse =
                objectMapper.readValue(activityListResponseAsString, new TypeReference<PaginatedResponse<FishingActivityReportDTO>>() {
                });

        List<FishingActivityReportDTO> resultList = activityListResponse.getResultList();
        resultList.sort(Comparator.comparing(FishingActivityDTO::getOccurence));

        FishingActivityReportDTO firstActivity = resultList.get(0);
        FishingActivityReportDTO nextActivity = resultList.get(1);

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("next")
                .path(Integer.toString(firstActivity.getFishingActivityId()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ResponseDto<Integer> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<Integer>>() {
                });

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        assertEquals(nextActivity.getFishingActivityId(), responseDto.getData().intValue());
    }

    private String list(FishingActivityQuery query) {
        return getWebTarget()
                .path("fa")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null)
                .header("roleName", "myRole")
                .post(Entity.json(query), String.class);
    }
}
