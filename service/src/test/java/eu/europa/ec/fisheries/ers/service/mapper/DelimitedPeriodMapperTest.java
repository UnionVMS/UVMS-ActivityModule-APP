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
import static org.junit.Assert.assertNull;

import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;

/**
 * Created by padhyad on 7/28/2016.
 */
public class DelimitedPeriodMapperTest {

    @Test
    public void testDelimitedPeriodMapperForFishingActivity() {
        DelimitedPeriod delimitedPeriod = MapperUtil.getDelimitedPeriod();
        DelimitedPeriodEntity delimitedPeriodEntity = DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodEntity(delimitedPeriod);

        assertEquals(delimitedPeriod.getStartDateTime().getDateTime().toGregorianCalendar().getTime(), delimitedPeriodEntity.getStartDate());
        assertEquals(delimitedPeriod.getEndDateTime().getDateTime().toGregorianCalendar().getTime(), delimitedPeriodEntity.getEndDate());
        assertEquals(delimitedPeriod.getDurationMeasure().getValue().intValue(), delimitedPeriodEntity.getDuration().intValue());
        assertEquals(delimitedPeriod.getDurationMeasure().getUnitCode(), delimitedPeriodEntity.getDurationUnitCode());
        assertEquals(delimitedPeriod.getDurationMeasure().getValue().intValue(), delimitedPeriodEntity.getCalculatedDuration().intValue());
        assertNull(delimitedPeriodEntity.getFishingActivity());
    }

    @Test
    public void testDelimitedPeriodMapperForFishingTrip() {
        DelimitedPeriod delimitedPeriod = MapperUtil.getDelimitedPeriod();
        DelimitedPeriodEntity delimitedPeriodEntity = new DelimitedPeriodEntity();
        FishingTripEntity fishingTrip = null;
        DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodEntity(delimitedPeriod, fishingTrip, delimitedPeriodEntity);

        assertEquals(delimitedPeriod.getStartDateTime().getDateTime().toGregorianCalendar().getTime(), delimitedPeriodEntity.getStartDate());
        assertEquals(delimitedPeriod.getEndDateTime().getDateTime().toGregorianCalendar().getTime(), delimitedPeriodEntity.getEndDate());
        assertEquals(delimitedPeriod.getDurationMeasure().getValue().intValue(), delimitedPeriodEntity.getDuration().intValue());
        assertEquals(delimitedPeriod.getDurationMeasure().getUnitCode(), delimitedPeriodEntity.getDurationUnitCode());
        assertEquals(delimitedPeriod.getDurationMeasure().getValue().intValue(), delimitedPeriodEntity.getCalculatedDuration().intValue());
        assertNull(delimitedPeriodEntity.getFishingTrip());
    }
}
