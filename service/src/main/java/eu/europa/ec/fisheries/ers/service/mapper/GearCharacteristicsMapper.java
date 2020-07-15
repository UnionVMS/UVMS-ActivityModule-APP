/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_Q_CODE_C62;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_GD;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_GM;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_GN;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_HE;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_ME;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_NI;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_NL;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_NN;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_QG;
import static org.mockito.internal.util.collections.Sets.newSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.GearDto;
import eu.europa.ec.fisheries.ers.service.util.CustomBigDecimal;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearCharacteristic;

@Mapper(imports = BaseMapper.class, uses = CustomBigDecimal.class)
public abstract class GearCharacteristicsMapper {

    public static final GearCharacteristicsMapper INSTANCE = Mappers.getMapper(GearCharacteristicsMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "description", expression = "java(BaseMapper.getTextFromList(gearCharacteristic.getDescriptions()))"),
            @Mapping(target = "descLanguageId", expression = "java(BaseMapper.getLanguageIdFromList(gearCharacteristic.getDescriptions()))"),
            @Mapping(target = "valueMeasure", source = "valueMeasure.value"),
            @Mapping(target = "valueMeasureUnitCode", source = "valueMeasure.unitCode"),
            @Mapping(target = "valueDateTime", source = "valueDateTime.dateTime"),
            @Mapping(target = "valueIndicator", source = "valueIndicator.indicatorString.value"),
            @Mapping(target = "valueCode", source = "valueCode.value"),
            @Mapping(target = "valueText", source = "value.value"),
            @Mapping(target = "valueQuantity", source = "valueQuantity.value"),
            @Mapping(target = "valueQuantityCode", source = "valueQuantity.unitCode"),
    })
    public abstract GearCharacteristicEntity mapToGearCharacteristicEntity(GearCharacteristic gearCharacteristic);

    @InheritInverseConfiguration
    public abstract GearCharacteristic mapToGearCharacteristic(GearCharacteristicEntity gearCharacteristic);

    public GearDto mapGearDtoToFishingGearEntity(FishingGearEntity fishingGearEntity) {
        GearDto gearDto = null;
        List<GearDto> gearDtos = mapFishingGearEntitiesToGearDtos(newSet(fishingGearEntity));
        if (!CollectionUtils.isEmpty(gearDtos)){
            gearDto = gearDtos.get(0);

        }
        return gearDto;
    }

    public List<GearDto> mapFishingGearEntitiesToGearDtos(Set<FishingGearEntity> fishingGearEntities) {
        if (CollectionUtils.isEmpty(fishingGearEntities)) {
            return Collections.emptyList();
        }
        List<GearDto> gearDtoList = new ArrayList<>();
        for (FishingGearEntity gearEntity : fishingGearEntities) {
            GearDto gearDto = new GearDto();
            gearDto.setType(gearEntity.getTypeCode());
            fillRoleAndCharacteristics(gearDto, gearEntity);
            gearDtoList.add(gearDto);
        }
        return gearDtoList;
    }

    private void fillRoleAndCharacteristics(GearDto gearDto, FishingGearEntity gearEntity) {
        Set<FishingGearRoleEntity> fishingGearRole = gearEntity.getFishingGearRole();
        if (CollectionUtils.isNotEmpty(fishingGearRole)) {
            FishingGearRoleEntity role = fishingGearRole.iterator().next();
            gearDto.setRole(role.getRoleCode());
        }
        Set<GearCharacteristicEntity> gearCharacteristics = gearEntity.getGearCharacteristics();
        if (CollectionUtils.isNotEmpty(gearCharacteristics)) {
            for (GearCharacteristicEntity charac : gearCharacteristics) {
                fillCharacteristicField(charac, gearDto);
            }
        }
    }

    private void fillCharacteristicField(GearCharacteristicEntity charac, GearDto gearDto) {

        String quantityOnly = charac.getValueMeasure() != null ? charac.getValueMeasure().toString() : StringUtils.EMPTY;
        String quantityWithUnit = quantityOnly + charac.getValueMeasureUnitCode();

        switch (charac.getTypeCode()) {
            case GEAR_CHARAC_TYPE_CODE_ME:
                gearDto.setMeshSize(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_GM:
                gearDto.setLengthWidth(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_GN:
                gearDto.setNumberOfGears(Integer.parseInt(quantityOnly));
                break;
            case GEAR_CHARAC_TYPE_CODE_HE:
                gearDto.setHeight(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_NI:
                gearDto.setNrOfLines(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_NN:
                gearDto.setNrOfNets(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_NL:
                gearDto.setNominalLengthOfNet(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_QG:
                if (!Objects.equals(charac.getValueQuantityCode(), GEAR_CHARAC_Q_CODE_C62)) {
                    gearDto.setQuantity(quantityWithUnit);
                }
                break;
            case GEAR_CHARAC_TYPE_CODE_GD:
                gearDto.setDescription(charac.getDescription());
                break;
            default:
                break;
        }
    }
}