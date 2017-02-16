package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.Area;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by sanera on 30/01/2017.
 */
@Mapper
public abstract class FACatchSummaryMapper extends BaseMapper {

    public static final FACatchSummaryMapper INSTANCE = Mappers.getMapper(FACatchSummaryMapper.class);

    @Mappings({
        //    @Mapping(target = "date", source = "date"),
            @Mapping(target = "day", expression = "java(customEntity.getDay())"),
            @Mapping(target = "month", expression = "java(customEntity.getMonth())"),
            @Mapping(target = "year", expression = "java(customEntity.getYear())"),
            @Mapping(target = "area", expression = "java(getArea(customEntity))"),
            @Mapping(target = "flagState", expression = "java(customEntity.getFlagState())"),
            @Mapping(target = "gearType", expression = "java(customEntity.getGearType())"),
            @Mapping(target = "presentation", expression = "java(customEntity.getPresentation())"),
            @Mapping(target = "vesselTransportGuid", expression = "java(customEntity.getVesselTransportGuid())"),
            @Mapping(target = "summaryTable", expression = "java(getSummaryTable(catchSummaryEntityList))")
    })
    public abstract FACatchSummaryRecordDTO mapToFACatchSummaryDTO(FaCatchSummaryCustomEntity customEntity, List<FaCatchSummaryCustomEntity> catchSummaryEntityList);



    @Mappings({
            //    @Mapping(target = "date", source = "date"),
            @Mapping(target = "fishSizeSummaries", expression = "java(getSummaryFishSizeList(summaryTableDTO.getSummaryFishSize()))"),
            @Mapping(target = "faCatchTypeSummaries", expression = "java(getFaCatchTypeSummaries(summaryTableDTO.getSummaryFaCatchType()))")
    })
    public abstract SummaryTable mapToSummaryTable(SummaryTableDTO summaryTableDTO);


    @Mappings({
            //    @Mapping(target = "date", source = "date"),
            @Mapping(target = "summaryFishSize", expression = "java(getSummaryFishSizeMap(summaryTable.getFishSizeSummaries()))"),
            @Mapping(target = "summaryFaCatchType", expression = "java(getSummaryFaCatchTypeMap(summaryTable.getFaCatchTypeSummaries()))")
    })
    public abstract SummaryTableDTO mapToSummaryTableDTO(SummaryTable summaryTable);


    protected Map<FishSizeClassEnum,Map<String,Long>> getSummaryFishSizeMap(List<SummaryFishSize> fishSizeSummaries){

        Map<FishSizeClassEnum,Map<String,Long>> fishSizeClassEnumMapMap = new HashMap<>();

        for(SummaryFishSize summaryFishSize:fishSizeSummaries){
            FishSizeClassEnum fishSize=  summaryFishSize.getFishSize();
            Map<String,Long> speciesMap = new HashMap<>();
            List<SpeciesCount> speciesCounts= summaryFishSize.getSpecies();
            for(SpeciesCount speciesCount: speciesCounts){
                speciesMap.put(speciesCount.getSpaciesName(),speciesCount.getCount());
            }
            fishSizeClassEnumMapMap.put(fishSize,speciesMap);
        }


        return fishSizeClassEnumMapMap;

    }

    protected Map<FaCatchTypeEnum,Map<String,Long>> getSummaryFaCatchTypeMap(List<SummaryFACatchtype> summaryFACatchtypes){

        Map<FaCatchTypeEnum,Map<String,Long>> faCatchTypeEnumMap = new HashMap<>();

        for(SummaryFACatchtype summaryFACatchtype:summaryFACatchtypes){
            FaCatchTypeEnum catchType=  summaryFACatchtype.getCatchType();
            Map<String,Long> speciesMap = new HashMap<>();
            List<SpeciesCount> speciesCounts= summaryFACatchtype.getSpecies();
            for(SpeciesCount speciesCount: speciesCounts){
                speciesMap.put(speciesCount.getSpaciesName(),speciesCount.getCount());
            }
            faCatchTypeEnumMap.put(catchType,speciesMap);
        }


        return faCatchTypeEnumMap;

    }


    protected List<SummaryFishSize> getSummaryFishSizeList(Map<FishSizeClassEnum,Map<String,Long>> summaryFishSizeMap){
        List<SummaryFishSize> summaryFishSizes = new ArrayList<>();

        if(!MapUtils.isEmpty(summaryFishSizeMap)) {
            for (Map.Entry<FishSizeClassEnum, Map<String, Long>> entry : summaryFishSizeMap.entrySet()) {
                SummaryFishSize summaryFishSize = new SummaryFishSize();

                summaryFishSize.setFishSize(entry.getKey());

                Map<String, Long> speciesMap = entry.getValue();
                if(MapUtils.isNotEmpty(speciesMap)) {
                    List<SpeciesCount> speciesCounts = getSpeciesCounts(speciesMap);
                    summaryFishSize.setSpecies(speciesCounts);
                }

                summaryFishSizes.add(summaryFishSize);
            }
        }

        return summaryFishSizes;

    }

    @NotNull
    private List<SpeciesCount> getSpeciesCounts(Map<String, Long> speciesMap) {
        List<SpeciesCount> speciesCounts = new ArrayList<>();
        for (Map.Entry<String, Long> entrySpecies : speciesMap.entrySet()) {
            SpeciesCount speciesCount = new SpeciesCount();
            speciesCount.setSpaciesName(entrySpecies.getKey());
            speciesCount.setCount(entrySpecies.getValue());
            speciesCounts.add(speciesCount);
        }
        return speciesCounts;
    }


    protected List<SummaryFACatchtype> getFaCatchTypeSummaries(Map<FaCatchTypeEnum,Map<String,Long>> summaryFaCatchTypeMap){
        List<SummaryFACatchtype> summaryFishCatchTypes = new ArrayList<>();

        if(!MapUtils.isEmpty(summaryFaCatchTypeMap)) {
            for (Map.Entry<FaCatchTypeEnum, Map<String, Long>> entry : summaryFaCatchTypeMap.entrySet()) {
                SummaryFACatchtype summaryFACatchtype = new SummaryFACatchtype();
                summaryFACatchtype.setCatchType(entry.getKey());

                Map<String, Long> speciesMap = entry.getValue();
                if(MapUtils.isNotEmpty(speciesMap)) {
                    List<SpeciesCount> speciesCounts = getSpeciesCounts(speciesMap);
                    summaryFACatchtype.setSpecies(speciesCounts);
                }
                summaryFishCatchTypes.add(summaryFACatchtype);
            }
        }

        return summaryFishCatchTypes;

    }

    protected Area getArea(FaCatchSummaryCustomEntity customEntity) {
        return new Area(customEntity.getTerritory(), customEntity.getFaoArea(), customEntity.getIcesStatRectangle(), customEntity.getEffortZone()
                , customEntity.getRfmo(), customEntity.getGfcmGsa(), customEntity.getGfcmStatRectangle());
    }

    protected SummaryTableDTO getSummaryTable(List<FaCatchSummaryCustomEntity> catchSummaryEntityList) {
        SummaryTableDTO summaryTable = new SummaryTableDTO();

        for(FaCatchSummaryCustomEntity entity :catchSummaryEntityList){
            populateSummaryTable(summaryTable,entity);
        }
        return summaryTable;
    }


    private void populateSummaryTable(SummaryTableDTO summaryTable,FaCatchSummaryCustomEntity customEntity){
        long speciesCnt = customEntity.getCount();

        if(customEntity.getFishClass()!=null) {
            summaryTable.setSummaryFishSize(getFishSizeClassMap(summaryTable, customEntity, speciesCnt));
        }

        if(customEntity.getTypeCode()!=null) {
            summaryTable.setSummaryFaCatchType(getFaCatchTypeEnumMap(summaryTable, customEntity, speciesCnt));
        }

    }

    @NotNull
    private Map<FaCatchTypeEnum, Map<String, Long>> getFaCatchTypeEnumMap(SummaryTableDTO summaryTable, FaCatchSummaryCustomEntity customEntity, long speciesCnt) {
        Map<FaCatchTypeEnum, Map<String, Long>> faCatchSummaryMap = summaryTable.getSummaryFaCatchType();
        if (MapUtils.isEmpty(faCatchSummaryMap)) {
            faCatchSummaryMap = new HashMap<>();
            faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), getStringLongMap(customEntity, speciesCnt));
        } else {
            Map<String, Long> speciesCountMap = faCatchSummaryMap.get(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()));
            if (MapUtils.isEmpty(speciesCountMap)) {
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), getStringLongMap(customEntity, speciesCnt));
            } else if (customEntity.getSpecies() != null) {
                populateSpeciesMap(customEntity, speciesCnt, speciesCountMap);
            }
        }
        return faCatchSummaryMap;
    }



    @NotNull
    private Map<FishSizeClassEnum, Map<String, Long>> getFishSizeClassMap(SummaryTableDTO summaryTable, FaCatchSummaryCustomEntity customEntity, long speciesCnt) {
        Map<FishSizeClassEnum, Map<String, Long>> fishSizeSummaryMap = summaryTable.getSummaryFishSize();

        if (MapUtils.isEmpty(fishSizeSummaryMap)) {
            fishSizeSummaryMap = new HashMap<>();
            fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), getStringLongMap(customEntity, speciesCnt));

        } else {
            Map<String, Long> speciesCountMap = fishSizeSummaryMap.get(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()));
            if (MapUtils.isEmpty(speciesCountMap)) {
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), getStringLongMap(customEntity, speciesCnt));
            } else if (customEntity.getSpecies() != null) {
                populateSpeciesMap(customEntity, speciesCnt, speciesCountMap);
            }
        }
        return fishSizeSummaryMap;
    }

    private void populateSpeciesMap(FaCatchSummaryCustomEntity customEntity, long speciesCnt, Map<String, Long> speciesCountMap) {
        Long totalCountForSpecies = speciesCountMap.get(customEntity.getSpecies().toUpperCase());
        if (totalCountForSpecies == null) {
            speciesCountMap.put(customEntity.getSpecies().toUpperCase(), speciesCnt);
        } else {
            totalCountForSpecies = totalCountForSpecies + speciesCnt;
            speciesCountMap.put(customEntity.getSpecies().toUpperCase(), totalCountForSpecies);
        }
    }

    @NotNull
    private Map<String, Long> getStringLongMap(FaCatchSummaryCustomEntity customEntity, long speciesCnt) {
        Map<String,Long> speciesCountMap = new HashMap<>();
        if(customEntity.getSpecies() !=null) {
            speciesCountMap.put(customEntity.getSpecies().toUpperCase(), speciesCnt);
        }
        return speciesCountMap;
    }


}
