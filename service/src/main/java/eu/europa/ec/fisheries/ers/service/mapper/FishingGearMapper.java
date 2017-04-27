/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.ers.service.dto.FishingGearDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

@Mapper(imports = BaseMapper.class, uses = {GearCharacteristicsMapper.class})
public interface FishingGearMapper {

    FishingGearMapper INSTANCE = Mappers.getMapper(FishingGearMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "fishingGearRole", expression = "java(BaseMapper.mapToFishingGears(fishingGear.getRoleCodes(), fishingGearEntity))"),
            @Mapping(target = "gearCharacteristics", expression = "java(BaseMapper.getGearCharacteristicEntities(fishingGear.getApplicableGearCharacteristics(), fishingGearEntity))"),
    })
    FishingGearEntity mapToFishingGearEntity(FishingGear fishingGear);

    @Mappings({
            @Mapping(source = "typeCode",target = "gearTypeCode"),
    })
    FishingGearDTO mapToFishingGearDTO(FishingGearEntity fishingGearEntity);

    @Mappings({
            @Mapping(target = "roleCode", source = "value"),
            @Mapping(target = "roleCodeListId", source = "listID")
    })
    FishingGearRoleEntity mapToFishingGearRoleEntity(CodeType codeType);

}