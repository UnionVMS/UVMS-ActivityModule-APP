/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.fa.utils.StructuredAddressTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.AddressDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.IterableNonItatableUtil;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;

@Mapper(uses = {FluxCharacteristicsMapper.class, StructuredAddressMapper.class, IterableNonItatableUtil.class, FluxCharacteristicsMapper.class})
public abstract class FluxLocationMapper extends BaseMapper {

    public static final FluxLocationMapper INSTANCE = Mappers.getMapper(FluxLocationMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "fluxLocation.typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "fluxLocation.typeCode.listID"),
            @Mapping(target = "countryId", source = "fluxLocation.countryID.value"),
            @Mapping(target = "rfmoCode", source = "fluxLocation.regionalFisheriesManagementOrganizationCode.value"),
            @Mapping(target = "longitude", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.longitudeMeasure.value"),
            @Mapping(target = "latitude", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.latitudeMeasure.value"),
            @Mapping(target = "altitude", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.altitudeMeasure.value"),
            @Mapping(target = "countryIdSchemeId", source = "fluxLocation.countryID.schemeID"),
            @Mapping(target = "fluxLocationIdentifier", source = "fluxLocation.ID.value"),
            @Mapping(target = "fluxLocationIdentifierSchemeId", source = "fluxLocation.ID.schemeID"),
            @Mapping(target = "geopoliticalRegionCode", source = "fluxLocation.geopoliticalRegionCode.value"),
            @Mapping(target = "geopoliticalRegionCodeListId", source = "fluxLocation.geopoliticalRegionCode.listID"),
            @Mapping(target = "name", expression = "java(getTextFromList(fluxLocation.getNames()))"),
            @Mapping(target = "nameLanguageId", expression = "java(getLanguageIdFromList(fluxLocation.getNames()))"),
            @Mapping(target = "sovereignRightsCountryCode", source = "fluxLocation.sovereignRightsCountryID.value"),
            @Mapping(target = "jurisdictionCountryCode", source = "fluxLocation.jurisdictionCountryID.value"),
            @Mapping(target = "systemId", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.systemID.value"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntities(fluxLocation.getPostalStructuredAddresses(), fluxLocation.getPhysicalStructuredAddress(), fluxLocationEntity))")
    })
    public abstract FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation);

    @Mappings({
            @Mapping(target = "geometry", source = "wkt")
    })
    public abstract FluxLocationDto mapEntityToFluxLocationDto(FluxLocationEntity fluxLocation);

    @InheritInverseConfiguration
    public abstract Set<FluxLocationDto> mapEntityToFluxLocationDto(Set<FluxLocationEntity> fluxLocation);

    protected AddressDetailsDTO getPhysicalAddressDetails(Set<StructuredAddressEntity> structuredAddresses) {
        for (StructuredAddressEntity structuredAddressEntity : structuredAddresses) {
            if (structuredAddressEntity.getStructuredAddressType().equalsIgnoreCase(StructuredAddressTypeEnum.FLUX_PHYSICAL.getType())) {
                return StructuredAddressMapper.INSTANCE.mapToAddressDetailsDTO(structuredAddressEntity);
            }
        }
        return null;
    }

    protected List<AddressDetailsDTO> getPostalAddressDetails(Set<StructuredAddressEntity> structuredAddresses) {
        List<AddressDetailsDTO> addressDetailsDTOs = new ArrayList<>();
        for (StructuredAddressEntity structuredAddressEntity : structuredAddresses) {
            if (structuredAddressEntity.getStructuredAddressType().equalsIgnoreCase(StructuredAddressTypeEnum.FLUX_POSTAL.getType())) {
                addressDetailsDTOs.add(StructuredAddressMapper.INSTANCE.mapToAddressDetailsDTO(structuredAddressEntity));
            }
        }
        return addressDetailsDTOs;
    }

    protected Set<StructuredAddressEntity> getStructuredAddressEntities(List<StructuredAddress> postalStructuredAddresses, StructuredAddress physicalStructuredAddress, FluxLocationEntity fluxLocationEntity) {
        Set<StructuredAddressEntity> structuredAddressEntities = new HashSet<>();
        if (postalStructuredAddresses != null && !postalStructuredAddresses.isEmpty()) {
            for (StructuredAddress structuredAddress : postalStructuredAddresses) {
                StructuredAddressEntity structuredAddressEntity = StructuredAddressMapper.INSTANCE.mapToStructuredAddress(structuredAddress);
                structuredAddressEntity.setStructuredAddressType(StructuredAddressTypeEnum.FLUX_POSTAL.getType());
                structuredAddressEntity.setFluxLocation(fluxLocationEntity);
                structuredAddressEntities.add(structuredAddressEntity);
            }
        }
        if (physicalStructuredAddress != null) {
            StructuredAddressEntity structuredAddressEntity = StructuredAddressMapper.INSTANCE.mapToStructuredAddress(physicalStructuredAddress);
            structuredAddressEntity.setFluxLocation(fluxLocationEntity);
            structuredAddressEntity.setStructuredAddressType(StructuredAddressTypeEnum.FLUX_PHYSICAL.getType());
            structuredAddressEntities.add(structuredAddressEntity);
        }
       return structuredAddressEntities;
    }

}