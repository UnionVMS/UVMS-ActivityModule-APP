/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.view.ActivityArrivalViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.ActivityViewEnum;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.ActivityViewMapperFactory;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by kovian on 09/02/2017.
 */
public class FishingActivityViewMapperTest {

    FishingActivityEntity fishingActivity;

    @Before
    @SneakyThrows
    public void initFishingActivityEntity(){
        FLUXFAReportMessage fluxfaReportMessage = getActivityDataFromXML();
        FluxFaReportMessageEntity fluxRepMessageEntity = FluxFaReportMessageMapper.INSTANCE.mapToFluxFaReportMessage(fluxfaReportMessage, FaReportSourceEnum.FLUX, new FluxFaReportMessageEntity());
        List<FaReportDocumentEntity> faReportDocuments = new ArrayList<>(fluxRepMessageEntity.getFaReportDocuments());
        fishingActivity = faReportDocuments.get(0).getFishingActivities().iterator().next();
    }

    @Test
    @SneakyThrows
    public void testActivityArrivalViewMapper(){

        BaseActivityViewMapper mapperForView = ActivityViewMapperFactory.getMapperForView(ActivityViewEnum.ARRIVAL);

        FishingActivityViewDTO fishingActivityViewDTO = mapperForView.mapFaEntityToFaDto(getFishingActivityEntity());

        assertNotNull(fishingActivityViewDTO.getActivityDetails());
        assertNotNull(fishingActivityViewDTO.getGears());
        assertNotNull(fishingActivityViewDTO.getReportDoc());
        assertTrue(fishingActivityViewDTO.getGears().size() == 1);
        assertNull(mapperForView.mapFaEntityToFaDto(null));

        printDtoOnConsole(fishingActivityViewDTO, FishingActivityView.Arrival.class);
    }


    @Test
    @SneakyThrows
    public void testActivityLandingViewMapper(){

        BaseActivityViewMapper mapperForView = ActivityViewMapperFactory.getMapperForView(ActivityViewEnum.LANDING);
        FishingActivityEntity fishingActivityEntity = getFishingActivityEntity();

        Set<FaCatchEntity> faCatches = generateFaCatches(fishingActivityEntity.getFaCatchs().iterator().next());
        fishingActivityEntity.setFaCatchs(faCatches);

        FishingActivityViewDTO fishingActivityViewDTO = mapperForView.mapFaEntityToFaDto(fishingActivityEntity);

        assertNotNull(fishingActivityViewDTO.getActivityDetails());
        assertNotNull(fishingActivityViewDTO.getReportDoc());
        assertNull(ActivityArrivalViewMapper.INSTANCE.mapFaEntityToFaDto(null));

        printDtoOnConsole(fishingActivityViewDTO, FishingActivityView.Landing.class);
    }

    private Set<FaCatchEntity> generateFaCatches(FaCatchEntity faCatchExample) {
        List<FaCatchEntity> faCatchList = new ArrayList<>();
        FaCatchEntity clone_1 = (FaCatchEntity) SerializationUtils.clone(faCatchExample);
        FaCatchEntity clone_2 = (FaCatchEntity) SerializationUtils.clone(faCatchExample);
        FaCatchEntity clone_3 = (FaCatchEntity) SerializationUtils.clone(faCatchExample);
        FaCatchEntity clone_4 = (FaCatchEntity) SerializationUtils.clone(faCatchExample);

        faCatchList.add(cloneEntity(clone_1, "LSC", 100.00));
        faCatchList.add(cloneEntity(clone_2, "LSC", 100.00));
        faCatchList.add(cloneEntity(clone_3, "BMS", 200.00));
        faCatchList.add(cloneEntity(clone_4, "BMS", 200.00));

        return new HashSet<>(Arrays.asList(clone_1, clone_2, clone_3, clone_4));
    }

    private FaCatchEntity cloneEntity(FaCatchEntity faCatchExample, String fishClassCode, Double weight) {
        faCatchExample.setFishClassCode(fishClassCode);
        faCatchExample.setCalculatedWeightMeasure(weight);
        return faCatchExample;
    }

    private FLUXFAReportMessage getActivityDataFromXML() throws JAXBException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);
    }

    private FishingActivityEntity getFishingActivityEntity() throws JAXBException {
        if(fishingActivity == null){
            initFishingActivityEntity();
        }
        return fishingActivity;
    }


    private void printDtoOnConsole(FishingActivityViewDTO fishingActivityViewDTO, final Class<?> view) throws JsonProcessingException {
        System.out.println(getObjectMapperForView(view).configure(SerializationFeature.INDENT_OUTPUT, true).writeValueAsString(fishingActivityViewDTO));
    }

    @NotNull
    private ObjectMapper getObjectMapperForView(final Class<?> view) {
        return new ObjectMapper() {
                private static final long serialVersionUID = 1L;
                @Override
                protected DefaultSerializerProvider _serializerProvider(SerializationConfig config) {
                    return super._serializerProvider(config.withView(view));
                }
            };
    }
}
