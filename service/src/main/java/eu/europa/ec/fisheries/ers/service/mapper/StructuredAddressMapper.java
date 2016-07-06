/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

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
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.StructuredAddress;

/**
 * Created by padhyad on 6/13/2016.
 */
@Mapper
public abstract class StructuredAddressMapper extends BaseMapper {

    public static StructuredAddressMapper INSTANCE = Mappers.getMapper(StructuredAddressMapper.class);

    @Mappings({
            @Mapping(target = "blockName", expression = "java(getTextType(structuredAddress.getBlockName()))"),
            @Mapping(target = "buildingName", expression = "java(getTextType(structuredAddress.getBuildingName()))"),
            @Mapping(target = "cityName", expression = "java(getTextType(structuredAddress.getCityName()))"),
            @Mapping(target = "citySubdivisionName", expression = "java(getTextType(structuredAddress.getCitySubDivisionName()))"),
            @Mapping(target = "country", expression = "java(getIdType(structuredAddress.getCountryID()))"),
            @Mapping(target = "countryName", expression = "java(getTextType(structuredAddress.getCountryName()))"),
            @Mapping(target = "countrySubdivisionName", expression = "java(getTextType(structuredAddress.getCountrySubDivisionName()))"),
            @Mapping(target = "addressId", expression = "java(getIdType(structuredAddress.getID()))"),
            @Mapping(target = "plotId", expression = "java(getTextType(structuredAddress.getPlotIdentification()))"),
            @Mapping(target = "postOfficeBox", expression = "java(getTextType(structuredAddress.getPostOfficeBox()))"),
            @Mapping(target = "postcode", expression = "java(getCodeType(structuredAddress.getPostcodeCode()))"),
            @Mapping(target = "streetname", expression = "java(getTextType(structuredAddress.getStreetName()))"),
            @Mapping(target = "contactParty", expression = "java(contactPartyEntity)"),
            @Mapping(target = "structuredAddressType", expression = "java(structuredAddressTypeEnum.getType())")
    })
    public abstract StructuredAddressEntity mapToStructuredAddress(StructuredAddress structuredAddress, StructuredAddressTypeEnum structuredAddressTypeEnum, ContactPartyEntity contactPartyEntity, @MappingTarget StructuredAddressEntity structuredAddressEntity);

    @Mappings({
            @Mapping(target = "blockName", expression = "java(getTextType(structuredAddress.getBlockName()))"),
            @Mapping(target = "buildingName", expression = "java(getTextType(structuredAddress.getBuildingName()))"),
            @Mapping(target = "cityName", expression = "java(getTextType(structuredAddress.getCityName()))"),
            @Mapping(target = "citySubdivisionName", expression = "java(getTextType(structuredAddress.getCitySubDivisionName()))"),
            @Mapping(target = "country", expression = "java(getIdType(structuredAddress.getCountryID()))"),
            @Mapping(target = "countryName", expression = "java(getTextType(structuredAddress.getCountryName()))"),
            @Mapping(target = "countrySubdivisionName", expression = "java(getTextType(structuredAddress.getCountrySubDivisionName()))"),
            @Mapping(target = "addressId", expression = "java(getIdType(structuredAddress.getID()))"),
            @Mapping(target = "plotId", expression = "java(getTextType(structuredAddress.getPlotIdentification()))"),
            @Mapping(target = "postOfficeBox", expression = "java(getTextType(structuredAddress.getPostOfficeBox()))"),
            @Mapping(target = "postcode", expression = "java(getCodeType(structuredAddress.getPostcodeCode()))"),
            @Mapping(target = "streetname", expression = "java(getTextType(structuredAddress.getStreetName()))"),
            @Mapping(target = "fluxLocation", expression = "java(fluxLocationEntity)"),
            @Mapping(target = "structuredAddressType", expression = "java(structuredAddressTypeEnum.getType())")
    })
    public abstract StructuredAddressEntity mapToStructuredAddress(StructuredAddress structuredAddress, StructuredAddressTypeEnum structuredAddressTypeEnum, FluxLocationEntity fluxLocationEntity, @MappingTarget StructuredAddressEntity structuredAddressEntity);


}