/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.FluxLocationDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;

import java.util.List;
import java.util.Set;

@Mapper(imports = BaseMapper.class,
        uses = {FluxCharacteristicsMapper.class, StructuredAddressMapper.class, FluxCharacteristicsMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FluxLocationMapper {

    FluxLocationMapper INSTANCE = Mappers.getMapper(FluxLocationMapper.class);


    @Mapping(target = "typeCode", source = "typeCode.value")
    @Mapping(target = "fluxLocationCatchTypeMapperInfo", ignore = true) // Stored but only used for mapping decisions
    @Mapping(target = "typeCodeListId", source = "typeCode.listID")
    @Mapping(target = "countryId", source = "countryID.value")
    @Mapping(target = "longitude", source = "specifiedPhysicalFLUXGeographicalCoordinate.longitudeMeasure.value")
    @Mapping(target = "latitude", source = "specifiedPhysicalFLUXGeographicalCoordinate.latitudeMeasure.value")
    @Mapping(target = "countryIdSchemeId", source = "countryID.schemeID")
    @Mapping(target = "fluxLocationIdentifier", source = "ID.value")
    @Mapping(target = "fluxLocationIdentifierSchemeId", source = "ID.schemeID")
    @Mapping(target = "name", expression = "java(BaseMapper.getTextFromList(fluxLocation.getNames()))")
    @Mapping(target = "nameLanguageId", expression = "java(BaseMapper.getLanguageIdFromList(fluxLocation.getNames()))")
    @Mapping(target = "jurisdictionCountryCode", source = "jurisdictionCountryID.value")
    @Mapping(target = "systemId", source = "specifiedPhysicalFLUXGeographicalCoordinate.systemID.value")
    @Mapping(target = "regionalFisheriesManagementOrganizationCode", source = "regionalFisheriesManagementOrganizationCode.value")
    @Mapping(target = "regionalFisheriesManagementOrganizationCodeListId", source = "regionalFisheriesManagementOrganizationCode.listID")
    FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation);

    @InheritInverseConfiguration
    FLUXLocation mapToFluxLocation(FluxLocationEntity fluxLocation);

    List<FLUXLocation> mapToFluxLocationList(Set<FluxLocationEntity> fluxLocation);


    @Mapping(target = "geometry", source = "wkt")
    @Mapping(target = "jurisdictionCountry", source = "jurisdictionCountryCode")
    @Mapping(target = "rfmoCode", source = "regionalFisheriesManagementOrganizationCode")
    FluxLocationDto mapEntityToFluxLocationDto(FluxLocationEntity fluxLocation);

    Set<FluxLocationDto> mapEntityToFluxLocationDto(Set<FluxLocationEntity> fluxLocation);

}
