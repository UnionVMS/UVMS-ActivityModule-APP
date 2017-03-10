/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.fa.utils.StructuredAddressTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.AddressDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXGeographicalCoordinate;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;

import java.util.*;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper(uses = {FluxCharacteristicsMapper.class})
public abstract class FluxLocationMapper extends BaseMapper {

    public static final FluxLocationMapper INSTANCE = Mappers.getMapper(FluxLocationMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fluxLocation.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fluxLocation.getTypeCode()))"),
            @Mapping(target = "countryId", expression = "java(getIdType(fluxLocation.getCountryID()))"),
            @Mapping(target = "rfmoCode", expression = "java(getCodeType(fluxLocation.getRegionalFisheriesManagementOrganizationCode()))"),
            @Mapping(target = "longitude", expression = "java(getLongitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "latitude", expression = "java(getLatitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "fluxLocationType", expression = "java(fluxLocationTypeEnum.getType())"),
            @Mapping(target = "countryIdSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getCountryID()))"),
            @Mapping(target = "fluxLocationIdentifier", expression = "java(getIdType(fluxLocation.getID()))"),
            @Mapping(target = "fluxLocationIdentifierSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getID()))"),
            @Mapping(target = "geopoliticalRegionCode", expression = "java(getCodeType(fluxLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "geopoliticalRegionCodeListId", expression = "java(getCodeTypeListId(fluxLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "name", expression = "java(getTextFromList(fluxLocation.getNames()))"),
            @Mapping(target = "nameLanguageId", expression = "java(getLanguageIdFromList(fluxLocation.getNames()))"),
            @Mapping(target = "sovereignRightsCountryCode", expression = "java(getIdType(fluxLocation.getSovereignRightsCountryID()))"),
            @Mapping(target = "jurisdictionCountryCode", expression = "java(getIdType(fluxLocation.getJurisdictionCountryID()))"),
            @Mapping(target = "altitude", expression = "java(getAltitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "systemId", expression = "java(getSystemId(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)"),
        //    @Mapping(target = "fluxCharacteristics", expression = "java(getFluxCharacteristicEntities(fluxLocation.getApplicableFLUXCharacteristics(), fluxLocationEntity))"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntities(fluxLocation.getPostalStructuredAddresses(), fluxLocation.getPhysicalStructuredAddress(), fluxLocationEntity))")
    })
    public abstract FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation, FluxLocationCatchTypeEnum fluxLocationTypeEnum, FishingActivityEntity fishingActivityEntity, @MappingTarget FluxLocationEntity fluxLocationEntity);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fluxLocation.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fluxLocation.getTypeCode()))"),
            @Mapping(target = "countryId", expression = "java(getIdType(fluxLocation.getCountryID()))"),
            @Mapping(target = "rfmoCode", expression = "java(getCodeType(fluxLocation.getRegionalFisheriesManagementOrganizationCode()))"),
            @Mapping(target = "longitude", expression = "java(getLongitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "latitude", expression = "java(getLatitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "fluxLocationType", expression = "java(fluxLocationTypeEnum.getType())"),
            @Mapping(target = "countryIdSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getCountryID()))"),
            @Mapping(target = "fluxLocationIdentifier", expression = "java(getIdType(fluxLocation.getID()))"),
            @Mapping(target = "fluxLocationIdentifierSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getID()))"),
            @Mapping(target = "geopoliticalRegionCode", expression = "java(getCodeType(fluxLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "geopoliticalRegionCodeListId", expression = "java(getCodeTypeListId(fluxLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "name", expression = "java(getTextFromList(fluxLocation.getNames()))"),
            @Mapping(target = "nameLanguageId", expression = "java(getLanguageIdFromList(fluxLocation.getNames()))"),
            @Mapping(target = "sovereignRightsCountryCode", expression = "java(getIdType(fluxLocation.getSovereignRightsCountryID()))"),
            @Mapping(target = "jurisdictionCountryCode", expression = "java(getIdType(fluxLocation.getJurisdictionCountryID()))"),
            @Mapping(target = "altitude", expression = "java(getAltitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "systemId", expression = "java(getSystemId(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "gearProblem", expression = "java(gearProblem)"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntities(fluxLocation.getPostalStructuredAddresses(), fluxLocation.getPhysicalStructuredAddress(), fluxLocationEntity))")
    })
    public abstract FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation, FluxLocationCatchTypeEnum fluxLocationTypeEnum, GearProblemEntity gearProblem, @MappingTarget FluxLocationEntity fluxLocationEntity);


    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fluxLocation.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fluxLocation.getTypeCode()))"),
            @Mapping(target = "countryId", expression = "java(getIdType(fluxLocation.getCountryID()))"),
            @Mapping(target = "rfmoCode", expression = "java(getCodeType(fluxLocation.getRegionalFisheriesManagementOrganizationCode()))"),
            @Mapping(target = "longitude", expression = "java(getLongitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "latitude", expression = "java(getLatitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "fluxLocationType", expression = "java(fluxLocationTypeEnum.getType())"),
            @Mapping(target = "countryIdSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getCountryID()))"),
            @Mapping(target = "fluxLocationIdentifier", expression = "java(getIdType(fluxLocation.getID()))"),
            @Mapping(target = "fluxLocationIdentifierSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getID()))"),
            @Mapping(target = "geopoliticalRegionCode", expression = "java(getCodeType(fluxLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "geopoliticalRegionCodeListId", expression = "java(getCodeTypeListId(fluxLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "name", expression = "java(getTextFromList(fluxLocation.getNames()))"),
            @Mapping(target = "nameLanguageId", expression = "java(getLanguageIdFromList(fluxLocation.getNames()))"),
            @Mapping(target = "sovereignRightsCountryCode", expression = "java(getIdType(fluxLocation.getSovereignRightsCountryID()))"),
            @Mapping(target = "jurisdictionCountryCode", expression = "java(getIdType(fluxLocation.getJurisdictionCountryID()))"),
            @Mapping(target = "altitude", expression = "java(getAltitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "systemId", expression = "java(getSystemId(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)"),
       //     @Mapping(target = "fluxCharacteristics", expression = "java(getFluxCharacteristicEntities(fluxLocation.getApplicableFLUXCharacteristics(), fluxLocationEntity))"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntities(fluxLocation.getPostalStructuredAddresses(), fluxLocation.getPhysicalStructuredAddress(), fluxLocationEntity))")
    })
    public abstract FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation, FluxLocationCatchTypeEnum fluxLocationTypeEnum, FaCatchEntity faCatchEntity, @MappingTarget FluxLocationEntity fluxLocationEntity);

    @Mappings({
            @Mapping(source = "wkt",target = "geometry")
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

    protected String getSystemId(FLUXGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getSystemID() != null) {
            return coordinate.getSystemID().getValue();
        }
        else {
            return null;
        }
    }

    protected Double getLongitude(FLUXGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getLongitudeMeasure() != null) {
            return coordinate.getLongitudeMeasure().getValue().doubleValue();
        } else {
            return null;
        }
    }

    protected Double getLatitude(FLUXGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getLatitudeMeasure() != null) {
            return coordinate.getLatitudeMeasure().getValue().doubleValue();
        } else {
            return null;
        }
    }

    protected Double getAltitude(FLUXGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getAltitudeMeasure() != null) {
            return coordinate.getAltitudeMeasure().getValue().doubleValue();
        } else {
            return null;
        }
    }
}