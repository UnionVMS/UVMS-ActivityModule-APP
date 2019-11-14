/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.mapper.view.base;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.facatch.FaCatchGroupDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.AreaDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.GearDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ProcessingProductsDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.RelocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.AapProductMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.AreaDtoMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.BaseMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FaCatchMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.view.FaCatchesProcessorMapper;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

    protected AreaDto getSortedAreas(FishingActivityEntity faEntity, Comparator<FluxLocationDto> comparator) {
        AreaDto areaDto = getAreas(faEntity);
        final List<FluxLocationDto> fluxLocationDtos = new ArrayList<>(areaDto.getFluxLocations());

        TreeSet<FluxLocationDto> fluxLocationDtoTreeSet = new TreeSet<FluxLocationDto>(comparator);
        fluxLocationDtoTreeSet.addAll(fluxLocationDtos);
        areaDto.setFluxLocations(fluxLocationDtoTreeSet);

        return areaDto;
    }

    protected List<RelocationDto> getRelocations(FishingActivityEntity fishingActivityEntity) {
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

    private List<ProcessingProductsDto> getProcessingProductByAapProcess(Collection<AapProcessEntity> aapProcesses) {
        if (aapProcesses == null) {
            return Collections.emptyList();
        }
        List<ProcessingProductsDto> productsDtos = new ArrayList<>();
        for (AapProcessEntity aapProcessEntity : aapProcesses) {
            productsDtos.addAll(getPPByAapProduct(aapProcessEntity.getAapProducts()));
        }
        return productsDtos;
    }

    private List<ProcessingProductsDto> getPPByAapProduct(Collection<AapProductEntity> aapProducts) {
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

    protected Set<FaCatchGroupDto> mapCatchesToGroupDto(FishingActivityEntity faEntity) {
        return FaCatchesProcessorMapper.getCatchGroupsFromListEntity(faEntity.getFaCatchs());
    }

}