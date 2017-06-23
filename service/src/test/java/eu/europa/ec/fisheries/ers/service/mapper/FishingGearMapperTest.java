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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;

/**
 * Created by padhyad on 7/29/2016.
 */
public class FishingGearMapperTest {

    @Test
    public void testFishingGearMapperWithFishingActivity() {
        FishingGear fishingGear = MapperUtil.getFishingGear();
        FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear);

        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCodeListId());
        assertNull(fishingGearEntity.getFishingActivity());

        assertNotNull(fishingGearEntity.getGearCharacteristics());
        fishingGearEntity = fishingGearEntity.getGearCharacteristics().iterator().next().getFishingGear();
        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCodeListId());
    }

    @Test
    public void testFishingGearMapperWithFaCatch() {
        FishingGear fishingGear = MapperUtil.getFishingGear();
        FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear);

        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCodeListId());
        assertNull(fishingGearEntity.getFaCatch());

        assertNotNull(fishingGearEntity.getGearCharacteristics());
        fishingGearEntity = fishingGearEntity.getGearCharacteristics().iterator().next().getFishingGear();
        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCodeListId());
    }

    @Test
    public void testFishingGearMapperWithGearProblem() {
        FishingGear fishingGear = MapperUtil.getFishingGear();
        FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear);

        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCodeListId());
        assertNull(fishingGearEntity.getGearProblem());

        assertNotNull(fishingGearEntity.getGearCharacteristics());
        fishingGearEntity = fishingGearEntity.getGearCharacteristics().iterator().next().getFishingGear();
        assertEquals(fishingGear.getTypeCode().getValue(), fishingGearEntity.getTypeCode());
        assertEquals(fishingGear.getTypeCode().getListID(), fishingGearEntity.getTypeCodeListId());
        assertEquals(fishingGear.getRoleCodes().get(0).getValue(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCode());
        assertEquals(fishingGear.getRoleCodes().get(0).getListID(), fishingGearEntity.getFishingGearRole().iterator().next().getRoleCodeListId());
    }
}
