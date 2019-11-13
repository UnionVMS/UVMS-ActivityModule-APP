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
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPStock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AapStockMapperTest {

    @Test
    public void testAapStockMapper() {
        AAPStock aapStock = MapperUtil.getAapStock();
        AapStockEntity aapStockEntity = AapStockMapper.INSTANCE.mapToAapStockEntity(aapStock);

        assertEquals(aapStock.getID().getValue(), aapStockEntity.getStockId());
        assertEquals(aapStock.getID().getSchemeID(), aapStockEntity.getStockSchemeId());
        assertNull(aapStockEntity.getFaCatch());
    }
}


