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

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FluxLocationMapperTest {

    @Test
    public void testFluxLocationMapper() {
        FLUXLocation fluxLocation = MapperUtil.getFluxLocation();
        FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation);
        assertEquals(fluxLocation.getTypeCode().getListID(), fluxLocationEntity.getTypeCodeListId());
        assertEquals(fluxLocation.getTypeCode().getValue(), fluxLocationEntity.getTypeCode());
        assertEquals(fluxLocation.getCountryID().getValue(), fluxLocationEntity.getCountryId());
        assertEquals(fluxLocation.getCountryID().getSchemeID(), fluxLocationEntity.getCountryIdSchemeId());
        assertEquals(fluxLocation.getRegionalFisheriesManagementOrganizationCode().getValue(), fluxLocationEntity.getRegionalFisheriesManagementOrganizationCode());
        assertEquals(fluxLocation.getRegionalFisheriesManagementOrganizationCode().getListID(), fluxLocationEntity.getRegionalFisheriesManagementOrganizationCodeListId());
        assertEquals(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate().getLongitudeMeasure().getValue().intValue(), fluxLocationEntity.getLongitude().intValue());
        assertEquals(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate().getLatitudeMeasure().getValue().intValue(), fluxLocationEntity.getLatitude().intValue());
        assertEquals(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate().getAltitudeMeasure().getValue().intValue(), fluxLocationEntity.getAltitude().intValue());
        assertEquals(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate().getSystemID().getValue(), fluxLocationEntity.getSystemId());
        assertEquals(fluxLocation.getID().getValue(), fluxLocationEntity.getFluxLocationIdentifier());
        assertEquals(fluxLocation.getID().getSchemeID(), fluxLocationEntity.getFluxLocationIdentifierSchemeId());
        assertEquals(fluxLocation.getGeopoliticalRegionCode().getValue(), fluxLocationEntity.getGeopoliticalRegionCode());
        assertEquals(fluxLocation.getGeopoliticalRegionCode().getListID(), fluxLocationEntity.getGeopoliticalRegionCodeListId());
        assertTrue(fluxLocationEntity.getName().startsWith(fluxLocation.getNames().get(0).getValue()));
        assertEquals(fluxLocation.getSovereignRightsCountryID().getValue(), fluxLocationEntity.getSovereignRightsCountryCode());
        assertEquals(fluxLocation.getJurisdictionCountryID().getValue(), fluxLocationEntity.getJurisdictionCountryCode());
        assertNull(fluxLocationEntity.getFishingActivity());
    }

    @Test
    public void mapToFluxLocationDTOTest(){
        FluxLocationEntity fluxLocationEntity = getFluxLocationEntityMock();
        FluxLocationDto locationDto = FluxLocationMapper.INSTANCE.mapEntityToFluxLocationDto(fluxLocationEntity);
        assertNotNull(locationDto);
    }

    private FluxLocationEntity getFluxLocationEntityMock() {
        FLUXLocation fluxLocation = MapperUtil.getFluxLocation();
        FluxLocationCatchTypeEnum fluxLocationTypeEnum = FluxLocationCatchTypeEnum.FA_CATCH_SPECIFIED;
        FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation);
        fluxLocationEntity.setFluxLocationType(fluxLocationTypeEnum.getType());
        return fluxLocationEntity;
    }
}
