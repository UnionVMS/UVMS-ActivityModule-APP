/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view.base;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.GearDto;
import eu.europa.ec.fisheries.ers.service.dto.view.facatch.FaCatchGroupDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.model.StringWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.MappingTarget;

import java.util.*;

import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.*;

/**
 * Created by kovian on 14/02/2017.
 *
 * Base Class to be extended by all the mappers related to Activity Views (LANDING, ARRIVAL, AREA_ENTRY  etc..)
 */
public abstract class BaseActivityViewMapper {

    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity, @MappingTarget FishingActivityViewDTO viewDto);

    /**
     * This method will populate the type, fluxCharacteristics and occurrence and will leave the population of the
     * other fields to the specific (the class that extends BaseActivityViewMapper) implementation of  populateActivityDetails(faEntity, activityDetails).
     *
     * @param faEntity
     * @return
     */
    protected ActivityDetailsDto mapActivityDetails(FishingActivityEntity faEntity){
        ActivityDetailsDto activityDetails = new ActivityDetailsDto();
        activityDetails.setType(faEntity.getTypeCode());
        activityDetails.setOccurrence(faEntity.getOccurence());
        Set<FluxCharacteristicEntity> fluxCharacteristics = faEntity.getFluxCharacteristics();
        if(CollectionUtils.isNotEmpty(fluxCharacteristics)){
            Map<String, String> characMap = new HashMap<>();
            for(FluxCharacteristicEntity fluxCharac : fluxCharacteristics){
                characMap.put(fluxCharac.getTypeCode(), fluxCharac.getValueText());
            }
            activityDetails.setCharacteristics(characMap);
        }
        return populateActivityDetails(faEntity, activityDetails);
    }

    /**
     * The method mapActivityDetails(..,..) maps type and fluxCharacteristics to the ActivityDetailsDto.
     * The rest of the fields are mapped by this method which MUST be implemented by all the Mappers that extend this one.
     *
     * @param faEntity
     * @param activityDetails
     * @return
     */
    protected abstract ActivityDetailsDto populateActivityDetails(FishingActivityEntity faEntity, ActivityDetailsDto activityDetails);

    protected List<FluxLocationDto> getPortsFromFluxLocation(Set<FluxLocationEntity> fLocEntities) {
        if (CollectionUtils.isEmpty(fLocEntities)) {
            return null;
        }
        List<FluxLocationDto> portsListDto = new ArrayList<>();
        for(FluxLocationEntity flEntity : fLocEntities){
            FluxLocationDto port = new FluxLocationDto();
            StringWrapper geomStrWrapper = GeometryMapper.INSTANCE.geometryToWkt(flEntity.getGeom());
            if(geomStrWrapper != null){
                port.setGeometry(geomStrWrapper.getValue());
            }
            port.setName(flEntity.getFluxLocationIdentifierSchemeId());
            portsListDto.add(port);
        }
        return portsListDto;
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

    public List<GearDto> getGearsFromEntity(Set<FishingGearEntity> fishingGearEntities) {
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

    protected FaCatchGroupDetailsDto getCatchesFromEntity(FishingActivityEntity faEntity){
        FaCatchGroupDetailsDto catchDto = new FaCatchGroupDetailsDto();
        return catchDto;
    }

}
