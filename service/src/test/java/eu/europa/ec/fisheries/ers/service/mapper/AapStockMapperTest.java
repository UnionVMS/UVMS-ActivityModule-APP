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

import eu.europa.ec.fisheries.ers.fa.entities.AapStockEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.AapProductDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.AapStockDetailsDTO;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.AAPStock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by padhyad on 7/27/2016.
 */
public class AapStockMapperTest {

    @Test
    public void testAapStockMapper() {
        AAPStock aapStock = MapperUtil.getAapStock();
        AapStockEntity aapStockEntity = new AapStockEntity();
        AapStockMapper.INSTANCE.mapToAapStockEntity(aapStock, null, aapStockEntity);

        assertEquals(aapStock.getID().getValue(), aapStockEntity.getStockId());
        assertEquals(aapStock.getID().getSchemeID(), aapStockEntity.getStockSchemeId());
        assertNull(aapStockEntity.getFaCatch());
    }

    @Test
    public void testAapStockDetailsDTOMapper() {
        AAPStock aapStock = MapperUtil.getAapStock();
        AapStockEntity aapStockEntity = new AapStockEntity();
        AapStockMapper.INSTANCE.mapToAapStockEntity(aapStock, null, aapStockEntity);

        AapStockDetailsDTO aapStockDetailsDTO = AapStockMapper.INSTANCE.mapToAapStockDetailsDTO(aapStockEntity);
        assertEquals(aapStockEntity.getStockId(), aapStockDetailsDTO.getStockId());
    }

    @Test
    public void testAapStockDetailsDTOListMapper() {
        AAPStock aapStock = MapperUtil.getAapStock();
        AapStockEntity aapStockEntity = new AapStockEntity();
        AapStockMapper.INSTANCE.mapToAapStockEntity(aapStock, null, aapStockEntity);

        List<AapStockDetailsDTO> aapStockDetailsDTO = AapStockMapper.INSTANCE.mapToAapStockDetailsDTOList(new HashSet<AapStockEntity>(Arrays.asList(aapStockEntity)));
        assertEquals(aapStockEntity.getStockId(), aapStockDetailsDTO.get(0).getStockId());
    }
}


