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

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.fa.utils.StructuredAddressTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.AddressDetailsDTO;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by padhyad on 7/27/2016.
 */
public class StructuredAddressMapperTest {

    @Test
    public void testStructuredAddressMapperForContactParty() {
        StructuredAddress structuredAddress = MapperUtil.getStructuredAddress();
        StructuredAddressEntity structuredAddressEntity = new StructuredAddressEntity();
        StructuredAddressTypeEnum structuredAddressTypeEnum = StructuredAddressTypeEnum.CANTACT_PARTY_SPECIFIED;
        ContactPartyEntity contactPartyEntity = null;
        StructuredAddressMapper.INSTANCE.mapToStructuredAddress(structuredAddress, structuredAddressTypeEnum, contactPartyEntity, structuredAddressEntity);

        assertEquals(structuredAddress.getBlockName().getValue(), structuredAddressEntity.getBlockName());
        assertEquals(structuredAddress.getBuildingName().getValue(), structuredAddressEntity.getBuildingName());
        assertEquals(structuredAddress.getID().getValue(), structuredAddressEntity.getAddressId());
        assertEquals(structuredAddress.getCityName().getValue(), structuredAddressEntity.getCityName());
        assertEquals(structuredAddress.getCitySubDivisionName().getValue(), structuredAddressEntity.getCitySubdivisionName());
        assertEquals(structuredAddress.getCountryID().getValue(), structuredAddressEntity.getCountry());
        assertEquals(structuredAddress.getCountryName().getValue(), structuredAddressEntity.getCountryName());
        assertEquals(structuredAddress.getCountrySubDivisionName().getValue(), structuredAddressEntity.getCountrySubdivisionName());
        assertEquals(structuredAddress.getCountryID().getSchemeID(), structuredAddressEntity.getCountryIdSchemeId());
        assertEquals(structuredAddress.getPlotIdentification().getValue(), structuredAddressEntity.getPlotId());
        assertEquals(structuredAddress.getPostcodeCode().getValue(), structuredAddressEntity.getPostcode());
        assertEquals(structuredAddress.getPostOfficeBox().getValue(), structuredAddressEntity.getPostOfficeBox());
        assertEquals(structuredAddress.getStreetName().getValue(), structuredAddressEntity.getStreetName());
        assertEquals(structuredAddressTypeEnum.getType(), structuredAddressEntity.getStructuredAddressType());

        assertNull(structuredAddressEntity.getContactParty());
    }

    @Test
    public void testStructuredAddressForFluxLocation() {
        StructuredAddress structuredAddress = MapperUtil.getStructuredAddress();
        StructuredAddressEntity structuredAddressEntity = new StructuredAddressEntity();
        StructuredAddressTypeEnum structuredAddressTypeEnum = StructuredAddressTypeEnum.FLUX_PHYSICAL;
        FluxLocationEntity fluxLocationEntity = null;
        StructuredAddressMapper.INSTANCE.mapToStructuredAddress(structuredAddress, structuredAddressTypeEnum, fluxLocationEntity, structuredAddressEntity);

        assertEquals(structuredAddress.getBlockName().getValue(), structuredAddressEntity.getBlockName());
        assertEquals(structuredAddress.getBuildingName().getValue(), structuredAddressEntity.getBuildingName());
        assertEquals(structuredAddress.getID().getValue(), structuredAddressEntity.getAddressId());
        assertEquals(structuredAddress.getCityName().getValue(), structuredAddressEntity.getCityName());
        assertEquals(structuredAddress.getCitySubDivisionName().getValue(), structuredAddressEntity.getCitySubdivisionName());
        assertEquals(structuredAddress.getCountryID().getValue(), structuredAddressEntity.getCountry());
        assertEquals(structuredAddress.getCountryName().getValue(), structuredAddressEntity.getCountryName());
        assertEquals(structuredAddress.getCountrySubDivisionName().getValue(), structuredAddressEntity.getCountrySubdivisionName());
        assertEquals(structuredAddress.getCountryID().getSchemeID(), structuredAddressEntity.getCountryIdSchemeId());
        assertEquals(structuredAddress.getPlotIdentification().getValue(), structuredAddressEntity.getPlotId());
        assertEquals(structuredAddress.getPostcodeCode().getValue(), structuredAddressEntity.getPostcode());
        assertEquals(structuredAddress.getPostOfficeBox().getValue(), structuredAddressEntity.getPostOfficeBox());
        assertEquals(structuredAddress.getStreetName().getValue(), structuredAddressEntity.getStreetName());
        assertEquals(structuredAddressTypeEnum.getType(), structuredAddressEntity.getStructuredAddressType());

        assertNull(structuredAddressEntity.getFluxLocation());
    }

    @Test
    public void testStructuredAddressDetailsDTOMapper() {
        StructuredAddress structuredAddress = MapperUtil.getStructuredAddress();
        StructuredAddressEntity structuredAddressEntity = new StructuredAddressEntity();
        StructuredAddressTypeEnum structuredAddressTypeEnum = StructuredAddressTypeEnum.FLUX_PHYSICAL;
        FluxLocationEntity fluxLocationEntity = null;
        StructuredAddressMapper.INSTANCE.mapToStructuredAddress(structuredAddress, structuredAddressTypeEnum, fluxLocationEntity, structuredAddressEntity);

        AddressDetailsDTO addressDetailsDTO = StructuredAddressMapper.INSTANCE.mapToAddressDetailsDTO(structuredAddressEntity);
        assertEquals(structuredAddressEntity.getAddressId(), addressDetailsDTO.getAddressId());
        assertEquals(structuredAddressEntity.getBlockName(), addressDetailsDTO.getBlockName());
        assertEquals(structuredAddressEntity.getBuildingName(), addressDetailsDTO.getBuildingName());
        assertEquals(structuredAddressEntity.getCityName(), addressDetailsDTO.getCityName());
        assertEquals(structuredAddressEntity.getCitySubdivisionName(), addressDetailsDTO.getCitySubdivisionName());
        assertEquals(structuredAddressEntity.getCountry(), addressDetailsDTO.getCountry());
        assertEquals(structuredAddressEntity.getCountryName(), addressDetailsDTO.getCountryName());
        assertEquals(structuredAddressEntity.getCountrySubdivisionName(), addressDetailsDTO.getCountrySubdivisionName());
        assertEquals(structuredAddressEntity.getPlotId(), addressDetailsDTO.getPlotId());
        assertEquals(structuredAddressEntity.getPostcode(), addressDetailsDTO.getPostcode());
        assertEquals(structuredAddressEntity.getPostOfficeBox(), addressDetailsDTO.getPostOfficeBox());
        assertEquals(structuredAddressEntity.getStreetName(), addressDetailsDTO.getStreetName());
    }

    @Test
    public void testStructuredAddressDetailsDTOListMapper() {
        StructuredAddress structuredAddress = MapperUtil.getStructuredAddress();
        StructuredAddressEntity structuredAddressEntity = new StructuredAddressEntity();
        StructuredAddressTypeEnum structuredAddressTypeEnum = StructuredAddressTypeEnum.FLUX_PHYSICAL;
        FluxLocationEntity fluxLocationEntity = null;
        StructuredAddressMapper.INSTANCE.mapToStructuredAddress(structuredAddress, structuredAddressTypeEnum, fluxLocationEntity, structuredAddressEntity);

        List<AddressDetailsDTO> addressDetailsDTO = new ArrayList<>(StructuredAddressMapper.INSTANCE.mapToAddressDetailsDTOList(new HashSet<>(Arrays.asList(structuredAddressEntity))));
        assertEquals(structuredAddressEntity.getAddressId(), addressDetailsDTO.get(0).getAddressId());
        assertEquals(structuredAddressEntity.getBlockName(), addressDetailsDTO.get(0).getBlockName());
        assertEquals(structuredAddressEntity.getBuildingName(), addressDetailsDTO.get(0).getBuildingName());
        assertEquals(structuredAddressEntity.getCityName(), addressDetailsDTO.get(0).getCityName());
        assertEquals(structuredAddressEntity.getCitySubdivisionName(), addressDetailsDTO.get(0).getCitySubdivisionName());
        assertEquals(structuredAddressEntity.getCountry(), addressDetailsDTO.get(0).getCountry());
        assertEquals(structuredAddressEntity.getCountryName(), addressDetailsDTO.get(0).getCountryName());
        assertEquals(structuredAddressEntity.getCountrySubdivisionName(), addressDetailsDTO.get(0).getCountrySubdivisionName());
        assertEquals(structuredAddressEntity.getPlotId(), addressDetailsDTO.get(0).getPlotId());
        assertEquals(structuredAddressEntity.getPostcode(), addressDetailsDTO.get(0).getPostcode());
        assertEquals(structuredAddressEntity.getPostOfficeBox(), addressDetailsDTO.get(0).getPostOfficeBox());
        assertEquals(structuredAddressEntity.getStreetName(), addressDetailsDTO.get(0).getStreetName());
    }
}
