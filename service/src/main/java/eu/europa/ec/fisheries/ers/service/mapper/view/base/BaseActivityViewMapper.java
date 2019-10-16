/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view.base;

import java.util.*;
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.ers.service.dto.facatch.FaCatchGroupDto;
import eu.europa.ec.fisheries.ers.service.dto.view.*;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.*;
import eu.europa.ec.fisheries.ers.service.mapper.view.FaCatchesProcessorMapper;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import static eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants.*;

/**
 * Created by kovian on 14/02/2017.
 * <p>
 * Base Class to be extended by all the mappers related to Activity Views (LANDING, ARRIVAL, AREA_ENTRY  etc..)
 */
public abstract class BaseActivityViewMapper extends BaseMapper {

    public static AreaDto getAreas(FishingActivityEntity faEntity) {
        AreaDto areaDto = new AreaDtoMapper().mapToAreaDto(faEntity);
        areaDto.setFluxLocations(mapFromFluxLocation(faEntity.getFluxLocations(), FluxLocationEnum.AREA));
        return areaDto;
    }

    public AreaDto getSortedAreas(FishingActivityEntity faEntity, Comparator<FluxLocationDto> comparator) {
        AreaDto areaDto = getAreas(faEntity);
        final List<FluxLocationDto> fluxLocationDtos = new ArrayList<>(areaDto.getFluxLocations());

        TreeSet<FluxLocationDto> fluxLocationDtoTreeSet = new TreeSet<FluxLocationDto>(comparator);
        fluxLocationDtoTreeSet.addAll(fluxLocationDtos);
        areaDto.setFluxLocations(fluxLocationDtoTreeSet);

        return areaDto;
    }

    public List<RelocationDto> getRelocations(FishingActivityEntity fishingActivityEntity) {
        List<RelocationDto> relocationDtos = new ArrayList<>();
        Set<FishingActivityEntity> relatedActivities = fishingActivityEntity.getAllRelatedFishingActivities();
        for (FishingActivityEntity fishingActivity : relatedActivities) {
            if (fishingActivity.getTypeCode().toUpperCase().equalsIgnoreCase(ActivityViewEnum.RELOCATION.name())) {
                relocationDtos.addAll(FaCatchMapper.INSTANCE.mapToRelocationDtoList(fishingActivity.getFaCatchs()));
            }
        }
        return relocationDtos;
    }

    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity);

    /**
     * The method mapActivityDetails(..,..) maps type and fluxCharacteristics to the ActivityDetailsDto.
     * The rest of the fields are mapped by this method which CAN be implemented by all the Mappers that extend this one.
     *
     * @param faEntity
     * @param activityDetails
     * @return
     */
    protected ActivityDetailsDto populateActivityDetails(FishingActivityEntity faEntity, ActivityDetailsDto activityDetails) {
        return activityDetails;
    }

    protected List<ProcessingProductsDto> getProcessingProductsByFaCatches(Collection<FaCatchEntity> faCatches) {
        if (faCatches == null) {
            return Collections.emptyList();
        }
        List<ProcessingProductsDto> productsDtos = new ArrayList<>();
        for (FaCatchEntity faCatchEntity : faCatches) {
            productsDtos.addAll(getProcessingProductByAapProcess(faCatchEntity.getAapProcesses()));
        }
        return productsDtos;
    }

    protected List<ProcessingProductsDto> getProcessingProductByAapProcess(Collection<AapProcessEntity> aapProcesses) {
        if (aapProcesses == null) {
            return Collections.emptyList();
        }
        List<ProcessingProductsDto> productsDtos = new ArrayList<>();
        for (AapProcessEntity aapProcessEntity : aapProcesses) {
            productsDtos.addAll(getPPByAapProduct(aapProcessEntity.getAapProducts()));
        }
        return productsDtos;
    }

    protected List<ProcessingProductsDto> getPPByAapProduct(Collection<AapProductEntity> aapProducts) {
        if (aapProducts == null) {
            return Collections.emptyList();
        }
        List<ProcessingProductsDto> productsDtos = new ArrayList<>();
        for (AapProductEntity aapProductEntity : aapProducts) {
            productsDtos.add(AapProductMapper.INSTANCE.mapToProcessingProduct(aapProductEntity));
        }
        return productsDtos;
    }

    protected Map<String, String> getFluxCharacteristicsTypeCodeValue(Set<FluxCharacteristicEntity> fluxCharacteristics) {
        if (fluxCharacteristics == null) {
            return Collections.emptyMap();
        }
        Map<String, String> characMap = new HashMap<>();
        for (FluxCharacteristicEntity fluxCharacteristic : fluxCharacteristics) {
            String value = null;
            if (fluxCharacteristic.getValueMeasure() != null) {
                value = String.valueOf(fluxCharacteristic.getValueMeasure());
            } else if (fluxCharacteristic.getValueDateTime() != null) {
                value = DateUtils.dateToString(fluxCharacteristic.getValueDateTime());
            } else if (fluxCharacteristic.getValueIndicator() != null) {
                value = fluxCharacteristic.getValueIndicator();
            } else if (fluxCharacteristic.getValueCode() != null) {
                value = fluxCharacteristic.getValueCode();
            } else if (fluxCharacteristic.getValueText() != null) {
                value = fluxCharacteristic.getValueText();
            } else if (fluxCharacteristic.getValueQuantity() != null) {
                value = String.valueOf(fluxCharacteristic.getValueQuantity());
            }
            characMap.put(fluxCharacteristic.getTypeCode(), value);
        }
        return characMap;
    }

    /**
     * This method will populate the type, fluxCharacteristics and occurrence and will leave the population of the
     * other fields to the specific (the class that extends BaseActivityViewMapper) implementation of  populateActivityDetails(faEntity, activityDetails).
     *
     * @param faEntity
     * @return
     */
    protected ActivityDetailsDto mapActivityDetails(FishingActivityEntity faEntity) {
        ActivityDetailsDto activityDetails = FishingActivityMapper.INSTANCE.mapFishingActivityEntityToActivityDetailsDto(faEntity);
        return populateActivityDetails(faEntity, activityDetails);
    }

    public ReportDocumentDto getReportDocsFromEntity(FaReportDocumentEntity faRepDocEntity) {
        return FaReportDocumentMapper.INSTANCE.mapFaReportDocumentToReportDocumentDto(faRepDocEntity);
    }

    public List<GearDto> getGearsFromEntity(Set<FishingGearEntity> fishingGearEntities) {
        if (CollectionUtils.isEmpty(fishingGearEntities)) {
            return Collections.emptyList();
        }
        List<GearDto> gearDtoList = new ArrayList<>();
        for (FishingGearEntity gearEntity : fishingGearEntities) {
            gearDtoList.add(mapSingleGearToDto(gearEntity));
        }
        return gearDtoList;
    }

    private GearDto mapSingleGearToDto(FishingGearEntity gearEntity) {
        GearDto gearDto = new GearDto();
        gearDto.setType(gearEntity.getTypeCode());
        fillRoleAndCharacteristics(gearDto, gearEntity);
        return gearDto;
    }

    protected GearDto mapToFirstFishingGear(Set<FishingGearEntity> fishingGearEntities) {
        if (CollectionUtils.isEmpty(fishingGearEntities)) {
            return null;
        }
        return mapSingleGearToDto(fishingGearEntities.iterator().next());
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
        String quantityWithUnit = new StringBuilder(quantityOnly).append(charac.getValueMeasureUnitCode()).toString();
        switch (charac.getTypeCode()) {
            case GEAR_CHARAC_TYPE_CODE_ME:
                gearDto.setMeshSize(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_GM:
                gearDto.setLengthWidth(quantityWithUnit);
                break;
            case GEAR_CHARAC_TYPE_CODE_GN:
                gearDto.setNumberOfGears(StringUtils.isEmpty(quantityOnly) ? 0 : charac.getValueMeasure().intValue());
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
                String description = charac.getDescription();
                if (StringUtils.isNoneEmpty(description)){
                    gearDto.setDescription(charac.getDescription());
                }
                else {
                    gearDto.setDescription(charac.getValueText());
                }
                break;
            default:
                break;
        }
    }

    protected Set<FaCatchGroupDto> mapCatchesToGroupDto(FishingActivityEntity faEntity) {
        return FaCatchesProcessorMapper.getCatchGroupsFromListEntity(faEntity.getFaCatchs());
    }

}
