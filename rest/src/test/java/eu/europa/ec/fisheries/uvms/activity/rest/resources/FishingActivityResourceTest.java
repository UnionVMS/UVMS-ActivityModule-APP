package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class FishingActivityResourceTest extends BaseActivityArquillianTest {

    @Test
    public void getCommunicationChannels_OK() {
        // Given

        // When
        Response response = getWebTarget()
                .path("fa")
                .path("commChannels")
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Then
        assertEquals(200, response.getStatus());
        FaReportSourceEnum[] faReportSourceEnums = response.readEntity(FaReportSourceEnum[].class);
        FaReportSourceEnum[] faReportSourceEnumsExpected = {FaReportSourceEnum.FLUX, FaReportSourceEnum.MANUAL};
        assertArrayEquals(faReportSourceEnumsExpected, faReportSourceEnums);
    }
}
