package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishSizeClassEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.FACatchDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class FACatchResourceTest extends BaseActivityArquillianTest {

    @Before
    public void setUp() throws NamingException, IOException, JAXBException, ServiceException, NotSupportedException, SystemException {
        super.setUp();
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

        assertEquals(114923.9, lscFishMap.get("HER"), 0.0);
        assertEquals(617.84, lscFishMap.get("SPR"), 0.0);
        assertEquals(1442.54, lscFishMap.get("MXV"), 0.0);
    }
}
