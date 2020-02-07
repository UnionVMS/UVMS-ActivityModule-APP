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

import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class VesselTransportMeansMapperTest {

    @Test
    public void testVesselTransportMeansMapper() {
        VesselTransportMeans vesselTransportMeans = MapperUtil.getVesselTransportMeans();

        VesselTransportMeansMapper vesselTransportMeansMapper = Mappers.getMapper(VesselTransportMeansMapper.class);
        VesselTransportMeansEntity vesselTransportMeansEntity = vesselTransportMeansMapper.mapToVesselTransportMeansEntity(vesselTransportMeans);

        assertTrue(vesselTransportMeansEntity.getName().startsWith(vesselTransportMeans.getNames().get(0).getValue()));
        assertEquals(vesselTransportMeans.getRoleCode().getValue(), vesselTransportMeansEntity.getRoleCode());
        assertEquals(vesselTransportMeans.getRoleCode().getListID(), vesselTransportMeansEntity.getRoleCodeListId());
        assertEquals(vesselTransportMeans.getRegistrationVesselCountry().getID().getSchemeID(), vesselTransportMeansEntity.getCountrySchemeId());
        assertEquals(vesselTransportMeans.getRegistrationVesselCountry().getID().getValue(), vesselTransportMeansEntity.getCountry());

        assertNotNull(vesselTransportMeansEntity.getVesselIdentifiers());
        VesselIdentifierEntity vesselIdentifierEntity = vesselTransportMeansEntity.getVesselIdentifiers().iterator().next();
        assertEquals(vesselTransportMeans.getIDS().get(0).getValue(), vesselIdentifierEntity.getVesselIdentifierId());
        assertEquals(vesselTransportMeans.getIDS().get(0).getSchemeID(), vesselIdentifierEntity.getVesselIdentifierSchemeId());

        assertNotNull(vesselTransportMeansEntity.getContactParty());
        vesselTransportMeansEntity = vesselTransportMeansEntity.getContactParty().iterator().next().getVesselTransportMeans();
        assertTrue(vesselTransportMeansEntity.getName().startsWith(vesselTransportMeans.getNames().get(0).getValue()));
        assertEquals(vesselTransportMeans.getRoleCode().getValue(), vesselTransportMeansEntity.getRoleCode());
        assertEquals(vesselTransportMeans.getRoleCode().getListID(), vesselTransportMeansEntity.getRoleCodeListId());

        assertNotNull(vesselTransportMeansEntity.getRegistrationEvent());
        vesselTransportMeansEntity = vesselTransportMeansEntity.getRegistrationEvent().getVesselTransportMeanses();
        assertTrue(vesselTransportMeansEntity.getName().startsWith(vesselTransportMeans.getNames().get(0).getValue()));
        assertEquals(vesselTransportMeans.getRoleCode().getValue(), vesselTransportMeansEntity.getRoleCode());
        assertEquals(vesselTransportMeans.getRoleCode().getListID(), vesselTransportMeansEntity.getRoleCodeListId());
    }
}
