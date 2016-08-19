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

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FishingGearDetailsDTO;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingGear;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by padhyad on 7/29/2016.
 */
public class FishingGearMapperTest {

    @Test
    public void testFishingGearMapperWithFishingActivity() {
        FishingGear fishingGear = MapperUtil.getFishingGear();
        FishingGearEntity fishingGearEntity = new FishingGearEntity();
        FishingActivityEntity fishingActivityEntity = null;
        FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear, fishingActivityEntity, fishingGearEntity);

        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getRoleCodeListId());
        assertNull(fishingGearEntity.getFishingActivity());

        assertNotNull(fishingGearEntity.getGearCharacteristics());
        fishingGearEntity = fishingGearEntity.getGearCharacteristics().iterator().next().getFishingGear();
        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getRoleCodeListId());
    }

    @Test
    public void testFishingGearMapperWithFaCatch() {
        FishingGear fishingGear = MapperUtil.getFishingGear();
        FishingGearEntity fishingGearEntity = new FishingGearEntity();
        FaCatchEntity faCatchEntity = null;
        FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear, faCatchEntity, fishingGearEntity);

        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getRoleCodeListId());
        assertNull(fishingGearEntity.getFaCatch());

        assertNotNull(fishingGearEntity.getGearCharacteristics());
        fishingGearEntity = fishingGearEntity.getGearCharacteristics().iterator().next().getFishingGear();
        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getRoleCodeListId());
    }

    @Test
    public void testFishingGearMapperWithGearProblem() {
        FishingGear fishingGear = MapperUtil.getFishingGear();
        FishingGearEntity fishingGearEntity = new FishingGearEntity();
        GearProblemEntity gearProblemEntity = null;
        FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear, gearProblemEntity, fishingGearEntity);

        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getRoleCodeListId());
        assertNull(fishingGearEntity.getGearProblem());

        assertNotNull(fishingGearEntity.getGearCharacteristics());
        fishingGearEntity = fishingGearEntity.getGearCharacteristics().iterator().next().getFishingGear();
        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getRoleCodeListId());
    }

    @Test
    public void testFishingGearDetailsDTOMapper() {
        FishingGear fishingGear = MapperUtil.getFishingGear();
        FishingGearEntity fishingGearEntity = new FishingGearEntity();
        FishingActivityEntity fishingActivityEntity = null;
        FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear, fishingActivityEntity, fishingGearEntity);

        FishingGearDetailsDTO fishingGearDetailsDTO = FishingGearMapper.INSTANCE.mapToFishingGearDetailsDTO(fishingGearEntity);
        assertEquals(fishingGearEntity.getTypeCode(), fishingGearDetailsDTO.getGearType());
        assertEquals(fishingGearEntity.getRoleCode(), fishingGearDetailsDTO.getRole());
    }

    @Test
    public void testFishingGearDetailsDTOListMapper() {
        FishingGear fishingGear = MapperUtil.getFishingGear();
        FishingGearEntity fishingGearEntity = new FishingGearEntity();
        FishingActivityEntity fishingActivityEntity = null;
        FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear, fishingActivityEntity, fishingGearEntity);

        List<FishingGearDetailsDTO> fishingGearDetailsDTO = FishingGearMapper.INSTANCE.mapToFishingGearDetailsDTOList(new HashSet<FishingGearEntity>(Arrays.asList(fishingGearEntity)));
        assertEquals(fishingGearEntity.getTypeCode(), fishingGearDetailsDTO.get(0).getGearType());
        assertEquals(fishingGearEntity.getRoleCode(), fishingGearDetailsDTO.get(0).getRole());
    }
}
