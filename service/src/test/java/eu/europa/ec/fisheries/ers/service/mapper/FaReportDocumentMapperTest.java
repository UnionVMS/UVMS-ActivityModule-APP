/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import un.unece.uncefact.data.standard.fluxfareportmessage._1.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselTransportMeans;

import javax.validation.constraints.AssertFalse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by padhyad on 7/18/2016.
 */
public class FaReportDocumentMapperTest {

    @Test
    public void testFaReportDocumentEntityCreate() throws JAXBException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        FLUXFAReportMessage fluxfaReportMessage = (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        for (FAReportDocument faReportDocument : fluxfaReportMessage.getFAReportDocuments()) {
            FaReportDocumentEntity faReportDocumentEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity());
            faReportDocumentEntities.add(faReportDocumentEntity);
        }

        assertNotNull(faReportDocumentEntities);
        FaReportDocumentEntity faReportDocumentEntity = faReportDocumentEntities.get(0);
        assertNotNull(faReportDocumentEntity);
        assertNotNull(faReportDocumentEntity.getFluxReportDocument());
        assertNotNull(faReportDocumentEntity.getFaReportIdentifiers());
        assertVesselTransportMeans(faReportDocumentEntity);
        assertFishingActivity(faReportDocumentEntity);
    }

    private void assertVesselTransportMeans(FaReportDocumentEntity faReportDocumentEntity) {
        VesselTransportMeansEntity vesselTransportMeansEntity = faReportDocumentEntity.getVesselTransportMeans();
        assertNotNull(vesselTransportMeansEntity);
        assertNotNull(vesselTransportMeansEntity.getRegistrationEvent());
        assertNotNull(vesselTransportMeansEntity.getRegistrationEvent().getRegistrationLocation());
        assertNotNull(vesselTransportMeansEntity.getContactParty());
        assertNotNull(vesselTransportMeansEntity.getVesselIdentifiers());

        for (ContactPartyEntity contactPartyEntity : vesselTransportMeansEntity.getContactParty()) {
            assertNotNull(contactPartyEntity);
            assertNotNull(contactPartyEntity.getContactPerson());
            assertNotNull(contactPartyEntity.getStructuredAddresses());
        }
    }

    private void assertFishingActivity(FaReportDocumentEntity faReportDocumentEntity) {
        assertNotNull(faReportDocumentEntity.getFishingActivities());
        for(FishingActivityEntity fishingActivityEntity : faReportDocumentEntity.getFishingActivities()) {
            assertNotNull(fishingActivityEntity);
            assertNotNull(fishingActivityEntity.getDestVesselCharId());
            assertNotNull(fishingActivityEntity.getSourceVesselCharId());
            assertNotNull(fishingActivityEntity.getFaCatchs());
            assertNotNull(fishingActivityEntity.getFishingTrips());
            assertNotNull(fishingActivityEntity.getFishingGears());
        }
    }
}
