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

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FishingTripDetailsDTO;
import org.junit.Ignore;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingTrip;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by padhyad on 7/29/2016.
 */
public class FishingTripMapperTest {

    @Test
    public void getFishingTripWithFishingActivity() {
        FishingTrip fishingTrip = MapperUtil.getFishingTrip();
        FishingTripEntity fishingTripEntity = new FishingTripEntity();
        FishingActivityEntity fishingActivityEntity = null;
        FishingTripMapper.INSTANCE.mapToFishingTripEntity(fishingTrip, fishingActivityEntity, fishingTripEntity);

        assertEquals(fishingTrip.getTypeCode().getValue(), fishingTripEntity.getTypeCode());
        assertEquals(fishingTrip.getTypeCode().getListID(), fishingTripEntity.getTypeCodeListId());
        assertNull(fishingTripEntity.getFishingActivity());
        assertNotNull(fishingTripEntity.getDelimitedPeriods());

        assertNotNull(fishingTripEntity.getFishingTripIdentifiers());
        FishingTripIdentifierEntity identifierEntity = fishingTripEntity.getFishingTripIdentifiers().iterator().next();
        assertNotNull(identifierEntity);
        assertEquals(fishingTrip.getIDS().get(0).getValue(), identifierEntity.getTripId());
        assertEquals(fishingTrip.getIDS().get(0).getSchemeID(), identifierEntity.getTripSchemeId());

        DelimitedPeriodEntity delimitedPeriodEntity = fishingTripEntity.getDelimitedPeriods().iterator().next();
        assertNotNull(delimitedPeriodEntity);
        assertEquals(fishingTrip.getTypeCode().getValue(), delimitedPeriodEntity.getFishingTrip().getTypeCode());
        assertEquals(fishingTrip.getTypeCode().getListID(), delimitedPeriodEntity.getFishingTrip().getTypeCodeListId());
    }

    @Test
    public void getFishingTripWithFACatch() {
        FishingTrip fishingTrip = MapperUtil.getFishingTrip();
        FishingTripEntity fishingTripEntity = new FishingTripEntity();
        FaCatchEntity faCatchEntity = null;
        FishingTripMapper.INSTANCE.mapToFishingTripEntity(fishingTrip, faCatchEntity, fishingTripEntity);

        assertEquals(fishingTrip.getTypeCode().getValue(), fishingTripEntity.getTypeCode());
        assertEquals(fishingTrip.getTypeCode().getListID(), fishingTripEntity.getTypeCodeListId());
        assertNull(fishingTripEntity.getFaCatch());
        assertNotNull(fishingTripEntity.getDelimitedPeriods());

        assertNotNull(fishingTripEntity.getFishingTripIdentifiers());
        FishingTripIdentifierEntity identifierEntity = fishingTripEntity.getFishingTripIdentifiers().iterator().next();
        assertNotNull(identifierEntity);
        assertEquals(fishingTrip.getIDS().get(0).getValue(), identifierEntity.getTripId());
        assertEquals(fishingTrip.getIDS().get(0).getSchemeID(), identifierEntity.getTripSchemeId());

        DelimitedPeriodEntity delimitedPeriodEntity = fishingTripEntity.getDelimitedPeriods().iterator().next();
        assertNotNull(delimitedPeriodEntity);
        assertEquals(fishingTrip.getTypeCode().getValue(), delimitedPeriodEntity.getFishingTrip().getTypeCode());
        assertEquals(fishingTrip.getTypeCode().getListID(), delimitedPeriodEntity.getFishingTrip().getTypeCodeListId());
    }
}
