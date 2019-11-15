/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.GearShotRetrievalDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.view.ActivityArrivalViewMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.view.GearShotRetrievalTileMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.view.JointFishingOperationViewMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.view.base.ActivityViewEnum;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.view.base.ActivityViewMapperFactory;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.view.base.BaseActivityViewMapper;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil.getFishingActivity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by kovian on 09/02/2017.
 */
public class FishingActivityViewMapperTest {

    private FishingActivityEntity fishingActivity;

    @Before
    @SneakyThrows
    public void initFishingActivityEntity(){
        FLUXFAReportMessage fluxfaReportMessage = getActivityDataFromXML();
        FluxFaReportMessageEntity fluxRepMessageEntity = new FluxFaReportMessageMapper().mapToFluxFaReportMessage(fluxfaReportMessage, FaReportSourceEnum.FLUX);
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
        assertNotNull(fishingActivityViewDTO.getReportDetails());
        assertEquals(1, fishingActivityViewDTO.getGears().size());
        assertNull(mapperForView.mapFaEntityToFaDto(null));
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
        assertNotNull(fishingActivityViewDTO.getReportDetails());
        assertNull(ActivityArrivalViewMapper.INSTANCE.mapFaEntityToFaDto(null));
    }

    @Test
    @SneakyThrows
         public void testActivityDepartureViewMapper() {
        BaseActivityViewMapper mapperForView = ActivityViewMapperFactory.getMapperForView(ActivityViewEnum.DEPARTURE);
        FishingActivityEntity fishingActivityEntity = getFishingActivityEntity();

        Set<FaCatchEntity> faCatches = generateFaCatches(fishingActivityEntity.getFaCatchs().iterator().next());
        fishingActivityEntity.setFaCatchs(faCatches);

        FishingActivityViewDTO fishingActivityViewDTO = mapperForView.mapFaEntityToFaDto(fishingActivityEntity);

        assertNotNull(fishingActivityViewDTO.getActivityDetails());
        assertNotNull(fishingActivityViewDTO.getReportDetails());
        assertNull(ActivityArrivalViewMapper.INSTANCE.mapFaEntityToFaDto(null));
    }

    @Test
    @SneakyThrows
    public void testActivityRelocationViewMapper() {
        BaseActivityViewMapper mapperForView = ActivityViewMapperFactory.getMapperForView(ActivityViewEnum.RELOCATION);
        FishingActivityEntity fishingActivityEntity = getFishingActivityEntity();

        Set<FaCatchEntity> faCatches = generateFaCatches(fishingActivityEntity.getFaCatchs().iterator().next());
        fishingActivityEntity.setFaCatchs(faCatches);

        FishingActivityViewDTO fishingActivityViewDTO = mapperForView.mapFaEntityToFaDto(fishingActivityEntity);

        assertNotNull(fishingActivityViewDTO.getActivityDetails());
        assertNotNull(fishingActivityViewDTO.getReportDetails());
        assertNull(ActivityArrivalViewMapper.INSTANCE.mapFaEntityToFaDto(null));
    }

    @Test
    @SneakyThrows
    public void testGearShotAndRetrieval() {
        // Given
        FishingActivityEntity fishingActivity = getFishingActivityEntity();
        Set<FishingActivityEntity> fishingActivityEntitySet = new HashSet<>();
        fishingActivityEntitySet.add(fishingActivity);

        // When
        List<GearShotRetrievalDto> fishingActivityViewDTO = GearShotRetrievalTileMapper.INSTANCE.mapEntityListToDtoList(fishingActivityEntitySet);

        // Then
        assertEquals(1, fishingActivityViewDTO.size());
        GearShotRetrievalDto gearShotRetrievalDto = fishingActivityViewDTO.get(0);

        assertEquals(fishingActivity.getTypeCode(), gearShotRetrievalDto.getType());
        assertEquals(fishingActivity.getOccurence(), Instant.parse(gearShotRetrievalDto.getOccurrence() + "Z"));

        assertEquals(1, fishingActivity.getFishingActivityIdentifiers().size());

        assertEquals(1, fishingActivity.getDelimitedPeriods().size());

        assertNull(gearShotRetrievalDto.getCalculatedDuration());
        FishingGearEntity fishingGearEntity = fishingActivity.getFishingGears().iterator().next();
        assertEquals(fishingGearEntity.getTypeCode(), gearShotRetrievalDto.getGear().getType());
        assertEquals(fishingGearEntity.getFishingGearRole().iterator().next().getRoleCode(), gearShotRetrievalDto.getGear().getRole());

        assertEquals(1, gearShotRetrievalDto.getCharacteristics().size());
        assertEquals(1, gearShotRetrievalDto.getGearProblems().size());

        FluxLocationDto location = gearShotRetrievalDto.getLocation();
        FluxLocationEntity fluxLocation = fishingActivity.getFluxLocations().iterator().next();
        assertEquals(fluxLocation.getCountryId(), location.getCountryId());
        assertEquals(fluxLocation.getSovereignRightsCountryCode(), location.getSovereignCountry());
        assertEquals(fluxLocation.getJurisdictionCountryCode(), location.getJurisdictionCountry());
        assertEquals(fluxLocation.getFluxLocationIdentifier(), location.getFluxLocationIdentifier());
        assertEquals(fluxLocation.getFluxLocationIdentifierSchemeId(), location.getFluxLocationIdentifierSchemeId());
        assertEquals(fluxLocation.getTypeCode(), location.getTypeCode());
    }

    @Test
    @SneakyThrows
    public void testActivityJointFishingOperationViewMapper() {
        FishingActivity fishingActivity = getFishingActivity();
        FishingActivityEntity fishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(fishingActivity, null);
        fishingActivityEntity.setTypeCode("JOINT_FISHING_OPERATION");
        fishingActivityEntity.getAllRelatedFishingActivities().iterator().next().setTypeCode("RELOCATION");
        fishingActivityEntity.getFaCatchs().iterator().next().setId(1);
        JointFishingOperationViewMapper mapper = new JointFishingOperationViewMapper();
        FishingActivityViewDTO dto = mapper.mapFaEntityToFaDto(fishingActivityEntity);
        assertNotNull(dto);
        assertNotNull(dto.getActivityDetails());
        assertNotNull(dto.getCatches());
        assertNotNull(dto.getGears());
        assertNotNull(dto.getGearProblems());
        assertNotNull(dto.getVesselDetails());
        assertNotNull(dto.getRelocations());
        assertNotNull(dto.getProcessingProducts());
        assertNotNull(dto.getLocations());
    }

    @SneakyThrows
    private Set<FaCatchEntity> generateFaCatches(FaCatchEntity faCatchExample) {
        FaCatchEntity clone_1 = (FaCatchEntity) BeanUtils.cloneBean(faCatchExample);
        FaCatchEntity clone_2 = (FaCatchEntity) BeanUtils.cloneBean(faCatchExample);
        FaCatchEntity clone_3 = (FaCatchEntity) BeanUtils.cloneBean(faCatchExample);
        FaCatchEntity clone_4 = (FaCatchEntity) BeanUtils.cloneBean(faCatchExample);

        return new HashSet<>(Arrays.asList(clone_1, clone_2, clone_3, clone_4));
    }

    private FLUXFAReportMessage getActivityDataFromXML() throws JAXBException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fishingActivityViewMapperTest.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);
    }

    private FishingActivityEntity getFishingActivityEntity() {
        if (fishingActivity == null) {
            initFishingActivityEntity();
        }
        return fishingActivity;
    }
}
