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
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.AAPProduct;

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
        assertEquals(aapProduct.getPackagingUnitQuantity().getValue().doubleValue(), aapProductEntity.getPackagingUnitCount());
    }

    @Test
    public void testMapToAapProductDetailsDTO() {
        AAPProduct aapProduct = MapperUtil.getAapProduct();
        AapProductEntity aapProductEntity = new AapProductEntity();
        AapProductMapper.INSTANCE.mapToAapProductEntity(aapProduct, null, aapProductEntity);
        AapProductDetailsDTO aapProductDetailsDTO = AapProductMapper.INSTANCE.mapToAapProductDetailsDTO(aapProductEntity);

        assertEquals(aapProductEntity.getPackagingTypeCode(), aapProductDetailsDTO.getPackagingTypeCode());
        assertEquals(aapProductEntity.getPackagingUnitAvarageWeight(), aapProductDetailsDTO.getPackagingUnitAvarageWeight());
        assertEquals(aapProductEntity.getPackagingUnitCount(), aapProductDetailsDTO.getPackagingUnitCount());
    }

    @Test
    public void testMapToAapProductDetailsDTOList() {
        AAPProduct aapProduct = MapperUtil.getAapProduct();
        AapProductEntity aapProductEntity = new AapProductEntity();
        AapProductMapper.INSTANCE.mapToAapProductEntity(aapProduct, null, aapProductEntity);
        List<AapProductDetailsDTO> aapProductDetailsDTO = AapProductMapper.INSTANCE.mapToAapProductDetailsDTOList(new HashSet<AapProductEntity>(Arrays.asList(aapProductEntity)));

        assertEquals(aapProductEntity.getPackagingTypeCode(), aapProductDetailsDTO.get(0).getPackagingTypeCode());
        assertEquals(aapProductEntity.getPackagingUnitAvarageWeight(), aapProductDetailsDTO.get(0).getPackagingUnitAvarageWeight());
        assertEquals(aapProductEntity.getPackagingUnitCount(), aapProductDetailsDTO.get(0).getPackagingUnitCount());
    }
}
