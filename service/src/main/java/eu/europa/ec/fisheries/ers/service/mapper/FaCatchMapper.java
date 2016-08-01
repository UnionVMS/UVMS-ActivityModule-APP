/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class FaCatchMapper extends BaseMapper {

    public static FaCatchMapper INSTANCE = Mappers.getMapper(FaCatchMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(faCatch.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(faCatch.getTypeCode()))"),
            @Mapping(target = "speciesCode", expression = "java(getCodeType(faCatch.getSpeciesCode()))"),
            @Mapping(target = "speciesCodeListid", expression = "java(getCodeTypeListId(faCatch.getSpeciesCode()))"),
            @Mapping(target = "unitQuantity", expression = "java(getQuantityInLong(faCatch.getUnitQuantity()))"),
            @Mapping(target = "weightMeasure", expression = "java(getMeasure(faCatch.getWeightMeasure()))"),
            @Mapping(target = "weightMeasureUnitCode", expression = "java(getMeasureUnitCode(faCatch.getWeightMeasure()))"),
            @Mapping(target = "weightMeasureListId", expression = "java(getMeasureListId(faCatch.getWeightMeasure()))"),
            @Mapping(target = "usageCode", expression = "java(getCodeType(faCatch.getUsageCode()))"),
            @Mapping(target = "usageCodeListId", expression = "java(getCodeTypeListId(faCatch.getUsageCode()))"),
            @Mapping(target = "weighingMeansCode", expression = "java(getCodeType(faCatch.getWeighingMeansCode()))"),
            @Mapping(target = "weighingMeansCodeListId", expression = "java(getCodeTypeListId(faCatch.getWeighingMeansCode()))"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)"),
            @Mapping(target = "sizeDistribution", expression = "java(getSizeDistributionEntity(faCatch.getSpecifiedSizeDistribution(), faCatchEntity))"),
            @Mapping(target = "aapProcesses", expression = "java(getAapProcessEntities(faCatch.getAppliedAAPProcesses(), faCatchEntity))"),
            @Mapping(target = "fishingGears", expression = "java(getFishingGearEntities(faCatch.getUsedFishingGears(), faCatchEntity))"),
            @Mapping(target = "fluxLocations", expression = "java(getFluxLocationEntities(faCatch.getSpecifiedFLUXLocations(), faCatch.getDestinationFLUXLocations(), faCatchEntity))"),
            @Mapping(target = "fluxCharacteristics", expression = "java(getFluxCharacteristicEntities(faCatch.getApplicableFLUXCharacteristics(), faCatchEntity))"),
            @Mapping(target = "aapStocks", expression = "java(getAapStockEntities(faCatch.getRelatedAAPStocks(), faCatchEntity))"),
            @Mapping(target = "fishingTrips", expression = "java(getFishingTripEntities(faCatch.getRelatedFishingTrips(), faCatchEntity))")
    })
    public abstract FaCatchEntity mapToFaCatchEntity(FACatch faCatch, FishingActivityEntity fishingActivityEntity, @MappingTarget FaCatchEntity faCatchEntity);

    protected SizeDistributionEntity getSizeDistributionEntity(SizeDistribution sizeDistribution, FaCatchEntity faCatchEntity) {
        if (sizeDistribution == null) {
            return null;
        }
        return SizeDistributionMapper.INSTANCE.mapToSizeDistributionEntity(sizeDistribution, faCatchEntity, new SizeDistributionEntity());
    }

    protected Set<FishingTripEntity> getFishingTripEntities(List<FishingTrip> fishingTrips, FaCatchEntity faCatchEntity) {
        if (fishingTrips == null || fishingTrips.isEmpty()) {
            return null;
        }
        Set<FishingTripEntity> fishingTripEntities = new HashSet<>();
        for (FishingTrip fishingTrip : fishingTrips) {
            fishingTripEntities.add(FishingTripMapper.INSTANCE.mapToFishingTripEntity(fishingTrip, faCatchEntity, new FishingTripEntity()));
        }
        return fishingTripEntities;
    }

    protected Set<AapStockEntity> getAapStockEntities(List<AAPStock> aapStocks, FaCatchEntity faCatchEntity) {
        if (aapStocks == null || aapStocks.isEmpty()) {
            return null;
        }
        Set<AapStockEntity> aapStockEntities = new HashSet<>();
        for (AAPStock aapStock : aapStocks) {
            aapStockEntities.add(AapStockMapper.INSTANCE.mapToAapStockEntity(aapStock, faCatchEntity, new AapStockEntity()));
        }
        return aapStockEntities;
    }

    protected Set<FluxCharacteristicEntity> getFluxCharacteristicEntities(List<FLUXCharacteristic> fluxCharacteristics, FaCatchEntity faCatchEntity) {
        if (fluxCharacteristics == null || fluxCharacteristics.isEmpty()) {
            return null;
        }
        Set<FluxCharacteristicEntity> fluxCharacteristicEntities = new HashSet<>();
        for (FLUXCharacteristic fluxCharacteristic : fluxCharacteristics) {
            fluxCharacteristicEntities.add(FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(fluxCharacteristic, faCatchEntity, new FluxCharacteristicEntity()));
        }
        return fluxCharacteristicEntities;
    }

    protected Set<FluxLocationEntity> getFluxLocationEntities(List<FLUXLocation> specifiedFluxLocations, List<FLUXLocation> destFluxLocations, FaCatchEntity faCatchEntity) {
        Set<FluxLocationEntity> fluxLocationEntities = new HashSet<>();
        if (specifiedFluxLocations != null && !specifiedFluxLocations.isEmpty()) {
            for (FLUXLocation fluxLocation : specifiedFluxLocations) {
                fluxLocationEntities.add(FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation, FluxLocationTypeEnum.FA_CATCH_SPECIFIED, faCatchEntity, new FluxLocationEntity()));
            }
        }

        if (destFluxLocations != null && !destFluxLocations.isEmpty()) {
            for (FLUXLocation fluxLocation : destFluxLocations) {
                fluxLocationEntities.add(FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation, FluxLocationTypeEnum.FA_CATCH_DESTINATION, faCatchEntity, new FluxLocationEntity()));
            }
        }

        return fluxLocationEntities;
    }

    protected Set<FishingGearEntity> getFishingGearEntities(List<FishingGear> fishingGears, FaCatchEntity faCatchEntity) {
        if (fishingGears == null || fishingGears.isEmpty()) {
            return null;
        }
        Set<FishingGearEntity> fishingGearEntities = new HashSet<>();
        for (FishingGear fishingGear : fishingGears) {
            fishingGearEntities.add(FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear, faCatchEntity, new FishingGearEntity()));
        }
        return fishingGearEntities;
    }

    protected Set<AapProcessEntity> getAapProcessEntities(List<AAPProcess> aapProcesses, FaCatchEntity faCatchEntity) {
        if (aapProcesses == null || aapProcesses.isEmpty()) {
            return null;
        }
        Set<AapProcessEntity> aapProcessEntities = new HashSet<>();
        for (AAPProcess aapProcess : aapProcesses) {
            AapProcessEntity aapProcessEntity = AapProcessMapper.INSTANCE.mapToAapProcessEntity(aapProcess, faCatchEntity, new AapProcessEntity());
            aapProcessEntities.add(aapProcessEntity);
        }
        return aapProcessEntities;
    }
}