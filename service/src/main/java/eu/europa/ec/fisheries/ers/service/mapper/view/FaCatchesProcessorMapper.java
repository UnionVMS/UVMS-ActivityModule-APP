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
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.facatch.DestinationLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.facatch.FaCatchGroupDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.facatch.FaCatchGroupDto;
import eu.europa.ec.fisheries.ers.service.dto.view.facatch.FluxCharacteristicsViewDto;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseViewWithInstanceMapper;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.model.StringWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by kovian on 03/03/2017.
 */
@Slf4j
public class FaCatchesProcessorMapper {

    public static final String LSC = "LSC";
    public static final String BMS = "BMS";

    /**
     * Takes a list of FaCatches and returns the list of groupDtos.
     *
     * @param faCatches
     * @return List<FaCatchGroupDto>
     */
    public static List<FaCatchGroupDto> getCatchGroupsFromListEntity(Set<FaCatchEntity> faCatches) {
        if(CollectionUtils.isEmpty(faCatches)){
            return null;
        }
        Map<String, List<FaCatchEntity>> faCatchGroups = groupCatches(faCatches);
        return computeSumsAndMapToDtoGroups(faCatchGroups);
    }

    /**
     * Groups the faCatches List matching those that have in common all the properties but BMS, SLC and size;
     *
     * @param faCatches
     * @return
     */
    private static Map<String, List<FaCatchEntity>> groupCatches(Set<FaCatchEntity> faCatches) {
        Map<String, List<FaCatchEntity>> groups    = new HashMap<>();
        Iterator<FaCatchEntity> catchesIteratorExt = faCatches.iterator();
        Iterator<FaCatchEntity> catchesIteratorInt = faCatches.iterator();
        int group_nr = 0;
        while(faCatches.size() != 0){
            groups.put(String.valueOf(group_nr), extractGroupBelongingToThisFaCatch(catchesIteratorExt, catchesIteratorInt));
            group_nr++;
        }
        return groups;
    }

    /**
     * Extracts a group that has the needed properties in common with the catchesIteratorExt.next()
     *
     * @param catchesIteratorExt
     * @param catchesIteratorInt
     * @return
     */
    private static List<FaCatchEntity> extractGroupBelongingToThisFaCatch(Iterator<FaCatchEntity> catchesIteratorExt,
                                                                          Iterator<FaCatchEntity> catchesIteratorInt) {
        List<FaCatchEntity> group = new ArrayList<>();
        while(catchesIteratorExt.hasNext()) {
            FaCatchEntity catchEntity = catchesIteratorExt.next();
            group.add(catchEntity);
            while (catchesIteratorInt.hasNext()) {
                FaCatchEntity intCatch = catchesIteratorInt.next();
                if (FaCatchForViewComparator.catchesAreEqual(intCatch, catchEntity)) {
                    group.add(intCatch);
                    catchesIteratorInt.remove();
                }
            }
        }
        return group;
    }

    /**
     * Subgroups and fills the List<FaCatchGroupDto> mapping the required properties.
     *
     * @param faCatchGroups
     * @return
     */
    private static List<FaCatchGroupDto> computeSumsAndMapToDtoGroups(Map<String, List<FaCatchEntity>> faCatchGroups) {
        List<FaCatchGroupDto> faCatchGroupsDtoList = new ArrayList<>();
        for(Map.Entry<String, List<FaCatchEntity>> group : faCatchGroups.entrySet()){
            faCatchGroupsDtoList.add(mapFaCatchListToCatchGroupDto(group.getKey(), group.getValue()));
        }
        return faCatchGroupsDtoList;
    }

    /**
     * Maps a list of CatchEntities (rappresenting a froup) to a  FaCatchGroupDto;
     *
     * @param groupNr
     * @param groupCatchList
     * @return
     */
    private static FaCatchGroupDto mapFaCatchListToCatchGroupDto(String groupNr, List<FaCatchEntity> groupCatchList) {
        FaCatchGroupDto groupDto  = new FaCatchGroupDto();
        FaCatchEntity catchEntity = groupCatchList.get(0);
        // Set primary properties on groupDto
        groupDto.setType(catchEntity.getTypeCode());
        groupDto.setSpecies(catchEntity.getSpeciesCode());
        // Fill the denomination location part of the GroupDto.
        groupDto.setLocation(FaCatchGroupMapper.INSTANCE.mapFaCatchEntityToDenominationLocation(catchEntity));
        // calculate Totals And Fill Soecified Locations and Gears Per each Subgroup (subgrupped on BMS/LSC)
        calculateTotalsAndFillSubgroups(groupCatchList, groupDto);
        return groupDto;
    }

    /**
     *  Subgroups by BMS and LSC and :
     *  Calculates the total and fills all the details related to each subgroup.
     *
     * @param groupCatchList
     * @param groupDto
     */
    private static void calculateTotalsAndFillSubgroups(List<FaCatchEntity> groupCatchList, FaCatchGroupDto groupDto) {
        Map<String, FaCatchGroupDetailsDto> groupingDetailsMap = groupDto.getGroupingDetails();
        FaCatchGroupDetailsDto lscGroupDetailsDto = new FaCatchGroupDetailsDto();
        FaCatchGroupDetailsDto bmsGroupDetailsDto = new FaCatchGroupDetailsDto();
        // Weight and units totals
        Double lscGroupTotalWeight  = 0.0;
        Double lscGroupTotalUnits   = 0.0;
        Double bmsGroupTotalWeight  = 0.0;
        Double bmsGroupTotalUnits   = 0.0;
        for(FaCatchEntity entity : groupCatchList){
            switch(entity.getFishClassCode()){
                case LSC:
                    // Weight and Units calculation
                    lscGroupTotalWeight += entity.getCalculatedWeightMeasure();
                    lscGroupTotalUnits  += entity.getUnitQuantity();
                    fillDetailsForSubGroup(lscGroupDetailsDto, entity);
                    break;
                case BMS:
                    // Weight and Units calculation
                    bmsGroupTotalWeight += entity.getCalculatedWeightMeasure();
                    bmsGroupTotalUnits  += entity.getUnitQuantity();
                    fillDetailsForSubGroup(bmsGroupDetailsDto, entity);
                    break;
                default :
                    log.error("While constructing Fa Catch Section of the view the FaCatchEntity with id : "+entity.getId()+" is neither LSC or BMS!");
            }
        }

        // Set total weight and units for BMS and LSC
        lscGroupDetailsDto.setUnit(lscGroupTotalUnits);
        lscGroupDetailsDto.setWeight(lscGroupTotalWeight);
        bmsGroupDetailsDto.setUnit(bmsGroupTotalUnits);
        bmsGroupDetailsDto.setWeight(bmsGroupTotalWeight);
        groupingDetailsMap.put(LSC, lscGroupDetailsDto);
        groupingDetailsMap.put(BMS, bmsGroupDetailsDto);

        // Set total group weight (LSC + BMS)
        groupDto.setCalculatedWeight(lscGroupTotalWeight + bmsGroupTotalWeight);
    }


    /**
     * Fills all the details related to the specific LCS / BMS subgroup.
     *
     * @param groupDetailsDto
     * @param actualEntity
     */
    private static void fillDetailsForSubGroup(FaCatchGroupDetailsDto groupDetailsDto, FaCatchEntity actualEntity) {
        fillSpecifiedAndDestinationLocationsInGroupDetails(actualEntity.getFluxLocations(), groupDetailsDto);
        fillFishingGears(actualEntity.getFishingGears(), groupDetailsDto);
        if(!groupDetailsDto.areDetailsSet()){
            fillGroupDetails(groupDetailsDto, actualEntity);
        }
        fillFluxCharacteristics(groupDetailsDto, actualEntity.getFluxCharacteristics());
    }


    /**
     * Fills the locations on FaCatchGroupDetailsDto DTO.
     *
     * @param fluxLocations
     * @param groupDetailsDto
     */
    private static void fillSpecifiedAndDestinationLocationsInGroupDetails(Set<FluxLocationEntity> fluxLocations, FaCatchGroupDetailsDto groupDetailsDto) {
        if(CollectionUtils.isEmpty(fluxLocations)){
            return;
        }
        List<DestinationLocationDto> destLocDtoList = groupDetailsDto.getDestinationLocation();
        List<FluxLocationDto> specifiedFluxLocDto   = groupDetailsDto.getSpecifiedFluxLocation();
        for (FluxLocationEntity actLoc : fluxLocations) {
            String fluxLocationType = actLoc.getFluxLocationType();
            if(StringUtils.equals(fluxLocationType, FluxLocationCatchTypeEnum.FA_CATCH_DESTINATION.getType())){
                destLocDtoList.add(new DestinationLocationDto(actLoc.getFluxLocationIdentifierSchemeId(), actLoc.getCountryId(), actLoc.getName()));
            } else if(StringUtils.equals(fluxLocationType, FluxLocationCatchTypeEnum.FA_CATCH_SPECIFIED.getType())){
                StringWrapper geometryStrWrapp = GeometryMapper.INSTANCE.geometryToWkt(actLoc.getGeom());
                specifiedFluxLocDto.add(new FluxLocationDto(actLoc.getName(), geometryStrWrapp != null ? geometryStrWrapp.getValue() : null));
            }
        }
        groupDetailsDto.setDestinationLocation(destLocDtoList);
        groupDetailsDto.setSpecifiedFluxLocation(specifiedFluxLocDto);
    }

    /**
     * Fills the fishing gears toi the FaCatchGroupDetailsDto.
     *
     * @param fishingGears
     * @param groupDetailsDto
     */
    private static void fillFishingGears(Set<FishingGearEntity> fishingGears, FaCatchGroupDetailsDto groupDetailsDto) {
        groupDetailsDto.setGears(BaseViewWithInstanceMapper.INSTANCE.getGearsFromEntity(fishingGears));
    }

    /**
     * Fill remaining details on group level;
     *
     * @param groupDetailsDto
     * @param actualEntity
     */
    private static void fillGroupDetails(FaCatchGroupDetailsDto groupDetailsDto, FaCatchEntity actualEntity) {
        groupDetailsDto.setStockId(CollectionUtils.isNotEmpty(actualEntity.getAapStocks()) ? actualEntity.getAapStocks().iterator().next().getStockId() : null);
        groupDetailsDto.setSize(actualEntity.getSizeDistribution() != null ? actualEntity.getSizeDistribution().getCategoryCode() : null);
        groupDetailsDto.setWeightingMeans(actualEntity.getWeighingMeansCode());
        groupDetailsDto.setUsage(actualEntity.getUsageCode());
        Set<FishingTripIdentifierEntity> fishingTripIdentifierEntities = CollectionUtils.isNotEmpty(actualEntity.getFishingTrips()) ? actualEntity.getFishingTrips().iterator().next().getFishingTripIdentifiers() : null;
        groupDetailsDto.setTripId(CollectionUtils.isNotEmpty(fishingTripIdentifierEntities) ? fishingTripIdentifierEntities.iterator().next().getTripId() : null);
        groupDetailsDto.setDetailsAreSet(true);
    }

    private static void fillFluxCharacteristics(FaCatchGroupDetailsDto groupDetailsDto, Set<FluxCharacteristicEntity> fluxCharacteristics) {
        if(CollectionUtils.isEmpty(fluxCharacteristics)){
            return;
        }
        List<FluxCharacteristicsViewDto> applicableFluxCharacteristics = groupDetailsDto.getApplicableFluxCharacteristics();
        for(FluxCharacteristicEntity flCharacEnt : fluxCharacteristics){
            applicableFluxCharacteristics.add(FluxCharacteristicsViewDtoMapper.INSTANCE.mapFluxCharacteristicsEntityListToDtoList(flCharacEnt));
        }
    }

    /**
     * Class that serves as a comparator for 2 FaCatch entities.
     */
    private static class FaCatchForViewComparator {

        public static boolean catchesAreEqual(FaCatchEntity thisCatch, FaCatchEntity thatCatch) {
            if (thisCatch == thatCatch)
                return true;
            if(thisCatch == null && thatCatch == null)
                return true;
            if(thisCatch == null || thatCatch == null)
                return false;
            if (thisCatch.getTypeCode() != null ? !thisCatch.getTypeCode().equals(thatCatch.getTypeCode()) : thatCatch.getTypeCode() != null)
                return false;
            if (thisCatch.getSpeciesCode() != null ? !thisCatch.getSpeciesCode().equals(thatCatch.getSpeciesCode()) : thatCatch.getSpeciesCode() != null)
                return false;
            if (thisCatch.getUsageCode() != null ? !thisCatch.getUsageCode().equals(thatCatch.getUsageCode()) : thatCatch.getUsageCode() != null)
                return false;
            if (thisCatch.getTerritory() != null ? !thisCatch.getTerritory().equals(thatCatch.getTerritory()) : thatCatch.getTerritory() != null)
                return false;
            if (thisCatch.getFaoArea() != null ? !thisCatch.getFaoArea().equals(thatCatch.getFaoArea()) : thatCatch.getFaoArea() != null) return false;
            if (thisCatch.getIcesStatRectangle() != null ? !thisCatch.getIcesStatRectangle().equals(thatCatch.getIcesStatRectangle()) : thatCatch.getIcesStatRectangle() != null)
                return false;
            if (thisCatch.getEffortZone() != null ? !thisCatch.getEffortZone().equals(thatCatch.getEffortZone()) : thatCatch.getEffortZone() != null)
                return false;
            if (thisCatch.getRfmo() != null ? !thisCatch.getRfmo().equals(thatCatch.getRfmo()) : thatCatch.getRfmo() != null) return false;
            if (thisCatch.getGfcmGsa() != null ? !thisCatch.getGfcmGsa().equals(thatCatch.getGfcmGsa()) : thatCatch.getGfcmGsa() != null) return false;
            if (thisCatch.getGfcmStatRectangle() != null ? !thisCatch.getGfcmStatRectangle().equals(thatCatch.getGfcmStatRectangle()) : thatCatch.getGfcmStatRectangle() != null)
                return false;
            if (!sizeDestributionsAreEqual(thisCatch.getSizeDistribution(), thatCatch.getSizeDistribution()))
                return false;
            if (!aapStocsAreEqual(thisCatch.getAapStocks(), thatCatch.getAapStocks()))
                return false;
            if (!fishingTripsAreEquals(thisCatch.getFishingTrips(), thatCatch.getFishingTrips()))
                return false;
            return true;
        }

        private static boolean sizeDestributionsAreEqual(SizeDistributionEntity sizeThis, SizeDistributionEntity sizeThat){
            if (sizeThis == sizeThat)
                return true;
            if(sizeThis == null && sizeThat == null)
                return true;
            if(sizeThis == null || sizeThat == null)
                return false;
            if(!StringUtils.equals(sizeThis.getCategoryCode(), sizeThat.getCategoryCode()))
                return false;
            return true;
        }

        private static boolean aapStocsAreEqual(Set<AapStockEntity> aapList_1, Set<AapStockEntity> aapList_2){
            AapStockEntity aapStockThis = CollectionUtils.isNotEmpty(aapList_1) ? aapList_1.iterator().next() : null;
            AapStockEntity aapStockThat = CollectionUtils.isNotEmpty(aapList_2) ? aapList_2.iterator().next() : null;
            if (aapStockThis == aapStockThat)
                return true;
            if(aapStockThis == null && aapStockThat == null)
                return true;
            if(aapStockThis == null || aapStockThat == null)
                return false;
            if(!StringUtils.equals(aapStockThis.getStockId(), aapStockThat.getStockId()))
                return false;
            return true;
        }

        private static boolean fishingTripsAreEquals(Set<FishingTripEntity> aapList_1, Set<FishingTripEntity> aapList_2){
            FishingTripEntity aapStockThis = CollectionUtils.isNotEmpty(aapList_1) ? aapList_1.iterator().next() : null;
            FishingTripEntity aapStockThat = CollectionUtils.isNotEmpty(aapList_2) ? aapList_2.iterator().next() : null;
            if (aapStockThis == aapStockThat)
                return true;
            if(aapStockThis == null && aapStockThat == null)
                return true;
            if(aapStockThis == null || aapStockThat == null)
                return false;
            FishingTripIdentifierEntity identifierThis = CollectionUtils.isNotEmpty(aapStockThis.getFishingTripIdentifiers()) ? aapStockThis.getFishingTripIdentifiers().iterator().next() : null;
            FishingTripIdentifierEntity identifierThat = CollectionUtils.isNotEmpty(aapStockThis.getFishingTripIdentifiers()) ? aapStockThis.getFishingTripIdentifiers().iterator().next() : null;
            if (identifierThis == identifierThat)
                return true;
            if(identifierThis == null && identifierThat == null)
                return true;
            if(identifierThis == null || identifierThat == null)
                return false;
            if(!StringUtils.equals(identifierThis.getTripId(), identifierThat.getTripId()))
                return false;
            return true;
        }

    }

}

