package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.FishingActivityTypeDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.FishingTripSummaryViewDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.ReportDTO;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Arquillian.class)
public class FishingTripResourceTest extends BaseActivityArquillianTest {

    @Before
    public void setUp() throws NamingException {
        InitialContext ctx = new InitialContext();
        ctx.rebind("java:global/spatial_endpoint", "http://localhost:8080/activity-rest-test");
    }

    @Test
    public void getFishingTripSummary_noGeometry() throws JsonProcessingException {
        // Given
        String token = getToken();

        // When
        String responseAsString = getWebTarget()
                .path("trip")
                .path("reports")
                .path("ESP-TRP-20160630000003")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("scopeName", null) // "null" means that we will not look up any geometry
                .header("roleName", "myRole")
                .get(String.class);

        // Then

        // I spent hours trying to get a properly typed ResponseDto<FishingTripSummaryViewDto> from the query, but couldn't get it to work.
        // So the workaround is to get the response entity as a String and just deserialize it here in the test...
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseDto<FishingTripSummaryViewDTO> responseDto =
                objectMapper.readValue(responseAsString,
                        new TypeReference<ResponseDto<FishingTripSummaryViewDTO>>(){});

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

    // Can be extended to test more fields
    private void assertActivityReport(ReportDTO dto, String occurrenceAsString, int fishingActivityId, int faReportId, String activityType) {
        Instant occurrence = Instant.parse(occurrenceAsString);
        assertEquals(occurrence, dto.getOccurence().toInstant());
        assertEquals(occurrence, dto.getFaReportAcceptedDateTime().toInstant());

        assertEquals(fishingActivityId, dto.getFishingActivityId());
        assertEquals(faReportId, dto.getFaReportID());
        assertEquals(activityType, dto.getActivityType());
    }
}
