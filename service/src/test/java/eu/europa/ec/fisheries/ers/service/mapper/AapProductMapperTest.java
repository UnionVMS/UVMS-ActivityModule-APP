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

import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.AapProductDetailsDTO;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProduct;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by padhyad on 7/27/2016.
 */
public class AapProductMapperTest {

    @Test
    public void testAapProductMapper() {
        AAPProduct aapProduct = MapperUtil.getAapProduct();
        AapProductEntity aapProductEntity = new AapProductEntity();
        AapProductMapper.INSTANCE.mapToAapProductEntity(aapProduct, null, aapProductEntity);

        assertEquals(aapProduct.getPackagingTypeCode().getValue(), aapProductEntity.getPackagingTypeCode());
        assertEquals(aapProduct.getPackagingTypeCode().getListID(), aapProductEntity.getPackagingTypeCodeListId());
        assertNull(aapProductEntity.getAapProcess());
        assertEquals(aapProduct.getPackagingUnitAverageWeightMeasure().getValue().doubleValue(), aapProductEntity.getPackagingUnitAvarageWeight());
        assertEquals(aapProduct.getPackagingUnitAverageWeightMeasure().getUnitCode(), aapProductEntity.getPackagingWeightUnitCode());
        assertEquals(aapProduct.getPackagingUnitAverageWeightMeasure().getValue().intValue(), aapProductEntity.getCalculatedPackagingWeight().intValue());

        assertEquals(aapProduct.getPackagingUnitQuantity().getValue().doubleValue(), aapProductEntity.getPackagingUnitCount());
        assertEquals(aapProduct.getPackagingUnitQuantity().getUnitCode(), aapProductEntity.getPackagingUnitCountCode());
        assertEquals(aapProduct.getPackagingUnitQuantity().getValue().intValue(), aapProductEntity.getCalculatedPackagingUnitCount().intValue());

        assertEquals(aapProduct.getSpeciesCode().getValue(), aapProductEntity.getSpeciesCode());
        assertEquals(aapProduct.getSpeciesCode().getListID(), aapProductEntity.getSpeciesCodeListId());

        assertEquals(aapProduct.getUnitQuantity().getValue().intValue(), aapProductEntity.getUnitQuantity().intValue());
        assertEquals(aapProduct.getUnitQuantity().getUnitCode(), aapProductEntity.getUnitQuantityCode());
        assertEquals(aapProduct.getUnitQuantity().getValue().intValue(), aapProductEntity.getCalculatedUnitQuantity().intValue());

        assertEquals(aapProduct.getWeightMeasure().getValue().intValue(), aapProductEntity.getWeightMeasure().intValue());
        assertEquals(aapProduct.getWeightMeasure().getUnitCode(), aapProductEntity.getWeightMeasureUnitCode());
        assertEquals(aapProduct.getWeightMeasure().getValue().intValue(), aapProductEntity.getCalculatedWeightMeasure().intValue());

        assertEquals(aapProduct.getWeighingMeansCode().getValue(), aapProductEntity.getWeighingMeansCode());
        assertEquals(aapProduct.getWeighingMeansCode().getListID(), aapProductEntity.getWeighingMeansCodeListId());

        assertEquals(aapProduct.getUsageCode().getValue(), aapProductEntity.getUsageCode());
        assertEquals(aapProduct.getUsageCode().getListID(), aapProductEntity.getUsageCodeListId());
    }
}
