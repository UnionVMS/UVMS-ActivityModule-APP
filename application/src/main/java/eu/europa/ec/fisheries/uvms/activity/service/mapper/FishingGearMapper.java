/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearRoleEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi", uses = {GearCharacteristicsMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FishingGearMapper {


    @Mapping(target = "typeCode", source = "typeCode.value")
    @Mapping(target = "typeCodeListId", source = "typeCode.listID")
    @Mapping(target = "fishingGearRole", ignore = true)
    @Mapping(target = "gearCharacteristics", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "faCatch", ignore = true)
    @Mapping(target = "fishingActivity", ignore = true)
    @Mapping(target = "gearProblem", ignore = true)
    public abstract FishingGearEntity mapToFishingGearEntity(FishingGear fishingGear);

    @InheritInverseConfiguration
    @Mapping(target = "roleCodes", source = "fishingGearRole")
    @Mapping(target = "applicableGearCharacteristics", source = "gearCharacteristics")
    @Mapping(target = "illustrateFLUXPictures", ignore = true)
    public abstract FishingGear mapToFishingGear(FishingGearEntity fishingGearEntity);

    public abstract List<FishingGear> mapToFishingGearList(Set<FishingGearEntity> fishingGearEntitySet);

    @Mapping(target = "roleCode", source = "value")
    @Mapping(target = "roleCodeListId", source = "listID")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fishingGear", ignore = true)
    public abstract FishingGearRoleEntity mapToFishingGearRoleEntity(CodeType codeType);

    @InheritInverseConfiguration
    @Mapping(target = "listAgencyID", ignore = true)
    @Mapping(target = "listAgencyName", ignore = true)
    @Mapping(target = "listName", ignore = true)
    @Mapping(target = "listVersionID", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "languageID", ignore = true)
    @Mapping(target = "listURI", ignore = true)
    @Mapping(target = "listSchemeURI", ignore = true)
    public abstract CodeType mapToFishingGearRole(FishingGearRoleEntity codeType);

}
