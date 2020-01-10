/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.proxy.FaCatchSummaryCustomProxy;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishSizeClassEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteriaWithValue;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.SummaryTableDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
@Slf4j
public abstract class FACatchSummaryMapper extends BaseMapper {

    public static final FACatchSummaryMapper INSTANCE = Mappers.getMapper(FACatchSummaryMapper.class);

    @Mappings({
            @Mapping(target = "groups", expression = "java(populateGroupCriteriaWithValue(customEntity))"),
            @Mapping(target = "summaryTable", expression = "java(getSummaryTableDTO(catchSummaryEntityList))")
    })
    public abstract FACatchSummaryRecordDTO mapToFACatchSummaryRecordDTO(FaCatchSummaryCustomProxy customEntity, List<FaCatchSummaryCustomProxy> catchSummaryEntityList);


    @Mappings({
            @Mapping(target = "groups", expression = "java(populateGroupCriteriaWithValue(customEntity))"),
            @Mapping(target = "summaryTable", expression = "java(getSummaryTableDTOWithPresentation(catchSummaryEntityList))")
    })
    public abstract FACatchSummaryRecordDTO mapToFACatchSummaryRecordDTOWithPresentation(FaCatchSummaryCustomProxy customEntity, List<FaCatchSummaryCustomProxy> catchSummaryEntityList);

    /**
     * Create List of Group Criterias. Add only those groups which have some value associated with them.
     *
     * @param customEntity
     */
    public List<GroupCriteriaWithValue> populateGroupCriteriaWithValue(FaCatchSummaryCustomProxy customEntity) {
        List<GroupCriteriaWithValue> groups = new ArrayList<>();

        if (customEntity ==null) {
            return groups;
        }

        if (StringUtils.isNotEmpty(customEntity.getDate())) {
            groups.add( new GroupCriteriaWithValue(GroupCriteria.DATE,customEntity.getDate()));
        }

        if (StringUtils.isNotEmpty(customEntity.getDay())) {
            groups.add( new GroupCriteriaWithValue(GroupCriteria.DATE_DAY,customEntity.getDay()));
        }

        if (StringUtils.isNotEmpty(customEntity.getMonth())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.DATE_MONTH,customEntity.getMonth()));
        }

        if (StringUtils.isNotEmpty(customEntity.getYear())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.DATE_YEAR,customEntity.getYear()));
        }

        if (StringUtils.isNotEmpty(customEntity.getEffortZone())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.EFFORT_ZONE,customEntity.getEffortZone()));
        }

        if (StringUtils.isNotEmpty(customEntity.getFaoArea())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.FAO_AREA,customEntity.getFaoArea()));
        }

        if (StringUtils.isNotEmpty(customEntity.getGfcmGsa())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GFCM_GSA,customEntity.getGfcmGsa()));
        }

        if (StringUtils.isNotEmpty(customEntity.getGfcmStatRectangle())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GFCM_STAT_RECTANGLE,customEntity.getGfcmStatRectangle()));
        }

        if (StringUtils.isNotEmpty(customEntity.getIcesStatRectangle())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.ICES_STAT_RECTANGLE,customEntity.getIcesStatRectangle()));
        }

        if (StringUtils.isNotEmpty(customEntity.getTerritory())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.TERRITORY,customEntity.getTerritory()));
        }

        if (StringUtils.isNotEmpty(customEntity.getRfmo())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.RFMO,customEntity.getRfmo()));
        }

        if (StringUtils.isNotEmpty(customEntity.getFlagState())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.FLAG_STATE,customEntity.getFlagState()));
        }

        if (StringUtils.isNotEmpty(customEntity.getGearType())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GEAR_TYPE,customEntity.getGearType()));
        }

        if (StringUtils.isNotEmpty(customEntity.getPresentation())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.PRESENTATION,customEntity.getPresentation()));
        }

        if (StringUtils.isNotEmpty(customEntity.getVesselTransportGuid())) {
            groups.add(new GroupCriteriaWithValue(GroupCriteria.VESSEL,customEntity.getVesselTransportGuid()));
        }

        return groups;
    }

    /**
     * Process All rows belonging to same group to calculate weights. And create Summary table structure
     *
     * @param catchSummaryEntityList
     * @return SummaryTableDTO calculations for perticular group
     */
    public SummaryTableDTO getSummaryTableDTO(List<FaCatchSummaryCustomProxy> catchSummaryEntityList) {
        SummaryTableDTO summaryTable = new SummaryTableDTO();

        for (FaCatchSummaryCustomProxy entity : catchSummaryEntityList) {
            Double speciesCnt = entity.getCount();

            // If FishClass information is present then only build structure for FishClass
            if(entity.getFishClass()!=null) {
                summaryTable.setSummaryFishSize(getFishSizeClassMap(summaryTable, entity, speciesCnt));
            }

            // If CatchType information is present then only build structure for CatchType
            if(entity.getTypeCode()!=null) {
                summaryTable.setSummaryFaCatchType(getFaCatchTypeEnumMap(summaryTable, entity, speciesCnt));
            }
        }
        return summaryTable;
    }


    /**
     *  Process All rows belonging to same group to calculate weights. And create Summary table structure
     * @param catchSummaryEntityList
     * @return SummaryTableDTO calculations for perticular group
     */
    protected SummaryTableDTO getSummaryTableDTOWithPresentation(List<FaCatchSummaryCustomProxy> catchSummaryEntityList) {
        SummaryTableDTO summaryTable = new SummaryTableDTO();

        for (FaCatchSummaryCustomProxy entity : catchSummaryEntityList) {
            Double count = entity.getCount();

            // If FishClass information is present then only build structure for FishClass
            if(entity.getFishClass()!=null) {
                summaryTable.setSummaryFishSize(getFishSizeClassMapWithPresentation(summaryTable, entity, count));
            }

            // If CatchType information is present then only build structure for CatchType
            if(entity.getTypeCode()!=null) {
                summaryTable.setSummaryFaCatchType(getFaCatchTypeEnumMapWithPresentation(summaryTable, entity, count));
            }
        }
        return summaryTable;
    }


     /**
     *  Create Map with Different FishClasses and its aggregated weight values.If species information is present, calculate for different species as well.
     * @param summaryTable - Add to this object new calculations.
     * @param customEntity - Process this entity to extract FishSize data
     * @param speciesCnt - Add this count to existing count
     */
    @NotNull
    private Map<FishSizeClassEnum, Object> getFishSizeClassMap(SummaryTableDTO summaryTable, FaCatchSummaryCustomProxy customEntity, Double speciesCnt) {

        Map<FishSizeClassEnum, Object> fishSizeSummaryMap = summaryTable.getSummaryFishSize();

        // Perform logic to calculate weight sum if user has not asked for groupBy Species. So, do not calculate for individual species.
        if (customEntity.getFishClass() != null && customEntity.getSpecies() == null) {
            return populateFishSizeClassMapForOnlyForFishClass(customEntity, speciesCnt, fishSizeSummaryMap);
        }

        // Post process data to consider different species and calculate count for it.
        if (MapUtils.isEmpty(fishSizeSummaryMap)) {
            Map<String, Double> speciesMap=createSpeciesCountMap(customEntity, speciesCnt);
            if(MapUtils.isNotEmpty(speciesMap)) {
                fishSizeSummaryMap = new EnumMap<>(FishSizeClassEnum.class);
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), speciesMap);
            }
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


    /**
     *  Create Map with Different FishClasses and its aggregated weight values.If species information is present, calculate for different species as well.
     * @param summaryTable - Add to this object new calculations.
     * @param customEntity - Process this entity to extract FishSize data
     * @param speciesCnt - Add this count to existing count
     */
    @NotNull
    private Map<FishSizeClassEnum, Object> getFishSizeClassMapWithPresentation(SummaryTableDTO summaryTable, FaCatchSummaryCustomProxy customEntity, Double speciesCnt) {

        Map<FishSizeClassEnum, Object> fishSizeSummaryMap = summaryTable.getSummaryFishSize();

        // Post process data to consider different species and calculate count for it.
        if (MapUtils.isEmpty(fishSizeSummaryMap)) {
            Map<String, Map<String, Double>> presentationMap= createPresentationCountMap(customEntity, speciesCnt);
            if(MapUtils.isNotEmpty(presentationMap)) { // If you find any presntation value, then only add it.
                fishSizeSummaryMap = new EnumMap<>(FishSizeClassEnum.class);
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), presentationMap);
            }

        } else {

            Map<String, Map<String,Double>> speciesCountMap = ( Map<String, Map<String,Double>>) fishSizeSummaryMap.get(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()));
            if (MapUtils.isEmpty(speciesCountMap)) {
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), createPresentationCountMap(customEntity, speciesCnt));
            }else if (customEntity.getSpecies() != null) {
                populateSpeciesMapWithPresentation(customEntity, speciesCnt, speciesCountMap);
            }
        }
        return fishSizeSummaryMap;
    }


    /**
     *  Create Map with Different FaCatchTypes and its aggregated weight values.If species information is present, calculate for different species as well.
     * @param summaryTable - Add to this object new calculations.
     * @param customEntity - Process this entity to extract FishSize data
     * @param speciesCnt - Add this count to existing count
     */
    @NotNull
    private Map<FaCatchTypeEnum, Object> getFaCatchTypeEnumMapWithPresentation(SummaryTableDTO summaryTable, FaCatchSummaryCustomProxy customEntity, Double speciesCnt) {

        Map<FaCatchTypeEnum, Object> faCatchSummaryMap = summaryTable.getSummaryFaCatchType();

          // This method will calculate data for FACatchType including species information
        if (MapUtils.isEmpty(faCatchSummaryMap)) {
            Map<String, Map<String, Double>> faCatchTypeMap=createPresentationCountMap(customEntity, speciesCnt);
            if(MapUtils.isNotEmpty(faCatchTypeMap)) { // If you find any presntation value, then only add it.
                faCatchSummaryMap = new EnumMap<>(FaCatchTypeEnum.class);
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), createPresentationCountMap(customEntity, speciesCnt));
            }
        } else {
            Map<String, Map<String,Double>> speciesCountMap = (Map<String, Map<String,Double>>) faCatchSummaryMap.get(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()));
            if (MapUtils.isEmpty(speciesCountMap)) {
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), createPresentationCountMap(customEntity, speciesCnt));
            } else if (customEntity.getSpecies() != null) {
                populateSpeciesMapWithPresentation(customEntity, speciesCnt, speciesCountMap);
            }
        }
        return faCatchSummaryMap;
    }


    @NotNull
    private Map<FishSizeClassEnum, Object> populateFishSizeClassMapForOnlyForFishClass(FaCatchSummaryCustomProxy customEntity, Double speciesCnt, Map<FishSizeClassEnum, Object> fishSizeSummaryMap) {
        String fishClass = customEntity.getFishClass().toUpperCase();
        if (fishSizeSummaryMap == null) {
            fishSizeSummaryMap = new HashMap<>();
            fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(fishClass), speciesCnt);
        } else {
            Object count = fishSizeSummaryMap.get(FishSizeClassEnum.valueOf(fishClass));
            if (count instanceof Double) {
                count = (Double) count + speciesCnt;
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(fishClass), count);
            } else {
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(fishClass), speciesCnt);
            }
        }
        return fishSizeSummaryMap;
    }

    /**
     *  Create Map with Different FaCatchTypes and its aggregated weight values.If species information is present, calculate for different species as well.
     * @param summaryTable - Add to this object new calculations.
     * @param customEntity - Process this entity to extract FishSize data
     * @param speciesCnt - Add this count to existing count
     */
    @NotNull
    private Map<FaCatchTypeEnum, Object> getFaCatchTypeEnumMap(SummaryTableDTO summaryTable, FaCatchSummaryCustomProxy customEntity, Double speciesCnt) {

        Map<FaCatchTypeEnum, Object> faCatchSummaryMap = summaryTable.getSummaryFaCatchType();

        // This method will calculate data only for FACatchType and it will ignore species
        if (customEntity.getTypeCode() != null && customEntity.getSpecies() == null) {
            return populateFaCatchTypeMapOnlyForCatchType(customEntity, speciesCnt, faCatchSummaryMap);
        }

        // This method will calculate data for FACatchType including species information
        if (MapUtils.isEmpty(faCatchSummaryMap)) {
            Map<String, Double> speciesMap= createSpeciesCountMap(customEntity, speciesCnt);
            if(MapUtils.isNotEmpty(speciesMap)) { // We do not want to add the value if map is empty
                faCatchSummaryMap = new EnumMap<>(FaCatchTypeEnum.class);
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), speciesMap);
            }
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

    /**
     *
     * @param customEntity
     * @param speciesCnt
     * @param faCatchSummaryMap
     */
    @NotNull
    private Map<FaCatchTypeEnum, Object> populateFaCatchTypeMapOnlyForCatchType(FaCatchSummaryCustomProxy customEntity, Double speciesCnt, Map<FaCatchTypeEnum, Object> faCatchSummaryMap) {
        String typeCode = customEntity.getTypeCode().toUpperCase();
        if (MapUtils.isEmpty(faCatchSummaryMap)) {
            faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(typeCode), speciesCnt);
        } else {
            Object count = faCatchSummaryMap.get(FaCatchTypeEnum.valueOf(typeCode));
            if (count instanceof Double) {
                count = (Double) count + speciesCnt;
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(typeCode), count);
            } else {
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(typeCode), speciesCnt);
            }
        }
        return faCatchSummaryMap;
    }

    private void populateSpeciesMap(FaCatchSummaryCustomProxy customEntity, Double speciesCnt, Map<String, Double> speciesCountMap) {
        Double totalCountForSpecies = speciesCountMap.get(customEntity.getSpecies().toUpperCase());
        if (totalCountForSpecies == null) {
            speciesCountMap.put(customEntity.getSpecies().toUpperCase(), speciesCnt);
        } else {
            totalCountForSpecies = totalCountForSpecies + speciesCnt;
            speciesCountMap.put(customEntity.getSpecies().toUpperCase(), totalCountForSpecies);
        }
    }

    private void populateSpeciesMapWithPresentation(FaCatchSummaryCustomProxy customEntity, Double speciesCnt, Map<String, Map<String, Double>> speciesPresentationCountMap) {
        Map<String,Double> presentationMap= speciesPresentationCountMap.get(customEntity.getSpecies().toUpperCase());
        if(MapUtils.isEmpty(presentationMap)){
            if(customEntity.getPresentation()!=null){
                presentationMap = new HashMap<>();
                presentationMap.put(customEntity.getPresentation().toUpperCase(),speciesCnt);
                speciesPresentationCountMap.put(customEntity.getSpecies().toUpperCase(),presentationMap);
            }

        }else if(customEntity.getPresentation()!=null){
            Double value=presentationMap.get(customEntity.getPresentation().toUpperCase());
            if(value!=null){
                value = value+speciesCnt;
                presentationMap.put(customEntity.getPresentation().toUpperCase(),value);
            }else{
                presentationMap.put(customEntity.getPresentation().toUpperCase(),speciesCnt);
            }
            speciesPresentationCountMap.put(customEntity.getSpecies().toUpperCase(),presentationMap);
        }
    }


    @NotNull
    private Map<String, Double> createSpeciesCountMap(FaCatchSummaryCustomProxy customEntity, Double speciesCnt) {
        Map<String,Double> speciesCountMap = new HashMap<>();
        if(customEntity.getSpecies() !=null) {
            speciesCountMap.put(customEntity.getSpecies().toUpperCase(), speciesCnt);
        }
        return speciesCountMap;
    }


    @NotNull
    private Map<String, Map<String, Double>> createPresentationCountMap(FaCatchSummaryCustomProxy customEntity, Double presentationCnt) {
        Map<String,Map<String,Double>> speciesMap = new HashMap<>();

        if(customEntity.getSpecies() !=null  && customEntity.getPresentation() !=null) {
            Map<String,Double> presentationMap=new HashMap<>();
            presentationMap.put(customEntity.getPresentation().toUpperCase(),presentationCnt);
            speciesMap.put(customEntity.getSpecies().toUpperCase(),presentationMap);
        }

        return speciesMap;
    }

}
