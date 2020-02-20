/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

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

import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ProcessingProductsDto;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProcess;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProduct;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Arquillian.class)
public class AapProductMapperTest extends BaseActivityArquillianTest {

    @Inject
    AapProcessMapper aapProcessMapper;
    @Inject
    AapProductMapper aapProductMapper;
    @Inject
    FaCatchMapper faCatchMapper;



    @Test
    public void testMapToProcessingProduct() {

        // Prepare
        FACatch faCatch = MapperUtil.getFaCatch();
        FaCatchEntity faCatchEntity = new FaCatchEntity();
        faCatchMapper.mapToFaCatchEntity(faCatch);

        AAPProcess aapProcess = MapperUtil.getAapProcess();
        AapProcessEntity aapProcessEntity = aapProcessMapper.mapToAapProcessEntity(aapProcess);
        aapProcessEntity.setFaCatch(faCatchEntity);

        AAPProduct aapProduct = MapperUtil.getAapProduct();
        AapProductEntity aapProductEntity = aapProductMapper.mapToAapProductEntity(aapProduct);
        aapProductEntity.setAapProcess(aapProcessEntity);

        // Create Input data
        ProcessingProductsDto processingProductsDto = aapProductMapper.mapToProcessingProduct(aapProductEntity);
        assertEquals(processingProductsDto.getType(), faCatchEntity.getTypeCode());
        assertEquals(processingProductsDto.getGear(), faCatchEntity.getGearTypeCode());
        assertEquals(processingProductsDto.getSpecies(), faCatchEntity.getSpeciesCode());
        assertNull(processingProductsDto.getPreservation());
        assertNull(processingProductsDto.getPresentation());
        assertNull(processingProductsDto.getFreshness());
        assertEquals(processingProductsDto.getConversionFactor(), aapProcessEntity.getConversionFactor());
        assertEquals(processingProductsDto.getWeight(), aapProductEntity.getWeightMeasure());
        assertEquals(processingProductsDto.getQuantity(), aapProductEntity.getUnitQuantity());
        assertEquals(processingProductsDto.getPackageWeight(), aapProductEntity.getPackagingUnitAverageWeight());
        assertEquals(processingProductsDto.getPackageQuantity(), aapProductEntity.getPackagingUnitCount(), 0);
        assertEquals(processingProductsDto.getPackagingType(), aapProductEntity.getPackagingTypeCode());
    }
}
