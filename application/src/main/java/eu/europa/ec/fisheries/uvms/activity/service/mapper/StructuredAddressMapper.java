/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.AddressDetailsDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class StructuredAddressMapper {

    @Mapping(target = "blockName", source = "blockName.value")
    @Mapping(target = "buildingName", source = "buildingName.value")
    @Mapping(target = "buildingNumber", source = "buildingNumber.value")
    @Mapping(target = "cityName", source = "cityName.value")
    @Mapping(target = "citySubdivisionName", source = "citySubDivisionName.value")
    @Mapping(target = "countryIDValue", source = "countryID.value")
    @Mapping(target = "countryIDSchemeID", source = "countryID.schemeID")
    @Mapping(target = "countryName", source = "countryName.value")
    @Mapping(target = "countrySubdivisionName", source = "countrySubDivisionName.value")
    @Mapping(target = "addressId", source = "ID.value")
    @Mapping(target = "plotId", source = "plotIdentification.value")
    @Mapping(target = "postOfficeBox", source = "postOfficeBox.value")
    @Mapping(target = "postcode", source = "postcodeCode.value")
    @Mapping(target = "postcodeListID", source = "postcodeCode.listID")
    @Mapping(target = "postalAreaValue", source = "postalArea.value")
    @Mapping(target = "postalAreaLanguageLocaleID", source = "postalArea.languageLocaleID")
    @Mapping(target = "postalAreaLanguageID", source = "postalArea.languageID")
    @Mapping(target = "streetName", source = "streetName.value")
    @Mapping(target = "staircaseNumberValue", source = "staircaseNumber.value")
    @Mapping(target = "floorIdentificationValue", source = "floorIdentification.value")
    @Mapping(target = "roomIdentificationValue", source = "roomIdentification.value")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "structuredAddressType", ignore = true)
    @Mapping(target = "contactParty", ignore = true)
    public abstract StructuredAddressEntity mapToStructuredAddressEntity(StructuredAddress structuredAddress);

    @InheritInverseConfiguration
    public abstract StructuredAddress mapToStructuredAddress(StructuredAddressEntity structuredAddress);

    public abstract List<StructuredAddress> mapToStructuredAddressList(Set<StructuredAddressEntity> structuredAddress);

    public abstract AddressDetailsDTO mapToAddressDetailsDTO(StructuredAddressEntity structuredAddressEntity);

    public abstract Set<AddressDetailsDTO> mapToAddressDetailsDTOList(Set<StructuredAddressEntity> structuredAddressEntities);

}
