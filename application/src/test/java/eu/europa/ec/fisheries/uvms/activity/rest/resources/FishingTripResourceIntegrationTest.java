package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.google.common.collect.Sets;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.AddressDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.ContactPartyDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchEvolutionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchSummaryListDTO;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
public class FishingTripResourceIntegrationTest extends BaseActivityArquillianTest {

    @Before
    public void setUp() throws NamingException, IOException, JAXBException, ServiceException, NotSupportedException, SystemException {
        super.setUp();
    }

    @Test
    public void getFishingTripSummary_noGeometry() throws IOException {
        // Given

        // When
        Response response = getWebTarget()
                .path("trip")
                .path("reports")
                .path("UUR-XSM-45913768")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null) // "null" means that we will not look up any geometry
                .header("roleName", "myRole")
                .get();

        // Then

        // I spent hours trying to get a properly typed ResponseDto<FishingTripSummaryViewDto> from the query, but couldn't get it to work.
        // So the workaround is to get the response entity as a String and just deserialize it here in the test...
        ResponseDto<FishingTripSummaryViewDTO> responseDto = response.readEntity(new GenericType<ResponseDto<FishingTripSummaryViewDTO>>() {});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingTripSummaryViewDTO data = responseDto.getData();
        assertEquals(2, data.getSummary().size());

        FishingActivityTypeDTO departure = data.getSummary().get("DEPARTURE");
        assertEquals(1364927847000L, departure.getDate().getTime());
        assertNull(departure.getLocations());

        FishingActivityTypeDTO arrival = data.getSummary().get("ARRIVAL");
        assertEquals(1365102447000L, arrival.getDate().getTime());
        assertNull(arrival.getLocations());

        List<ReportDTO> activityReports = data.getActivityReports();
        assertEquals(22, activityReports.size());

        assertReportDto(activityReports.get(0), 1364927280000L, "DECLARATION", false, "094946e6-544b-4e42-85e6-a199774a31c1", null, "DEPARTURE", 1364927847000L, "FIS", new DelimitedPeriodDTO(new Date(1364927847000L), new Date(1364927847000L), 0.0, "MIN"));

        for (int i = 1; i <= 16; i++) {
            assertIsGenericFishingOperation(activityReports.get(i));
        }

        assertReportDto(activityReports.get(17), 1365087630000L, "NOTIFICATION", false, "8b84d89d-57c5-456f-a96a-f60a035f8bf8", null, "ARRIVAL", 1365100647000L, "LAN", new DelimitedPeriodDTO(new Date(1365100647000L), new Date(1365100647000L), 0.0, "MIN"));
        assertReportDto(activityReports.get(18), 1365091060000L, "NOTIFICATION", true, "14bdf7bd-a5db-4aa5-b8b8-0cc5ed7d7ab2", "8b84d89d-57c5-456f-a96a-f60a035f8bf8", "ARRIVAL", 1365100647000L, "LAN", new DelimitedPeriodDTO(new Date(1365100647000L), new Date(1365100647000L), 0.0, "MIN"));
        assertReportDto(activityReports.get(19), 1365115480000L, "DECLARATION", false, "a84f1363-6a23-4a49-975a-e73c95837bcb", null, "ARRIVAL", 1365102447000L, null, new DelimitedPeriodDTO(new Date(1365102447000L), new Date(1365102447000L), 0.0, "MIN"));
        assertReportDto(activityReports.get(20), 1365115483000L, "DECLARATION", false, "8c90fa8b-9778-4b08-811b-050608589590", null, "LANDING", null, null, new DelimitedPeriodDTO(new Date(1365104247000L), new Date(1365104247000L), 0.0, "MIN"));
        assertReportDto(activityReports.get(21), 1365175252000L, "DECLARATION", true, "27d8c389-c792-4271-90d4-e7108d6f5f79", "8c90fa8b-9778-4b08-811b-050608589590", "LANDING", null, null, new DelimitedPeriodDTO(new Date(1365104247000L), new Date(1365104247000L), 0.0, "MIN"));
    }

    private void assertReportDto(ReportDTO dto, long acceptedDateTime, String type, boolean correction, String faUniqueReportId, String faReferenceId, String activityType, Long occurrence, String reason, DelimitedPeriodDTO delimitedPeriodDTO) {
        assertEquals(acceptedDateTime, dto.getFaReportAcceptedDateTime().getTime());
        assertEquals(type, dto.getFaReportDocumentType());
        assertNull(dto.getLocations());
        assertEquals(correction, dto.isCorrection());
        assertNull(dto.getUniqueFAReportId());
        assertNotEquals(0, dto.getFishingActivityId());
        assertNotEquals(0, dto.getFaReportID());
        assertEquals(0, dto.getCancelingReportID());
        assertEquals(0, dto.getDeletingReportID());
        assertEquals(faUniqueReportId, dto.getFaUniqueReportID());
        assertEquals(faUniqueReportId == null ? null : "UUID", dto.getFaUniqueReportSchemeID());
        assertEquals(faReferenceId, dto.getFaReferenceID());
        assertEquals(faReferenceId == null ? null : "UUID", dto.getFaReferenceSchemeID());
        assertEquals(activityType, dto.getActivityType());
        assertNull(dto.getGeometry());

        if (occurrence == null) {
            assertNull(dto.getOccurence());
        } else {
            assertEquals(occurrence.longValue(), dto.getOccurence().getTime());
        }
        assertEquals(reason, dto.getReason());
        assertEquals(correction ? "5" : "9", dto.getPurposeCode());
        assertNull(dto.getFluxCharacteristics());

        if (delimitedPeriodDTO == null) {
            assertTrue(dto.getDelimitedPeriod() == null || dto.getDelimitedPeriod().isEmpty());
        } else {
            assertEquals(1, dto.getDelimitedPeriod().size());
            assertEquals(delimitedPeriodDTO.getStartDate(), dto.getDelimitedPeriod().get(0).getStartDate());
            assertEquals(delimitedPeriodDTO.getEndDate(), dto.getDelimitedPeriod().get(0).getEndDate());
            assertEquals(delimitedPeriodDTO.getDuration(), dto.getDelimitedPeriod().get(0).getDuration());
            assertEquals(delimitedPeriodDTO.getUnitCode(), dto.getDelimitedPeriod().get(0).getUnitCode());
        }
    }

    private void assertIsGenericFishingOperation(ReportDTO dto) {
        assertNotNull(dto.getFaReportAcceptedDateTime());
        assertEquals("DECLARATION", dto.getFaReportDocumentType());
        assertNull(dto.getLocations());
        assertNull(dto.getUniqueFAReportId());
        assertNotEquals(0, dto.getFishingActivityId());
        assertNotEquals(0, dto.getFaReportID());
        assertEquals(0, dto.getCancelingReportID());
        assertEquals(0, dto.getDeletingReportID());
        assertNotNull(dto.getFaUniqueReportID());
        assertEquals("UUID", dto.getFaUniqueReportSchemeID());
        if (dto.isCorrection()) {
            assertNotNull(dto.getFaReferenceID());
            assertEquals("UUID", dto.getFaReferenceSchemeID());
        } else {
            assertNull(dto.getFaReferenceID());
            assertNull(dto.getFaReferenceSchemeID());
        }
        assertEquals("FISHING_OPERATION", dto.getActivityType());
        assertNull(dto.getGeometry());
        assertNotNull(dto.getOccurence());
        assertNull(dto.getReason());
        assertEquals(dto.isCorrection() ? "5" : "9", dto.getPurposeCode());
        assertEquals(1, dto.getFishingGears().size());
        assertNull(dto.getFluxCharacteristics());
        assertEquals(1, dto.getDelimitedPeriod().size());
    }

    @Test
    public void getFishingTripSummary_tripNotFound() throws IOException {
        // Given

        // When
        Response response = getWebTarget()
                .path("trip")
                .path("reports")
                .path("THIS-HERE-AINT-NO-TRIP")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null) // "null" means that we will not look up any geometry
                .header("roleName", "myRole")
                .get();

        // Then
        ResponseDto<FishingTripSummaryViewDTO> responseDto = response.readEntity(new GenericType<ResponseDto<FishingTripSummaryViewDTO>>() {});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingTripSummaryViewDTO data = responseDto.getData();
        assertNull(data.getSummary());
        assertNull(data.getActivityReports());
    }

    @Test
    public void getVesselDetails() throws IOException {
        // Given

        // When
        Response response = getWebTarget()
                .path("trip")
                .path("vessel")
                .path("details")
                .path("XQF-NYK-8726D815443D8")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get();

        // Then
        ResponseDto<VesselDetailsDTO> responseDto = response.readEntity(new GenericType<ResponseDto<VesselDetailsDTO>>() {});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        VesselDetailsDTO vesselDetails = responseDto.getData();
        assertEquals("WVO", vesselDetails.getCountry());

        Set<ContactPartyDetailsDTO> contactPartyDetailsDTOSet = vesselDetails.getContactPartyDetailsDTOSet();
        assertEquals(1, contactPartyDetailsDTOSet.size());

        ContactPartyDetailsDTO contactPartyDetailsDto = contactPartyDetailsDTOSet.iterator().next();
        assertEquals("MASTER", contactPartyDetailsDto.getRole());
        assertEquals("jbnhciaw", contactPartyDetailsDto.getContactPerson().getGivenName());
        assertEquals("otcebzfk", contactPartyDetailsDto.getContactPerson().getFamilyName());

        assertEquals(1, contactPartyDetailsDto.getStructuredAddresses().size());
        AddressDetailsDTO address = contactPartyDetailsDto.getStructuredAddresses().get(0);
        assertEquals("epoquqlk", address.getCityName());
        assertEquals("WVO", address.getCountryIDValue());
        assertEquals("61444", address.getPostalAreaValue());
        assertEquals("yenoyter", address.getPlotId());
        assertEquals("yenoyter", address.getStreetName());

        Set<AssetIdentifierDto> vesselIdentifiers = vesselDetails.getVesselIdentifiers();
        assertEquals(3, vesselIdentifiers.size());
        assertContainsAssetIdentifier(vesselIdentifiers, new AssetIdentifierDto(VesselIdentifierSchemeIdEnum.CFR, "YWA454122867"));
        assertContainsAssetIdentifier(vesselIdentifiers, new AssetIdentifierDto(VesselIdentifierSchemeIdEnum.IRCS, "RIWZ"));
        assertContainsAssetIdentifier(vesselIdentifiers, new AssetIdentifierDto(VesselIdentifierSchemeIdEnum.EXT_MARK, "UCJ-3694-Q"));
    }

    private void assertContainsAssetIdentifier(Collection<AssetIdentifierDto> collectionToTest, AssetIdentifierDto dtoToLookFor) {
        boolean foundOne = false;
        for (AssetIdentifierDto assetIdentifierDto : collectionToTest) {
            if (
                Objects.equals(assetIdentifierDto.getIdentifierSchemeId(), dtoToLookFor.getIdentifierSchemeId()) &&
                Objects.equals(assetIdentifierDto.getFaIdentifierId(), dtoToLookFor.getFaIdentifierId())
            ) {
                if (foundOne) {
                    fail("Found more than one matching identifier DTO in vessel identifiers");
                }
                foundOne = true;
            }
        }
        assertTrue(foundOne);
    }

    @Test
    public void getVesselDetails_tripNotFound() throws IOException {
        // Given

        // When
        Response response = getWebTarget()
                .path("trip")
                .path("vessel")
                .path("details")
                .path("YOU-WILL-NEVER-SEE-A-TRIP-ID-SUCH-AS-THIS")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get();

        // Then
        ResponseDto<VesselDetailsDTO> responseDto = response.readEntity(new GenericType<ResponseDto<VesselDetailsDTO>>() {});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());
        assertNull(responseDto.getData());
    }

    @Test
    public void getFishingTripMessageCounter() throws IOException {
        // Given

        // When
        Response response = getWebTarget()
                .path("trip")
                .path("messages")
                .path("ICV-MOM-83R964412B3")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get();

        // Then
        ResponseDto<MessageCountDTO> responseDto = response.readEntity(new GenericType<ResponseDto<MessageCountDTO>>() {});

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
    public void getFishingTripMessageCounter_tripNotFound() throws IOException {
        // Given

        // When
        Response response = getWebTarget()
                .path("trip")
                .path("messages")
                .path("BLARRRRGHH")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get();

        // Then
        ResponseDto<MessageCountDTO> responseDto = response.readEntity(new GenericType<ResponseDto<MessageCountDTO>>() {});

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
    public void getFishingTripCatchReports() throws IOException {
        // Given

        // When
        Response response = getWebTarget()
                .path("trip")
                .path("catches")
                .path("ICV-MOM-83R964412B3")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get();

        // Then
        ResponseDto<Map<String, CatchSummaryListDTO>> responseDto = response.readEntity(new GenericType<ResponseDto<Map<String, CatchSummaryListDTO>>>() {});

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
    public void getFishingTripCatchReports_tripNotFound() throws IOException {
        // Given

        // When
        Response response = getWebTarget()
                .path("trip")
                .path("catches")
                .path("WHAT-ME-PROVIDE-A-REAL-TRIP-ID")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get();

        // Then
        ResponseDto<Map<String, CatchSummaryListDTO>> responseDto = response.readEntity(new GenericType<ResponseDto<Map<String, CatchSummaryListDTO>>>() {});

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

//    @Test
//    public void getTripMapData() throws IOException {
//        // Given
//
//        // When
//        Response response = getWebTarget()
//                .path("trip")
//                .path("mapData")
//                .path("UUR-XSM-45913768")
//                .request(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, authToken)
//                .get();
//
//        // Then
//        ObjectMapper objectMapper = new ObjectMapper();
//        ResponseDto<ObjectNode> responseDto =
//                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<ObjectNode>>(){});
//
//        assertEquals(200, responseDto.getCode());
//        assertNull(responseDto.getMsg());
//
//        ObjectNode response = responseDto.getData();
//
//        ArrayNode featureCollection = (ArrayNode) response.get("features");
//
//        assertEquals(12, featureCollection.size());
//
//        ArrayList<CoordinateForTest> testCoordinates = Lists.newArrayList(
//                new CoordinateForTest(2.772, 14.911),
//                new CoordinateForTest(5.713, 61.891),
//                new CoordinateForTest(7.955, 13.976),
//                new CoordinateForTest(4.641, 61.891),
//                new CoordinateForTest(9.938, 18.292),
//                new CoordinateForTest(1.237, 61.891),
//                new CoordinateForTest(2.656, 53.571),
//                new CoordinateForTest(4.441, 53.571),
//                new CoordinateForTest(7.266, 33.953),
//                new CoordinateForTest(3.733, 96.513),
//                new CoordinateForTest(2.569, 96.513),
//                new CoordinateForTest(1.298, 56.758)
//        );
//
//        for (JsonNode jsonNode : featureCollection) {
//            for (int i = 0; i < testCoordinates.size(); i++) {
//                CoordinateForTest coordinateForTest = testCoordinates.get(i);
//                if (coordinateForTest.aboutEqualTo(jsonNode)) {
//                    testCoordinates.remove(i);
//                    break;
//                }
//            }
//        }
//
//        // all expected test coordinates found
//        assertTrue(testCoordinates.isEmpty());
//    }

//    @Test
//    public void getTripMapData_tripNotFound() throws IOException {
//        // Given
//
//        // When
//        String responseAsString = getWebTarget()
//                .path("trip")
//                .path("mapData")
//                .path("YOU-GOT-TO-FIND-THE-TRIP-ID-WITHIN-YOU-MAN")
//                .request(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, authToken)
//                .get(String.class);
//
//        // Then
//        ObjectMapper objectMapper = new ObjectMapper();
//        ResponseDto<ObjectNode> responseDto =
//                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<ObjectNode>>(){});
//
//        assertEquals(200, responseDto.getCode());
//        assertNull(responseDto.getMsg());
//
//        ObjectNode response = responseDto.getData();
//
//        ArrayNode featureCollection = (ArrayNode) response.get("features");
//
//        assertEquals(0, featureCollection.size());
//    }

    @Test
    public void getFishingTripCatchEvolution() throws IOException {
        // Given

        // When
        Response response = getWebTarget()
                .path("trip")
                .path("catchevolution")
                .path("UUR-XSM-45913768")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get();

        // Then
        ResponseDto<CatchEvolutionDTO> responseDto = response.readEntity(new GenericType<ResponseDto<CatchEvolutionDTO>>() {});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        CatchEvolutionDTO catchEvolutionDTO = responseDto.getData();

        TripWidgetDto tripDetails = catchEvolutionDTO.getTripDetails();

        assertNull(tripDetails.getFlapDocuments());

        VesselDetailsDTO vesselDetails = tripDetails.getVesselDetails();
        assertEquals("CATCHING_VESSEL", vesselDetails.getRoleCode());
        assertEquals("UVH", vesselDetails.getCountry());
        assertEquals(1, vesselDetails.getContactPartyDetailsDTOSet().size()); // let's not assert any deeper into this rabbit hole in this test
        assertEquals(1, vesselDetails.getVesselIdentifiers().size());
        AssetIdentifierDto vesselIdentifier = vesselDetails.getVesselIdentifiers().iterator().next();
        assertEquals(VesselIdentifierSchemeIdEnum.CFR, vesselIdentifier.getIdentifierSchemeId());
        assertEquals("PSD472287491", vesselIdentifier.getFaIdentifierId());

        assertEquals(1, tripDetails.getTrips().size());
        TripOverviewDto trip = tripDetails.getTrips().get(0);
        assertEquals(1, trip.getTripId().size());
        assertEquals("UUR-XSM-45913768", trip.getTripId().get(0).getId());
        assertEquals("EU_TRIP_ID", trip.getTripId().get(0).getSchemeId());
        assertNull(trip.getTypeCode());
        assertEquals(1364927847000L, trip.getDepartureTime().getTime());
        assertEquals(1365102447000L, trip.getArrivalTime().getTime());
        assertNull(trip.getLandingTime());

        List<CatchEvolutionProgressDTO> catchEvolutionProgress = catchEvolutionDTO.getCatchEvolutionProgress();
        assertEquals(21, catchEvolutionProgress.size());

        // check first "evolution"
        assertEquals("DEPARTURE", catchEvolutionProgress.get(0).getActivityType());
        assertEquals("DECLARATION", catchEvolutionProgress.get(0).getReportType());

        // check rest of trip
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 1, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 2, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 3, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 4, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 5, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 6, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 7, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 8, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 9, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 10, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 11, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 12, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 13, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 14, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 15, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 16, "FISHING_OPERATION", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 17, "ARRIVAL", "NOTIFICATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 18, "ARRIVAL", "NOTIFICATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 19, "LANDING", "DECLARATION");
        assertCatchEvolutionProgressDto(catchEvolutionProgress, 20, "LANDING", "DECLARATION");

        // assert final catches
        CatchEvolutionProgressDTO lastCatchDto = catchEvolutionProgress.get(20);
        CatchSummaryListDTO lastCumulated = lastCatchDto.getCatchEvolution().get("cumulated");
        CatchSummaryListDTO lastOnboard = lastCatchDto.getCatchEvolution().get("onboard");
        assertEquals(8197.3, lastCumulated.getTotal(), 0.1d);
        assertEquals(10197.3, lastOnboard.getTotal(), 0.1d);
        assertLastCatchSpecies(lastCumulated.getSpeciesList());
        assertLastCatchSpecies(lastOnboard.getSpeciesList());
    }

    private void assertCatchEvolutionProgressDto(List<CatchEvolutionProgressDTO> dtos, int i, String activityType, String reportType) {
        CatchEvolutionProgressDTO dto = dtos.get(i);
        CatchEvolutionProgressDTO previous = dtos.get(i - 1);

        Map<String, CatchSummaryListDTO> thisCatchEvolution = dto.getCatchEvolution();
        Map<String, CatchSummaryListDTO> previousCatchEvolution = previous.getCatchEvolution();

        assertEquals(2, thisCatchEvolution.size());

        CatchSummaryListDTO thisCumulated = thisCatchEvolution.get("cumulated");
        CatchSummaryListDTO previousCumulated = previousCatchEvolution.get("cumulated");

        // the amount of catch and the number of species keep increasing
        assertTrue((int)Math.round(thisCumulated.getTotal()) >= (int)Math.round(previousCumulated.getTotal())); // round to avoid very minor differences
        assertTrue(thisCumulated.getSpeciesList().size() >= previousCumulated.getSpeciesList().size());

        assertEquals(activityType, dto.getActivityType());
        assertEquals(reportType, dto.getReportType());
        assertEquals(i + 1, dto.getOrderId());
    }

    private void assertLastCatchSpecies(List<SpeciesQuantityDTO> actualSpeciesList) {
        Set<String> actualSpeciesSet = actualSpeciesList.stream().map(s -> s.getSpeciesCode()).collect(Collectors.toSet());
        HashSet<String> expectedSpeciesSet = Sets.newHashSet("WIT", "HKE", "HAD", "POL", "USK", "POK", "MON", "LIN", "TUR", "HAL", "LEM", "CAT", "WHG", "COD", "SQZ");

        assertTrue(actualSpeciesSet.containsAll(expectedSpeciesSet));
    }

    @Test
    public void getFishingTripCatchEvolution_tripNotFound() throws IOException {
        // Given

        // When
        Response response = getWebTarget()
                .path("trip")
                .path("catchevolution")
                .path("TRIP-ID-DI-PIRT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .get();

        // Then
        ResponseDto<CatchEvolutionDTO> responseDto = response.readEntity(new GenericType<ResponseDto<CatchEvolutionDTO>>() {});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        CatchEvolutionDTO catchEvolutionDTO = responseDto.getData();

        assertNull(catchEvolutionDTO.getTripDetails());
    }
//
//    private static class CoordinateForTest {
//        public final double latitude;
//        public final double longitude;
//
//        public CoordinateForTest(double latitude, double longitude) {
//            this.latitude = latitude;
//            this.longitude = longitude;
//        }
//
//        public boolean aboutEqualTo(JsonNode jsonNode) {
//            JsonNode geometryNode = jsonNode.get("geometry");
//            JsonNode coordinates = geometryNode.get("coordinates");
//            ArrayNode coordsArray = (ArrayNode) coordinates.get(0);
//            DoubleNode latNode = (DoubleNode) coordsArray.get(0);
//            DoubleNode longNode = (DoubleNode) coordsArray.get(1);
//
//            return  Math.abs(latNode.doubleValue() - latitude) <= 0.000001 &&
//                    Math.abs(longNode.doubleValue() - longitude) <= 0.000001;
//        }
//    }
}
