/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.fa.utils.StructuredAddressTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.AddressDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;

import java.util.Set;

@Mapper
public abstract class StructuredAddressMapper extends BaseMapper {

    public static final StructuredAddressMapper INSTANCE = Mappers.getMapper(StructuredAddressMapper.class);

    @Mappings({
            @Mapping(target = "blockName", source = "structuredAddress.blockName.value"),
            @Mapping(target = "buildingName", source = "structuredAddress.buildingName.value"),
            @Mapping(target = "cityName", source = "structuredAddress.cityName.value"),
            @Mapping(target = "citySubdivisionName", source = "structuredAddress.citySubDivisionName.value"),
            @Mapping(target = "country", source = "structuredAddress.countryID.value"),
            @Mapping(target = "countryName", source = "structuredAddress.countryName.value"),
            @Mapping(target = "countrySubdivisionName", source = "structuredAddress.countrySubDivisionName.value"),
            @Mapping(target = "countryIdSchemeId", source = "structuredAddress.countryID.schemeID"),
            @Mapping(target = "addressId", source = "structuredAddress.ID.value"),
            @Mapping(target = "plotId", source = "structuredAddress.plotIdentification.value"),
            @Mapping(target = "postOfficeBox", source = "structuredAddress.postOfficeBox.value"),
            @Mapping(target = "postcode", source = "structuredAddress.postcodeCode.value"),
            @Mapping(target = "streetName", source = "structuredAddress.streetName.value"),
            @Mapping(target = "contactParty", expression = "java(contactPartyEntity)"),
            @Mapping(target = "structuredAddressType", expression = "java(structuredAddressTypeEnum.getType())")
    })
    public abstract StructuredAddressEntity mapToStructuredAddress(StructuredAddress structuredAddress, StructuredAddressTypeEnum structuredAddressTypeEnum, ContactPartyEntity contactPartyEntity, @MappingTarget StructuredAddressEntity structuredAddressEntity);

    @Mappings({
            @Mapping(target = "blockName", source = "structuredAddress.blockName.value"),
            @Mapping(target = "buildingName", source = "structuredAddress.buildingName.value"),
            @Mapping(target = "cityName", source = "structuredAddress.cityName.value"),
            @Mapping(target = "citySubdivisionName", source = "structuredAddress.citySubDivisionName.value"),
            @Mapping(target = "country", source = "structuredAddress.countryID.value"),
            @Mapping(target = "countryName", source = "structuredAddress.countryName.value"),
            @Mapping(target = "countrySubdivisionName", source = "structuredAddress.countrySubDivisionName.value"),
            @Mapping(target = "countryIdSchemeId", source = "structuredAddress.countryID.schemeID"),
            @Mapping(target = "addressId", source = "structuredAddress.ID.value"),
            @Mapping(target = "plotId", source = "structuredAddress.plotIdentification.value"),
            @Mapping(target = "postOfficeBox", source = "structuredAddress.postOfficeBox.value"),
            @Mapping(target = "postcode", source = "structuredAddress.postcodeCode.value"),
            @Mapping(target = "streetName", source = "structuredAddress.streetName.value"),
            @Mapping(target = "fluxLocation", expression = "java(fluxLocationEntity)"),
            @Mapping(target = "structuredAddressType", expression = "java(structuredAddressTypeEnum.getType())")
    })
    public abstract StructuredAddressEntity mapToStructuredAddress(StructuredAddress structuredAddress, StructuredAddressTypeEnum structuredAddressTypeEnum, FluxLocationEntity fluxLocationEntity, @MappingTarget StructuredAddressEntity structuredAddressEntity);

    public abstract AddressDetailsDTO mapToAddressDetailsDTO(StructuredAddressEntity structuredAddressEntity);

    public abstract Set<AddressDetailsDTO> mapToAddressDetailsDTOList(Set<StructuredAddressEntity> structuredAddressEntities);
}