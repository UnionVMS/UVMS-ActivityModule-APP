/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.LocationEntity;
import org.mapstruct.*;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FluxLocationMapper {


    @Mapping(target = "typeCode", source = "typeCode.value")
    @Mapping(target = "typeCodeListId", source = "typeCode.listID")
    @Mapping(target = "countryId", source = "countryID.value")
    @Mapping(target = "countryIdSchemeId", source = "countryID.schemeID")
    @Mapping(target = "fluxLocationIdentifier", source = "ID.value")
    @Mapping(target = "fluxLocationIdentifierSchemeId", source = "ID.schemeID")
    @Mapping(target = "name", expression = "java(BaseUtil.getTextFromList(fluxLocation.getNames()))")
    @Mapping(target = "nameLanguageId", expression = "java(BaseUtil.getLanguageIdFromList(fluxLocation.getNames()))")
    @Mapping(target = "regionalFisheriesManagementOrganizationCode", source = "regionalFisheriesManagementOrganizationCode.value")
    @Mapping(target = "regionalFisheriesManagementOrganizationCodeListId", source = "regionalFisheriesManagementOrganizationCode.listID")
    LocationEntity mapToLocationEntity(FLUXLocation fluxLocation);

    @InheritInverseConfiguration
    @Mapping(target = "specifiedPhysicalFLUXGeographicalCoordinate", ignore = true)
    FLUXLocation mapToFluxLocation(LocationEntity fluxLocation);

    List<FLUXLocation> mapToFluxLocationList(Set<LocationEntity> fluxLocation);
}
