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

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationTypeEnum;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXLocation;

import static org.junit.Assert.*;

/**
 * Created by padhyad on 7/29/2016.
 */
public class FluxLocationMapperTest {

    @Test
    public void testFluxLocationMapperWithFishingActivity() {
        FLUXLocation fluxLocation = MapperUtil.getFluxLocation();
        FluxLocationEntity fluxLocationEntity = new FluxLocationEntity();
        FluxLocationTypeEnum fluxLocationTypeEnum = FluxLocationTypeEnum.FA_RELATED;
        FishingActivityEntity fishingActivityEntity = null;
        FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation, fluxLocationTypeEnum, fishingActivityEntity, fluxLocationEntity);

        assertFluxLocationEntityFields(fluxLocation, fluxLocationEntity, fluxLocationTypeEnum);
        assertNull(fluxLocationEntity.getFishingActivity());

        assertNotNull(fluxLocationEntity.getStructuredAddresses());
        StructuredAddressEntity structuredAddressEntity = fluxLocationEntity.getStructuredAddresses().iterator().next();
        assertNotNull(structuredAddressEntity);
        assertFluxLocationEntityFields(fluxLocation, structuredAddressEntity.getFluxLocation(), fluxLocationTypeEnum);

        assertNotNull(fluxLocationEntity.getFluxCharacteristics());
        FluxCharacteristicEntity fluxCharacteristicEntity = fluxLocationEntity.getFluxCharacteristics().iterator().next();
        assertNotNull(fluxCharacteristicEntity);
        assertFluxLocationEntityFields(fluxLocation, fluxCharacteristicEntity.getFluxLocation(), fluxLocationTypeEnum);
    }

    @Test
    public void testFluxLocationMapperWithFACatch() {
        FLUXLocation fluxLocation = MapperUtil.getFluxLocation();
        FluxLocationEntity fluxLocationEntity = new FluxLocationEntity();
        FluxLocationTypeEnum fluxLocationTypeEnum = FluxLocationTypeEnum.FA_CATCH_SPECIFIED;
        FaCatchEntity faCatchEntity = null;
        FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation, fluxLocationTypeEnum, faCatchEntity, fluxLocationEntity);

        assertFluxLocationEntityFields(fluxLocation, fluxLocationEntity, fluxLocationTypeEnum);
        assertNull(fluxLocationEntity.getFaCatch());

        assertNotNull(fluxLocationEntity.getStructuredAddresses());
        StructuredAddressEntity structuredAddressEntity = fluxLocationEntity.getStructuredAddresses().iterator().next();
        assertNotNull(structuredAddressEntity);
        assertFluxLocationEntityFields(fluxLocation, structuredAddressEntity.getFluxLocation(), fluxLocationTypeEnum);

        assertNotNull(fluxLocationEntity.getFluxCharacteristics());
        FluxCharacteristicEntity fluxCharacteristicEntity = fluxLocationEntity.getFluxCharacteristics().iterator().next();
        assertNotNull(fluxCharacteristicEntity);
        assertFluxLocationEntityFields(fluxLocation, fluxCharacteristicEntity.getFluxLocation(), fluxLocationTypeEnum);
    }

    private void assertFluxLocationEntityFields(FLUXLocation fluxLocation, FluxLocationEntity fluxLocationEntity, FluxLocationTypeEnum fluxLocationTypeEnum) {
        assertEquals(fluxLocation.getTypeCode().getListID(), fluxLocationEntity.getTypeCodeListId());
        assertEquals(fluxLocation.getTypeCode().getValue(), fluxLocationEntity.getTypeCode());
        assertEquals(fluxLocation.getCountryID().getValue(), fluxLocationEntity.getCountryId());
        assertEquals(fluxLocation.getCountryID().getSchemeID(), fluxLocationEntity.getCountryIdSchemeId());
        assertEquals(fluxLocation.getRegionalFisheriesManagementOrganizationCode().getValue(), fluxLocationEntity.getRfmoCode());
        assertEquals(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate().getLongitudeMeasure().getValue().intValue(), fluxLocationEntity.getLongitude().intValue());
        assertEquals(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate().getLatitudeMeasure().getValue().intValue(), fluxLocationEntity.getLatitude().intValue());
        assertEquals(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate().getAltitudeMeasure().getValue().intValue(), fluxLocationEntity.getAltitude().intValue());
        assertEquals(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate().getSystemID().getValue(), fluxLocationEntity.getSystemId());
        assertEquals(fluxLocationTypeEnum.getType(), fluxLocationEntity.getFluxLocationType());
        assertEquals(fluxLocation.getID().getValue(), fluxLocationEntity.getFluxLocationIdentifier());
        assertEquals(fluxLocation.getID().getSchemeID(), fluxLocationEntity.getFluxLocationIdentifierSchemeId());
        assertEquals(fluxLocation.getGeopoliticalRegionCode().getValue(), fluxLocationEntity.getGeopoliticalRegionCode());
        assertEquals(fluxLocation.getGeopoliticalRegionCode().getListID(), fluxLocationEntity.getGeopoliticalRegionCodeListId());
        assertTrue(fluxLocationEntity.getName().startsWith(fluxLocation.getNames().get(0).getValue()));
        assertEquals(fluxLocation.getSovereignRightsCountryID().getValue(), fluxLocationEntity.getSovereignRightsCountryCode());
        assertEquals(fluxLocation.getJurisdictionCountryID().getValue(), fluxLocationEntity.getJurisdictionCountryCode());
    }
}
