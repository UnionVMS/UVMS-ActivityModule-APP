package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.ContactPartyDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchEvolutionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CronologyTripDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.FishingActivityTypeDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.FishingTripSummaryViewDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.MessageCountDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.ReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.SpeciesAreaDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.SpeciesQuantityDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripOverviewDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripWidgetDto;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class FishingTripResourceTest extends BaseActivityArquillianTest {

    @Before
    public void setUp() throws NamingException, IOException, JAXBException, ServiceException, NotSupportedException, SystemException {
        super.setUp();
    }

    @Ignore("TODO fix for new test data")
    @Test
    public void getFishingTripSummary_noGeometry() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("reports")
                .path("QPS-ECV-7513J636531W5")
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
        assertEquals(1351923792000L, departure.getDate().getTime());

        List<ReportDTO> activityReports = data.getActivityReports();
        assertEquals(2, activityReports.size());

        ReportDTO activityReportDto0 = activityReports.get(0);
        ReportDTO activityReportDto1 = activityReports.get(1);

        assertEquals(Instant.parse("2012-11-03T06:23:12Z"), activityReportDto0.getOccurence().toInstant());
        assertEquals(Instant.parse("2012-11-07T21:48:12Z"), activityReportDto0.getFaReportAcceptedDateTime().toInstant());
        assertEquals(14, activityReportDto0.getFishingActivityId());
        assertEquals(14, activityReportDto0.getFaReportID());
        assertEquals("DEPARTURE", activityReportDto0.getActivityType());

        assertEquals(Instant.parse("2012-11-04T00:30:12Z"), activityReportDto1.getOccurence().toInstant());
        assertEquals(Instant.parse("2012-11-07T21:48:13Z"), activityReportDto1.getFaReportAcceptedDateTime().toInstant());
        assertEquals(15, activityReportDto1.getFishingActivityId());
        assertEquals(15, activityReportDto1.getFaReportID());
        assertEquals("FISHING_OPERATION", activityReportDto1.getActivityType());
    }

    @Ignore("TODO fix for new test data")
    @Test
    public void getFishingTripSummary_tripNotFound() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("reports")
                .path("THIS-HERE-AINT-NO-TRIP")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null) // "null" means that we will not look up any geometry
                .header("roleName", "myRole")
                .get(String.class);

        // Then

        ObjectMapper objectMapper = new ObjectMapper();

        ResponseDto<FishingTripSummaryViewDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FishingTripSummaryViewDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingTripSummaryViewDTO data = responseDto.getData();
        assertNull(data.getSummary());
        assertNull(data.getActivityReports());
    }

    @Ignore("TODO fix for new test data")
    @Test
    public void getVesselDetails() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("vessel")
                .path("details")
                .path("QPS-ECV-7513J636531W5")
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
        assertEquals("Test Ship 001", vesselDetails.getName());
        assertEquals("MLT", vesselDetails.getCountry());

        Set<ContactPartyDetailsDTO> contactPartyDetailsDTOSet = vesselDetails.getContactPartyDetailsDTOSet();
        assertEquals(1, contactPartyDetailsDTOSet.size());

        ContactPartyDetailsDTO contactPartyDetailsDto = contactPartyDetailsDTOSet.iterator().next();
        assertEquals("MASTER", contactPartyDetailsDto.getRole());
        assertEquals("Contact Person 001 Alias", contactPartyDetailsDto.getContactPerson().getAlias());

        Set<AssetIdentifierDto> vesselIdentifiers = vesselDetails.getVesselIdentifiers();
        assertEquals(1, vesselIdentifiers.size());

        AssetIdentifierDto identifier = vesselIdentifiers.iterator().next();
        assertEquals(VesselIdentifierSchemeIdEnum.CFR, identifier.getIdentifierSchemeId());
        assertEquals("SWEFAKE00001", identifier.getFaIdentifierId());
    }

    @Test
    public void getVesselDetails_tripNotFound() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("vessel")
                .path("details")
                .path("YOU-WILL-NEVER-SEE-A-TRIP-ID-SUCH-AS-THIS")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto<VesselDetailsDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<VesselDetailsDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());
        assertNull(responseDto.getData());
    }

    @Test
    public void getFishingTripMessageCounter() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("messages")
                .path("ICV-MOM-83R964412B3")
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
        assertEquals(4, messageCountDTO.getNoOfReports());
        assertEquals(3, messageCountDTO.getNoOfDeclarations());
        assertEquals(1, messageCountDTO.getNoOfNotifications());
        assertEquals(0, messageCountDTO.getNoOfCorrections());
        assertEquals(2, messageCountDTO.getNoOfFishingOperations());
        assertEquals(0, messageCountDTO.getNoOfDeletions());
        assertEquals(0, messageCountDTO.getNoOfCancellations());
    }

    @Test
    public void getFishingTripMessageCounter_tripNotFound() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("messages")
                .path("BLARRRRGHH")
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
        assertEquals(0, messageCountDTO.getNoOfReports());
        assertEquals(0, messageCountDTO.getNoOfDeclarations());
        assertEquals(0, messageCountDTO.getNoOfNotifications());
        assertEquals(0, messageCountDTO.getNoOfCorrections());
        assertEquals(0, messageCountDTO.getNoOfFishingOperations());
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
                .path("ICV-MOM-83R964412B3")
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
        assertEquals(371662.94, onboard.getTotal(), 0.1d);
        List<SpeciesQuantityDTO> onboardSpeciesList = onboard.getSpeciesList();
        assertEquals(3, onboardSpeciesList.size());
        assertTypicalSpeciesQuantityDTO(onboardSpeciesList.get(0), "HER", 182535.41, 182535.41);
        assertTypicalSpeciesQuantityDTO(onboardSpeciesList.get(1), "MXV", 2060.38, 2060.38);
        assertTypicalSpeciesQuantityDTO(onboardSpeciesList.get(2), "SPR", 1235.68, 1235.68);

        CatchSummaryListDTO landed = messageCountDTO.get("landed");
        assertEquals(137694.38, landed.getTotal(), 0.1d);
        List<SpeciesQuantityDTO> landedSpeciesList = landed.getSpeciesList();
        assertEquals(3, landedSpeciesList.size());
        assertTypicalSpeciesQuantityDTO(landedSpeciesList.get(0), "HER", 67611.51, 67611.51);
        assertTypicalSpeciesQuantityDTO(landedSpeciesList.get(1), "MXV", 617.84, 617.84);
        assertTypicalSpeciesQuantityDTO(landedSpeciesList.get(2), "SPR", 617.84, 617.84);
    }

    private void assertTypicalSpeciesQuantityDTO(SpeciesQuantityDTO dto, String speciesCode, double area0Weight, double area1Weight) {
        assertEquals(speciesCode, dto.getSpeciesCode());
        assertEquals(area0Weight + area1Weight, dto.getWeight(), 0.1d);
        List<SpeciesAreaDTO> areaInfoList = dto.getAreaInfo();
        assertEquals(2, areaInfoList.size());
        assertSpeciesAreaDTO(areaInfoList.get(0), "17.8.s", area0Weight);
        assertSpeciesAreaDTO(areaInfoList.get(1), "KEF", area1Weight);
    }

    private void assertSpeciesAreaDTO(SpeciesAreaDTO dto, String areaName, double weight) {
        assertEquals(areaName, dto.getAreaName());
        assertEquals(weight, dto.getWeight(), 0.1d);
    }

    @Test
    public void getFishingTripCatchReports_tripNotFound() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("catches")
                .path("WHAT-ME-PROVIDE-A-REAL-TRIP-ID")
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
        assertEquals(0.0, onboard.getTotal(), 0.1d);
        assertTrue(onboard.getSpeciesList().isEmpty());

        CatchSummaryListDTO landed = messageCountDTO.get("landed");
        assertEquals(0, landed.getTotal(), 0.1d);
        assertTrue(landed.getSpeciesList().isEmpty());
    }

    /**
     * This test tests the behaviour of the getCronologyOfFishingTrip (sic) endpoint
     * as it is at the time of writing the tests. Comments in the test point out dubious behaviours
     * in the end point logic.
     */
    @Ignore("TODO fix for new test data")
    @Test
    public void getChronologyOfFishingTrip() throws JsonProcessingException {
        // Weird thing #1: All fishing trips include themselves in "previousTrips"
        // #2: Request for the chronology of the "ESP" and "FRA" trips return the exact same set of previous trips, both saying that they are chronologically after the other.
        //     The "MLT" trip for some reason doesn't have the "FRA" trip as a previous trip
        // #3: No response says that they have any "next trips", that collection is always null
        assertChronologyOfFishingTrip("MLT-TRP-20160630000001", 100, "MLT-TRP-20160630000001", "ESP-TRP-20160630000003");
        assertChronologyOfFishingTrip("ESP-TRP-20160630000003", 100, "MLT-TRP-20160630000001", "ESP-TRP-20160630000003", "FRA-TRP-2016122102030");
        assertChronologyOfFishingTrip("FRA-TRP-2016122102030", 100, "MLT-TRP-20160630000001", "ESP-TRP-20160630000003", "FRA-TRP-2016122102030");

        // #4: The way the number of previous trips (and next trips, if we where to receive any) is calculated is pretty unintuitive, as seen in the example below
        assertChronologyOfFishingTrip("FRA-TRP-2016122102030", 0);
        assertChronologyOfFishingTrip("FRA-TRP-2016122102030", 1);
        assertChronologyOfFishingTrip("FRA-TRP-2016122102030", 2, "ESP-TRP-20160630000003");
        assertChronologyOfFishingTrip("FRA-TRP-2016122102030", 3, "ESP-TRP-20160630000003", "FRA-TRP-2016122102030");
        assertChronologyOfFishingTrip("FRA-TRP-2016122102030", 4, "MLT-TRP-20160630000001", "ESP-TRP-20160630000003", "FRA-TRP-2016122102030");
    }

    private void assertChronologyOfFishingTrip(String tripId, int count, String ... expectedPreviousTripIds) throws JsonProcessingException {
        String responseAsString = getWebTarget()
                .path("trip")
                .path("cronology")// sic
                .path(tripId)
                .path(Integer.toString(count))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto<CronologyTripDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<CronologyTripDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        CronologyTripDTO cronologyTripDTO = responseDto.getData();
        assertEquals("FRA-TRP-2016122102030", cronologyTripDTO.getCurrentTrip());
        assertEquals(tripId, cronologyTripDTO.getSelectedTrip());

        assertNull(cronologyTripDTO.getNextTrips());

        if (expectedPreviousTripIds.length > 0) {
            assertEquals(expectedPreviousTripIds.length, cronologyTripDTO.getPreviousTrips().size());
            for (int i = 0; i < expectedPreviousTripIds.length; i++) {
                assertEquals(expectedPreviousTripIds[i], cronologyTripDTO.getPreviousTrips().get(i));
            }
        } else {
            assertNull(cronologyTripDTO.getPreviousTrips());
        }
    }

    @Test
    public void getChronologyOfFishingTrip_tripNotFound() throws JsonProcessingException {
        String responseAsString = getWebTarget()
                .path("trip")
                .path("cronology")// sic
                .path("NONEXISTANT-TRIP-ID")
                .path(Integer.toString(1000))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto<CronologyTripDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<CronologyTripDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        CronologyTripDTO cronologyTripDTO = responseDto.getData();
        assertNull(cronologyTripDTO.getCurrentTrip());
        assertEquals("NONEXISTANT-TRIP-ID", cronologyTripDTO.getSelectedTrip());

        assertNull(cronologyTripDTO.getPreviousTrips());
        assertNull(cronologyTripDTO.getNextTrips());
    }

    @Test
    public void getTripMapData() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("mapData")
                .path("UUR-XSM-45913768")
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

        assertEquals(12, featureCollection.size());

        ArrayList<CoordinateForTest> testCoordinates = Lists.newArrayList(
                new CoordinateForTest(2.772, 14.911),
                new CoordinateForTest(5.713, 61.891),
                new CoordinateForTest(7.955, 13.976),
                new CoordinateForTest(4.641, 61.891),
                new CoordinateForTest(9.938, 18.292),
                new CoordinateForTest(1.237, 61.891),
                new CoordinateForTest(2.656, 53.571),
                new CoordinateForTest(4.441, 53.571),
                new CoordinateForTest(7.266, 33.953),
                new CoordinateForTest(3.733, 96.513),
                new CoordinateForTest(2.569, 96.513),
                new CoordinateForTest(1.298, 56.758)
        );

        for (JsonNode jsonNode : featureCollection) {
            for (int i = 0; i < testCoordinates.size(); i++) {
                CoordinateForTest coordinateForTest = testCoordinates.get(i);
                if (coordinateForTest.aboutEqualTo(jsonNode)) {
                    testCoordinates.remove(i);
                    break;
                }
            }
        }

        // all expected test coordinates found
        assertTrue(testCoordinates.isEmpty());
    }

    @Test
    public void getTripMapData_tripNotFound() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("mapData")
                .path("YOU-GOT-TO-FIND-THE-TRIP-ID-WITHIN-YOU-MAN")
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

        assertEquals(0, featureCollection.size());
    }

    @Ignore("TODO fix for new test data")
    @Test
    public void getFishingTripCatchEvolution() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("catchevolution")
                .path("TODO")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto<CatchEvolutionDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<CatchEvolutionDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        CatchEvolutionDTO response = responseDto.getData();

        TripWidgetDto tripDetails = response.getTripDetails();
        assertNull(tripDetails.getVesselDetails());
        assertNull(tripDetails.getFlapDocuments());

        assertEquals(1, tripDetails.getTrips().size());
        TripOverviewDto trip = tripDetails.getTrips().get(0);
        // ...
    }

    private void assertCatchEvolutionProgressDto(List<CatchEvolutionProgressDTO> dtos, int i, String activityType, String reportType) {
        CatchEvolutionProgressDTO dto = dtos.get(i);
        CatchEvolutionProgressDTO previous = dtos.get(i - 1);

        Map<String, CatchSummaryListDTO> catchEvolution = dto.getCatchEvolution();
        assertEquals(2, catchEvolution.size());
        assertTrue(catchEvolution.get("cumulated").getTotal() >= previous.getCatchEvolution().get("cumulated").getTotal());

        assertEquals(2, catchEvolution.get("cumulated").getSpeciesList().size());

        assertEquals(activityType, dto.getActivityType());
        assertEquals(reportType, dto.getReportType());
        assertEquals(i + 1, dto.getOrderId());
    }

    @Ignore("TODO fix for new test data")
    @Test
    public void getFishingTripCatchEvolution_tripNotFound() throws JsonProcessingException {
        // Given

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("catchevolution")
                .path("TRIP-ID-DI-PIRT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get(String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto<CatchEvolutionDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<CatchEvolutionDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        CatchEvolutionDTO response = responseDto.getData();

        assertNull(response.getTripDetails());
    }

    private static class CoordinateForTest {
        public final double latitude;
        public final double longitude;

        public CoordinateForTest(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public boolean aboutEqualTo(JsonNode jsonNode) {
            JsonNode geometryNode = jsonNode.get("geometry");
            JsonNode coordinates = geometryNode.get("coordinates");
            ArrayNode coordsArray = (ArrayNode) coordinates.get(0);
            DoubleNode latNode = (DoubleNode) coordsArray.get(0);
            DoubleNode longNode = (DoubleNode) coordsArray.get(1);

            return  Math.abs(latNode.doubleValue() - latitude) <= 0.000001 &&
                    Math.abs(longNode.doubleValue() - longitude) <= 0.000001;
        }
    }
}
