/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.AddressDetailsDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StructuredAddressMapper {

    StructuredAddressMapper INSTANCE = Mappers.getMapper(StructuredAddressMapper.class);

    @Mappings({
            @Mapping(target = "blockName", source = "blockName.value"),
            @Mapping(target = "buildingName", source = "buildingName.value"),
            @Mapping(target = "buildingNumber", source = "buildingNumber.value"),
            @Mapping(target = "cityName", source = "cityName.value"),
            @Mapping(target = "citySubdivisionName", source = "citySubDivisionName.value"),
            @Mapping(target = "countryIDValue", source = "countryID.value"),
            @Mapping(target = "countryIDSchemeID", source = "countryID.schemeID"),
            @Mapping(target = "countryName", source = "countryName.value"),
            @Mapping(target = "countrySubdivisionName", source = "countrySubDivisionName.value"),
            @Mapping(target = "addressId", source = "ID.value"),
            @Mapping(target = "plotId", source = "plotIdentification.value"),
            @Mapping(target = "postOfficeBox", source = "postOfficeBox.value"),
            @Mapping(target = "postcode", source = "postcodeCode.value"),
            @Mapping(target = "postcodeListID", source = "postcodeCode.listID"),
            @Mapping(target = "postalAreaValue", source = "postalArea.value"),
            @Mapping(target = "postalAreaLanguageLocaleID", source = "postalArea.languageLocaleID"),
            @Mapping(target = "postalAreaLanguageID", source = "postalArea.languageID"),
            @Mapping(target = "streetName", source = "streetName.value"),
            @Mapping(target = "staircaseNumberValue", source = "staircaseNumber.value"),
            @Mapping(target = "floorIdentificationValue", source = "floorIdentification.value"),
            @Mapping(target = "roomIdentificationValue", source = "roomIdentification.value")
    })
    StructuredAddressEntity mapToStructuredAddressEntity(StructuredAddress structuredAddress);

    @InheritInverseConfiguration
    StructuredAddress mapToStructuredAddress(StructuredAddressEntity structuredAddress);

    List<StructuredAddress> mapToStructuredAddressList(Set<StructuredAddressEntity> structuredAddress);

    Set<StructuredAddressEntity> mapToStructuredAddressEntitySet(List<StructuredAddress> structuredAddress);

    AddressDetailsDTO mapToAddressDetailsDTO(StructuredAddressEntity structuredAddressEntity);

    Set<AddressDetailsDTO> mapToAddressDetailsDTOList(Set<StructuredAddressEntity> structuredAddressEntities);

}