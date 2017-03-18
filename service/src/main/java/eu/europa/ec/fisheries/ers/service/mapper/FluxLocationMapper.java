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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.fa.utils.StructuredAddressTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.AddressDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.common.mapper.IterableNonItatableUtil;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;

@Mapper(uses = {FluxCharacteristicsMapper.class, StructuredAddressMapper.class, IterableNonItatableUtil.class})
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
            @Mapping(target = "fluxLocationType", source = "fluxLocationTypeEnum.type"),
            @Mapping(target = "countryIdSchemeId", source = "fluxLocation.countryID.schemeID"),
            @Mapping(target = "fluxLocationIdentifier", expression = "java(getIdType(fluxLocation.getID()))"),
            @Mapping(target = "fluxLocationIdentifierSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getID()))"),
            @Mapping(target = "geopoliticalRegionCode", source = "fluxLocation.geopoliticalRegionCode.value"),
            @Mapping(target = "geopoliticalRegionCodeListId", source = "fluxLocation.geopoliticalRegionCode.listID"),
            @Mapping(target = "name", expression = "java(getTextFromList(fluxLocation.getNames()))"),
            @Mapping(target = "nameLanguageId", expression = "java(getLanguageIdFromList(fluxLocation.getNames()))"),
            @Mapping(target = "sovereignRightsCountryCode", source = "fluxLocation.sovereignRightsCountryID.value"),
            @Mapping(target = "jurisdictionCountryCode", source = "fluxLocation.jurisdictionCountryID.value"),
            @Mapping(target = "systemId", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.systemID.value"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntities(fluxLocation.getPostalStructuredAddresses(), fluxLocation.getPhysicalStructuredAddress(), fluxLocationEntity))")
    })
    public abstract FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation, FluxLocationCatchTypeEnum fluxLocationTypeEnum, FishingActivityEntity fishingActivityEntity, @MappingTarget FluxLocationEntity fluxLocationEntity);

    @Mappings({
            @Mapping(target = "typeCode", source = "fluxLocation.typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "fluxLocation.typeCode.listID"),
            @Mapping(target = "countryId", source = "fluxLocation.countryID.value"),
            @Mapping(target = "rfmoCode", source = "fluxLocation.regionalFisheriesManagementOrganizationCode.value"),
            @Mapping(target = "longitude", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.longitudeMeasure.value"),
            @Mapping(target = "latitude", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.latitudeMeasure.value"),
            @Mapping(target = "altitude", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.altitudeMeasure.value"),
            @Mapping(target = "fluxLocationType", source = "fluxLocationTypeEnum.type"),
            @Mapping(target = "countryIdSchemeId", source = "fluxLocation.countryID.schemeID"),
            @Mapping(target = "fluxLocationIdentifier", expression = "java(getIdType(fluxLocation.getID()))"),
            @Mapping(target = "fluxLocationIdentifierSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getID()))"),
            @Mapping(target = "geopoliticalRegionCode", source = "fluxLocation.geopoliticalRegionCode.value"),
            @Mapping(target = "geopoliticalRegionCodeListId", source = "fluxLocation.geopoliticalRegionCode.listID"),
            @Mapping(target = "name", expression = "java(getTextFromList(fluxLocation.getNames()))"),
            @Mapping(target = "nameLanguageId", expression = "java(getLanguageIdFromList(fluxLocation.getNames()))"),
            @Mapping(target = "sovereignRightsCountryCode", source = "fluxLocation.sovereignRightsCountryID.value"),
            @Mapping(target = "jurisdictionCountryCode", source = "fluxLocation.jurisdictionCountryID.value"),
            @Mapping(target = "systemId", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.systemID.value"),
            @Mapping(target = "gearProblem", expression = "java(gearProblem)"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntities(fluxLocation.getPostalStructuredAddresses(), fluxLocation.getPhysicalStructuredAddress(), fluxLocationEntity))")
    })
    public abstract FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation, FluxLocationCatchTypeEnum fluxLocationTypeEnum, GearProblemEntity gearProblem, @MappingTarget FluxLocationEntity fluxLocationEntity);


    @Mappings({
            @Mapping(target = "typeCode", source = "fluxLocation.typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "fluxLocation.typeCode.listID"),
            @Mapping(target = "countryId", source = "fluxLocation.countryID.value"),
            @Mapping(target = "rfmoCode", source = "fluxLocation.regionalFisheriesManagementOrganizationCode.value"),
            @Mapping(target = "longitude", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.longitudeMeasure.value"),
            @Mapping(target = "latitude", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.latitudeMeasure.value"),
            @Mapping(target = "altitude", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.altitudeMeasure.value"),
            @Mapping(target = "fluxLocationType", source = "fluxLocationTypeEnum.type"),
            @Mapping(target = "countryIdSchemeId", source = "fluxLocation.countryID.schemeID"),
            @Mapping(target = "fluxLocationIdentifier", expression = "java(getIdType(fluxLocation.getID()))"),
            @Mapping(target = "fluxLocationIdentifierSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getID()))"),
            @Mapping(target = "geopoliticalRegionCode", source = "fluxLocation.geopoliticalRegionCode.value"),
            @Mapping(target = "geopoliticalRegionCodeListId", source = "fluxLocation.geopoliticalRegionCode.listID"),
            @Mapping(target = "name", expression = "java(getTextFromList(fluxLocation.getNames()))"),
            @Mapping(target = "nameLanguageId", expression = "java(getLanguageIdFromList(fluxLocation.getNames()))"),
            @Mapping(target = "sovereignRightsCountryCode", source = "fluxLocation.sovereignRightsCountryID.value"),
            @Mapping(target = "jurisdictionCountryCode", source = "fluxLocation.jurisdictionCountryID.value"),
            @Mapping(target = "systemId", source = "fluxLocation.specifiedPhysicalFLUXGeographicalCoordinate.systemID.value"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntities(fluxLocation.getPostalStructuredAddresses(), fluxLocation.getPhysicalStructuredAddress(), fluxLocationEntity))")
    })
    public abstract FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation, FluxLocationCatchTypeEnum fluxLocationTypeEnum, FaCatchEntity faCatchEntity, @MappingTarget FluxLocationEntity fluxLocationEntity);

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
                StructuredAddressEntity structuredAddressEntity = StructuredAddressMapper.INSTANCE.mapToStructuredAddress(structuredAddress, StructuredAddressTypeEnum.FLUX_POSTAL, fluxLocationEntity, new StructuredAddressEntity());
                structuredAddressEntities.add(structuredAddressEntity);
            }
        }
        if (physicalStructuredAddress != null) {
            structuredAddressEntities.add(StructuredAddressMapper.INSTANCE.mapToStructuredAddress(physicalStructuredAddress, StructuredAddressTypeEnum.FLUX_PHYSICAL, fluxLocationEntity, new StructuredAddressEntity()));
        }
       return structuredAddressEntities;
    }

    protected Set<FluxCharacteristicEntity> getFluxCharacteristicEntities(List<FLUXCharacteristic> fluxCharacteristics, FluxLocationEntity fluxLocationEntity) {
        if (fluxCharacteristics == null || fluxCharacteristics.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FluxCharacteristicEntity> fluxCharacteristicEntities = new HashSet<>();
        for(FLUXCharacteristic fluxCharacteristic : fluxCharacteristics) {
            FluxCharacteristicEntity fluxCharacteristicEntity = FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(fluxCharacteristic, fluxLocationEntity, new FluxCharacteristicEntity());
            fluxCharacteristicEntities.add(fluxCharacteristicEntity);
        }
       return fluxCharacteristicEntities;
    }
}