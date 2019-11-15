package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.ContactPartyDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityHistoryDtoElement;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityViewDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.RelatedReportDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripOverviewDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripWidgetDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class FishingActivityViewsResourceTest extends BaseActivityArquillianTest {

    private String authToken;

    @Before
    public void setUp() throws NamingException {
        InitialContext ctx = new InitialContext();
        ctx.rebind("java:global/mdr_endpoint", "http://localhost:8080/activity-rest-test");

        authToken = getToken();
    }

    @Test
    public void getActivityArrivalView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("arrival")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null)
                .header("roleName", "myRole")
                .post(Entity.json(activityViewDto), String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(ActivityDetailsDto.class, ActivityDetailsDtoMixin.class);
        objectMapper.addMixIn(FluxLocationDto.class, FluxLocationDtoMixin.class);

        ResponseDto<FishingActivityViewDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FishingActivityViewDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingActivityViewDTO fishingActivityViewDTO = responseDto.getData();

        ActivityDetailsDto activityDetails = fishingActivityViewDTO.getActivityDetails();
        assertEquals("AREA_ENTRY", activityDetails.getType());
        assertEquals(Long.valueOf(1), activityDetails.getId());
        assertEquals("FSH", activityDetails.getReason());
        assertEquals(1483931160000l, activityDetails.getArrivalTime().getTime());
        assertTrue(activityDetails.getFlapDocuments().isEmpty());
        assertNull(activityDetails.getFluxCharacteristics());
        assertNull(activityDetails.getOccurrence());
        assertNull(activityDetails.getIntendedLandingTime());
        assertNull(activityDetails.getEstimatedArrivalTime());
        assertNull(activityDetails.getLandingTime());
        assertNull(activityDetails.getFisheryType());
        assertNull(activityDetails.getSpeciesTarget());
        assertNull(activityDetails.getVesselActivity());
        assertNull(activityDetails.getNrOfOperation());
        assertNull(activityDetails.getIdentifiers());
        assertNull(activityDetails.getFishingTime());
        assertNull(activityDetails.getTranshipmentTime());

        ReportDocumentDto reportDetails = fishingActivityViewDTO.getReportDetails();
        assertEquals("NOTIFICATION", reportDetails.getType());
        assertEquals("2017-01-09T03:06:00", reportDetails.getCreationDate());
        assertEquals("9", reportDetails.getPurposeCode());
        assertEquals("MLT", reportDetails.getOwner());
        assertEquals("20D3FFFE-0D3D-40ED-A49E-DF532ACD4EFC", reportDetails.getId());
        assertEquals("2017-01-09T03:06:00", reportDetails.getAcceptedDate());
        assertEquals("1", reportDetails.getFmcMark());
        List<RelatedReportDto> relatedReports = reportDetails.getRelatedReports();
        assertEquals(3, relatedReports.size());
        assertEquals("SYC20160630000002", relatedReports.get(0).getId());
        assertEquals("EU-ERS3.1_ON-QUE", relatedReports.get(0).getSchemeId());
        assertEquals("MLT20160630000001", relatedReports.get(1).getId());
        assertEquals("EU-ERS3.1_ON", relatedReports.get(1).getSchemeId());
        assertEquals("ESP20160630000003", relatedReports.get(2).getId());
        assertEquals("EU-ERS3.1_RN", relatedReports.get(2).getSchemeId());
        assertNull(reportDetails.getRefId());

        TripWidgetDto tripDetails = fishingActivityViewDTO.getTripDetails();
        assertNull(tripDetails.getFlapDocuments());

        VesselDetailsDTO vesselDetails = tripDetails.getVesselDetails();
        assertEquals("MADONNA DI POMPEI", vesselDetails.getName());
        assertEquals("MLT", vesselDetails.getCountry());
        assertEquals(1, vesselDetails.getContactPartyDetailsDTOSet().size());
        ContactPartyDetailsDTO contactPartyDetailsDto = vesselDetails.getContactPartyDetailsDTOSet().iterator().next();
        assertEquals("MASTER", contactPartyDetailsDto.getRole());
        assertEquals("Miguel Nunes", contactPartyDetailsDto.getContactPerson().getAlias());
        assertEquals("MARSAXLOKK (KAVALLERIZZA)", contactPartyDetailsDto.getStructuredAddresses().get(0).getStreetName());

        assertEquals(1, vesselDetails.getVesselIdentifiers().size());
        AssetIdentifierDto assetIdentifierDto = vesselDetails.getVesselIdentifiers().iterator().next();
        assertEquals(VesselIdentifierSchemeIdEnum.CFR, assetIdentifierDto.getIdentifierSchemeId());
        assertEquals("SWE000004540", assetIdentifierDto.getFaIdentifierId());
        assertNull(assetIdentifierDto.isFromAssets());
        assertNull(assetIdentifierDto.getFaIdentifierSchemeId());

        assertEquals(1, tripDetails.getTrips().size());
        TripOverviewDto tripOverviewDto = tripDetails.getTrips().get(0);
        assertEquals(1, tripOverviewDto.getTripId().size());
        assertEquals("ESP-TRP-20160630000003", tripOverviewDto.getTripId().get(0).getId());
        assertEquals("EU_TRIP_ID", tripOverviewDto.getTripId().get(0).getSchemeId());
        assertEquals(1483966440000l, tripOverviewDto.getDepartureTime().getTime());
        assertNull(tripOverviewDto.getTypeCode());
        assertNull(tripOverviewDto.getArrivalTime());
        assertNull(tripOverviewDto.getLandingTime());

        assertEquals(1, fishingActivityViewDTO.getHistory().size());
        ActivityHistoryDtoElement activityHistoryDtoElement = fishingActivityViewDTO.getHistory().get(0);
        assertEquals(Integer.valueOf(1), activityHistoryDtoElement.getFaReportId());
        assertEquals(Integer.valueOf(9), activityHistoryDtoElement.getPurposeCode());
        assertEquals(1483931160000l, activityHistoryDtoElement.getAcceptanceDate().getTime());
        assertEquals(1, activityHistoryDtoElement.getFishingActivityIds().size());
        assertEquals(Integer.valueOf(1), activityHistoryDtoElement.getFishingActivityIds().get(0));

        assertEquals(3, fishingActivityViewDTO.getLocations().size());
        assertTrue(fishingActivityViewDTO.getGears().isEmpty());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getCatches());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertNull(fishingActivityViewDTO.getAreas());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    private abstract class ActivityDetailsDtoMixin {
        @JsonIgnore
        public abstract Map<String, Set<Object>> getCharacteristics();

        @JsonIgnore
        public abstract String getWkt();
    }

    private abstract class FluxLocationDtoMixin {
        @JsonIgnore
        public abstract Map<String, String> getIdentifier();
    }
}
