/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class FluxFaReportMessageMapperIntegrationTest extends BaseActivityArquillianTest {

    @Inject
    FluxFaReportMessageMapper fluxFaReportMessageMapper;

    @Test
    public void fluxFaReportMessageMapper() throws JAXBException, IOException {
        // Given
        FLUXFAReportMessage original = getMessageFromTestResource("fa_flux_message.xml");

        // When
        FluxFaReportMessageEntity mapped = fluxFaReportMessageMapper.mapToFluxFaReportMessage(original, FaReportSourceEnum.FLUX);

        // Then
        assertEquals("FLUX_REPORT_ID_1", mapped.getFluxReportDocument_Id());
        assertEquals("FLUX_SCHEME_ID1", mapped.getFluxReportDocument_IdSchemeId());
        assertEquals("REF_ID 1", mapped.getFluxReportDocument_ReferencedFaQueryMessageId());
        assertEquals("47rfh-5hry4-thfur75-4hf743", mapped.getFluxReportDocument_ReferencedFaQueryMessageSchemeId());
        assertEquals(Instant.parse("2016-07-01T11:14:00Z"), mapped.getFluxReportDocument_CreationDatetime());
        assertEquals("9", mapped.getFluxReportDocument_PurposeCode());
        assertEquals("Purpose", mapped.getFluxReportDocument_Purpose());
        assertEquals("Owner flux party id 1", mapped.getFluxParty_identifier());
        assertEquals("47rfh-5hry4-thfur75-4hf743", mapped.getFluxParty_schemeId());
        assertEquals("fluxPartyOwnerName 1", mapped.getFluxParty_name());

        // assert FA report documents
        assertEquals(2, mapped.getFaReportDocuments().size());
        FaReportDocumentEntity mappedFAReportDocument = mapped.getFaReportDocuments().iterator().next();
        assertEquals("DECLARATION", mappedFAReportDocument.getTypeCode());
        assertEquals("fhr574fh-thrud754-kgitjf754-gjtufe89", mappedFAReportDocument.getTypeCodeListId());
        assertTrue(mappedFAReportDocument.getFmcMarker().contains("Fmz marker"));
        assertEquals("h49rh-fhrus33-fj84hjs82-4h84hw82", mappedFAReportDocument.getFmcMarkerListId());
        assertEquals("ID 1", mappedFAReportDocument.getRelatedFaReportIdentifiers().iterator().next().getFaReportIdentifierId());
        assertEquals("47rfh-5hry4-thfur75-4hf743", mappedFAReportDocument.getRelatedFaReportIdentifiers().iterator().next().getFaReportIdentifierSchemeId());
        assertEquals(Instant.parse("2016-07-01T11:14:00Z"), mappedFAReportDocument.getAcceptedDatetime());
    }


    private FLUXFAReportMessage getMessageFromTestResource(String fileName) throws IOException, JAXBException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = contextClassLoader.getResourceAsStream(fileName)) {
            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                BufferedReader reader = new BufferedReader(isr);
                String fileAsString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                return JAXBUtils.unMarshallMessage(fileAsString, FLUXFAReportMessage.class);
            }
        }
    }

}
