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

import eu.europa.ec.fisheries.uvms.activity.fa.entities.LocationEntity;
import eu.europa.ec.fisheries.uvms.activity.rest.BaseActivityArquillianTest;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class FluxLocationMapperIntegrationTest extends BaseActivityArquillianTest {

    @Inject
    FluxLocationMapper locationMapper;

    @Test
    public void fluxLocationMapper() {
        FLUXLocation fluxLocation = MapperUtil.getFluxLocation();
        LocationEntity locationEntity = locationMapper.mapToLocationEntity(fluxLocation);
        assertEquals(fluxLocation.getTypeCode().getListID(), locationEntity.getTypeCodeListId());
        assertEquals(fluxLocation.getTypeCode().getValue(), locationEntity.getTypeCode().name());
        assertEquals(fluxLocation.getCountryID().getValue(), locationEntity.getCountryId());
        assertEquals(fluxLocation.getCountryID().getSchemeID(), locationEntity.getCountryIdSchemeId());
        assertEquals(fluxLocation.getRegionalFisheriesManagementOrganizationCode().getValue(), locationEntity.getRegionalFisheriesManagementOrganizationCode());
        assertEquals(fluxLocation.getRegionalFisheriesManagementOrganizationCode().getListID(), locationEntity.getRegionalFisheriesManagementOrganizationCodeListId());
        assertEquals(fluxLocation.getID().getValue(), locationEntity.getFluxLocationIdentifier());
        assertEquals(fluxLocation.getID().getSchemeID(), locationEntity.getFluxLocationIdentifierSchemeId());
        assertTrue(locationEntity.getName().startsWith(fluxLocation.getNames().get(0).getValue()));
    }
}
