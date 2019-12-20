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
import org.junit.Test;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by kovian on 26/01/2017.
 */
public class FluxFaReportMessageMapperTest {

    @Test
    public void fluxFaReportMessageMapper() throws JAXBException {
        // Given
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        FLUXFAReportMessage original = (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);

        // When
        FluxFaReportMessageEntity mapped = FluxFaReportMessageMapper.INSTANCE.mapToFluxFaReportMessage(original, FaReportSourceEnum.FLUX);

        // Then

        assertEquals("FLUX_REPORT_ID_1", mapped.getFluxReportDocument_Id());
        assertEquals("FLUX_SCHEME_ID1", mapped.getFluxReportDocument_IdSchemeId());
        assertEquals("REF_ID 1", mapped.getFluxReportDocument_ReferenceId());
        assertEquals("47rfh-5hry4-thfur75-4hf743", mapped.getFluxReportDocument_ReferenceIdSchemeId());
        assertEquals(Instant.parse("2016-07-01T11:14:00Z"), mapped.getFluxReportDocument_CreationDatetime());
        assertEquals("9", mapped.getFluxReportDocument_PurposeCode());
        assertEquals("Purpose", mapped.getFluxReportDocument_Purpose());
        assertEquals("Owner flux party id 1", mapped.getFluxReportDocument_FluxParty().getFluxPartyIdentifiers().iterator().next().getFluxPartyIdentifierId());
        assertEquals("47rfh-5hry4-thfur75-4hf743", mapped.getFluxReportDocument_FluxParty().getFluxPartyIdentifiers().iterator().next().getFluxPartyIdentifierSchemeId());
        assertEquals("fluxPartyOwnerName 1", mapped.getFluxReportDocument_FluxParty().getFluxPartyName());

        // assert FA report documents
        assertEquals(2, mapped.getFaReportDocuments().size());
        FaReportDocumentEntity mappedFAReportDocument = mapped.getFaReportDocuments().iterator().next();
        assertEquals("DECLARATION", mappedFAReportDocument.getTypeCode());
        assertEquals("fhr574fh-thrud754-kgitjf754-gjtufe89", mappedFAReportDocument.getTypeCodeListId());
        assertTrue(mappedFAReportDocument.getFmcMarker().contains("Fmz marker"));
        assertEquals("h49rh-fhrus33-fj84hjs82-4h84hw82", mappedFAReportDocument.getFmcMarkerListId());
        assertEquals("ID 1", mappedFAReportDocument.getFaReportIdentifiers().iterator().next().getFaReportIdentifierId());
        assertEquals("47rfh-5hry4-thfur75-4hf743", mappedFAReportDocument.getFaReportIdentifiers().iterator().next().getFaReportIdentifierSchemeId());
        assertEquals(Instant.parse("2016-07-01T11:14:00Z"), mappedFAReportDocument.getAcceptedDatetime());
    }

}
