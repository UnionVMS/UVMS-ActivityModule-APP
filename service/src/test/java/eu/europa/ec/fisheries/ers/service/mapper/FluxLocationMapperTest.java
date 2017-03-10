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
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.AddressDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXGeographicalCoordinate;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by padhyad on 7/29/2016.
 */
public class FluxLocationMapperTest {

    @Test
    public void testFluxLocationMapperWithFishingActivity() {
        FLUXLocation fluxLocation = MapperUtil.getFluxLocation();
        FluxLocationEntity fluxLocationEntity = new FluxLocationEntity();
        FluxLocationCatchTypeEnum fluxLocationTypeEnum = FluxLocationCatchTypeEnum.FA_RELATED;
        FishingActivityEntity fishingActivityEntity = null;
        FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation, fluxLocationTypeEnum, fishingActivityEntity, fluxLocationEntity);

        assertFluxLocationEntityFields(fluxLocation, fluxLocationEntity, fluxLocationTypeEnum);
        assertNull(fluxLocationEntity.getFishingActivity());

        assertNotNull(fluxLocationEntity.getStructuredAddresses());
        StructuredAddressEntity structuredAddressEntity = fluxLocationEntity.getStructuredAddresses().iterator().next();
        assertNotNull(structuredAddressEntity);
        assertFluxLocationEntityFields(fluxLocation, structuredAddressEntity.getFluxLocation(), fluxLocationTypeEnum);
    }

    @Test
    public void testFluxLocationMapperWithFACatch() {
        FLUXLocation fluxLocation = MapperUtil.getFluxLocation();
        FluxLocationEntity fluxLocationEntity = new FluxLocationEntity();
        FluxLocationCatchTypeEnum fluxLocationTypeEnum = FluxLocationCatchTypeEnum.FA_CATCH_SPECIFIED;
        FaCatchEntity faCatchEntity = null;
        FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation, fluxLocationTypeEnum, faCatchEntity, fluxLocationEntity);

        assertFluxLocationEntityFields(fluxLocation, fluxLocationEntity, fluxLocationTypeEnum);
        assertNull(fluxLocationEntity.getFaCatch());

        assertNotNull(fluxLocationEntity.getStructuredAddresses());
        StructuredAddressEntity structuredAddressEntity = fluxLocationEntity.getStructuredAddresses().iterator().next();
        assertNotNull(structuredAddressEntity);
        assertFluxLocationEntityFields(fluxLocation, structuredAddressEntity.getFluxLocation(), fluxLocationTypeEnum);
    }

    @Test
    public void mapToFluxLocationDTOTest(){
        FluxLocationEntity fluxLocationEntity = getFluxLocationEntityMock();
        FluxLocationDto locationDto = FluxLocationMapper.INSTANCE.mapEntityToFluxLocationDto(fluxLocationEntity);
        assertNotNull(locationDto);
    }

    @Test
    public void getPostalAddressDetailsTest(){
        FluxLocationEntity fluxLocationEntity = getFluxLocationEntityMock();
        List<AddressDetailsDTO> fluxLocationDTO = FluxLocationMapper.INSTANCE.getPostalAddressDetails(fluxLocationEntity.getStructuredAddresses());
        assertNotNull(fluxLocationDTO);
        assertTrue(fluxLocationDTO.size() > 0);
    }

    @Test
    public void getPhysicalAddressDetailsTest(){
        FluxLocationEntity fluxLocationEntity = getFluxLocationEntityMock();
        AddressDetailsDTO fluxLocationDTO = FluxLocationMapper.INSTANCE.getPhysicalAddressDetails(fluxLocationEntity.getStructuredAddresses());
        assertNotNull(fluxLocationDTO);
        assertTrue(StringUtils.isNotEmpty(fluxLocationDTO.getBlockName()));
    }

    @Test
    public void getFluxCharacteristicEntitiesTest(){
        List<FLUXCharacteristic> fluxCarList = new ArrayList<FLUXCharacteristic>(){{
            add(MapperUtil.getFluxCharacteristics());
            add(MapperUtil.getFluxCharacteristics());
        }};
        FluxLocationEntity fluxLocationEntity = getFluxLocationEntityMock();
        Set<FluxCharacteristicEntity> fluxCharacteristicEntities =
                FluxLocationMapper.INSTANCE.getFluxCharacteristicEntities(fluxCarList, fluxLocationEntity);
        assertNotNull(fluxCharacteristicEntities);
        assertTrue(fluxCharacteristicEntities.size() > 0);
    }

    @Test
    public void testNullityConditions(){
        assertNull(FluxLocationMapper.INSTANCE.getAltitude(null));
        assertNull(FluxLocationMapper.INSTANCE.getLatitude(null));
        assertNull(FluxLocationMapper.INSTANCE.getLongitude(null));
        assertNull(FluxLocationMapper.INSTANCE.getSystemId(null));

        FLUXGeographicalCoordinate fluxGeographicalCoordinate = new FLUXGeographicalCoordinate(null,null,null,null);

        assertNull(FluxLocationMapper.INSTANCE.getAltitude(fluxGeographicalCoordinate));
        assertNull(FluxLocationMapper.INSTANCE.getLatitude(fluxGeographicalCoordinate));
        assertNull(FluxLocationMapper.INSTANCE.getLongitude(fluxGeographicalCoordinate));
        assertNull(FluxLocationMapper.INSTANCE.getSystemId(fluxGeographicalCoordinate));

    }

    private void assertFluxLocationEntityFields(FLUXLocation fluxLocation, FluxLocationEntity fluxLocationEntity, FluxLocationCatchTypeEnum fluxLocationTypeEnum) {
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

    private FluxLocationEntity getFluxLocationEntityMock() {
        FLUXLocation fluxLocation = MapperUtil.getFluxLocation();
        FluxLocationEntity fluxLocationEntity = new FluxLocationEntity();
        FluxLocationCatchTypeEnum fluxLocationTypeEnum = FluxLocationCatchTypeEnum.FA_CATCH_SPECIFIED;
        FaCatchEntity faCatchEntity = null;
        FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation, fluxLocationTypeEnum, faCatchEntity, fluxLocationEntity);
        return fluxLocationEntity;
    }
}
