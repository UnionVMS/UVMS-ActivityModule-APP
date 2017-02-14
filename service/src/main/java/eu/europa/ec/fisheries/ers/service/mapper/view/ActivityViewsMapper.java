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
import eu.europa.ec.fisheries.ers.service.mapper.BaseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.ArrivalDto;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.GearDto;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.PortDto;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.ReportDocumentDto;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.model.StringWrapper;
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

/**
 * Created by kovian on 09/02/2017.
 */
@Mapper
public abstract class ActivityViewsMapper extends BaseMapper {

    public static final ActivityViewsMapper INSTANCE = Mappers.getMapper(ActivityViewsMapper.class);

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


    protected List<PortDto> getPortsFromFluxLocation(Set<FluxLocationEntity> fLocEntities) {
        if (CollectionUtils.isEmpty(fLocEntities)) {
            return null;
        }
        List<PortDto> portsListDto = new ArrayList<>();
        for(FluxLocationEntity flEntity : fLocEntities){
            PortDto port = new PortDto();
            StringWrapper geomStrWrapper = GeometryMapper.INSTANCE.geometryToWkt(flEntity.getGeom());
            if(geomStrWrapper != null){
                port.setGeometry(geomStrWrapper.getValue());
            }
            port.setName(flEntity.getFluxLocationIdentifierSchemeId());
            portsListDto.add(port);
        }
        return portsListDto;
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


    protected ReportDocumentDto getReportDocsFromEntity(FaReportDocumentEntity faRepDocEntity){
        if(faRepDocEntity == null){
            return null;
        }
        ReportDocumentDto repDocDto = new ReportDocumentDto();
        repDocDto.setType(faRepDocEntity.getTypeCode());
        repDocDto.setDateAccepted(DateUtils.dateToString(faRepDocEntity.getAcceptedDatetime()));

        FluxReportDocumentEntity fluxReportDocument = faRepDocEntity.getFluxReportDocument();
        if(fluxReportDocument != null){
            repDocDto.setCreationDate(DateUtils.dateToString(fluxReportDocument.getCreationDatetime()));
            repDocDto.setId(fluxReportDocument.getReferenceSchemeId());
            repDocDto.setRefId(fluxReportDocument.getReferenceId());
            repDocDto.setPurpose(fluxReportDocument.getPurpose());
            repDocDto.setPurposeCode(fluxReportDocument.getPurposeCode());
        }
        return repDocDto;
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
            case "ME" :
                gearDto.setMeshSize(quantityWithUnit);
                break;
            case "GM" :
                gearDto.setLengthWidth(quantityWithUnit);
                break;
            case "GN" :
                gearDto.setNumberOfGears(Integer.parseInt(quantityOnly));
                break;
            case "HE" :
                gearDto.setHeight(quantityWithUnit);
                break;
            case "NI" :
                gearDto.setNrOfLines(quantityWithUnit);
                break;
            case "NN" :
                gearDto.setNrOfNets(quantityWithUnit);
                break;
            case "NL" :
                gearDto.setNominalLengthOfNet(quantityWithUnit);
                break;
            case "QG" :
                if(charac.getValueQuantityCode() != "C62"){
                    gearDto.setQuantity(quantityWithUnit);
                }
                break;
            case "GD" :
                gearDto.setDescription(charac.getDescription());
                break;
            default :
                break;
        }
    }

}


