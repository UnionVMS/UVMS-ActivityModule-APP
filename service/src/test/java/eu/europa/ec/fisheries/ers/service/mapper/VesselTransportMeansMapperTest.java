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
import static org.junit.Assert.assertTrue;

import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;

public class VesselTransportMeansMapperTest {

    @Test
    public void testVesselTransportMeansMapper() {
        VesselTransportMeans vesselTransportMeans = MapperUtil.getVesselTransportMeans();

        VesselTransportMeansEntity vesselTransportMeansEntity =VesselTransportMeansMapper.INSTANCE.mapToVesselTransportMeansEntity(vesselTransportMeans);

        assertEquals(vesselTransportMeans.getGrantedFLAPDocuments().get(0).getID().getValue(), vesselTransportMeansEntity.getFlapDocuments().iterator().next().getFlapDocumentId());
        assertEquals(vesselTransportMeans.getGrantedFLAPDocuments().get(0).getID().getSchemeID(), vesselTransportMeansEntity.getFlapDocuments().iterator().next().getFlapDocumentSchemeId());
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
        assertEquals(vesselTransportMeans.getGrantedFLAPDocuments().get(0).getID().getValue(), vesselTransportMeansEntity.getFlapDocuments().iterator().next().getFlapDocumentId());
        assertEquals(vesselTransportMeans.getGrantedFLAPDocuments().get(0).getID().getSchemeID(), vesselTransportMeansEntity.getFlapDocuments().iterator().next().getFlapDocumentSchemeId());
        assertTrue(vesselTransportMeansEntity.getName().startsWith(vesselTransportMeans.getNames().get(0).getValue()));
        assertEquals(vesselTransportMeans.getRoleCode().getValue(), vesselTransportMeansEntity.getRoleCode());
        assertEquals(vesselTransportMeans.getRoleCode().getListID(), vesselTransportMeansEntity.getRoleCodeListId());

        assertNotNull(vesselTransportMeansEntity.getRegistrationEvent());
        vesselTransportMeansEntity = vesselTransportMeansEntity.getRegistrationEvent().getVesselTransportMeanses();
        assertEquals(vesselTransportMeans.getGrantedFLAPDocuments().get(0).getID().getValue(), vesselTransportMeansEntity.getFlapDocuments().iterator().next().getFlapDocumentId());
        assertEquals(vesselTransportMeans.getGrantedFLAPDocuments().get(0).getID().getSchemeID(), vesselTransportMeansEntity.getFlapDocuments().iterator().next().getFlapDocumentSchemeId());
        assertTrue(vesselTransportMeansEntity.getName().startsWith(vesselTransportMeans.getNames().get(0).getValue()));
        assertEquals(vesselTransportMeans.getRoleCode().getValue(), vesselTransportMeansEntity.getRoleCode());
        assertEquals(vesselTransportMeans.getRoleCode().getListID(), vesselTransportMeansEntity.getRoleCodeListId());
    }
}
