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

import eu.europa.ec.fisheries.ers.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearCharacteristic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by padhyad on 7/29/2016.
 */
public class GearCharacteristicsMapperTest {

    @Test
    public void testGearCharacteristicsMapper() {
        GearCharacteristic gearCharacteristic = MapperUtil.getGearCharacteristics();
        GearCharacteristicEntity gearCharacteristicEntity = new GearCharacteristicEntity();
        GearCharacteristicsMapper.INSTANCE.mapToGearCharacteristicEntity(gearCharacteristic, null, gearCharacteristicEntity);

        assertEquals(gearCharacteristic.getTypeCode().getValue(), gearCharacteristicEntity.getTypeCode());
        assertEquals(gearCharacteristic.getTypeCode().getListID(), gearCharacteristicEntity.getTypeCodeListId());
        assertTrue(gearCharacteristicEntity.getDescription().startsWith(gearCharacteristic.getDescriptions().get(0).getValue()));
        assertEquals(gearCharacteristic.getValueMeasure().getValue().intValue(), gearCharacteristicEntity.getValueMeasure().intValue());
        assertEquals(gearCharacteristic.getValueMeasure().getUnitCode(), gearCharacteristicEntity.getValueMeasureUnitCode());
        assertEquals(gearCharacteristic.getValueMeasure().getValue().intValue(), gearCharacteristicEntity.getCalculatedValueMeasure().intValue());
        assertEquals(gearCharacteristic.getValueDateTime().getDateTime().toGregorianCalendar().getTime(), gearCharacteristicEntity.getValueDateTime());
        assertEquals(gearCharacteristic.getValueIndicator().getIndicatorString().getValue(), gearCharacteristicEntity.getValueIndicator());
        assertEquals(gearCharacteristic.getValueCode().getValue(), gearCharacteristicEntity.getValueCode());
        assertTrue(gearCharacteristicEntity.getValueText().startsWith(gearCharacteristic.getValue().getValue()));
        assertEquals(gearCharacteristic.getValueQuantity().getValue().intValue(), gearCharacteristicEntity.getValueQuantity().intValue());
        assertEquals(gearCharacteristic.getValueQuantity().getUnitCode(), gearCharacteristicEntity.getValueQuantityCode());
        assertEquals(gearCharacteristic.getValueQuantity().getValue().intValue(), gearCharacteristicEntity.getCalculatedValueQuantity().intValue());
        assertNull(gearCharacteristicEntity.getFishingGear());
    }
}
