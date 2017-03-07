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
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by padhyad on 8/1/2016.
 */
public class FaCatchMapperTest {

    @Test
    public void testFaCatchMapper() {
        FACatch faCatch = MapperUtil.getFaCatch();
        FaCatchEntity faCatchEntity = new FaCatchEntity();
        FaCatchMapper.INSTANCE.mapToFaCatchEntity(faCatch, null, faCatchEntity);

        assertFaCatchFields(faCatch, faCatchEntity);
        assertNull(faCatchEntity.getFishingActivity());

        assertNotNull(faCatchEntity.getAapProcesses());
        AapProcessEntity aapProcessEntity = faCatchEntity.getAapProcesses().iterator().next();
        assertNotNull(aapProcessEntity);
        assertFaCatchFields(faCatch, aapProcessEntity.getFaCatch());

        assertNotNull(faCatchEntity.getAapStocks());
        AapStockEntity aapStockEntity = faCatchEntity.getAapStocks().iterator().next();
        assertNotNull(aapStockEntity);
        assertFaCatchFields(faCatch, aapStockEntity.getFaCatch());

        assertNotNull(faCatchEntity.getSizeDistribution());
        assertFaCatchFields(faCatch, faCatchEntity.getSizeDistribution().getFaCatch());

        assertNotNull(faCatchEntity.getFishingGears());
        FishingGearEntity fishingGearEntity = faCatchEntity.getFishingGears().iterator().next();
        assertNotNull(fishingGearEntity);
        assertFaCatchFields(faCatch, fishingGearEntity.getFaCatch());

        assertNotNull(faCatchEntity.getFishingTrips());
        FishingTripEntity fishingTripEntity = faCatchEntity.getFishingTrips().iterator().next();
        assertNotNull(fishingTripEntity);
        assertFaCatchFields(faCatch, fishingTripEntity.getFaCatch());

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
        assertEquals(faCatch.getWeightMeasure().getValue().intValue(), faCatchEntity.getCalculatedWeightMeasure().intValue());
        assertEquals(faCatch.getWeightMeasure().getUnitCode(), faCatchEntity.getWeightMeasureUnitCode());
        assertEquals(faCatch.getUnitQuantity().getValue().intValue(), faCatchEntity.getUnitQuantity().intValue());
        assertEquals(faCatch.getUnitQuantity().getUnitCode(), faCatchEntity.getUnitQuantityCode());
        assertEquals(faCatch.getUnitQuantity().getValue().intValue(), faCatchEntity.getCalculatedUnitQuantity().intValue());
        assertEquals(faCatch.getUsageCode().getValue(), faCatchEntity.getUsageCode());
        assertEquals(faCatch.getUsageCode().getListID(), faCatchEntity.getUsageCodeListId());
    }
}
