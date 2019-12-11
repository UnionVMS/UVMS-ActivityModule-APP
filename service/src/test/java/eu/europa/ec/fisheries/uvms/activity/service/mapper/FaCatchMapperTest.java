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

import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapStockEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FaCatchMapperTest {

    @Test
    public void testFaCatchMapper() {
        FACatch faCatch = MapperUtil.getFaCatch();
        FaCatchEntity faCatchEntity = FaCatchMapper.INSTANCE.mapToFaCatchEntity(faCatch);

        assertFaCatchFields(faCatch, faCatchEntity);
        assertNull(faCatchEntity.getFishingActivity());

        assertNotNull(faCatchEntity.getAapStocks());
        AapStockEntity aapStockEntity = faCatchEntity.getAapStocks().iterator().next();
        assertNotNull(aapStockEntity);
        assertFaCatchFields(faCatch, aapStockEntity.getFaCatch());

        assertNotNull(faCatchEntity.getFishingGears());
        FishingGearEntity fishingGearEntity = faCatchEntity.getFishingGears().iterator().next();
        assertNotNull(fishingGearEntity);
        assertFaCatchFields(faCatch, fishingGearEntity.getFaCatch());

        assertNotNull(faCatchEntity.getFishingTrip());
        FishingTripEntity fishingTripEntity = faCatchEntity.getFishingTrip();
        assertNotNull(fishingTripEntity);
        // TODO: Makes no sense?
        //assertFaCatchFields(faCatch, fishingTripEntity.getFaCatch());

        assertNotNull(faCatchEntity.getFluxCharacteristics());
        FluxCharacteristicEntity fluxCharacteristicEntity = faCatchEntity.getFluxCharacteristics().iterator().next();
        assertNotNull(fluxCharacteristicEntity);
        assertFaCatchFields(faCatch, fluxCharacteristicEntity.getFaCatch());

        assertNotNull(faCatchEntity.getFluxLocations());
        FluxLocationEntity fluxLocationEntity = faCatchEntity.getFluxLocations().iterator().next();
        assertNotNull(fluxLocationEntity);
        assertFaCatchFields(faCatch, fluxLocationEntity.getFaCatch());
    }

    private void assertFaCatchFields(FACatch faCatch, FaCatchEntity faCatchEntity) {
        assertEquals(faCatch.getTypeCode().getValue(), faCatchEntity.getTypeCode());
        assertEquals(faCatch.getTypeCode().getListID(), faCatchEntity.getTypeCodeListId());
        assertEquals(faCatch.getSpeciesCode().getValue(), faCatchEntity.getSpeciesCode());
        assertEquals(faCatch.getSpeciesCode().getListID(), faCatchEntity.getSpeciesCodeListid());
        assertEquals(faCatch.getWeighingMeansCode().getValue(), faCatchEntity.getWeighingMeansCode());
        assertEquals(faCatch.getWeighingMeansCode().getListID(), faCatchEntity.getWeighingMeansCodeListId());
        assertEquals(faCatch.getWeightMeasure().getValue().intValue(), faCatchEntity.getWeightMeasure().intValue());
        assertEquals(faCatch.getWeightMeasure().getUnitCode(), faCatchEntity.getWeightMeasureUnitCode());
        assertEquals(faCatch.getUnitQuantity().getValue().intValue(), faCatchEntity.getUnitQuantity().intValue());
        assertEquals(faCatch.getUnitQuantity().getUnitCode(), faCatchEntity.getUnitQuantityCode());
        assertEquals(faCatch.getUsageCode().getValue(), faCatchEntity.getUsageCode());
        assertEquals(faCatch.getUsageCode().getListID(), faCatchEntity.getUsageCodeListId());
    }
}
