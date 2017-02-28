/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityArrivalViewMapper;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.parent.FishingActivityViewDTO;
import lombok.SneakyThrows;
import org.junit.Test;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by kovian on 09/02/2017.
 */
public class FishingActivityViewMapperTest {

    @Test
    @SneakyThrows
    public void testActivityViewMapper(){

        FLUXFAReportMessage fluxfaReportMessage = getActivityDataFromXML();
        FluxFaReportMessageEntity fluxRepMessageEntity = FluxFaReportMessageMapper.INSTANCE.mapToFluxFaReportMessage(fluxfaReportMessage, FaReportSourceEnum.FLUX, new FluxFaReportMessageEntity());
        List<FaReportDocumentEntity> faReportDocuments = new ArrayList<>(fluxRepMessageEntity.getFaReportDocuments());
        FishingActivityEntity fishingActivity = faReportDocuments.get(0).getFishingActivities().iterator().next();

        FishingActivityViewDTO fishingActivityViewDTO = ActivityArrivalViewMapper.INSTANCE.mapFaEntityToFaDto(fishingActivity, new FishingActivityViewDTO());

        assertNotNull(fishingActivityViewDTO.getArrival());
        assertNotNull(fishingActivityViewDTO.getGears());
        assertNotNull(fishingActivityViewDTO.getReportDoc());
        assertTrue(fishingActivityViewDTO.getGears().size() == 1);

        assertNull(ActivityArrivalViewMapper.INSTANCE.mapFaEntityToFaDto(null, new FishingActivityViewDTO()));

        System.out.println(new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValueAsString(fishingActivityViewDTO));

    }

    private FLUXFAReportMessage getActivityDataFromXML() throws JAXBException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);
    }
}
