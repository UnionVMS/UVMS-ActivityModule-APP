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
import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by padhyad on 7/29/2016.
 */
public class FluxCharacteristicsMapperTest {

    @Test
    public void testFluxCharacteristicsMapperWithFishingActivity() {
        FLUXCharacteristic fluxCharacteristic = MapperUtil.getFluxCharacteristics();
        FluxCharacteristicEntity fluxCharacteristicEntity = new FluxCharacteristicEntity();
        FishingActivityEntity fishingActivityEntity = null;
        FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(fluxCharacteristic, fishingActivityEntity, fluxCharacteristicEntity);

        assertEquals(fluxCharacteristic.getTypeCode().getValue(), fluxCharacteristicEntity.getTypeCode());
        assertEquals(fluxCharacteristic.getTypeCode().getListID(), fluxCharacteristicEntity.getTypeCodeListId());
        assertEquals(fluxCharacteristic.getValueMeasure().getValue().intValue(), fluxCharacteristicEntity.getValueMeasure().intValue());
        assertEquals(fluxCharacteristic.getValueMeasure().getUnitCode(), fluxCharacteristicEntity.getValueMeasureUnitCode());
        assertEquals(fluxCharacteristic.getValueMeasure().getValue().intValue(), fluxCharacteristicEntity.getCalculatedValueMeasure().intValue());
        assertEquals(fluxCharacteristic.getValueDateTime().getDateTime().toGregorianCalendar().getTime(), fluxCharacteristicEntity.getValueDateTime());
        assertEquals(fluxCharacteristic.getValueIndicator().getIndicatorString().getValue(), fluxCharacteristicEntity.getValueIndicator());
        assertEquals(fluxCharacteristic.getValueCode().getValue(), fluxCharacteristicEntity.getValueCode());
        assertTrue(fluxCharacteristicEntity.getValueText().startsWith(fluxCharacteristic.getValues().get(0).getValue()));
        assertEquals(fluxCharacteristic.getValueQuantity().getValue().intValue(), fluxCharacteristicEntity.getValueQuantity().intValue());
        assertEquals(fluxCharacteristic.getValueQuantity().getUnitCode(), fluxCharacteristicEntity.getValueQuantityCode());
        assertEquals(fluxCharacteristic.getValueQuantity().getValue().intValue(), fluxCharacteristicEntity.getCalculatedValueQuantity().intValue());
        assertTrue(fluxCharacteristicEntity.getDescription().startsWith(fluxCharacteristic.getDescriptions().get(0).getValue()));
        assertNull(fluxCharacteristicEntity.getFishingActivity());
    }

    @Test
    public void testFluxCharacteristicsMapperWithFACatch() {
        FLUXCharacteristic fluxCharacteristic = MapperUtil.getFluxCharacteristics();
        FluxCharacteristicEntity fluxCharacteristicEntity = new FluxCharacteristicEntity();
        FaCatchEntity faCatchEntity = null;
        FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(fluxCharacteristic, faCatchEntity, fluxCharacteristicEntity);

        assertEquals(fluxCharacteristic.getTypeCode().getValue(), fluxCharacteristicEntity.getTypeCode());
        assertEquals(fluxCharacteristic.getTypeCode().getListID(), fluxCharacteristicEntity.getTypeCodeListId());
        assertEquals(fluxCharacteristic.getValueMeasure().getValue().intValue(), fluxCharacteristicEntity.getValueMeasure().intValue());
        assertEquals(fluxCharacteristic.getValueMeasure().getUnitCode(), fluxCharacteristicEntity.getValueMeasureUnitCode());
        assertEquals(fluxCharacteristic.getValueMeasure().getValue().intValue(), fluxCharacteristicEntity.getCalculatedValueMeasure().intValue());
        assertEquals(fluxCharacteristic.getValueDateTime().getDateTime().toGregorianCalendar().getTime(), fluxCharacteristicEntity.getValueDateTime());
        assertEquals(fluxCharacteristic.getValueIndicator().getIndicatorString().getValue(), fluxCharacteristicEntity.getValueIndicator());
        assertEquals(fluxCharacteristic.getValueCode().getValue(), fluxCharacteristicEntity.getValueCode());
        assertTrue(fluxCharacteristicEntity.getValueText().startsWith(fluxCharacteristic.getValues().get(0).getValue()));
        assertEquals(fluxCharacteristic.getValueQuantity().getValue().intValue(), fluxCharacteristicEntity.getValueQuantity().intValue());
        assertEquals(fluxCharacteristic.getValueQuantity().getUnitCode(), fluxCharacteristicEntity.getValueQuantityCode());
        assertEquals(fluxCharacteristic.getValueQuantity().getValue().intValue(), fluxCharacteristicEntity.getCalculatedValueQuantity().intValue());
        assertTrue(fluxCharacteristicEntity.getDescription().startsWith(fluxCharacteristic.getDescriptions().get(0).getValue()));
        assertNull(fluxCharacteristicEntity.getFaCatch());
    }

    @Test
    public void testFluxCharacteristicsMapperWithFluxLocation() {
        FLUXCharacteristic fluxCharacteristic = MapperUtil.getFluxCharacteristics();
        FluxCharacteristicEntity fluxCharacteristicEntity = new FluxCharacteristicEntity();
        FluxLocationEntity fluxLocationEntity = null;
        FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(fluxCharacteristic, fluxLocationEntity, fluxCharacteristicEntity);

        assertEquals(fluxCharacteristic.getTypeCode().getValue(), fluxCharacteristicEntity.getTypeCode());
        assertEquals(fluxCharacteristic.getTypeCode().getListID(), fluxCharacteristicEntity.getTypeCodeListId());
        assertEquals(fluxCharacteristic.getValueMeasure().getValue().intValue(), fluxCharacteristicEntity.getValueMeasure().intValue());
        assertEquals(fluxCharacteristic.getValueMeasure().getUnitCode(), fluxCharacteristicEntity.getValueMeasureUnitCode());
        assertEquals(fluxCharacteristic.getValueMeasure().getValue().intValue(), fluxCharacteristicEntity.getCalculatedValueMeasure().intValue());
        assertEquals(fluxCharacteristic.getValueDateTime().getDateTime().toGregorianCalendar().getTime(), fluxCharacteristicEntity.getValueDateTime());
        assertEquals(fluxCharacteristic.getValueIndicator().getIndicatorString().getValue(), fluxCharacteristicEntity.getValueIndicator());
        assertEquals(fluxCharacteristic.getValueCode().getValue(), fluxCharacteristicEntity.getValueCode());
        assertTrue(fluxCharacteristicEntity.getValueText().startsWith(fluxCharacteristic.getValues().get(0).getValue()));
        assertEquals(fluxCharacteristic.getValueQuantity().getValue().intValue(), fluxCharacteristicEntity.getValueQuantity().intValue());
        assertEquals(fluxCharacteristic.getValueQuantity().getUnitCode(), fluxCharacteristicEntity.getValueQuantityCode());
        assertEquals(fluxCharacteristic.getValueQuantity().getValue().intValue(), fluxCharacteristicEntity.getCalculatedValueQuantity().intValue());
        assertTrue(fluxCharacteristicEntity.getDescription().startsWith(fluxCharacteristic.getDescriptions().get(0).getValue()));
        assertNull(fluxCharacteristicEntity.getFluxLocation());
    }
}
