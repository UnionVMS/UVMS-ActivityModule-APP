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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearProblem;

/**
 * Created by padhyad on 7/29/2016.
 */
public class GearProblemMapperTest {

    @Test
    public void testGearProblemMapper() {
        GearProblem gearProblem = MapperUtil.getGearProblem();
        GearProblemEntity gearProblemEntity = GearProblemMapper.INSTANCE.mapToGearProblemEntity(gearProblem);

        assertEquals(gearProblem.getTypeCode().getValue(), gearProblemEntity.getTypeCode());
        assertEquals(gearProblem.getTypeCode().getListID(), gearProblemEntity.getTypeCodeListId());
        assertEquals(gearProblem.getAffectedQuantity().getValue().intValue(), gearProblemEntity.getAffectedQuantity());
        assertEquals(gearProblem.getRecoveryMeasureCodes().get(0).getValue(), gearProblemEntity.getGearProblemRecovery().iterator().next().getRecoveryMeasureCode());
        assertEquals(gearProblem.getRecoveryMeasureCodes().get(0).getListID(), gearProblemEntity.getGearProblemRecovery().iterator().next().getRecoveryMeasureCodeListId());
        assertNull(gearProblemEntity.getFishingActivity());

        assertNotNull(gearProblemEntity.getFishingGears());
        FishingGearEntity fishingGearEntity = gearProblemEntity.getFishingGears().iterator().next();
        assertEquals(gearProblem.getTypeCode().getValue(), fishingGearEntity.getGearProblem().getTypeCode());
        assertEquals(gearProblem.getTypeCode().getListID(), fishingGearEntity.getGearProblem().getTypeCodeListId());
        assertEquals(gearProblem.getAffectedQuantity().getValue().intValue(), fishingGearEntity.getGearProblem().getAffectedQuantity());
        assertEquals(gearProblem.getRecoveryMeasureCodes().get(0).getValue(), fishingGearEntity.getGearProblem().getGearProblemRecovery().iterator().next().getRecoveryMeasureCode());
        assertEquals(gearProblem.getRecoveryMeasureCodes().get(0).getListID(), fishingGearEntity.getGearProblem().getGearProblemRecovery().iterator().next().getRecoveryMeasureCodeListId());
    }
}
