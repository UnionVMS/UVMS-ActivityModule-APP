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

import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;

@Mapper(imports = BaseMapper.class, uses = {FluxCharacteristicsMapper.class,
        StructuredAddressMapper.class, FluxCharacteristicsMapper.class, CodeTypeMapper.class})
public interface FluxLocationMapper {

    FluxLocationMapper INSTANCE = Mappers.getMapper(FluxLocationMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "fluxLocationType", source = "typeCode.value"), // Why is this duplicated?
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "countryId", source = "countryID.value"),
            @Mapping(target = "rfmoCode", source = "regionalFisheriesManagementOrganizationCode.value"),
            @Mapping(target = "longitude", source = "specifiedPhysicalFLUXGeographicalCoordinate.longitudeMeasure.value"),
            @Mapping(target = "latitude", source = "specifiedPhysicalFLUXGeographicalCoordinate.latitudeMeasure.value"),
            @Mapping(target = "altitude", source = "specifiedPhysicalFLUXGeographicalCoordinate.altitudeMeasure.value"),
            @Mapping(target = "countryIdSchemeId", source = "countryID.schemeID"),
            @Mapping(target = "fluxLocationIdentifier", source = "ID.value"),
            @Mapping(target = "fluxLocationIdentifierSchemeId", source = "ID.schemeID"),
            @Mapping(target = "geopoliticalRegionCode", source = "geopoliticalRegionCode.value"),
            @Mapping(target = "geopoliticalRegionCodeListId", source = "geopoliticalRegionCode.listID"),
            @Mapping(target = "name", expression = "java(BaseMapper.getTextFromList(fluxLocation.getNames()))"),
            @Mapping(target = "nameLanguageId", expression = "java(BaseMapper.getLanguageIdFromList(fluxLocation.getNames()))"),
            @Mapping(target = "sovereignRightsCountryCode", source = "sovereignRightsCountryID.value"),
            @Mapping(target = "jurisdictionCountryCode", source = "jurisdictionCountryID.value"),
            @Mapping(target = "systemId", source = "specifiedPhysicalFLUXGeographicalCoordinate.systemID.value")
    })
    FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation);

    @InheritInverseConfiguration
    FLUXLocation mapToFluxLocation(FluxLocationEntity fluxLocation);

    List<FLUXLocation> mapToFluxLocationList(Set<FluxLocationEntity> fluxLocation);

    @Mappings({
            @Mapping(target = "geometry", source = "wkt")
    })
    FluxLocationDto mapEntityToFluxLocationDto(FluxLocationEntity fluxLocation);

    Set<FluxLocationDto> mapEntityToFluxLocationDto(Set<FluxLocationEntity> fluxLocation);

}