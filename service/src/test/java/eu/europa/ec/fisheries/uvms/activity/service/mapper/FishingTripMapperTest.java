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

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;

import static org.junit.Assert.*;

/**
 * Created by padhyad on 7/29/2016.
 */
public class FishingTripMapperTest {

    @Test
    public void getFishingTripWithFishingActivity() {
        FishingTrip fishingTrip = MapperUtil.getFishingTrip();
        FishingTripEntity fishingTripEntity = FishingTripMapper.INSTANCE.mapToFishingTripEntity(fishingTrip);

        assertEquals(fishingTrip.getTypeCode().getValue(), fishingTripEntity.getTypeCode());
        assertEquals(fishingTrip.getTypeCode().getListID(), fishingTripEntity.getTypeCodeListId());
        assertNotNull(fishingTripEntity.getFishingActivities());
        assertNotNull(fishingTripEntity.getDelimitedPeriods());

        assertNotNull(fishingTripEntity.getFishingTripIdentifier());

    }

    @Test
    public void getFishingTripWithFACatch() {
        FishingTrip fishingTrip = MapperUtil.getFishingTrip();
        FishingTripEntity fishingTripEntity = FishingTripMapper.INSTANCE.mapToFishingTripEntity(fishingTrip);

        assertEquals(fishingTrip.getTypeCode().getValue(), fishingTripEntity.getTypeCode());
        assertEquals(fishingTrip.getTypeCode().getListID(), fishingTripEntity.getTypeCodeListId());
        assertTrue(fishingTripEntity.getCatchEntities().size() == 0);
        assertNotNull(fishingTripEntity.getDelimitedPeriods());

        assertNotNull(fishingTripEntity.getFishingTripIdentifier());

    }
}
