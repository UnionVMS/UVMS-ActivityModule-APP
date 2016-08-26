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
import static org.junit.Assert.assertNull;

/**
 * Created by padhyad on 8/25/2016.
 */
public class PreferenceConfigMapperTest {

    @Test
    public void testMergeUserPreference() {
        ActivityConfigDTO merged = PreferenceConfigMapper.INSTANCE.mergeUserPreference(getTargetActivityConfigDTO(), getSourceActivityConfigDTO());

        assertEquals(getTargetActivityConfigDTO().getFaReportConfig().getFluxCharacteristics(), merged.getFaReportConfig().getFluxCharacteristics());
        assertEquals(getTargetActivityConfigDTO().getFaReportConfig().getStructuredAddress(), merged.getFaReportConfig().getStructuredAddress());
        assertEquals(getTargetActivityConfigDTO().getFaReportConfig().getAapProcess(), merged.getFaReportConfig().getAapProcess());
        assertEquals(getTargetActivityConfigDTO().getFaReportConfig().getAapProduct(), merged.getFaReportConfig().getAapProduct());
        assertEquals(getTargetActivityConfigDTO().getFaReportConfig().getAapStock(), merged.getFaReportConfig().getAapStock());
        assertEquals(getTargetActivityConfigDTO().getFaReportConfig().getGearProblems(), merged.getFaReportConfig().getGearProblems());
        assertEquals(getTargetActivityConfigDTO().getFaReportConfig().getVessel(), merged.getFaReportConfig().getVessel());
        assertEquals(getTargetActivityConfigDTO().getFaReportConfig().getContactParty(), merged.getFaReportConfig().getContactParty());
        assertEquals(getTargetActivityConfigDTO().getFaReportConfig().getContactPerson(), merged.getFaReportConfig().getContactPerson());

        assertEquals(getSourceActivityConfigDTO().getFaReportConfig().getFaReport(), merged.getFaReportConfig().getFaReport());
        assertEquals(getSourceActivityConfigDTO().getFaReportConfig().getFishingActivity(), merged.getFaReportConfig().getFishingActivity());
        assertEquals(getSourceActivityConfigDTO().getFaReportConfig().getFishingTrip(), merged.getFaReportConfig().getFishingTrip());
        assertEquals(getSourceActivityConfigDTO().getFaReportConfig().getDelimitedPeriod(), merged.getFaReportConfig().getDelimitedPeriod());
        assertEquals(getSourceActivityConfigDTO().getFaReportConfig().getFaCatches(), merged.getFaReportConfig().getFaCatches());
        assertEquals(getSourceActivityConfigDTO().getFaReportConfig().getFishingGears(), merged.getFaReportConfig().getFishingGears());
        assertEquals(getSourceActivityConfigDTO().getFaReportConfig().getGearCharacteristics(), merged.getFaReportConfig().getGearCharacteristics());
        assertEquals(getSourceActivityConfigDTO().getFaReportConfig().getFluxLocations(), merged.getFaReportConfig().getFluxLocations());
    }

    @Test
    public void testResetUserPreference() {
        ActivityConfigDTO merged = PreferenceConfigMapper.INSTANCE.mergeUserPreference(getTargetActivityConfigDTO(), getSourceActivityConfigDTO());
        ActivityConfigDTO updated = PreferenceConfigMapper.INSTANCE.resetUserPreference(merged, getSourceActivityConfigDTO());

        assertEquals(merged.getFaReportConfig().getFluxCharacteristics(), updated.getFaReportConfig().getFluxCharacteristics());
        assertEquals(merged.getFaReportConfig().getStructuredAddress(), updated.getFaReportConfig().getStructuredAddress());
        assertEquals(merged.getFaReportConfig().getAapProcess(), updated.getFaReportConfig().getAapProcess());
        assertEquals(merged.getFaReportConfig().getAapProduct(), updated.getFaReportConfig().getAapProduct());
        assertEquals(merged.getFaReportConfig().getAapStock(), updated.getFaReportConfig().getAapStock());
        assertEquals(merged.getFaReportConfig().getGearProblems(), updated.getFaReportConfig().getGearProblems());
        assertEquals(merged.getFaReportConfig().getVessel(), updated.getFaReportConfig().getVessel());
        assertEquals(merged.getFaReportConfig().getContactParty(), updated.getFaReportConfig().getContactParty());
        assertEquals(merged.getFaReportConfig().getContactPerson(), updated.getFaReportConfig().getContactPerson());

        assertNull(updated.getFaReportConfig().getFaReport());
        assertNull(updated.getFaReportConfig().getFishingActivity());
        assertNull(updated.getFaReportConfig().getFishingTrip());
        assertNull(updated.getFaReportConfig().getDelimitedPeriod());
        assertNull(updated.getFaReportConfig().getFaCatches());
        assertNull(updated.getFaReportConfig().getFishingGears());
        assertNull(updated.getFaReportConfig().getGearCharacteristics());
        assertNull(updated.getFaReportConfig().getFluxLocations());
    }


}
