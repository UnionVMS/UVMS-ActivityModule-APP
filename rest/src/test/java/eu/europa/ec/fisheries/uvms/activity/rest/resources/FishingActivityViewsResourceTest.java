package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.facatch.FaCatchGroupDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.AddressDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.ContactPartyDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.ContactPersonDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityHistoryDtoElement;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityViewDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.AreaDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.RelatedReportDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripIdDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripOverviewDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.TripWidgetDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.parent.FishingActivityViewDTO;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Ignore("Fix to work with new test data")
@RunWith(Arquillian.class)
@Ignore("TODO: robin test fix")
public class FishingActivityViewsResourceTest extends BaseActivityArquillianTest {

    private ActivityDetailsDto expectedActivityDetailsDto;
    private ReportDocumentDto expectedReportDocumentDto;
    private TripWidgetDto expectedTripWidgetDto;
    private ActivityHistoryDtoElement expectedActivityHistoryDto;
    private ContactPartyDetailsDTO expectedContactPartyDetailsDto;

    @Before
    public void setUp() throws NamingException, IOException, JAXBException, ServiceException, NotSupportedException, SystemException {
        super.setUp();

        expectedActivityDetailsDto = new ActivityDetailsDto();
        expectedActivityDetailsDto.setType("AREA_ENTRY");
        expectedActivityDetailsDto.setId(1L);
        expectedActivityDetailsDto.setReason("FSH");
        expectedActivityDetailsDto.setArrivalTime(new Date(1483931160000L));
        expectedActivityDetailsDto.setFlapDocuments(new HashSet<>());

        expectedReportDocumentDto = new ReportDocumentDto();
        expectedReportDocumentDto.setType("NOTIFICATION");
        expectedReportDocumentDto.setCreationDate("2017-01-09T03:06:00");
        expectedReportDocumentDto.setPurposeCode("9");
        expectedReportDocumentDto.setOwner("MLT");
        expectedReportDocumentDto.setId("20D3FFFE-0D3D-40ED-A49E-DF532ACD4EFC");
        expectedReportDocumentDto.setAcceptedDate("2017-01-09T03:06:00");
        expectedReportDocumentDto.setFmcMark("1");
        List<RelatedReportDto> expectedRelatedReports = new ArrayList<>();
        RelatedReportDto expectedRelatedReport1 = new RelatedReportDto();
        expectedRelatedReport1.setId("SYC20160630000002");
        expectedRelatedReport1.setSchemeId("EU-ERS3.1_ON-QUE");
        expectedRelatedReports.add(expectedRelatedReport1);
        RelatedReportDto expectedRelatedReport2 = new RelatedReportDto();
        expectedRelatedReport2.setId("MLT20160630000001");
        expectedRelatedReport2.setSchemeId("EU-ERS3.1_ON");
        expectedRelatedReports.add(expectedRelatedReport2);
        RelatedReportDto expectedRelatedReport3 = new RelatedReportDto();
        expectedRelatedReport3.setId("ESP20160630000003");
        expectedRelatedReport3.setSchemeId("EU-ERS3.1_RN");
        expectedRelatedReports.add(expectedRelatedReport3);
        expectedReportDocumentDto.setRelatedReports(expectedRelatedReports);

        expectedTripWidgetDto = new TripWidgetDto();

        VesselDetailsDTO expectedVesselDetailsDto = new VesselDetailsDTO();
        expectedVesselDetailsDto.setName("Test Ship 001");
        expectedVesselDetailsDto.setCountry("MLT");
        expectedContactPartyDetailsDto = new ContactPartyDetailsDTO();
        expectedContactPartyDetailsDto.setRole("MASTER");
        expectedContactPartyDetailsDto.setContactPerson(new ContactPersonDetailsDTO(null, null, null, null, null, null, null, "Contact Person 001 Alias"));
        expectedContactPartyDetailsDto.setStructuredAddresses(
                Lists.newArrayList(
                        new AddressDetailsDTO(null, null, null, null, null, null, null, null, null, null, null, null, "Contact Person 001 Street Name")));
        expectedVesselDetailsDto.setContactPartyDetailsDTOSet(Sets.newHashSet(expectedContactPartyDetailsDto));
        AssetIdentifierDto expectedAssetIdentifierDto = new AssetIdentifierDto(VesselIdentifierSchemeIdEnum.CFR, "SWEFAKE00001");
        expectedVesselDetailsDto.setVesselIdentifiers(Sets.newHashSet(expectedAssetIdentifierDto));
        expectedVesselDetailsDto.setFlapDocuments(new HashSet<>());
        expectedTripWidgetDto.setVesselDetails(expectedVesselDetailsDto);

        TripOverviewDto expectedTripOverviewDto = new TripOverviewDto();
        TripIdDto expectedTripIdDto = new TripIdDto();
        expectedTripIdDto.setId("ESP-TRP-20160630000003");
        expectedTripIdDto.setSchemeId("EU_TRIP_ID");
        expectedTripOverviewDto.setTripId(Lists.newArrayList(expectedTripIdDto));
        expectedTripOverviewDto.setDepartureTime(new Date(1483966440000L));
        expectedTripWidgetDto.setTrips(Lists.newArrayList(expectedTripOverviewDto));

        expectedActivityHistoryDto = new ActivityHistoryDtoElement(1, new Date(1483931160000L), 9, Lists.newArrayList(1));
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

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertNull(fishingActivityViewDTO.getCatches());

        assertNull(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());

        assertTrue(fishingActivityViewDTO.getGears().isEmpty());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    @Test
    public void getActivityLandingView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        expectedActivityDetailsDto.setType("AREA_ENTRY");
        expectedActivityDetailsDto.setReason(null);
        expectedActivityDetailsDto.setArrivalTime(null);
        expectedActivityDetailsDto.setOccurrence(new Date(1483931160000L));
        expectedActivityDetailsDto.setLandingTime(new DelimitedPeriodDTO());

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("landing")
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

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertCatches(fishingActivityViewDTO.getCatches());

        assertNull(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());
        assertNull(fishingActivityViewDTO.getGears());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    @Test
    public void getDiscardAtSeaView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        expectedActivityDetailsDto.setReason(null);
        expectedActivityDetailsDto.setArrivalTime(null);
        expectedActivityDetailsDto.setOccurrence(new Date(1483931160000L));

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("discard")
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

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertCatches(fishingActivityViewDTO.getCatches());

        assertNull(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());
        assertNull(fishingActivityViewDTO.getGears());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    @Test
    public void getActivityDepartureView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        expectedActivityDetailsDto.setArrivalTime(null);
        expectedActivityDetailsDto.setOccurrence(new Date(1483931160000L));

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("departure")
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

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertCatches(fishingActivityViewDTO.getCatches());

        assertNull(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());
        assertTrue(fishingActivityViewDTO.getGears().isEmpty());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    @Test
    public void getActivityNotificationOfArrivalView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        expectedActivityDetailsDto.setArrivalTime(null);
        expectedActivityDetailsDto.setOccurrence(new Date(1483931160000L));

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("notification")
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

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertCatches(fishingActivityViewDTO.getCatches());

        assertNull(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());

        assertNull(fishingActivityViewDTO.getGears());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    @Test
    public void getActivityAreaEntryView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        expectedActivityDetailsDto.setArrivalTime(null);
        expectedActivityDetailsDto.setOccurrence(new Date(1483931160000L));

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("areaEntry")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null)
                .header("roleName", "myRole")
                .post(Entity.json(activityViewDto), String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(ActivityDetailsDto.class, ActivityDetailsDtoMixin.class);
        objectMapper.addMixIn(FluxLocationDto.class, FluxLocationDtoMixin.class);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ResponseDto<FishingActivityViewDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FishingActivityViewDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingActivityViewDTO fishingActivityViewDTO = responseDto.getData();

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertCatches(fishingActivityViewDTO.getCatches());

        assertAreas(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());

        assertNull(fishingActivityViewDTO.getGears());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    @Test
    public void getActivityAreaExitView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        expectedActivityDetailsDto.setReason(null);
        expectedActivityDetailsDto.setArrivalTime(null);
        expectedActivityDetailsDto.setOccurrence(new Date(1483931160000L));

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("areaExit")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null)
                .header("roleName", "myRole")
                .post(Entity.json(activityViewDto), String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(ActivityDetailsDto.class, ActivityDetailsDtoMixin.class);
        objectMapper.addMixIn(FluxLocationDto.class, FluxLocationDtoMixin.class);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ResponseDto<FishingActivityViewDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FishingActivityViewDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingActivityViewDTO fishingActivityViewDTO = responseDto.getData();

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertCatches(fishingActivityViewDTO.getCatches());

        assertAreas(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());

        assertNull(fishingActivityViewDTO.getGears());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
   }

    @Test
    public void getGearShotAndRetrievalView_activityIdAndHistory() throws JsonProcessingException {
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
                .path("gearshot")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null)
                .header("roleName", "myRole")
                .post(Entity.json(activityViewDto), String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(ActivityDetailsDto.class, ActivityDetailsDtoMixin.class);
        objectMapper.addMixIn(FluxLocationDto.class, FluxLocationDtoMixin.class);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ResponseDto<FishingActivityViewDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FishingActivityViewDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingActivityViewDTO fishingActivityViewDTO = responseDto.getData();

        assertNull(fishingActivityViewDTO.getActivityDetails());

        assertNull(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertNull(fishingActivityViewDTO.getCatches());

        assertNull(fishingActivityViewDTO.getAreas());

        assertNull(fishingActivityViewDTO.getLocations());

        assertNull(fishingActivityViewDTO.getGears());
        assertNull(fishingActivityViewDTO.getProcessingProducts());
        assertTrue(fishingActivityViewDTO.getGearShotRetrievalList().isEmpty());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());
    }

    @Test
    public void getTranshipmentView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        expectedActivityDetailsDto.setReason(null);
        expectedActivityDetailsDto.setArrivalTime(null);
        expectedActivityDetailsDto.setIdentifiers(new HashSet<>());

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("transhipment")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null)
                .header("roleName", "myRole")
                .post(Entity.json(activityViewDto), String.class);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(ActivityDetailsDto.class, ActivityDetailsDtoMixin.class);
        objectMapper.addMixIn(FluxLocationDto.class, FluxLocationDtoMixin.class);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ResponseDto<FishingActivityViewDTO> responseDto =
                objectMapper.readValue(responseAsString, new TypeReference<ResponseDto<FishingActivityViewDTO>>(){});

        assertEquals(200, responseDto.getCode());
        assertNull(responseDto.getMsg());

        FishingActivityViewDTO fishingActivityViewDTO = responseDto.getData();

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertCatches(fishingActivityViewDTO.getCatches());

        assertNull(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());

        assertNull(fishingActivityViewDTO.getGears());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    @Test
    public void getFishingOperationView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        expectedActivityDetailsDto.setReason(null);
        expectedActivityDetailsDto.setArrivalTime(null);
        expectedActivityDetailsDto.setOccurrence(new Date(1483931160000L));
        expectedActivityDetailsDto.setIdentifiers(new HashSet<>());
        expectedActivityDetailsDto.setFishingTime(new DelimitedPeriodDTO());

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("fishingoperation")
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

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertCatches(fishingActivityViewDTO.getCatches());

        assertNull(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());

        assertTrue(fishingActivityViewDTO.getGears().isEmpty());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertTrue(fishingActivityViewDTO.getGearShotRetrievalList().isEmpty());
        assertNull(fishingActivityViewDTO.getVesselDetails());
        assertTrue(fishingActivityViewDTO.getGearProblems().isEmpty());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    @Test
    public void getRelocationView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        expectedActivityDetailsDto.setReason(null);
        expectedActivityDetailsDto.setArrivalTime(null);
        expectedActivityDetailsDto.setOccurrence(new Date(1483931160000L));
        expectedActivityDetailsDto.setIdentifiers(new HashSet<>());

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("relocation")
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

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertCatches(fishingActivityViewDTO.getCatches());

        assertNull(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());

        assertNull(fishingActivityViewDTO.getGears());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertTrue(fishingActivityViewDTO.getVesselDetails().isEmpty());
        assertNull(fishingActivityViewDTO.getGearProblems());
        assertNull(fishingActivityViewDTO.getRelocations());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    @Test
    public void getJointFishingOperationView_activityIdAndHistory() throws JsonProcessingException {
        // Given
        ActivityViewDto activityViewDto = new ActivityViewDto();
        activityViewDto.setActivityId(1);
        activityViewDto.setTripId(null);
        activityViewDto.setReportId(null);
        activityViewDto.setWithHistory(true);

        expectedActivityDetailsDto.setReason(null);
        expectedActivityDetailsDto.setArrivalTime(null);
        expectedActivityDetailsDto.setOccurrence(new Date(1483931160000L));
        expectedActivityDetailsDto.setIdentifiers(new HashSet<>());

        // When
        String responseAsString = getWebTarget()
                .path("fa")
                .path("views")
                .path("jointfishingoperation")
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

        assertActivityDetailsDto(fishingActivityViewDTO.getActivityDetails());

        assertReportDocumentDto(fishingActivityViewDTO.getReportDetails());

        assertTripWidgetDto((fishingActivityViewDTO.getTripDetails()));

        assertActivityHistory(fishingActivityViewDTO.getHistory());

        assertCatches(fishingActivityViewDTO.getCatches());

        assertNull(fishingActivityViewDTO.getAreas());

        assertEquals(3, fishingActivityViewDTO.getLocations().size());

        assertTrue(fishingActivityViewDTO.getGears().isEmpty());
        assertTrue(fishingActivityViewDTO.getProcessingProducts().isEmpty());
        assertNull(fishingActivityViewDTO.getGearShotRetrievalList());
        assertTrue(fishingActivityViewDTO.getVesselDetails().isEmpty());
        assertTrue(fishingActivityViewDTO.getGearProblems().isEmpty());
        assertTrue(fishingActivityViewDTO.getRelocations().isEmpty());

        // assert stuff that we couldn't deserialize back to the DTO class
        assertTrue(responseAsString.contains("\"geom\":\"MULTIPOINT ((11.97"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"FAO_AREA\",\"id\":\"51.5\"}"));
        assertTrue(responseAsString.contains("\"identifier\":{\"schemeId\":\"TERRITORY\",\"id\":\"SYC\"}"));
    }

    private void assertActivityDetailsDto(ActivityDetailsDto activityDetails) {
        assertEquals(expectedActivityDetailsDto.getType(), activityDetails.getType());
        assertEquals(expectedActivityDetailsDto.getId(), activityDetails.getId());
        assertEquals(expectedActivityDetailsDto.getReason(), activityDetails.getReason());
        assertEquals(expectedActivityDetailsDto.getArrivalTime(), activityDetails.getArrivalTime());
        assertEquals(expectedActivityDetailsDto.getFlapDocuments(), activityDetails.getFlapDocuments());
        assertEquals(expectedActivityDetailsDto.getFluxCharacteristics(), activityDetails.getFluxCharacteristics());
        assertEquals(expectedActivityDetailsDto.getOccurrence(), activityDetails.getOccurrence());
        assertEquals(expectedActivityDetailsDto.getIntendedLandingTime(), activityDetails.getIntendedLandingTime());
        assertEquals(expectedActivityDetailsDto.getEstimatedArrivalTime(), activityDetails.getEstimatedArrivalTime());
        assertEquals(expectedActivityDetailsDto.getFisheryType(), activityDetails.getFisheryType());
        assertEquals(expectedActivityDetailsDto.getSpeciesTarget(), activityDetails.getSpeciesTarget());
        assertEquals(expectedActivityDetailsDto.getVesselActivity(), activityDetails.getVesselActivity());
        assertEquals(expectedActivityDetailsDto.getNrOfOperation(), activityDetails.getNrOfOperation());
        assertEquals(expectedActivityDetailsDto.getIdentifiers(), activityDetails.getIdentifiers());
        assertEquals(expectedActivityDetailsDto.getTranshipmentTime(), activityDetails.getTranshipmentTime());

        if (expectedActivityDetailsDto.getLandingTime() != null) {
            assertEquals(expectedActivityDetailsDto.getLandingTime().getStartDate(), activityDetails.getLandingTime().getStartDate());
            assertEquals(expectedActivityDetailsDto.getLandingTime().getEndDate(), activityDetails.getLandingTime().getEndDate());
            assertEquals(expectedActivityDetailsDto.getLandingTime().getDuration(), activityDetails.getLandingTime().getDuration());
            assertEquals(expectedActivityDetailsDto.getLandingTime().getUnitCode(), activityDetails.getLandingTime().getUnitCode());
        } else {
            assertNull(activityDetails.getLandingTime());
        }

        if (expectedActivityDetailsDto.getFishingTime() != null) {
            assertEquals(expectedActivityDetailsDto.getFishingTime().getUnitCode(), activityDetails.getFishingTime().getUnitCode());
            assertEquals(expectedActivityDetailsDto.getFishingTime().getDuration(), activityDetails.getFishingTime().getDuration());
            assertEquals(expectedActivityDetailsDto.getFishingTime().getStartDate(), activityDetails.getFishingTime().getStartDate());
            assertEquals(expectedActivityDetailsDto.getFishingTime().getEndDate(), activityDetails.getFishingTime().getEndDate());
        } else {
            assertNull(expectedActivityDetailsDto.getFishingTime());
        }
    }

    private void assertReportDocumentDto(ReportDocumentDto reportDocument) {
        assertEquals(expectedReportDocumentDto.getType(), reportDocument.getType());
        assertEquals(expectedReportDocumentDto.getCreationDate(), reportDocument.getCreationDate());
        assertEquals(expectedReportDocumentDto.getPurposeCode(), reportDocument.getPurposeCode());
        assertEquals(expectedReportDocumentDto.getOwner(), reportDocument.getOwner());
        assertEquals(expectedReportDocumentDto.getId(), reportDocument.getId());
        assertEquals(expectedReportDocumentDto.getRefId(), reportDocument.getRefId());
        assertEquals(expectedReportDocumentDto.getAcceptedDate(), reportDocument.getAcceptedDate());
        assertEquals(expectedReportDocumentDto.getFmcMark(), reportDocument.getFmcMark());

        List<RelatedReportDto> expectedRelatedReports = expectedReportDocumentDto.getRelatedReports();
        List<RelatedReportDto> relatedReports = reportDocument.getRelatedReports();
        assertEquals(expectedRelatedReports.size(), relatedReports.size());

        for (int i = 0; i < expectedRelatedReports.size(); i++) {
            assertEquals(expectedRelatedReports.get(i).getId(), relatedReports.get(i).getId());
            assertEquals(expectedRelatedReports.get(i).getSchemeId(), relatedReports.get(i).getSchemeId());
        }
    }

    private void assertTripWidgetDto(TripWidgetDto tripWidget) {
        VesselDetailsDTO expectedVesselDetails = expectedTripWidgetDto.getVesselDetails();
        VesselDetailsDTO vesselDetails = tripWidget.getVesselDetails();
        assertEquals(expectedVesselDetails.getId(), vesselDetails.getId());
        assertEquals(expectedVesselDetails.getRoleCode(), vesselDetails.getRoleCode());
        assertEquals(expectedVesselDetails.getName(), vesselDetails.getName());
        assertEquals(expectedVesselDetails.getCountry(), vesselDetails.getCountry());
        assertEquals(expectedVesselDetails.getStorageDto(), vesselDetails.getStorageDto());

        if (expectedVesselDetails.getContactPartyDetailsDTOSet() != null) {
            assertEquals(expectedVesselDetails.getContactPartyDetailsDTOSet().size(), vesselDetails.getContactPartyDetailsDTOSet().size());

            if (expectedVesselDetails.getContactPartyDetailsDTOSet().size() == 1) {
                ContactPartyDetailsDTO expectedContactPartyDetails = expectedVesselDetails.getContactPartyDetailsDTOSet().iterator().next();
                ContactPartyDetailsDTO contactPartyDetails = vesselDetails.getContactPartyDetailsDTOSet().iterator().next();

                assertEquals(expectedContactPartyDetails.getRole(), contactPartyDetails.getRole());
                if (expectedContactPartyDetails.getContactPerson() != null) {
                    assertEquals(expectedContactPartyDetails.getContactPerson().getGivenName(), contactPartyDetails.getContactPerson().getGivenName());
                    assertEquals(expectedContactPartyDetails.getContactPerson().getFamilyName(), contactPartyDetails.getContactPerson().getFamilyName());
                    assertEquals(expectedContactPartyDetails.getContactPerson().getAlias(), contactPartyDetails.getContactPerson().getAlias());
                } else {
                    assertNull(contactPartyDetails.getContactPerson());
                }

                for (int i = 0; i < expectedContactPartyDetails.getStructuredAddresses().size(); i++) {
                    AddressDetailsDTO expectedAddressDetails = expectedContactPartyDetails.getStructuredAddresses().get(i);
                    AddressDetailsDTO addressDetails = contactPartyDetails.getStructuredAddresses().get(i);

                    assertEquals(expectedAddressDetails.getCityName(), addressDetails.getCityName());
                    assertEquals(expectedAddressDetails.getCountryName(), addressDetails.getCountryName());
                    assertEquals(expectedAddressDetails.getCountryIDValue(), addressDetails.getCountryIDValue());
                    assertEquals(expectedAddressDetails.getPostalAreaValue(), addressDetails.getPostalAreaValue());
                    assertEquals(expectedAddressDetails.getPlotId(), addressDetails.getPlotId());
                    assertEquals(expectedAddressDetails.getPostcode(), addressDetails.getPostcode());
                    assertEquals(expectedAddressDetails.getStreetName(), addressDetails.getStreetName());
                }
            }
        } else {
            assertNull(vesselDetails.getContactPartyDetailsDTOSet());
        }

        if (expectedVesselDetails.getVesselIdentifiers() != null) {
            assertEquals(expectedVesselDetails.getVesselIdentifiers().size(), vesselDetails.getVesselIdentifiers().size());

            if (expectedVesselDetails.getVesselIdentifiers().size() == 1) {
                AssetIdentifierDto expectedAssetIdentifier = expectedVesselDetails.getVesselIdentifiers().iterator().next();
                AssetIdentifierDto assetIdentifier = vesselDetails.getVesselIdentifiers().iterator().next();

                assertEquals(expectedAssetIdentifier.isFromAssets(), assetIdentifier.isFromAssets());
                assertEquals(expectedAssetIdentifier.getIdentifierSchemeId(), assetIdentifier.getIdentifierSchemeId());
                assertEquals(expectedAssetIdentifier.getFaIdentifierId(), assetIdentifier.getFaIdentifierId());
                assertEquals(expectedAssetIdentifier.getFaIdentifierSchemeId(), assetIdentifier.getFaIdentifierSchemeId());
            }
        } else {
            assertNull(vesselDetails.getVesselIdentifiers());
        }

        if (expectedVesselDetails.getFlapDocuments() != null) {
            assertEquals(expectedVesselDetails.getFlapDocuments().size(), vesselDetails.getFlapDocuments().size());
        } else {
            assertNull(vesselDetails.getFlapDocuments());
        }

        if (expectedTripWidgetDto.getFlapDocuments() != null) {
            assertEquals(expectedTripWidgetDto.getFlapDocuments().size(), tripWidget.getFlapDocuments().size());
        } else {
            assertNull(expectedTripWidgetDto.getFlapDocuments());
        }

        if (expectedTripWidgetDto.getTrips() != null) {
            assertEquals(expectedTripWidgetDto.getTrips().size(), tripWidget.getTrips().size());
            for (int i = 0; i < expectedTripWidgetDto.getTrips().size(); i++) {
                TripOverviewDto expectedTripOverview = expectedTripWidgetDto.getTrips().get(i);
                TripOverviewDto tripOverview = tripWidget.getTrips().get(i);
                assertEquals(expectedTripOverview.getTypeCode(), tripOverview.getTypeCode());
                assertEquals(expectedTripOverview.getDepartureTime(), tripOverview.getDepartureTime());
                assertEquals(expectedTripOverview.getArrivalTime(), tripOverview.getArrivalTime());
                assertEquals(expectedTripOverview.getLandingTime(), tripOverview.getLandingTime());

                for (int j = 0; j < expectedTripOverview.getTripId().size(); j++) {
                    TripIdDto expectedTripId = expectedTripOverview.getTripId().get(j);
                    TripIdDto tripId = tripOverview.getTripId().get(j);
                    assertEquals(expectedTripId.getId(), tripId.getId());
                    assertEquals(expectedTripId.getSchemeId(), tripId.getSchemeId());
                }
            }
        } else {
            assertNull(tripWidget.getFlapDocuments());
        }
    }

    private void assertActivityHistory(List<ActivityHistoryDtoElement> activityHistoryDtoElements) {
        if (expectedActivityHistoryDto == null) {
            assertTrue(activityHistoryDtoElements == null || activityHistoryDtoElements.isEmpty());
            return;
        }

        assertEquals(1, activityHistoryDtoElements.size());
        ActivityHistoryDtoElement activityHistoryDto = activityHistoryDtoElements.get(0);
        assertEquals(expectedActivityHistoryDto.getFaReportId(), activityHistoryDto.getFaReportId());
        assertEquals(expectedActivityHistoryDto.getPurposeCode(), activityHistoryDto.getPurposeCode());
        assertEquals(expectedActivityHistoryDto.getAcceptanceDate(), activityHistoryDto.getAcceptanceDate());

        for (int i = 0; i < expectedActivityHistoryDto.getFishingActivityIds().size(); i++) {
            assertEquals(expectedActivityHistoryDto.getFishingActivityIds().get(i), activityHistoryDto.getFishingActivityIds().get(i));
        }
    }

    private void assertCatches(Set<FaCatchGroupDto> catches) {
        assertEquals(2, catches.size());
        for (FaCatchGroupDto aCatch : catches) {
            assertEquals("ONBOARD", aCatch.getType());
            assertNotNull(aCatch.getSpecies());
            assertNotNull(aCatch.getLocations().getTerritory());
            assertNotNull(aCatch.getLocations().getFaoArea());
            assertEquals(2, aCatch.getGroupingDetails().size());
            assertTrue(aCatch.getGroupingDetails().containsKey("BMS"));
            assertTrue(aCatch.getGroupingDetails().containsKey("LSC"));
        }
    }

    private void assertAreas(AreaDto areaDto) {
        assertNull(areaDto.getFluxLocations());
        assertEquals(1483931160000L, areaDto.getTransmission().getOccurence().getTime());
        assertEquals(1483931160000L, areaDto.getCrossing().getOccurence().getTime());
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
