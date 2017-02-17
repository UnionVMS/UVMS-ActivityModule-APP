package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.Area;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
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
           /* @Mapping(target = "day", expression = "java(customEntity.getDay())"),
            @Mapping(target = "month", expression = "java(customEntity.getMonth())"),
            @Mapping(target = "year", expression = "java(customEntity.getYear())"),
            @Mapping(target = "area", expression = "java(getArea(customEntity))"),
            @Mapping(target = "flagState", expression = "java(customEntity.getFlagState())"),
            @Mapping(target = "gearType", expression = "java(customEntity.getGearType())"),
            @Mapping(target = "presentation", expression = "java(customEntity.getPresentation())"),
            @Mapping(target = "vesselTransportGuid", expression = "java(customEntity.getVesselTransportGuid())"),*/
            @Mapping(target = "groups", expression = "java(populateGroupCriteriaWithValue(customEntity))"),
            @Mapping(target = "summaryTable", expression = "java(getSummaryTable(catchSummaryEntityList))")
    })
    public abstract FACatchSummaryRecordDTO mapToFACatchSummaryDTO(FaCatchSummaryCustomEntity customEntity, List<FaCatchSummaryCustomEntity> catchSummaryEntityList);



    @Mappings({
            //    @Mapping(target = "date", source = "date"),
            @Mapping(target = "fishSizeSummaries", expression = "java(getSummaryFishSizeList(summaryTableDTO.getSummaryFishSize()))"),
            @Mapping(target = "faCatchTypeSummaries", expression = "java(getFaCatchTypeSummaries(summaryTableDTO.getSummaryFaCatchType()))")
    })
    public abstract SummaryTable mapToSummaryTable(SummaryTableDTO summaryTableDTO);



    public List<GroupCriteriaWithValue> populateGroupCriteriaWithValue(FaCatchSummaryCustomEntity customEntity){
        List<GroupCriteriaWithValue> groups = new ArrayList<>();

        if(customEntity.getDay() !=0 ){
            groups.add( new GroupCriteriaWithValue(GroupCriteria.DATE_DAY,""+customEntity.getDay()));
        }

        if(StringUtils.isNotEmpty(customEntity.getMonth())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.DATE_MONTH,customEntity.getMonth()));
        }


        if(customEntity.getYear() !=0){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.DATE_YEAR,""+customEntity.getYear()));
        }

        if(StringUtils.isNotEmpty(customEntity.getEffortZone())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.EFFORT_ZONE,customEntity.getEffortZone()));
        }

        if(StringUtils.isNotEmpty(customEntity.getFaoArea())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.FAO_AREA,customEntity.getFaoArea()));
        }

        if(StringUtils.isNotEmpty(customEntity.getGfcmGsa())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GFCM_GSA,customEntity.getGfcmGsa()));
        }

        if(StringUtils.isNotEmpty(customEntity.getGfcmStatRectangle())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GFCM_STAT_RECTANGLE,customEntity.getGfcmStatRectangle()));
        }

        if(StringUtils.isNotEmpty(customEntity.getIcesStatRectangle())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.ICES_STAT_RECTANGLE,customEntity.getIcesStatRectangle()));
        }
        if(StringUtils.isNotEmpty(customEntity.getTerritory())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.TERRITORY,customEntity.getTerritory()));
        }

        if(StringUtils.isNotEmpty(customEntity.getRfmo())){

            groups.add(new GroupCriteriaWithValue(GroupCriteria.RFMO,customEntity.getRfmo()));
        }

        if(StringUtils.isNotEmpty(customEntity.getFlagState())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.FLAG_STATE,customEntity.getFlagState()));
        }

        if(StringUtils.isNotEmpty(customEntity.getGearType())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GEAR_TYPE,customEntity.getGearType()));
        }



        if(StringUtils.isNotEmpty(customEntity.getPresentation())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.PRESENTATION,customEntity.getPresentation()));
        }

        if(StringUtils.isNotEmpty(customEntity.getVesselTransportGuid())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.VESSEL,customEntity.getVesselTransportGuid()));
        }

        return groups;

    }



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


  //  protected List<SummaryFishSize> getSummaryFishSizeList(Map<FishSizeClassEnum,Map<String,Long>> summaryFishSizeMap){
  protected List<SummaryFishSize> getSummaryFishSizeList(Map<FishSizeClassEnum,Object> summaryFishSizeMap){
        List<SummaryFishSize> summaryFishSizes = new ArrayList<>();

        if(!MapUtils.isEmpty(summaryFishSizeMap)) {
          //  for (Map.Entry<FishSizeClassEnum, Map<String, Long>> entry : summaryFishSizeMap.entrySet()) {
            for (Map.Entry<FishSizeClassEnum, Object> entry : summaryFishSizeMap.entrySet()) {
                SummaryFishSize summaryFishSize = new SummaryFishSize();

                summaryFishSize.setFishSize(entry.getKey());


            //    Map<String, Long> speciesMap = entry.getValue();
               Object value = entry.getValue();
                if(value instanceof Map) {
                    Map<String, Long> speciesMap = (Map<String, Long>) value;
                    if (MapUtils.isNotEmpty(speciesMap)) {
                        List<SpeciesCount> speciesCounts = getSpeciesCounts(speciesMap);
                        summaryFishSize.setSpecies(speciesCounts);
                    }
                }else if(value instanceof  Long){
                    summaryFishSize.setFishSizeCount((Long) value);
                }
                summaryFishSizes.add(summaryFishSize);
            }
        }

        return summaryFishSizes;

    }


   // protected List<SummaryFACatchtype> getFaCatchTypeSummaries(Map<FaCatchTypeEnum,Map<String,Long>> summaryFaCatchTypeMap){
   protected List<SummaryFACatchtype> getFaCatchTypeSummaries(Map<FaCatchTypeEnum,Object> summaryFaCatchTypeMap){
        List<SummaryFACatchtype> summaryFishCatchTypes = new ArrayList<>();

        if(!MapUtils.isEmpty(summaryFaCatchTypeMap)) {
        //    for (Map.Entry<FaCatchTypeEnum, Map<String, Long>> entry : summaryFaCatchTypeMap.entrySet()) {
            for (Map.Entry<FaCatchTypeEnum, Object> entry : summaryFaCatchTypeMap.entrySet()) {
                SummaryFACatchtype summaryFACatchtype = new SummaryFACatchtype();
                summaryFACatchtype.setCatchType(entry.getKey());

              //  Map<String, Long> speciesMap = entry.getValue();
                Object value = entry.getValue();
                if(value instanceof Map) {
                    Map<String, Long> speciesMap = (Map<String, Long>) value;
                    if (MapUtils.isNotEmpty(speciesMap)) {
                        List<SpeciesCount> speciesCounts = getSpeciesCounts(speciesMap);
                        summaryFACatchtype.setSpecies(speciesCounts);
                    }
                }else if(value instanceof  Long){
                    summaryFACatchtype.setCatchTypeCount((Long) value);
                }
                summaryFishCatchTypes.add(summaryFACatchtype);
            }
        }

        return summaryFishCatchTypes;

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
  //  private Map<FaCatchTypeEnum, Map<String, Long>> getFaCatchTypeEnumMap(SummaryTableDTO summaryTable, FaCatchSummaryCustomEntity customEntity, long speciesCnt) {
    private Map<FaCatchTypeEnum, Object> getFaCatchTypeEnumMap(SummaryTableDTO summaryTable, FaCatchSummaryCustomEntity customEntity, long speciesCnt) {
      //  Map<FaCatchTypeEnum, Map<String, Long>> faCatchSummaryMap = summaryTable.getSummaryFaCatchType();
        Map<FaCatchTypeEnum, Object> faCatchSummaryMap = summaryTable.getSummaryFaCatchType();

        if(customEntity.getTypeCode() !=null && customEntity.getSpecies() ==null){
            if (MapUtils.isEmpty(faCatchSummaryMap)) {
                faCatchSummaryMap = new HashMap<>();
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), speciesCnt);
            }else {

                Object count = faCatchSummaryMap.get(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()));
                if (count != null && count instanceof Long) {
                    count = (Long) count + speciesCnt;
                    faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), count);
                } else {
                    faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), (Long) speciesCnt);
                }
            }
            return faCatchSummaryMap;
        }


        if (MapUtils.isEmpty(faCatchSummaryMap)) {
            faCatchSummaryMap = new HashMap<>();
            faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), getStringLongMap(customEntity, speciesCnt));
        } else {
            Map<String, Long> speciesCountMap = (Map<String, Long>) faCatchSummaryMap.get(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()));
            if (MapUtils.isEmpty(speciesCountMap)) {
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), getStringLongMap(customEntity, speciesCnt));
            } else if (customEntity.getSpecies() != null) {
                populateSpeciesMap(customEntity, speciesCnt, speciesCountMap);
            }
        }
        return faCatchSummaryMap;
    }



    @NotNull
   // private Map<FishSizeClassEnum, Map<String, Long>> getFishSizeClassMap(SummaryTableDTO summaryTable, FaCatchSummaryCustomEntity customEntity, long speciesCnt) {
    private Map<FishSizeClassEnum, Object> getFishSizeClassMap(SummaryTableDTO summaryTable, FaCatchSummaryCustomEntity customEntity, long speciesCnt) {
    //    Map<FishSizeClassEnum, Map<String, Long>> fishSizeSummaryMap = summaryTable.getSummaryFishSize();
        Map<FishSizeClassEnum, Object> fishSizeSummaryMap = summaryTable.getSummaryFishSize();

        if(customEntity.getFishClass() !=null && customEntity.getSpecies() ==null){
            if(MapUtils.isEmpty(fishSizeSummaryMap)){
                fishSizeSummaryMap = new HashMap<>();
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), speciesCnt);
            }else {
                Object count = fishSizeSummaryMap.get(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()));
                if (count != null && count instanceof Long) {
                    count = (Long) count + speciesCnt;
                    fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), count);
                } else {
                    fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), (Long) speciesCnt);
                }
            }
            return fishSizeSummaryMap;
        }

        if (MapUtils.isEmpty(fishSizeSummaryMap)) {
            fishSizeSummaryMap = new HashMap<>();
            fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), getStringLongMap(customEntity, speciesCnt));

        } else {
            Map<String, Long> speciesCountMap = (Map<String, Long>) fishSizeSummaryMap.get(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()));
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
