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

import eu.europa.ec.fisheries.uvms.activity.model.dto.config.ActivityConfigDTO;
import org.junit.Test;

import static eu.europa.ec.fisheries.ers.service.util.MapperUtil.getSourceActivityConfigDTO;
import static eu.europa.ec.fisheries.ers.service.util.MapperUtil.getTargetActivityConfigDTO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.runners.model.MultipleFailureException.assertEmpty;

/**
 * Created by padhyad on 8/25/2016.
 */
public class PreferenceConfigMapperTest {

    @Test
    public void testMergeUserPreference() {
        ActivityConfigDTO merged = PreferenceConfigMapper.INSTANCE.mergeUserPreference(getTargetActivityConfigDTO(), getSourceActivityConfigDTO());
        assertEquals(getSourceActivityConfigDTO().getFishingActivityConfig().getSummaryReport(), merged.getFishingActivityConfig().getSummaryReport());
    }

    @Test
    public void testResetUserPreference() {
        ActivityConfigDTO updated = PreferenceConfigMapper.INSTANCE.resetUserPreference(getTargetActivityConfigDTO(), getSourceActivityConfigDTO());
        assertNotNull(updated.getFishingActivityConfig());
        assertNull(updated.getFishingActivityConfig().getSummaryReport());
    }
}
