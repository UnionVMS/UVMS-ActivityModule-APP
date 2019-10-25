package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.fisheries.ers.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.ContactPartyDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.*;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Arquillian.class)
public class FishingTripResourceTest extends BaseActivityArquillianTest {

    private String authToken;

    @Before
    public void setUp() throws NamingException {
        InitialContext ctx = new InitialContext();
        ctx.rebind("java:global/spatial_endpoint", "http://localhost:8080/activity-rest-test");
        ctx.rebind("java:global/mdr_endpoint", "http://localhost:8080/activity-rest-test");

        authToken = getToken();
    }

    @Test
    public void getFishingTripSummary_noGeometry() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("reports")
                .path("ESP-TRP-20160630000003")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null) // "null" means that we will not look up any geometry
                .header("roleName", "myRole")
                .get(String.class);

        // Then

        // I spent hours trying to get a properly typed ResponseDto<FishingTripSummaryViewDto> from the query, but couldn't get it to work.
        // So the workaround is to get the response entity as a String and just deserialize it here in the test...
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseDto<FishingTripSummaryViewDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FishingTripSummaryViewDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingTripSummaryViewDTO data = responseDto.getData();
        assertEquals(1, data.getSummary().size());
        FishingActivityTypeDTO departure = data.getSummary().get("DEPARTURE");
        assertEquals(1483966440000L, departure.getDate().getTime());

        List<ReportDTO> activityReports = data.getActivityReports();
        assertEquals(17, activityReports.size());

        assertActivityReport(activityReports.get(0),  "2017-01-08T21:06:00.00Z", 77, 43, "AREA_ENTRY");
        assertActivityReport(activityReports.get(1),  "2017-01-09T03:06:00.00Z", 1, 1, "AREA_ENTRY");
        assertActivityReport(activityReports.get(2),  "2017-01-09T10:26:00.00Z", 48, 26, "AREA_ENTRY");
        assertActivityReport(activityReports.get(3),  "2017-01-09T11:04:00.00Z", 81, 45, "AREA_ENTRY");
        assertActivityReport(activityReports.get(4),  "2017-01-09T03:22:00.00Z", 68, 38, "AREA_EXIT");
        assertActivityReport(activityReports.get(5),  "2017-01-09T10:32:00.00Z", 92, 52, "AREA_EXIT");
        assertActivityReport(activityReports.get(6),  "2017-01-09T10:54:00.00Z", 85, 47, "AREA_EXIT");
        assertActivityReport(activityReports.get(7),  "2017-01-09T11:42:00.00Z", 21, 13, "AREA_EXIT");
        assertActivityReport(activityReports.get(8),  "2017-01-09T12:04:00.00Z", 44, 24, "AREA_EXIT");
        assertActivityReport(activityReports.get(9),  "2017-01-09T12:32:00.00Z", 28, 16, "AREA_EXIT");
        assertActivityReport(activityReports.get(10), "2017-01-09T12:54:00.00Z", 98, 56, "DEPARTURE");
        assertActivityReport(activityReports.get(11), "2017-01-08T15:50:00.00Z", 49, 27, "FISHING_OPERATION");
        assertActivityReport(activityReports.get(12), "2017-01-08T23:02:00.00Z", 67, 37, "FISHING_OPERATION");
        assertActivityReport(activityReports.get(13), "2017-01-09T10:24:00.00Z", 29, 17, "FISHING_OPERATION");
        assertActivityReport(activityReports.get(14), "2017-01-09T11:46:00.00Z", 69, 39, "FISHING_OPERATION");
        assertActivityReport(activityReports.get(15), "2017-01-09T12:44:00.00Z", 43, 23, "FISHING_OPERATION");
        assertActivityReport(activityReports.get(16), "2017-01-09T12:52:00.00Z", 70, 40, "FISHING_OPERATION");
    }

    @Test
    public void getVesselDetails() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("vessel")
                .path("details")
                .path("ESP-TRP-20160630000003")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto<VesselDetailsDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<VesselDetailsDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        VesselDetailsDTO vesselDetails = responseDto.getData();
        assertEquals("MADONNA DI POMPEI", vesselDetails.getName());
        assertEquals("MLT", vesselDetails.getCountry());

        Set<ContactPartyDetailsDTO> contactPartyDetailsDTOSet = vesselDetails.getContactPartyDetailsDTOSet();
        assertEquals(1, contactPartyDetailsDTOSet.size());

        ContactPartyDetailsDTO contactPartyDetailsDto = contactPartyDetailsDTOSet.iterator().next();
        assertEquals("MASTER", contactPartyDetailsDto.getRole());
        assertEquals("Miguel Nunes", contactPartyDetailsDto.getContactPerson().getAlias());

        Set<AssetIdentifierDto> vesselIdentifiers = vesselDetails.getVesselIdentifiers();
        assertEquals(1, vesselIdentifiers.size());

        AssetIdentifierDto identifier = vesselIdentifiers.iterator().next();
        assertEquals(VesselIdentifierSchemeIdEnum.CFR, identifier.getIdentifierSchemeId());
        assertEquals("SWE000004540", identifier.getFaIdentifierId());
    }

    @Test
    public void getFishingTripMessageCounter() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("messages")
                .path("FRA-TRP-2016122102030")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto<MessageCountDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<MessageCountDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        MessageCountDTO messageCountDTO = responseDto.getData();
        assertEquals(34, messageCountDTO.getNoOfReports());
        assertEquals(27, messageCountDTO.getNoOfDeclarations());
        assertEquals(7, messageCountDTO.getNoOfNotifications());
        assertEquals(0, messageCountDTO.getNoOfCorrections());
        assertEquals(22, messageCountDTO.getNoOfFishingOperations());
        assertEquals(0, messageCountDTO.getNoOfDeletions());
        assertEquals(0, messageCountDTO.getNoOfCancellations());
    }

    @Test
    public void getFishingTripCatchReports() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("catches")
                .path("MLT-TRP-20160630000001")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto<Map<String, CatchSummaryListDTO>> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<Map<String, CatchSummaryListDTO>>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        Map<String, CatchSummaryListDTO> messageCountDTO = responseDto.getData();
        assertEquals(2, messageCountDTO.size());

        CatchSummaryListDTO onboard = messageCountDTO.get("onboard");
        assertEquals(2117000.0, onboard.getTotal(), 0.1d);
        List<SpeciesQuantityDTO> onboardSpeciesList = onboard.getSpeciesList();
        assertEquals(3, onboardSpeciesList.size());
        assertTypicalSpeciesQuantityDTO(onboardSpeciesList.get(0), "BET", 101000.0, 23000.0);
        assertTypicalSpeciesQuantityDTO(onboardSpeciesList.get(1), "SKJ", 678000.0, 220000.0);

        SpeciesQuantityDTO onboardSpecies2 = onboardSpeciesList.get(2); // this one is a bit different
        assertEquals("YFT", onboardSpecies2.getSpeciesCode());
        assertEquals(1095000.0, onboardSpecies2.getWeight(), 0.1d);
        List<SpeciesAreaDTO> onboardSpecies2AreaList = onboardSpecies2.getAreaInfo();
        assertEquals(3, onboardSpecies2AreaList.size());
        assertSpeciesAreaDTO(onboardSpecies2AreaList.get(0), "51.5", 981000.0);
        assertSpeciesAreaDTO(onboardSpecies2AreaList.get(1), "51.6", 37000.0);
        assertSpeciesAreaDTO(onboardSpecies2AreaList.get(2), "SYC", 77000.0);

        CatchSummaryListDTO landed = messageCountDTO.get("landed");
        assertEquals(3200508.0, landed.getTotal(), 0.1d);
        List<SpeciesQuantityDTO> landedSpeciesList = landed.getSpeciesList();
        assertEquals(3, landedSpeciesList.size());
        assertTypicalSpeciesQuantityDTO(landedSpeciesList.get(0), "BET", 256466.0, 69000.0);
        assertTypicalSpeciesQuantityDTO(landedSpeciesList.get(1), "SKJ", 442708.0, 660000.0);
        assertTypicalSpeciesQuantityDTO(landedSpeciesList.get(2), "YFT", 1661334.0, 111000.0);
    }

    private void assertTypicalSpeciesQuantityDTO(SpeciesQuantityDTO dto, String speciesCode, double area0Weight, double area1Weight) {
        assertEquals(speciesCode, dto.getSpeciesCode());
        assertEquals(area0Weight + area1Weight, dto.getWeight(), 0.1d);
        List<SpeciesAreaDTO> areaInfoList = dto.getAreaInfo();
        assertEquals(2, areaInfoList.size());
        assertSpeciesAreaDTO(areaInfoList.get(0), "51.5", area0Weight);
        assertSpeciesAreaDTO(areaInfoList.get(1), "51.6", area1Weight);
    }

    private void assertSpeciesAreaDTO(SpeciesAreaDTO dto, String areaName, double weight) {
        assertEquals(areaName, dto.getAreaName());
        assertEquals(weight, dto.getWeight(), 0.1d);
    }

    @Test
    public void getTripMapData() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("mapData")
                .path("ESP-TRP-20160630000003")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto<ObjectNode> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<ObjectNode>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        ObjectNode response = responseDto.getData();

        ArrayNode featureCollection = (ArrayNode) response.get("features");

        assertEquals(17, featureCollection.size());

        List<JsonNode> geometryNodes = new ArrayList<>(17);
        for (JsonNode jsonNode : featureCollection) {
            geometryNodes.add(jsonNode);
        }

        assertGeometryNode(geometryNodes.get(0), 60.75, -15.4167);
        assertGeometryNode(geometryNodes.get(1), 10.42, 58.247);
        assertGeometryNode(geometryNodes.get(2), 11.232, 58.359);
        assertGeometryNode(geometryNodes.get(3), 10.3, 58.004);
        assertGeometryNode(geometryNodes.get(4), 11.9733, 57.7153);
        assertGeometryNode(geometryNodes.get(5), 11.257, 57.808);
        assertGeometryNode(geometryNodes.get(6), 11.271, 58.356);
        assertGeometryNode(geometryNodes.get(7), 11.9733, 57.7153);
        assertGeometryNode(geometryNodes.get(8), 11.9733, 57.7153);
        assertGeometryNode(geometryNodes.get(9), 11.9733, 57.7153);
        assertGeometryNode(geometryNodes.get(10), 10.584, 57.715);
        assertGeometryNode(geometryNodes.get(11), 10.352, 57.979);
        assertGeometryNode(geometryNodes.get(12), 14.681, 55.472);
        assertGeometryNode(geometryNodes.get(13), 14.48, 55.682);
        assertGeometryNode(geometryNodes.get(14), 11.233, 58.359);
        assertGeometryNode(geometryNodes.get(15), 14.902, 55.471);
        assertGeometryNode(geometryNodes.get(16), 11.9733, 57.7153);
    }

    // Can be extended to test more fields
    private void assertActivityReport(ReportDTO dto, String occurrenceAsString, int fishingActivityId, int faReportId, String activityType) {
        Instant occurrence = Instant.parse(occurrenceAsString);
        assertEquals(occurrence, dto.getOccurence().toInstant());
        assertEquals(occurrence, dto.getFaReportAcceptedDateTime().toInstant());

        assertEquals(fishingActivityId, dto.getFishingActivityId());
        assertEquals(faReportId, dto.getFaReportID());
        assertEquals(activityType, dto.getActivityType());
    }

    private void assertGeometryNode(JsonNode jsonNode, double latitude, double longitude) {
        JsonNode geometryNode = jsonNode.get("geometry");
        JsonNode coordinates = geometryNode.get("coordinates");
        ArrayNode coordsArray = (ArrayNode) coordinates.get(0);
        DoubleNode latNode = (DoubleNode) coordsArray.get(0);
        DoubleNode longNode = (DoubleNode) coordsArray.get(1);

        assertEquals(latitude, latNode.doubleValue(), 0.0001d);
        assertEquals(longitude, longNode.doubleValue(), 0.0001d);
    }
}
