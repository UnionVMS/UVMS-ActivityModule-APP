/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import lombok.SneakyThrows;
import org.junit.Test;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

/**
 * Created by kovian on 26/01/2017.
 */
public class FluxFaReportMessageMapperTest {

    @Test
    @SneakyThrows
    public void testFluxFaReportMessageMapper(){
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        FLUXFAReportMessage fluxfaReportMessage = (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);
        FluxFaReportMessageEntity fluxRepMessageEntity = new FluxFaReportMessageMapper().mapToFluxFaReportMessage(fluxfaReportMessage, FaReportSourceEnum.FLUX, new FluxFaReportMessageEntity());

        List<FaReportDocumentEntity> faReportDocuments = new ArrayList(fluxRepMessageEntity.getFaReportDocuments());
        FluxReportDocumentEntity fluxReportDocument = fluxRepMessageEntity.getFluxReportDocument();

        assertNotNull(fluxRepMessageEntity);
        assertNotNull(fluxReportDocument);
        assertNotNull(fluxReportDocument.getFluxFaReportMessage());
        assertNotNull(faReportDocuments.get(0).getFluxReportDocument());

        assertEquals(1, faReportDocuments.size());
    }

}
