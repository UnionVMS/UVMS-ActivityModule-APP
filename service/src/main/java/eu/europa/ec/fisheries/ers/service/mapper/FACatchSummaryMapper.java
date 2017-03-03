/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.*;


/**
 * Created by sanera on 30/01/2017.
 */
@Mapper
@Slf4j
public abstract class FACatchSummaryMapper extends BaseMapper {

    public static final FACatchSummaryMapper INSTANCE = Mappers.getMapper(FACatchSummaryMapper.class);

    @Mappings({
            @Mapping(target = "groups", expression = "java(populateGroupCriteriaWithValue(customEntity))"),
            @Mapping(target = "summaryTable", expression = "java(getSummaryTableDTO(catchSummaryEntityList))")
    })
    public abstract FACatchSummaryRecordDTO mapToFACatchSummaryRecordDTO(FaCatchSummaryCustomEntity customEntity, List<FaCatchSummaryCustomEntity> catchSummaryEntityList);


    @Mappings({
            @Mapping(target = "fishSizeSummaries", expression = "java(getSummaryFishSizeList(summaryTableDTO.getSummaryFishSize()))"),
            @Mapping(target = "faCatchTypeSummaries", expression = "java(getFaCatchTypeSummaries(summaryTableDTO.getSummaryFaCatchType()))")
    })
    public abstract SummaryTable mapToSummaryTable(SummaryTableDTO summaryTableDTO);


    /**
     * Create List of Group Criterias. Add only those groups which have some value associated with them.
     *
     * @param customEntity
     * @return
     */
    private List<GroupCriteriaWithValue> populateGroupCriteriaWithValue(FaCatchSummaryCustomEntity customEntity) {
        List<GroupCriteriaWithValue> groups = new ArrayList<>();

        if (customEntity == null) {
            return groups;
        }

        if (StringUtils.isNotEmpty(customEntity.getDay())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.DATE_DAY, customEntity.getDay()));
        }

        if (StringUtils.isNotEmpty(customEntity.getMonth())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.DATE_MONTH, customEntity.getMonth()));
        }


        if (StringUtils.isNotEmpty(customEntity.getYear())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.DATE_YEAR, customEntity.getYear()));
        }

        if (StringUtils.isNotEmpty(customEntity.getEffortZone())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.EFFORT_ZONE, customEntity.getEffortZone()));
        }

        if (StringUtils.isNotEmpty(customEntity.getFaoArea())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.FAO_AREA, customEntity.getFaoArea()));
        }

        if (StringUtils.isNotEmpty(customEntity.getGfcmGsa())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GFCM_GSA, customEntity.getGfcmGsa()));
        }

        if (StringUtils.isNotEmpty(customEntity.getGfcmStatRectangle())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GFCM_STAT_RECTANGLE, customEntity.getGfcmStatRectangle()));
        }

        if (StringUtils.isNotEmpty(customEntity.getIcesStatRectangle())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.ICES_STAT_RECTANGLE, customEntity.getIcesStatRectangle()));
        }
        if (StringUtils.isNotEmpty(customEntity.getTerritory())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.TERRITORY, customEntity.getTerritory()));
        }

        if (StringUtils.isNotEmpty(customEntity.getRfmo())) {

            groups.add(new GroupCriteriaWithValue(GroupCriteria.RFMO, customEntity.getRfmo()));
        }

        if (StringUtils.isNotEmpty(customEntity.getFlagState())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.FLAG_STATE, customEntity.getFlagState()));
        }

        if (StringUtils.isNotEmpty(customEntity.getGearType())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GEAR_TYPE, customEntity.getGearType()));
        }


        if (StringUtils.isNotEmpty(customEntity.getPresentation())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.PRESENTATION, customEntity.getPresentation()));
        }

        if (StringUtils.isNotEmpty(customEntity.getVesselTransportGuid())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.VESSEL, customEntity.getVesselTransportGuid()));
        }

        return groups;

    }


    /**
     * Process All rows belonging to same group to calculate weights. And create Summary table structure
     *
     * @param catchSummaryEntityList
     * @return SummaryTableDTO calculations for perticular group
     */
    private SummaryTableDTO getSummaryTableDTO(List<FaCatchSummaryCustomEntity> catchSummaryEntityList) {
        SummaryTableDTO summaryTable = new SummaryTableDTO();

        for (FaCatchSummaryCustomEntity entity : catchSummaryEntityList) {
            populateSummaryTable(summaryTable, entity);
        }
        return summaryTable;
    }

    private void populateSummaryTable(SummaryTableDTO summaryTable, FaCatchSummaryCustomEntity customEntity) {
        Double speciesCnt = customEntity.getCount();

        // If FishClass information is present then only build structure for FishClass
        if (customEntity.getFishClass() != null) {
            summaryTable.setSummaryFishSize(getFishSizeClassMap(summaryTable, customEntity, speciesCnt));
        }

        // If CatchType information is present then only build structure for CatchType
        if (customEntity.getTypeCode() != null) {
            summaryTable.setSummaryFaCatchType(getFaCatchTypeEnumMap(summaryTable, customEntity, speciesCnt));
        }

    }

    /**
     * Create Map with Different FishClasses and its aggregated weight values.If species information is present, calculate for different species as well.
     *
     * @param summaryTable - Add to this object new calculations.
     * @param customEntity - Process this entity to extract FishSize data
     * @param speciesCnt   - Add this count to existing count
     * @return
     */
    @NotNull
    private Map<FishSizeClassEnum, Object> getFishSizeClassMap(SummaryTableDTO summaryTable, FaCatchSummaryCustomEntity customEntity, Double speciesCnt) {

        Map<FishSizeClassEnum, Object> fishSizeSummaryMap = summaryTable.getSummaryFishSize();

        // Perform logic to calculate weight sum if user has not asked for groupBy Species. So, do not calculate for individual species.
        if (customEntity.getFishClass() != null && customEntity.getSpecies() == null) {
            return populateFishSizeClassMapForOnlyForFishClass(customEntity, speciesCnt, fishSizeSummaryMap);
        }

        // Post process data to consider different species and calculate count for it.
        if (MapUtils.isEmpty(fishSizeSummaryMap)) {
            fishSizeSummaryMap = new EnumMap<>(FishSizeClassEnum.class);
            fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), createSpeciesCountMap(customEntity, speciesCnt));

        } else {
            Map<String, Double> speciesCountMap = (Map<String, Double>) fishSizeSummaryMap.get(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()));
            if (MapUtils.isEmpty(speciesCountMap)) {
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), createSpeciesCountMap(customEntity, speciesCnt));
            } else if (customEntity.getSpecies() != null) {
                populateSpeciesMap(customEntity, speciesCnt, speciesCountMap);
            }
        }
        return fishSizeSummaryMap;
    }


    @NotNull
    private Map<FishSizeClassEnum, Object> populateFishSizeClassMapForOnlyForFishClass(FaCatchSummaryCustomEntity customEntity, Double speciesCnt, Map<FishSizeClassEnum, Object> fishSizeSummaryMap) {
        if (MapUtils.isEmpty(fishSizeSummaryMap)) {
            fishSizeSummaryMap = new EnumMap<>(FishSizeClassEnum.class);
            fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), speciesCnt);
        } else {
            Object count = fishSizeSummaryMap.get(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()));
            if (count != null && count instanceof Double) {
                count = (Double) count + speciesCnt;
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), count);
            } else {
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), speciesCnt);
            }
        }
        return fishSizeSummaryMap;
    }

    /**
     * Create Map with Different FaCatchTypes and its aggregated weight values.If species information is present, calculate for different species as well.
     *
     * @param summaryTable - Add to this object new calculations.
     * @param customEntity - Process this entity to extract FishSize data
     * @param speciesCnt   - Add this count to existing count
     * @return
     */
    @NotNull
    private Map<FaCatchTypeEnum, Object> getFaCatchTypeEnumMap(SummaryTableDTO summaryTable, FaCatchSummaryCustomEntity customEntity, Double speciesCnt) {

        Map<FaCatchTypeEnum, Object> faCatchSummaryMap = summaryTable.getSummaryFaCatchType();

        // This method will calculate data only for FACatchType and it will ignore species
        if (customEntity.getTypeCode() != null && customEntity.getSpecies() == null) {
            return populateFaCatchTypeMapOnlyForCatchType(customEntity, speciesCnt, faCatchSummaryMap);
        }

        // This method will calculate data for FACatchType including species information
        if (MapUtils.isEmpty(faCatchSummaryMap)) {
            faCatchSummaryMap = new EnumMap<>(FaCatchTypeEnum.class);
            faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), createSpeciesCountMap(customEntity, speciesCnt));
        } else {
            Map<String, Double> speciesCountMap = (Map<String, Double>) faCatchSummaryMap.get(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()));
            if (MapUtils.isEmpty(speciesCountMap)) {
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), createSpeciesCountMap(customEntity, speciesCnt));
            } else if (customEntity.getSpecies() != null) {
                populateSpeciesMap(customEntity, speciesCnt, speciesCountMap);
            }
        }
        return faCatchSummaryMap;
    }

    @NotNull
    private Map<FaCatchTypeEnum, Object> populateFaCatchTypeMapOnlyForCatchType(FaCatchSummaryCustomEntity customEntity, Double speciesCnt, Map<FaCatchTypeEnum, Object> faCatchSummaryMap) {
        if (MapUtils.isEmpty(faCatchSummaryMap)) {
            faCatchSummaryMap = new EnumMap<>(FaCatchTypeEnum.class);
            faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), speciesCnt);
        } else {

            Object count = faCatchSummaryMap.get(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()));
            if (count != null && count instanceof Double) {
                count = (Double) count + speciesCnt;
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), count);
            } else {
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), speciesCnt);
            }
        }
        return faCatchSummaryMap;
    }


    protected List<SummaryFishSize> getSummaryFishSizeList(Map<FishSizeClassEnum, Object> summaryFishSizeMap) {
        if (MapUtils.isEmpty(summaryFishSizeMap)) {
            return new ArrayList<>();
        }

        List<SummaryFishSize> summaryFishSizes = new ArrayList<>();

        for (Map.Entry<FishSizeClassEnum, Object> entry : summaryFishSizeMap.entrySet()) {
            SummaryFishSize summaryFishSize = new SummaryFishSize();
            summaryFishSize.setFishSize(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof Map) {
                summaryFishSize.setSpecies(getSpeciesCounts((Map<String, Double>) value));
            } else if (value instanceof Double) {
                summaryFishSize.setFishSizeCount((Double) value);
            }
            summaryFishSizes.add(summaryFishSize);
        }

        log.debug("SummaryFishSize List is created");
        return summaryFishSizes;

    }


    protected List<SummaryFACatchtype> getFaCatchTypeSummaries(Map<FaCatchTypeEnum, Object> summaryFaCatchTypeMap) {
        List<SummaryFACatchtype> summaryFishCatchTypes = new ArrayList<>();

        if (!MapUtils.isEmpty(summaryFaCatchTypeMap)) {

            for (Map.Entry<FaCatchTypeEnum, Object> entry : summaryFaCatchTypeMap.entrySet()) {
                SummaryFACatchtype summaryFACatchtype = new SummaryFACatchtype();
                summaryFACatchtype.setCatchType(entry.getKey());

                Object value = entry.getValue();
                if (value instanceof Map) {
                    summaryFACatchtype.setSpecies(getSpeciesCounts((Map<String, Double>) value));
                } else if (value instanceof Double) {
                    summaryFACatchtype.setCatchTypeCount((Double) value);
                }
                summaryFishCatchTypes.add(summaryFACatchtype);
            }
        }
        log.debug("SummaryFACatchtype List is created");
        return summaryFishCatchTypes;
    }

    @NotNull
    private List<SpeciesCount> getSpeciesCounts(Map<String, Double> speciesMap) {
        if (MapUtils.isEmpty(speciesMap)) {
            return new ArrayList<>();
        }
        List<SpeciesCount> speciesCounts = new ArrayList<>();
        for (Map.Entry<String, Double> entrySpecies : speciesMap.entrySet()) {
            SpeciesCount speciesCount = new SpeciesCount();
            speciesCount.setSpaciesName(entrySpecies.getKey());
            speciesCount.setCount(entrySpecies.getValue());
            speciesCounts.add(speciesCount);
        }
        return speciesCounts;
    }


    private void populateSpeciesMap(FaCatchSummaryCustomEntity customEntity, Double speciesCnt, Map<String, Double> speciesCountMap) {
        Double totalCountForSpecies = speciesCountMap.get(customEntity.getSpecies().toUpperCase());
        if (totalCountForSpecies == null) {
            speciesCountMap.put(customEntity.getSpecies().toUpperCase(), speciesCnt);
        } else {
            totalCountForSpecies = totalCountForSpecies + speciesCnt;
            speciesCountMap.put(customEntity.getSpecies().toUpperCase(), totalCountForSpecies);
        }
    }

    @NotNull
    private Map<String, Double> createSpeciesCountMap(FaCatchSummaryCustomEntity customEntity, Double speciesCnt) {
        Map<String, Double> speciesCountMap = new HashMap<>();
        if (customEntity.getSpecies() != null) {
            speciesCountMap.put(customEntity.getSpecies().toUpperCase(), speciesCnt);
        }
        return speciesCountMap;
    }


}
