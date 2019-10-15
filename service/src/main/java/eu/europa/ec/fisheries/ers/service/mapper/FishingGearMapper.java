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

import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.ers.service.dto.FishingGearDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

@Mapper(uses = {GearCharacteristicsMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FishingGearMapper {

    FishingGearMapper INSTANCE = Mappers.getMapper(FishingGearMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "fishingGearRole", ignore = true),
            @Mapping(target = "gearCharacteristics", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "faCatch", ignore = true),
            @Mapping(target = "fishingActivity", ignore = true),
            @Mapping(target = "gearProblem", ignore = true)
    })
    FishingGearEntity mapToFishingGearEntity(FishingGear fishingGear);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "roleCodes", source = "fishingGearRole"),
            @Mapping(target = "applicableGearCharacteristics", source = "gearCharacteristics"),
            @Mapping(target = "illustrateFLUXPictures", ignore = true)
    })
    FishingGear mapToFishingGear(FishingGearEntity fishingGearEntity);

    List<FishingGear> mapToFishingGearList(Set<FishingGearEntity> fishingGearEntitySet);

    @Mappings({
            @Mapping(source = "typeCode",target = "gearTypeCode"),
            @Mapping(target = "gearRoleCode", ignore = true)
    })
    FishingGearDTO mapToFishingGearDTO(FishingGearEntity fishingGearEntity);

    @Mappings({
            @Mapping(target = "roleCode", source = "value"),
            @Mapping(target = "roleCodeListId", source = "listID"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "fishingGear", ignore = true)
    })
    FishingGearRoleEntity mapToFishingGearRoleEntity(CodeType codeType);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "listAgencyID", ignore = true),
            @Mapping(target = "listAgencyName", ignore = true),
            @Mapping(target = "listName", ignore = true),
            @Mapping(target = "listVersionID", ignore = true),
            @Mapping(target = "name", ignore = true),
            @Mapping(target = "languageID", ignore = true),
            @Mapping(target = "listURI", ignore = true),
            @Mapping(target = "listSchemeURI", ignore = true)
    })
    CodeType mapToFishingGearRole(FishingGearRoleEntity codeType);

}