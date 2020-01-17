/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.RegistrationLocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;

@Mapper(imports = BaseMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class RegistrationLocationMapper {

    public static final RegistrationLocationMapper INSTANCE = Mappers.getMapper(RegistrationLocationMapper.class);

    @Mapping(target = "description", expression = "java(BaseMapper.getTextFromList(registrationLocation.getDescriptions()))")
    @Mapping(target = "descLanguageId", expression = "java(BaseMapper.getLanguageIdFromList(registrationLocation.getDescriptions()))")
    @Mapping(target = "regionCode", source = "geopoliticalRegionCode.value")
    @Mapping(target = "regionCodeListId", source = "geopoliticalRegionCode.listID")
    @Mapping(target = "name", expression = "java(BaseMapper.getTextFromList(registrationLocation.getNames()))")
    @Mapping(target = "nameLanguageId", expression = "java(BaseMapper.getLanguageIdFromList(registrationLocation.getNames()))")
    @Mapping(target = "typeCode", source = "typeCode.value")
    @Mapping(target = "typeCodeListId", source = "typeCode.listID")
    @Mapping(target = "locationCountryId", source = "countryID.value")
    @Mapping(target = "locationCountrySchemeId", source = "countryID.schemeID")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationEvent", ignore = true)
    public abstract RegistrationLocationEntity mapToRegistrationLocationEntity(RegistrationLocation registrationLocation);
}
