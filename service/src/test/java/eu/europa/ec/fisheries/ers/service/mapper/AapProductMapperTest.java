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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.ProcessingProductsDto;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProcess;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProduct;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;

/**
 * Created by padhyad on 7/27/2016.
 */
public class AapProductMapperTest {

    @Test
    public void testAapProductMapper() {
        AAPProduct aapProduct = MapperUtil.getAapProduct();
        AapProductEntity aapProductEntity = AapProductMapper.INSTANCE.mapToAapProductEntity(aapProduct);

        assertEquals(aapProduct.getPackagingTypeCode().getValue(), aapProductEntity.getPackagingTypeCode());
        assertEquals(aapProduct.getPackagingTypeCode().getListID(), aapProductEntity.getPackagingTypeCodeListId());
        assertNull(aapProductEntity.getAapProcess());
        assertEquals(aapProduct.getPackagingUnitAverageWeightMeasure().getValue().doubleValue(), aapProductEntity.getPackagingUnitAvarageWeight());
        assertEquals(aapProduct.getPackagingUnitAverageWeightMeasure().getUnitCode(), aapProductEntity.getPackagingWeightUnitCode());
        assertEquals(aapProduct.getPackagingUnitAverageWeightMeasure().getValue().intValue(), aapProductEntity.getCalculatedPackagingWeight().intValue());

        assertEquals(aapProduct.getPackagingUnitQuantity().getValue().doubleValue(), aapProductEntity.getPackagingUnitCount());
        assertEquals(aapProduct.getPackagingUnitQuantity().getUnitCode(), aapProductEntity.getPackagingUnitCountCode());

        assertEquals(aapProduct.getSpeciesCode().getValue(), aapProductEntity.getSpeciesCode());
        assertEquals(aapProduct.getSpeciesCode().getListID(), aapProductEntity.getSpeciesCodeListId());

        assertEquals(aapProduct.getUnitQuantity().getValue().intValue(), aapProductEntity.getUnitQuantity().intValue());
        assertEquals(aapProduct.getUnitQuantity().getUnitCode(), aapProductEntity.getUnitQuantityCode());

        assertEquals(aapProduct.getWeightMeasure().getValue().intValue(), aapProductEntity.getWeightMeasure().intValue());
        assertEquals(aapProduct.getWeightMeasure().getUnitCode(), aapProductEntity.getWeightMeasureUnitCode());
        assertEquals(aapProduct.getWeightMeasure().getValue().intValue(), aapProductEntity.getCalculatedWeightMeasure().intValue());

        assertEquals(aapProduct.getWeighingMeansCode().getValue(), aapProductEntity.getWeighingMeansCode());
        assertEquals(aapProduct.getWeighingMeansCode().getListID(), aapProductEntity.getWeighingMeansCodeListId());

        assertEquals(aapProduct.getUsageCode().getValue(), aapProductEntity.getUsageCode());
        assertEquals(aapProduct.getUsageCode().getListID(), aapProductEntity.getUsageCodeListId());
    }

    @Test
    public void testMapToProcessingProduct() {

        // Prepare
        FACatch faCatch = MapperUtil.getFaCatch();
        FaCatchEntity faCatchEntity = new FaCatchEntity();
        FaCatchMapper.INSTANCE.mapToFaCatchEntity(faCatch);

        AAPProcess aapProcess = MapperUtil.getAapProcess();
        AapProcessEntity aapProcessEntity = AapProcessMapper.INSTANCE.mapToAapProcessEntity(aapProcess);
        aapProcessEntity.setFaCatch(faCatchEntity);

        AAPProduct aapProduct = MapperUtil.getAapProduct();
        AapProductEntity aapProductEntity = AapProductMapper.INSTANCE.mapToAapProductEntity(aapProduct);
        aapProductEntity.setAapProcess(aapProcessEntity);

        // Create Input data
        ProcessingProductsDto processingProductsDto = AapProductMapper.INSTANCE.mapToProcessingProduct(aapProductEntity);
        assertEquals(processingProductsDto.getType(), faCatchEntity.getTypeCode());
        assertEquals(processingProductsDto.getGear(), faCatchEntity.getGearTypeCode());
        assertEquals(processingProductsDto.getSpecies(), faCatchEntity.getSpeciesCode());
        assertNull(processingProductsDto.getPreservation());
        assertNull(processingProductsDto.getPresentation());
        assertNull(processingProductsDto.getFreshness());
        assertEquals(processingProductsDto.getConversionFactor(), aapProcessEntity.getConversionFactor().doubleValue());
        assertEquals(processingProductsDto.getWeight(), aapProductEntity.getCalculatedWeightMeasure());
        assertEquals(processingProductsDto.getQuantity(), aapProductEntity.getUnitQuantity());
        assertEquals(processingProductsDto.getPackageWeight(), aapProductEntity.getPackagingUnitAvarageWeight());
        assertEquals(processingProductsDto.getPackageQuantity().doubleValue(), aapProductEntity.getPackagingUnitCount().doubleValue());
        assertEquals(processingProductsDto.getPackagingType(), aapProductEntity.getPackagingTypeCode());
    }
}
