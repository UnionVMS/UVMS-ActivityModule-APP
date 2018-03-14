/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapStockEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionClassCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.RelocationDto;
import eu.europa.ec.fisheries.ers.service.mapper.view.FluxCharacteristicsViewDtoMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProcess;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPStock;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SizeDistribution;

@Mapper(uses = {FishingGearMapper.class, FluxCharacteristicsMapper.class,
        FishingTripMapper.class, AapProcessMapper.class, AapStockMapper.class,
        FluxCharacteristicsViewDtoMapper.class, VesselIdentifierMapper.class})
public abstract class FaCatchMapper extends BaseMapper {

    public static final FaCatchMapper INSTANCE = Mappers.getMapper(FaCatchMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "faCatch.typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "faCatch.typeCode.listID"),
            @Mapping(target = "speciesCode", source = "faCatch.speciesCode.value"),
            @Mapping(target = "speciesCodeListid", source = "faCatch.speciesCode.listID"),
            @Mapping(target = "unitQuantity", source = "faCatch.unitQuantity.value"),
            @Mapping(target = "unitQuantityCode", source = "faCatch.unitQuantity.unitCode"),
            @Mapping(target = "calculatedUnitQuantity", expression = "java(getCalculatedQuantity(faCatch.getUnitQuantity()))"),
            @Mapping(target = "weightMeasure", source = "faCatch.weightMeasure.value"),
            @Mapping(target = "weightMeasureUnitCode", source = "faCatch.weightMeasure.unitCode"),
            @Mapping(target = "calculatedWeightMeasure", expression = "java(getCalculatedMeasure(faCatch.getWeightMeasure()))"),
            @Mapping(target = "usageCode", expression = "java(getCodeType(faCatch.getUsageCode()))"),
            @Mapping(target = "usageCodeListId", expression = "java(getCodeTypeListId(faCatch.getUsageCode()))"),
            @Mapping(target = "weighingMeansCode", expression = "java(getCodeType(faCatch.getWeighingMeansCode()))"),
            @Mapping(target = "weighingMeansCodeListId", expression = "java(getCodeTypeListId(faCatch.getWeighingMeansCode()))"),
            @Mapping(target = "sizeDistribution", expression = "java(getSizeDistributionEntity(faCatch.getSpecifiedSizeDistribution(), faCatchEntity))"),
            @Mapping(target = "aapProcesses", expression = "java(getAapProcessEntities(faCatch.getAppliedAAPProcesses(), faCatchEntity))"),
            @Mapping(target = "fishingGears", expression = "java(getFishingGearEntities(faCatch.getUsedFishingGears(), faCatchEntity))"),
            @Mapping(target = "fluxLocations", expression = "java(getFluxLocationEntities(faCatch.getSpecifiedFLUXLocations(), faCatch.getDestinationFLUXLocations(), faCatchEntity))"),
            @Mapping(target = "fluxCharacteristics", expression = "java(getFluxCharacteristicEntities(faCatch.getApplicableFLUXCharacteristics(), faCatchEntity))"),
            @Mapping(target = "aapStocks", expression = "java(getAapStockEntities(faCatch.getRelatedAAPStocks(), faCatchEntity))"),
            @Mapping(target = "fishingTrips", expression = "java(getFishingTripEntities(faCatch.getRelatedFishingTrips(), faCatchEntity))")
    })
    public abstract FaCatchEntity mapToFaCatchEntity(FACatch faCatch);

    @Mappings({
            @Mapping(target = "roleName", expression = "java(getVesselTransportMeansForRelocation(faCatch).getRoleCode())"),
            @Mapping(target = "country", expression = "java(getVesselTransportMeansForRelocation(faCatch).getCountry())"),
            @Mapping(target = "vesselIdentifiers", expression = "java(mapToAssetIdentifiers(faCatch))"),
            @Mapping(target = "name", expression = "java(getVesselTransportMeansForRelocation(faCatch).getName())"),
            @Mapping(target = "speciesCode", source = "faCatch.speciesCode"),
            @Mapping(target = "type", source = "faCatch.typeCode"),
            @Mapping(target = "weight", source = "faCatch.calculatedWeightMeasure"),
            @Mapping(target = "unit", source = "faCatch.calculatedUnitQuantity"),
            @Mapping(target = "characteristics", source = "faCatch.fishingActivity.fluxCharacteristics")
    })
    public abstract RelocationDto mapToRelocationDto(FaCatchEntity faCatch);


    public abstract List<RelocationDto> mapToRelocationDtoList(Set<FaCatchEntity> faCatches);

    protected List<AssetIdentifierDto> mapToAssetIdentifiers(FaCatchEntity faCatch) {
        List<AssetIdentifierDto> assetIdentifierDtos = new ArrayList<>();
        if (faCatch != null && faCatch.getFishingActivity() != null && CollectionUtils.isNotEmpty(faCatch.getFishingActivity().getVesselTransportMeans())) {
            VesselTransportMeansEntity vesselTransportMeans = faCatch.getFishingActivity().getVesselTransportMeans().iterator().next();

            Map<VesselIdentifierSchemeIdEnum, String> vesselIdentifiers = vesselTransportMeans.getVesselIdentifiersMap();

            // Set IRCS always if present
            if (vesselIdentifiers.get(VesselIdentifierSchemeIdEnum.IRCS) != null) {
                assetIdentifierDtos.add(new AssetIdentifierDto(VesselIdentifierSchemeIdEnum.IRCS, vesselIdentifiers.get(VesselIdentifierSchemeIdEnum.IRCS)));
            }

            if (vesselIdentifiers.get(VesselIdentifierSchemeIdEnum.ICCAT) != null) {
                assetIdentifierDtos.add(new AssetIdentifierDto(VesselIdentifierSchemeIdEnum.ICCAT, vesselIdentifiers.get(VesselIdentifierSchemeIdEnum.ICCAT)));
            } else if (vesselIdentifiers.get(VesselIdentifierSchemeIdEnum.CFR) != null) {
                assetIdentifierDtos.add(new AssetIdentifierDto(VesselIdentifierSchemeIdEnum.CFR, vesselIdentifiers.get(VesselIdentifierSchemeIdEnum.CFR)));
            }
        }
        return assetIdentifierDtos;
    }

    protected VesselTransportMeansEntity getVesselTransportMeansForRelocation(FaCatchEntity faCatch) {
        if (faCatch == null || faCatch.getFishingActivity() == null || CollectionUtils.isEmpty(faCatch.getFishingActivity().getVesselTransportMeans())) {
            return null;
        }

        return faCatch.getFishingActivity().getVesselTransportMeans().iterator().next();
    }


    protected SizeDistributionEntity getSizeDistributionEntity(SizeDistribution sizeDistribution, FaCatchEntity faCatchEntity) {
        if (sizeDistribution == null) {
            return null;
        }
        SizeDistributionEntity sizeDistributionEntity = SizeDistributionMapper.INSTANCE.mapToSizeDistributionEntity(sizeDistribution);
        sizeDistributionEntity.setFaCatch(faCatchEntity);
        Set<SizeDistributionClassCodeEntity> sizeDistributionSet = sizeDistributionEntity.getSizeDistributionClassCode();
        if (CollectionUtils.isNotEmpty(sizeDistributionSet)) {
            faCatchEntity.setFishClassCode(sizeDistributionSet.iterator().next().getClassCode());
        }

        return sizeDistributionEntity;
    }

    protected Set<FishingTripEntity> getFishingTripEntities(List<FishingTrip> fishingTrips, FaCatchEntity faCatchEntity) {
        if (fishingTrips == null || fishingTrips.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FishingTripEntity> fishingTripEntities = new HashSet<>();
        for (FishingTrip fishingTrip : fishingTrips) {
            FishingTripEntity fishingTripEntity = FishingTripMapper.INSTANCE.mapToFishingTripEntity(fishingTrip);
            fishingTripEntity.setFaCatch(faCatchEntity);
            fishingTripEntities.add(fishingTripEntity);
        }
        return fishingTripEntities;
    }

    protected Set<AapStockEntity> getAapStockEntities(List<AAPStock> aapStocks, FaCatchEntity faCatchEntity) {
        if (aapStocks == null || aapStocks.isEmpty()) {
            return Collections.emptySet();
        }
        Set<AapStockEntity> aapStockEntities = new HashSet<>();
        for (AAPStock aapStock : aapStocks) {
            AapStockEntity aapStockEntity = AapStockMapper.INSTANCE.mapToAapStockEntity(aapStock);
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

    protected Set<FluxLocationEntity> getFluxLocationEntities(List<FLUXLocation> specifiedFluxLocations, List<FLUXLocation> destFluxLocations, FaCatchEntity faCatchEntity) {
        Set<FluxLocationEntity> fluxLocationEntities = new HashSet<>();
        if (specifiedFluxLocations != null && !specifiedFluxLocations.isEmpty()) {
            for (FLUXLocation fluxLocation : specifiedFluxLocations) {
                FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation);
                fluxLocationEntity.setFaCatch(faCatchEntity);
                fluxLocationEntity.setFluxLocationType(FluxLocationCatchTypeEnum.FA_CATCH_SPECIFIED.getType());
                fluxLocationEntities.add(fluxLocationEntity);
            }
        }

        if (destFluxLocations != null && !destFluxLocations.isEmpty()) {
            for (FLUXLocation fluxLocation : destFluxLocations) {
                FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation);
                fluxLocationEntity.setFaCatch(faCatchEntity);
                fluxLocationEntity.setFluxLocationType(FluxLocationCatchTypeEnum.FA_CATCH_DESTINATION.getType());
                fluxLocationEntities.add(fluxLocationEntity);
            }
        }

        return fluxLocationEntities;
    }

    protected Set<FishingGearEntity> getFishingGearEntities(List<FishingGear> fishingGears, FaCatchEntity faCatchEntity) {
        if (fishingGears == null || fishingGears.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FishingGearEntity> fishingGearEntities = new HashSet<>();
        for (FishingGear fishingGear : fishingGears) {
            FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear);
            fishingGearEntity.setFaCatch(faCatchEntity);
            fishingGearEntities.add(fishingGearEntity);
        }
        return fishingGearEntities;
    }

    protected Set<AapProcessEntity> getAapProcessEntities(List<AAPProcess> aapProcesses, FaCatchEntity faCatchEntity) {
        if (aapProcesses == null || aapProcesses.isEmpty()) {
            return Collections.emptySet();
        }
        Set<AapProcessEntity> aapProcessEntities = new HashSet<>();
        for (AAPProcess aapProcess : aapProcesses) {
            AapProcessEntity aapProcessEntity = AapProcessMapper.INSTANCE.mapToAapProcessEntity(aapProcess);
            aapProcessEntity.setFaCatch(faCatchEntity);
            aapProcessEntities.add(aapProcessEntity);
        }
        return aapProcessEntities;
    }


    /**
     * Depending on the catch type (typeCode->FaCatchEntity) returns a DTO containing the sums of ONBOARD and
     * LANDED fishQuantity and species.
     *
     * @param faCatches
     * @return
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


            // Double weight      = (Double) faCatch[2];
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