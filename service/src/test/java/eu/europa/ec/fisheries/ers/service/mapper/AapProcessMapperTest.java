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

import com.google.common.collect.Sets;
import eu.europa.ec.fisheries.ers.fa.entities.AapProcessCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.AapProcessDetailsDTO;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProcess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by padhyad on 7/27/2016.
 */
public class AapProcessMapperTest {

    @Test
    public void testAapProcessMapper() {

        AAPProcess aapProcess = MapperUtil.getAapProcess();
        AapProcessEntity aapProcessEntity = new AapProcessEntity();
        AapProcessMapper.INSTANCE.mapToAapProcessEntity(aapProcess, null, aapProcessEntity);

        AapProcessCodeEntity entity = aapProcessEntity.getAapProcessCode().iterator().next();
        assertEquals(aapProcess.getTypeCodes().get(0).getValue(), entity.getTypeCode());
        assertEquals(aapProcess.getTypeCodes().get(0).getListID(), entity.getTypeCodeListId());
        assertEquals(aapProcess.getConversionFactorNumeric().getValue().intValue(), aapProcessEntity.getConversionFactor().intValue());
        assertNull(aapProcessEntity.getFaCatch());

        assertNotNull(aapProcessEntity.getAapProducts());
        AapProductEntity aapProductEntity = aapProcessEntity.getAapProducts().iterator().next();
        assertNotNull(aapProductEntity);

        aapProductEntity.getAapProcess().getAapProcessCode().iterator().next();
        assertEquals(aapProcess.getTypeCodes().get(0).getValue(), entity.getTypeCode());
    }
}
