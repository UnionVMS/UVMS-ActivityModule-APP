/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseViewMapper;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.ArrivalDto;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.GearDto;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.*;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_GD;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.GEAR_CHARAC_TYPE_CODE_QG;

/**
 * Created by kovian on 09/02/2017.
 */
@Mapper
public abstract class ActivityArrivalViewMapper extends BaseViewMapper {

    public static final ActivityArrivalViewMapper INSTANCE = Mappers.getMapper(ActivityArrivalViewMapper.class);

    @Override
    @Mappings({
            @Mapping(target = "arrival",   expression = "java(getArrivalFromFishingEntity(faEntity))"),
            @Mapping(target = "ports",     expression = "java(getPortsFromFluxLocation(faEntity.getFluxLocations()))"),
            @Mapping(target = "gears",     expression = "java(getGearsFromEntity(faEntity.getFishingGears()))"),
            @Mapping(target = "reportDoc", expression = "java(getReportDocsFromEntity(faEntity.getFaReportDocument()))")
    })
    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity, @MappingTarget FishingActivityViewDTO viewDto);


    protected ArrivalDto getArrivalFromFishingEntity(FishingActivityEntity faEntity) {
        ArrivalDto arrival = new ArrivalDto();
        arrival.setArrivalTime(DateUtils.dateToString(faEntity.getOccurence()));
        arrival.setIntendedLandingTime(extractLandingTime(faEntity.getFluxCharacteristics()));
        arrival.setReason(faEntity.getReasonCode());
        return arrival;
    }


    protected List<GearDto> getGearsFromEntity(Set<FishingGearEntity> fishingGearEntities) {
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


    private String extractLandingTime(Set<FluxCharacteristicEntity> fluxCharacteristics) {
        if(CollectionUtils.isNotEmpty(fluxCharacteristics)){
            for(FluxCharacteristicEntity charact : fluxCharacteristics){
                if(StringUtils.equals("FA_CHARACTERISTIC", charact.getTypeCodeListId())
                        && StringUtils.equals("START_DATETIME_LANDING", charact.getTypeCode())){
                    return DateUtils.dateToString(charact.getValueDateTime());
                }
            }
        }
        return null;
    }


    private void fillRoleAndCharacteristics(GearDto gearDto, FishingGearEntity gearEntity) {
        Set<FishingGearRoleEntity> fishingGearRole = gearEntity.getFishingGearRole();
        if (CollectionUtils.isNotEmpty(fishingGearRole)) {
            FishingGearRoleEntity role = fishingGearRole.iterator().next();
            gearDto.setRole(role.getRoleCode());
        }
        Set<GearCharacteristicEntity> gearCharacteristics = gearEntity.getGearCharacteristics();
        if (CollectionUtils.isNotEmpty(gearCharacteristics)) {
            for(GearCharacteristicEntity charac : gearCharacteristics){
                fillCharacteristicField(charac, gearDto);
            }
        }
    }

    private void fillCharacteristicField(GearCharacteristicEntity charac, GearDto gearDto) {
        String quantityOnly     = charac.getValueMeasure() != null ? charac.getValueMeasure().toString() : StringUtils.EMPTY;
        String quantityWithUnit = new  StringBuilder(quantityOnly).append(charac.getValueMeasureUnitCode()).toString();
        switch(charac.getTypeCode()){
            case GEAR_CHARAC_TYPE_CODE_ME :
                gearDto.setMeshSize(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_GM :
                gearDto.setLengthWidth(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_GN :
                gearDto.setNumberOfGears(Integer.parseInt(quantityOnly));
                break;
            case GEAR_CHARAC_TYPE_CODE_HE :
                gearDto.setHeight(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_NI :
                gearDto.setNrOfLines(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_NN :
                gearDto.setNrOfNets(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_NL :
                gearDto.setNominalLengthOfNet(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_QG :
                if(charac.getValueQuantityCode() != GEAR_CHARAC_Q_CODE_C62){
                    gearDto.setQuantity(quantityWithUnit);
                }
                break;
            case GEAR_CHARAC_TYPE_CODE_GD :
                gearDto.setDescription(charac.getDescription());
                break;
            default :
                break;
        }
    }

}


