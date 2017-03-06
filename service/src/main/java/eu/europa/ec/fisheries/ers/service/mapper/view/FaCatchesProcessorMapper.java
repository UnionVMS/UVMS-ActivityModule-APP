/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.facatch.DestinationLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.facatch.FaCatchGroupDetailsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.facatch.FaCatchGroupDto;
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
                if (intCatch.equals(catchEntity)) {
                    group.add(intCatch);
                    catchesIteratorInt.remove();
                }
            }
        }
        return group;
    }

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
        groupDto.setCalculatedWeight(catchEntity.getCalculatedUnitQuantity());

        // Fill the denomination location part of the GroupDto.
        groupDto.setLocation(FaCatchGroupMapper.INSTANCE.mapFaCatchEntityToDenominationLocation(catchEntity));

        // calculate Totals And Fill Locations Per Subgroup
        calculateTotalsAndFillLocationsPerSubgroup(groupCatchList, groupDto);

        return groupDto;
    }

    private static void calculateTotalsAndFillLocationsPerSubgroup(List<FaCatchEntity> groupCatchList, FaCatchGroupDto groupDto) {
        Map<String, FaCatchGroupDetailsDto> groupingDetailsMap = groupDto.getGroupingDetails();
        FaCatchGroupDetailsDto lscGroupDetailsDto = new FaCatchGroupDetailsDto();
        FaCatchGroupDetailsDto bmsGroupDetailsDto = new FaCatchGroupDetailsDto();

        // Weight and units totals
        Double lscGroupTotalWeight  = 0.0;
        Double lscGroupTotalUnits   = 0.0;
        Double bmsGroupTotalWeight  = 0.0;
        Double bmsGroupTotalUnits   = 0.0;
        for(FaCatchEntity entity : groupCatchList){
            Set<FluxLocationEntity> fluxLocationsList = entity.getFluxLocations();
            Set<FishingGearEntity>  fishingGears      = entity.getFishingGears();
            switch(entity.getFishClassCode()){
                case LSC:
                    // Weight and Units
                    lscGroupTotalWeight += entity.getWeightMeasure();
                    lscGroupTotalUnits  += entity.getUnitQuantity();
                    // Fill destinationLKocations and SpecifiedFluxLocation (FluxLocationCatchTypeEnum)
                    fillLocationsAndGearsToDetails(fluxLocationsList, lscGroupDetailsDto);
                    fillFishingGears(fishingGears, lscGroupDetailsDto);
                    break;
                case BMS:
                    // Weight and Units
                    bmsGroupTotalWeight += entity.getWeightMeasure();
                    bmsGroupTotalUnits  += entity.getUnitQuantity();
                    // Fill destinationLKocations and SpecifiedFluxLocation (FluxLocationCatchTypeEnum)
                    fillLocationsAndGearsToDetails(fluxLocationsList, bmsGroupDetailsDto);
                    fillFishingGears(fishingGears, bmsGroupDetailsDto);
                    break;
                default :
                    log.error("While constructing Fa Catch Section of the view the FaCatchEntity with id : "+entity.getId()+" is neither LSC or BMS!");
            }
        }
        lscGroupDetailsDto.setUnit(lscGroupTotalUnits);
        lscGroupDetailsDto.setWeight(lscGroupTotalWeight);
        bmsGroupDetailsDto.setUnit(bmsGroupTotalUnits);
        bmsGroupDetailsDto.setWeight(bmsGroupTotalWeight);
        groupingDetailsMap.put(LSC, lscGroupDetailsDto);
        groupingDetailsMap.put(BMS, bmsGroupDetailsDto);
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
     * Fills the locations on FaCatchGroupDetailsDto DTO.
     *
     * @param fluxLocations
     * @param groupDetailsDto
     */
    private static void fillLocationsAndGearsToDetails(Set<FluxLocationEntity> fluxLocations, FaCatchGroupDetailsDto groupDetailsDto) {
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
}

