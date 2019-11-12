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

package eu.europa.ec.fisheries.ers.activity.service.mapper;

import eu.europa.ec.fisheries.ers.activity.fa.entities.VesselStorageCharacteristicsEntity;
import eu.europa.ec.fisheries.ers.activity.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselStorageCharacteristic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VesselStorageCharacteristicsMapperTest {

    @Test
    public void testDestVesselStorageCharacteristicsMapper() {
        VesselStorageCharacteristic model = MapperUtil.getVesselStorageCharacteristic();
        VesselStorageCharacteristicsEntity vesselStorageCharacteristicsEntity = VesselStorageCharacteristicsMapper.INSTANCE.mapToDestVesselStorageCharEntity(model);

        assertEquals(model.getID().getValue(), vesselStorageCharacteristicsEntity.getVesselId());
        assertEquals(model.getID().getSchemeID(), vesselStorageCharacteristicsEntity.getVesselSchemaId());
        assertEquals(model.getTypeCodes().get(0).getValue(), vesselStorageCharacteristicsEntity.getVesselStorageCharCode().iterator().next().getVesselTypeCode());
        assertEquals(model.getTypeCodes().get(0).getListID(), vesselStorageCharacteristicsEntity.getVesselStorageCharCode().iterator().next().getVesselTypeCodeListId());
        assertNull(vesselStorageCharacteristicsEntity.getFishingActivitiesForDestVesselCharId());
    }

    @Test
    public void testSourceVesselStorageCharacteristicsMapper() {
        VesselStorageCharacteristic vesselStorageCharacteristic = MapperUtil.getVesselStorageCharacteristic();
        VesselStorageCharacteristicsEntity vesselStorageCharacteristicsEntity = VesselStorageCharacteristicsMapper.INSTANCE.mapToDestVesselStorageCharEntity(vesselStorageCharacteristic);

        assertEquals(vesselStorageCharacteristic.getID().getValue(), vesselStorageCharacteristicsEntity.getVesselId());
        assertEquals(vesselStorageCharacteristic.getID().getSchemeID(), vesselStorageCharacteristicsEntity.getVesselSchemaId());
        assertEquals(vesselStorageCharacteristic.getTypeCodes().get(0).getValue(), vesselStorageCharacteristicsEntity.getVesselStorageCharCode().iterator().next().getVesselTypeCode());
        assertEquals(vesselStorageCharacteristic.getTypeCodes().get(0).getListID(), vesselStorageCharacteristicsEntity.getVesselStorageCharCode().iterator().next().getVesselTypeCodeListId());
        assertNull(vesselStorageCharacteristicsEntity.getFishingActivitiesForSourceVesselCharId());
    }
}
