/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.VesselStorageCharCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselStorageCharacteristicsEntity;
import eu.europa.ec.fisheries.ers.service.dto.StorageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselStorageCharacteristic;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

@Mapper(uses = {VesselStorageCharCodeMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class VesselStorageCharacteristicsMapper {

    public static final VesselStorageCharacteristicsMapper INSTANCE = Mappers.getMapper(VesselStorageCharacteristicsMapper.class);

    @Mappings({
            @Mapping(target = "vesselId", source = "vesselStorageCharacteristic.ID.value"),
            @Mapping(target = "vesselSchemaId", source = "vesselStorageCharacteristic.ID.schemeID"),
            @Mapping(target = "vesselStorageCharCode", expression = "java(mapToVesselStorageCharCodes(vesselStorageCharacteristic.getTypeCodes(), vesselStorageCharacteristicsEntity))"),
    })
    public abstract VesselStorageCharacteristicsEntity mapToDestVesselStorageCharEntity(VesselStorageCharacteristic vesselStorageCharacteristic);

    @Mappings({
            @Mapping(target = "vesselTypeCode", source = "value"),
            @Mapping(target = "vesselTypeCodeListId", source = "listID")
    })
    protected abstract VesselStorageCharCodeEntity mapToVesselStorageCharCodeEntity(CodeType codeType);

    protected Set<VesselStorageCharCodeEntity> mapToVesselStorageCharCodes(List<CodeType> codeTypes, VesselStorageCharacteristicsEntity vesselStorageChar) {
        if(codeTypes == null || codeTypes.isEmpty()) {
            return Collections.emptySet();
        }
        Set<VesselStorageCharCodeEntity> vesselStorageCharCodes = new HashSet<>();
        for (CodeType codeType : codeTypes) {
            VesselStorageCharCodeEntity vesselStorageCharCode = VesselStorageCharacteristicsMapper.INSTANCE.mapToVesselStorageCharCodeEntity(codeType);
            vesselStorageCharCode.setVesselStorageCharacteristics(vesselStorageChar);
            vesselStorageCharCodes.add(vesselStorageCharCode);
        }
        return vesselStorageCharCodes;
    }

    @Mappings({
            @Mapping(target = "identifier.faIdentifierId", source = "vesselId"),
            @Mapping(target = "identifier.faIdentifierSchemeId", source = "vesselSchemaId"),
            @Mapping(target = "vesselStorageCharCodeDto", source = "firstVesselStorageCharCode"),
    })
    public abstract StorageDto mapToStorageDto(VesselStorageCharacteristicsEntity entity);

}