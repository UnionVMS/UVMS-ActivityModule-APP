/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapStockEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPStock;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SizeDistribution;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

import javax.inject.Inject;
import java.util.*;

@Mapper(componentModel = "cdi", uses = {AapProcessMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FaCatchMapper extends BaseMapper {

    @Inject
    FluxLocationMapper LOCATION_MAPPER;

    @Inject
    AapStockMapper aapStockMapper;

    @Inject
    FishingGearMapper fishingGearMapper;

    @Mapping(target = "typeCode", source = "typeCode.value")
    @Mapping(target = "typeCodeListId", source = "typeCode.listID")
    @Mapping(target = "speciesCode", source = "speciesCode.value")
    @Mapping(target = "speciesCodeListid", source = "speciesCode.listID")
    @Mapping(target = "unitQuantity", source = "unitQuantity.value")
    @Mapping(target = "unitQuantityCode", source = "unitQuantity.unitCode")
    @Mapping(target = "fishClassCode", expression = "java(mapToFishClassCode(faCatch.getSpecifiedSizeDistribution()))")
    @Mapping(target = "weightMeasure", source = "weightMeasure.value")
    @Mapping(target = "weightMeasureUnitCode", source = "weightMeasure.unitCode")
    @Mapping(target = "usageCode", source = "faCatch.usageCode.value")
    @Mapping(target = "usageCodeListId", source = "faCatch.usageCode.listID")
    @Mapping(target = "weighingMeansCode", source = "faCatch.weighingMeansCode.value")
    @Mapping(target = "weighingMeansCodeListId", source = "faCatch.weighingMeansCode.listID")
    @Mapping(target = "sizeDistributionCategoryCode", expression = "java(getSizeDistributionCategoryCode(faCatch.getSpecifiedSizeDistribution()))")
    @Mapping(target = "sizeDistributionCategoryCodeListId", expression = "java(getSizeDistributionCategoryCodeListId(faCatch.getSpecifiedSizeDistribution()))")
    @Mapping(target = "sizeDistributionClassCode", expression = "java(getSizeDistributionClassCode(faCatch.getSpecifiedSizeDistribution()))")
    @Mapping(target = "sizeDistributionClassCodeListId", expression = "java(getSizeDistributionClassCodeListId(faCatch.getSpecifiedSizeDistribution()))")
    @Mapping(target = "aapProcesses", ignore = true)
    @Mapping(target = "fishingGears", expression = "java(getFishingGearEntities(faCatch.getUsedFishingGears(), faCatchEntity))")
    @Mapping(target = "locations", expression = "java(getLocFluxLocationEntities(faCatch.getSpecifiedFLUXLocations()))")
    @Mapping(target = "destinations", expression = "java(getDestFluxLocationEntities(faCatch.getDestinationFLUXLocations()))")
    @Mapping(target = "fluxCharacteristics", expression = "java(getFluxCharacteristicEntities(faCatch.getApplicableFLUXCharacteristics(), faCatchEntity))")
    @Mapping(target = "aapStocks", expression = "java(getAapStockEntities(faCatch.getRelatedAAPStocks(), faCatchEntity))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fishingActivity", ignore = true)
    @Mapping(target = "calculatedUnitQuantity", ignore = true)
    @Mapping(target = "calculatedWeightMeasure", ignore = true)
    @Mapping(target = "presentation", ignore = true)
    //TODO: Fix this
    @Mapping(target = "fishingTrip", expression = "java(getFishingTripEntity(faCatch.getRelatedFishingTrips()))")
    @Mapping(target = "gearTypeCode", ignore = true)
    public abstract FaCatchEntity mapToFaCatchEntity(FACatch faCatch);

    @InheritInverseConfiguration
    @Mapping(target = "appliedAAPProcesses", source = "aapProcesses")
    @Mapping(target = "specifiedSizeDistribution", expression="java(getSizeDistribution(faCatch))")
    @Mapping(target = "relatedFishingTrips", ignore = true)
    @Mapping(target = "relatedAAPStocks", ignore = true)
    @Mapping(target = "relatedSalesBatches", ignore = true)
    @Mapping(target = "specifiedFLUXLocations", ignore = true)
    @Mapping(target = "usedFishingGears", ignore = true)
    @Mapping(target = "applicableFLUXCharacteristics", ignore = true)
    @Mapping(target = "destinationFLUXLocations", ignore = true)
    public abstract FACatch mapToFaCatch(FaCatchEntity faCatch);

    protected String mapToFishClassCode(SizeDistribution sizeDistrib){
        if(sizeDistrib != null && CollectionUtils.isNotEmpty(sizeDistrib.getClassCodes())){
            return sizeDistrib.getClassCodes().iterator().next().getValue();
        }
        return StringUtils.EMPTY;
    }

    protected FishingTripEntity getFishingTripEntity(List<FishingTrip> relatedFishingTrips) {
        if (CollectionUtils.isEmpty(relatedFishingTrips)) {
            return null;
        }
        FishingTrip fishingTrip = relatedFishingTrips.get(0);
        return FishingTripEntity.create(fishingTrip);
    }

    protected SizeDistribution getSizeDistribution(FaCatchEntity faCatch) {
        if (faCatch == null) {
            return null;
        }

        SizeDistribution result = new SizeDistribution();
        if (faCatch.getSizeDistributionCategoryCode() != null && faCatch.getSizeDistributionCategoryCodeListId() != null) {
            CodeType code = new CodeType();
            code.setValue(faCatch.getSizeDistributionCategoryCode());
            code.setListID(faCatch.getSizeDistributionCategoryCodeListId());
            result.setCategoryCode(code);
        }

        if (faCatch.getSizeDistributionClassCode() != null && faCatch.getSizeDistributionClassCodeListId() != null) {
            CodeType code = new CodeType();
            code.setValue(faCatch.getSizeDistributionClassCode());
            code.setListID(faCatch.getSizeDistributionClassCodeListId());
            result.setClassCodes(Arrays.asList(code));
        }

        return result;
    }

    protected String getSizeDistributionCategoryCode(SizeDistribution sizeDistribution) {
        if (sizeDistribution == null || sizeDistribution.getCategoryCode() == null) {
            return null;
        }

        return sizeDistribution.getCategoryCode().getValue();
    }

    protected String getSizeDistributionCategoryCodeListId(SizeDistribution sizeDistribution) {
        if (sizeDistribution == null || sizeDistribution.getCategoryCode() == null) {
            return null;
        }

        return sizeDistribution.getCategoryCode().getListID();
    }

    protected String getSizeDistributionClassCode(SizeDistribution sizeDistribution) {
        if (sizeDistribution == null || sizeDistribution.getClassCodes() == null || sizeDistribution.getClassCodes().isEmpty()) {
            return null;
        }

        return sizeDistribution.getClassCodes().get(0).getValue();
    }

    protected String getSizeDistributionClassCodeListId(SizeDistribution sizeDistribution) {
        if (sizeDistribution == null || sizeDistribution.getClassCodes() == null || sizeDistribution.getClassCodes().isEmpty()) {
            return null;
        }

        return sizeDistribution.getClassCodes().get(0).getListID();
    }

    protected Set<AapStockEntity> getAapStockEntities(List<AAPStock> aapStocks, FaCatchEntity faCatchEntity) {
        if (aapStocks == null || aapStocks.isEmpty()) {
            return Collections.emptySet();
        }
        Set<AapStockEntity> aapStockEntities = new HashSet<>();
        for (AAPStock aapStock : aapStocks) {
            AapStockEntity aapStockEntity = aapStockMapper.mapToAapStockEntity(aapStock);
            aapStockEntity.setFaCatch(faCatchEntity);
            aapStockEntities.add(aapStockEntity);
        }
        return aapStockEntities;
    }

    protected Set<FluxCharacteristicEntity> getFluxCharacteristicEntities(List<FLUXCharacteristic> fluxCharacteristics, FaCatchEntity faCatchEntity) {
        if (fluxCharacteristics == null || fluxCharacteristics.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FluxCharacteristicEntity> fluxCharacteristicEntities = new HashSet<>();
        for (FLUXCharacteristic fluxCharacteristic : fluxCharacteristics) {
            FluxCharacteristicEntity fluxCharacteristicEntity = FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(fluxCharacteristic);
            fluxCharacteristicEntity.setFaCatch(faCatchEntity);
            fluxCharacteristicEntities.add(fluxCharacteristicEntity);
        }
        return fluxCharacteristicEntities;
    }

    protected Set<FluxLocationEntity> getLocFluxLocationEntities(List<FLUXLocation> specifiedFluxLocations) {
        Set<FluxLocationEntity> fluxLocationEntities = new HashSet<>();
        for (FLUXLocation fluxLocation : Utils.safeIterable(specifiedFluxLocations)) {
            FluxLocationEntity fluxLocationEntity = LOCATION_MAPPER.mapToFluxLocationEntity(fluxLocation);
            // We ignore fluxLocation.getPhysicalStructuredAddress(); We will pick this up on VesselTransportMeans. See EFCA FLUX implementation document.
            fluxLocationEntities.add(fluxLocationEntity);
        }

        return fluxLocationEntities;
    }

    protected Set<FluxLocationEntity> getDestFluxLocationEntities(List<FLUXLocation> destFluxLocations) {
        Set<FluxLocationEntity> fluxLocationEntities = new HashSet<>();

        for (FLUXLocation fluxLocation : Utils.safeIterable(destFluxLocations)) {
            FluxLocationEntity fluxLocationEntity = LOCATION_MAPPER.mapToFluxLocationEntity(fluxLocation);
            fluxLocationEntities.add(fluxLocationEntity);
        }

        return fluxLocationEntities;
    }

    protected Set<FishingGearEntity> getFishingGearEntities(List<FishingGear> fishingGears, FaCatchEntity faCatchEntity) {
        if (fishingGears == null || fishingGears.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FishingGearEntity> fishingGearEntities = new HashSet<>();
        for (FishingGear fishingGear : fishingGears) {
            FishingGearEntity fishingGearEntity = fishingGearMapper.mapToFishingGearEntity(fishingGear);
            fishingGearEntity.setFaCatch(faCatchEntity);
            fishingGearEntities.add(fishingGearEntity);
        }
        return fishingGearEntities;
    }

    /**
     * Depending on the catch type (typeCode -&gt; FaCatchEntity) returns a DTO containing the sums of ONBOARD and
     * LANDED fishQuantity and species.
     *
     * @param faCatches
     */
    public Map<String, CatchSummaryListDTO> mapCatchesToSummaryDTO(List<Object[]> faCatches) {
        Map<String, CatchSummaryListDTO> catchSummary = new HashMap<>();
        CatchSummaryListDTO landedSummary = new CatchSummaryListDTO();
        CatchSummaryListDTO onBoardSummary = new CatchSummaryListDTO();
        catchSummary.put("landed", landedSummary);
        catchSummary.put("onboard", onBoardSummary);
        if (CollectionUtils.isEmpty(faCatches)) {
            return catchSummary;
        }

        for (Object[] faCatch : faCatches) {
            String typeCode = ((String) faCatch[0]).toUpperCase();
            String speciesCode = (String) faCatch[1];
            String areaName = (String) faCatch[2];
            Double weight = (Double) faCatch[3];
            if ("UNLOADED".equals(typeCode)) {
                landedSummary.addSpecieAndQuantity(speciesCode, weight, areaName);
            } else if ("ONBOARD".equals(typeCode)
                    || "KEPT_IN_NET".equals(typeCode)
                    || "TAKEN_ONBOARD".equals(typeCode)) {
                onBoardSummary.addSpecieAndQuantity(speciesCode, weight, areaName);
            }
        }

        return catchSummary;
    }
}
