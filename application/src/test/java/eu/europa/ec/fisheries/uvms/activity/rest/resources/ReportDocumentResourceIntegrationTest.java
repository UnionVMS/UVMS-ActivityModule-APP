package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQueryParameter;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;

import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class ReportDocumentResourceIntegrationTest extends BaseActivityArquillianTest {

    @Before
    public void setUp() throws NamingException, IOException, JAXBException, ServiceException, NotSupportedException, SystemException {
        super.setUp();
    }

    @Test
    public void loadReports() throws ActivityModelMarshallException {
        // Given
        List<FAQueryParameter> parameters = new ArrayList<>();

        FLUXParty fluxParty = new FLUXParty();
        fluxParty.setIDS(new ArrayList<>());

        Instant startInstant = Instant.parse("2013-04-02T01:00:00.000Z");
        Instant endInstant = Instant.parse("2013-04-05T16:00:00.000Z");

        DateTimeType startDateTime = new DateTimeType();
        startDateTime.setDateTime(XMLDateUtils.dateToXmlGregorian(new Date(startInstant.toEpochMilli())));

        DateTimeType endDateTime = new DateTimeType();
        endDateTime.setDateTime(XMLDateUtils.dateToXmlGregorian(new Date(endInstant.toEpochMilli())));

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setStartDateTime(startDateTime);
        delimitedPeriod.setEndDateTime(endDateTime);

        FAQuery query = new FAQuery();
        query.setSimpleFAQueryParameters(parameters);
        query.setSubmitterFLUXParty(fluxParty);
        query.setSpecifiedDelimitedPeriod(delimitedPeriod);

        FLUXFAQueryMessage fluxfaQueryMessage = new FLUXFAQueryMessage();
        fluxfaQueryMessage.setFAQuery(query);

        // When
        String xmlResponse = report(fluxfaQueryMessage);

        // Then
        FLUXFAReportMessage fluxfaReportMessage = JAXBMarshaller.unmarshallTextMessage(xmlResponse, FLUXFAReportMessage.class);

        List<FAReportDocument> faReportDocuments = fluxfaReportMessage.getFAReportDocuments();
        assertEquals(22, faReportDocuments.size());
    }

    private String report(FLUXFAQueryMessage fluxfaQueryMessage) throws ActivityModelMarshallException {
        String stringQuery = JAXBMarshaller.marshallJaxBObjectToString(fluxfaQueryMessage);

        return getWebTarget()
                .path("report")
                .request(MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header("scopeName", null)
                .header("roleName", "myRole")
                .post(Entity.entity(stringQuery, MediaType.APPLICATION_XML), String.class);
    }
}
