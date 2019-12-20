/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapStockEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.StructuredAddressTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.RelocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.view.FluxCharacteristicsViewDtoMapper;
import eu.europa.ec.fisheries.uvms.activity.service.util.CustomBigDecimal;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPStock;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SizeDistribution;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper(uses = {CustomBigDecimal.class, FishingGearMapper.class, FluxCharacteristicsMapper.class, AapProcessMapper.class, AapStockMapper.class, FluxCharacteristicsViewDtoMapper.class, VesselIdentifierMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FaCatchMapper extends BaseMapper {

    public static final FaCatchMapper INSTANCE = Mappers.getMapper(FaCatchMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "speciesCode", source = "speciesCode.value"),
            @Mapping(target = "speciesCodeListid", source = "speciesCode.listID"),
            @Mapping(target = "unitQuantity", source = "unitQuantity.value"),
            @Mapping(target = "unitQuantityCode", source = "unitQuantity.unitCode"),
            @Mapping(target = "fishClassCode", expression = "java(mapToFishClassCode(faCatch.getSpecifiedSizeDistribution()))"),
            @Mapping(target = "weightMeasure", source = "weightMeasure.value"),
            @Mapping(target = "weightMeasureUnitCode", source = "weightMeasure.unitCode"),
            @Mapping(target = "usageCode", source = "faCatch.usageCode.value"),
            @Mapping(target = "usageCodeListId", source = "faCatch.usageCode.listID"),
            @Mapping(target = "weighingMeansCode", source = "faCatch.weighingMeansCode.value"),
            @Mapping(target = "weighingMeansCodeListId", source = "faCatch.weighingMeansCode.listID"),
            @Mapping(target = "sizeDistributionCategoryCode", expression = "java(getSizeDistributionCategoryCode(faCatch.getSpecifiedSizeDistribution()))"),
            @Mapping(target = "sizeDistributionCategoryCodeListId", expression = "java(getSizeDistributionCategoryCodeListId(faCatch.getSpecifiedSizeDistribution()))"),
            @Mapping(target = "sizeDistributionClassCode", expression = "java(getSizeDistributionClassCode(faCatch.getSpecifiedSizeDistribution()))"),
            @Mapping(target = "sizeDistributionClassCodeListId", expression = "java(getSizeDistributionClassCodeListId(faCatch.getSpecifiedSizeDistribution()))"),
            @Mapping(target = "aapProcesses", ignore = true),
            @Mapping(target = "fishingGears", expression = "java(getFishingGearEntities(faCatch.getUsedFishingGears(), faCatchEntity))"),
            @Mapping(target = "fluxLocations", expression = "java(getFluxLocationEntities(faCatch.getSpecifiedFLUXLocations(), faCatch.getDestinationFLUXLocations(), faCatchEntity))"),
            @Mapping(target = "fluxCharacteristics", expression = "java(getFluxCharacteristicEntities(faCatch.getApplicableFLUXCharacteristics(), faCatchEntity))"),
            @Mapping(target = "aapStocks", expression = "java(getAapStockEntities(faCatch.getRelatedAAPStocks(), faCatchEntity))"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "fishingActivity", ignore = true),
            @Mapping(target = "calculatedUnitQuantity", ignore = true),
            @Mapping(target = "calculatedWeightMeasure", ignore = true),
            @Mapping(target = "territory", ignore = true),
            @Mapping(target = "faoArea", ignore = true),
            @Mapping(target = "icesStatRectangle", ignore = true),
            @Mapping(target = "effortZone", ignore = true),
            @Mapping(target = "rfmo", ignore = true),
            @Mapping(target = "gfcmGsa", ignore = true),
            @Mapping(target = "gfcmStatRectangle", ignore = true),
            @Mapping(target = "presentation", ignore = true),
            //TODO: Fix this
            @Mapping(target = "fishingTrip", expression = "java(getFishingTripEntity(faCatch.getRelatedFishingTrips()))"),
            @Mapping(target = "gearTypeCode", ignore = true)
    })
    public abstract FaCatchEntity mapToFaCatchEntity(FACatch faCatch);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "appliedAAPProcesses", source = "aapProcesses"),
            @Mapping(target = "specifiedSizeDistribution", expression="java(getSizeDistribution(faCatch))"),
            @Mapping(target = "relatedFishingTrips", ignore = true),
            @Mapping(target = "relatedAAPStocks", ignore = true),
            @Mapping(target = "relatedSalesBatches", ignore = true),
            @Mapping(target = "specifiedFLUXLocations", ignore = true),
            @Mapping(target = "usedFishingGears", ignore = true),
            @Mapping(target = "applicableFLUXCharacteristics", ignore = true),
            @Mapping(target = "destinationFLUXLocations", ignore = true)
    })
    public abstract FACatch mapToFaCatch(FaCatchEntity faCatch);

    @Mappings({
            @Mapping(target = "listID", ignore = true),
            @Mapping(target = "listAgencyID", ignore = true),
            @Mapping(target = "listAgencyName", ignore = true),
            @Mapping(target = "listName", ignore = true),
            @Mapping(target = "listVersionID", ignore = true),
            @Mapping(target = "name", ignore = true),
            @Mapping(target = "languageID", ignore = true),
            @Mapping(target = "listURI", ignore = true),
            @Mapping(target = "listSchemeURI", ignore = true)
    })
    public abstract un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType map(java.lang.String value);

    @Mappings({
            @Mapping(target = "roleName", expression = "java(getVesselTransportMeansForRelocation(faCatch).getRoleCode())"),
            @Mapping(target = "country", expression = "java(getVesselTransportMeansForRelocation(faCatch).getCountry())"),
            @Mapping(target = "vesselIdentifiers", expression = "java(mapToAssetIdentifiers(faCatch))"),
            @Mapping(target = "name", expression = "java(getVesselTransportMeansForRelocation(faCatch).getName())"),
            @Mapping(target = "speciesCode", source = "speciesCode"),
            @Mapping(target = "type", source = "typeCode"),
            @Mapping(target = "weight", source = "calculatedWeightMeasure"),
            @Mapping(target = "unit", source = "calculatedUnitQuantity"),
            @Mapping(target = "characteristics", source = "fishingActivity.fluxCharacteristics")
    })
    public abstract RelocationDto mapToRelocationDto(FaCatchEntity faCatch);

    public abstract List<RelocationDto> mapToRelocationDtoList(Set<FaCatchEntity> faCatches);

    protected String mapToFishClassCode(SizeDistribution sizeDistrib){
        if(sizeDistrib != null && CollectionUtils.isNotEmpty(sizeDistrib.getClassCodes())){
            return sizeDistrib.getClassCodes().iterator().next().getValue();
        }
        return StringUtils.EMPTY;
    }

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

    protected FishingTripEntity getFishingTripEntity(List<FishingTrip> relatedFishingTrips) {
        if(relatedFishingTrips == null || relatedFishingTrips.size() == 0) {
            return null;
        }
        FishingTrip fishingTrip = relatedFishingTrips.get(0);
        return FishingTripEntity.create(fishingTrip);
    }

    protected VesselTransportMeansEntity getVesselTransportMeansForRelocation(FaCatchEntity faCatch) {
        if (faCatch == null || faCatch.getFishingActivity() == null || CollectionUtils.isEmpty(faCatch.getFishingActivity().getVesselTransportMeans())) {
            return null;
        }

        return faCatch.getFishingActivity().getVesselTransportMeans().iterator().next();
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
            result.setClassCodes(Lists.newArrayList(code));
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

                Set<StructuredAddressEntity> structuredAddressEntitySet = new HashSet<>();

                StructuredAddress physicalStructuredAddress = fluxLocation.getPhysicalStructuredAddress();
                StructuredAddressEntity physicalStructuredAddressEntity = StructuredAddressMapper.INSTANCE.mapToStructuredAddressEntity(physicalStructuredAddress);

                if (physicalStructuredAddressEntity != null){
                    physicalStructuredAddressEntity.setFluxLocation(fluxLocationEntity);
                    physicalStructuredAddressEntity.setStructuredAddressType(StructuredAddressTypeEnum.FLUX_PHYSICAL.getType());
                    structuredAddressEntitySet.add(physicalStructuredAddressEntity);
                }

                List<StructuredAddress> postalStructuredAddresses = fluxLocation.getPostalStructuredAddresses();
                if (postalStructuredAddresses != null && !postalStructuredAddresses.isEmpty()) {
                    for (StructuredAddress structuredAddress : postalStructuredAddresses) {
                        StructuredAddressEntity structuredAddressEntity = StructuredAddressMapper.INSTANCE.mapToStructuredAddressEntity(structuredAddress);
                        if (structuredAddressEntity != null){
                            structuredAddressEntity.setStructuredAddressType(StructuredAddressTypeEnum.FLUX_POSTAL.getType());
                            structuredAddressEntity.setFluxLocation(fluxLocationEntity);
                            structuredAddressEntitySet.add(structuredAddressEntity);
                        }
                    }
                }

                fluxLocationEntity.setStructuredAddresses(structuredAddressEntitySet);
                fluxLocationEntity.setFaCatch(faCatchEntity);
                fluxLocationEntity.setFluxLocationCatchTypeMapperInfo(FluxLocationCatchTypeEnum.FA_CATCH_SPECIFIED);
                fluxLocationEntities.add(fluxLocationEntity);
            }
        }

        if (destFluxLocations != null && !destFluxLocations.isEmpty()) {
            for (FLUXLocation fluxLocation : destFluxLocations) {
                FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation);
                fluxLocationEntity.setFaCatch(faCatchEntity);
                fluxLocationEntity.setFluxLocationCatchTypeMapperInfo(FluxLocationCatchTypeEnum.FA_CATCH_DESTINATION);
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
