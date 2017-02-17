package eu.europa.ec.fisheries.ers.service.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.GroupCriteriaMapper;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryRecord;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishSizeClassEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sanera on 31/01/2017.
 */
@Slf4j
public class FACatchSummaryHelper {

    private FACatchSummaryHelper() {
    }

    public static FACatchSummaryHelper createFACatchSummaryHelper() {
        return new FACatchSummaryHelper();
    }

    public  FaCatchSummaryCustomEntity mapObjectArrayToFaCatchSummaryCustomEntity(Object[] catchSummaryArr, FishingActivityQuery query) throws ServiceException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        // FaCatchSummaryCustomEntity faCatchSummaryCustomEntity;
        Map<GroupCriteria, GroupCriteriaMapper> groupMappings = FilterMap.getGroupByMapping();
        List<GroupCriteria> groupList = query.getGroupByFields();
        if (ArrayUtils.isEmpty(catchSummaryArr))
            return new FaCatchSummaryCustomEntity();
        int objectArrSize = (catchSummaryArr.length - 1);
        if (objectArrSize != groupList.size())  // do not include count field from object array
            throw new ServiceException("selected number of SQL fields do not match with grouping criterias asked by user ");


        Class cls = Class.forName("eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity");
        Object faCatchSummaryCustomEntityObj = cls.newInstance();
        Class parameterType = String.class;

        for (int i = 0; i < objectArrSize; i++) {
            GroupCriteria criteria = groupList.get(i);
            Object value=catchSummaryArr[i];

         /*   if (GroupCriteria.DATE.equals(criteria)) {
                parameterType = Date.class;
                value= DateUtils.truncate(value,Calendar.DATE);
            }*/
            if (GroupCriteria.DATE_DAY.equals(criteria)) {
                parameterType = Integer.class;
                value= extractValueFromDate((Date) value,Calendar.DAY_OF_MONTH);
            }

            if (GroupCriteria.DATE_MONTH.equals(criteria)) {
                parameterType = String.class;
                value= extractValueFromDate((Date) value,Calendar.MONTH);
            }

            if (GroupCriteria.DATE_YEAR.equals(criteria)) {
                parameterType = Integer.class;
                value= extractValueFromDate((Date) value,Calendar.YEAR);
            }


            GroupCriteriaMapper mapper = groupMappings.get(criteria);
            Method method = cls.getDeclaredMethod(mapper.getMethodName(), parameterType);
            method.invoke(faCatchSummaryCustomEntityObj,value );
            parameterType = String.class;
        }

        Method method = cls.getDeclaredMethod("setCount", Long.TYPE);
        method.invoke(faCatchSummaryCustomEntityObj, catchSummaryArr[objectArrSize]);

        return (FaCatchSummaryCustomEntity) faCatchSummaryCustomEntityObj;


    }

    private Object extractValueFromDate(Date date,int field){

        if(Calendar.DAY_OF_MONTH == field){
            SimpleDateFormat day = new SimpleDateFormat("dd");
            return Integer.parseInt(day.format(date));
        }else if(Calendar.MONTH == field){
            SimpleDateFormat day = new SimpleDateFormat("MMM");
            return day.format(date);
        }else if(Calendar.YEAR == field){
            SimpleDateFormat day = new SimpleDateFormat("YYYY");
            return Integer.parseInt(day.format(date));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
       return sdf.format(date);
    }


/*    public List<GroupCriteria> mapAreaGroupCriteriaToAllSchemeIdTypes(List<GroupCriteria> groupList){

        if(!groupList.contains(GroupCriteria.AREA))
            return groupList;

        groupList.remove(GroupCriteria.AREA);

        groupList.add(GroupCriteria.TERRITORY);
        groupList.add(GroupCriteria.FAO_AREA);
        groupList.add(GroupCriteria.ICES_STAT_RECTANGLE);
        groupList.add(GroupCriteria.EFFORT_ZONE);
        groupList.add(GroupCriteria.RFMO);
        groupList.add(GroupCriteria.GFCM_GSA);
        groupList.add(GroupCriteria.GFCM_STAT_RECTANGLE);



        return groupList;
    }*/

    /**
     *  Method adds default grouping criterias which are required to build summary table.
     *
     * @param groupList
     */
    public void enrichGroupCriteriaWithFishSizeAndSpecies(List<GroupCriteria> groupList){

        if(groupList.indexOf(GroupCriteria.SIZE_CLASS) == -1){
            groupList.add(GroupCriteria.SIZE_CLASS);
        }

   //     if(groupList.indexOf(GroupCriteria.SPECIES) == -1){
     //       groupList.add(GroupCriteria.SPECIES);
       // }

        if(groupList.indexOf(GroupCriteria.CATCH_TYPE) != -1){
            groupList.remove(GroupCriteria.CATCH_TYPE);
        }

    }

    public void enrichGroupCriteriaWithFACatchType(List<GroupCriteria> groupList){

        if(groupList.indexOf(GroupCriteria.SIZE_CLASS) != -1){
            groupList.remove(GroupCriteria.SIZE_CLASS);
        }

        if(groupList.indexOf(GroupCriteria.CATCH_TYPE) == -1){
            groupList.add(GroupCriteria.CATCH_TYPE);
        }

    //    if(groupList.indexOf(GroupCriteria.SPECIES) == -1){
      //      groupList.add(GroupCriteria.SPECIES);
        //}

    }

    public Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupByFACatchCustomEntities(List<FaCatchSummaryCustomEntity> customEntities){
        Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap= new HashMap<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>>();
        for(FaCatchSummaryCustomEntity summaryObj: customEntities){
            List<FaCatchSummaryCustomEntity> tempList=groupedMap.get(summaryObj);
            if( Collections.isEmpty(tempList)){
                tempList = new ArrayList<>();
                tempList.add(summaryObj);
                groupedMap.put(summaryObj,tempList);
            }else{
                tempList.add(summaryObj);
            }

        }

        return groupedMap;
    }

    public  List<FACatchSummaryRecordDTO> buildFaCatchSummaryTable(Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap){
        List<FACatchSummaryRecordDTO> catchSummaryDTOList = new ArrayList<>();

        for (Map.Entry<FaCatchSummaryCustomEntity, List<FaCatchSummaryCustomEntity>> entry : groupedMap.entrySet()) {
            FaCatchSummaryCustomEntity customEntity= entry.getKey();
            FACatchSummaryRecordDTO faCatchSummaryDTO= FACatchSummaryMapper.INSTANCE.mapToFACatchSummaryDTO(customEntity,entry.getValue());
            catchSummaryDTOList.add(faCatchSummaryDTO);
        }


        return catchSummaryDTOList;

    }

    public  List<FACatchSummaryRecord> buildFACatchSummaryRecordList(List<FACatchSummaryRecordDTO> catchSummaryDTOList){
        List<FACatchSummaryRecord> faCatchSummaryRecords = new ArrayList<>();
        for(FACatchSummaryRecordDTO faCatchSummaryDTO: catchSummaryDTOList){
            FACatchSummaryRecord faCatchSummaryRecord = new FACatchSummaryRecord();
          //  faCatchSummaryRecord.setGroups(populateGroupCriteriaWithValue(faCatchSummaryDTO));
            faCatchSummaryRecord.setGroups(faCatchSummaryDTO.getGroups());
            faCatchSummaryRecord.setSummary(FACatchSummaryMapper.INSTANCE.mapToSummaryTable(faCatchSummaryDTO.getSummaryTable()));
            faCatchSummaryRecords.add(faCatchSummaryRecord);
        }

        log.debug("FACatchSummaryRecordList ---->");
        printJsonstructure(faCatchSummaryRecords);
        return faCatchSummaryRecords;
    }






    public SummaryTableDTO populateSummaryTableWithTotal( List<FACatchSummaryRecordDTO> catchSummaryDTOList){
        SummaryTableDTO summaryTableWithTotals= new SummaryTableDTO();

        for(FACatchSummaryRecordDTO faCatchSummaryDTO:catchSummaryDTOList){
            SummaryTableDTO summaryTable= faCatchSummaryDTO.getSummaryTable();

            extractTotalFishSizeMap(summaryTableWithTotals, summaryTable);
            extractTotalFaCatchMap(summaryTableWithTotals, summaryTable);

        }

        //---------------------------------------------------
        //print totals
       // log.debug("Totals---->");
      //  printJsonstructure(summaryTableWithTotals);
      return summaryTableWithTotals;
    }

    private void extractTotalFaCatchMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable) {
    //    Map<FaCatchTypeEnum, Map<String, Long>> catchTypeEnumMapMap=summaryTable.getSummaryFaCatchType();
        Map<FaCatchTypeEnum, Object> catchTypeEnumMapMap=summaryTable.getSummaryFaCatchType();
        if(MapUtils.isNotEmpty(catchTypeEnumMapMap)) {
         //   Map<FaCatchTypeEnum, Map<String, Long>> totalCatchTypeMap = summaryTableWithTotals.getSummaryFaCatchType();
            Map<FaCatchTypeEnum,Object> totalCatchTypeMap = summaryTableWithTotals.getSummaryFaCatchType();
            if (MapUtils.isEmpty(totalCatchTypeMap)) {
                totalCatchTypeMap = new HashMap<>();
                summaryTableWithTotals.setSummaryFaCatchType(totalCatchTypeMap);
            }

       //     for (Map.Entry<FaCatchTypeEnum, Map<String, Long>> entry : catchTypeEnumMapMap.entrySet()) {
            for (Map.Entry<FaCatchTypeEnum,Object> entry : catchTypeEnumMapMap.entrySet()) {
                FaCatchTypeEnum catchType = entry.getKey(); // key fishSize

              //  Map<String, Long> speciesMap = entry.getValue();
               Object value = entry.getValue();

                if(value instanceof Map){
                    Map<String, Long> fishSizeMap = (Map<String, Long>) totalCatchTypeMap.get(catchType); // check if already present
                    fishSizeMap = extractSpeciesCountMAp((Map<String, Long>) value, fishSizeMap);
                    totalCatchTypeMap.put(catchType, fishSizeMap);

                }else if(value instanceof Long){
                    Long totalValue = (Long) totalCatchTypeMap.get(catchType); // check if already present
                    if(totalValue ==null){
                        totalValue =(Long) value;
                    }else {
                        totalValue = totalValue + (Long) value;
                    }
                    totalCatchTypeMap.put(catchType, totalValue);
                    //  fishSizeMap = extractSpeciesCountMAp((Map<String, Long>) value, fishSizeMap);
                }



            }
        }
    }

    private void extractTotalFishSizeMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable) {
      //  Map<FishSizeClassEnum, Map<String, Long>> totalFishSizeSpeciesMap=summaryTableWithTotals.getSummaryFishSize();
        Map<FishSizeClassEnum, Object> totalFishSizeSpeciesMap=summaryTableWithTotals.getSummaryFishSize();
        if(MapUtils.isEmpty(totalFishSizeSpeciesMap)){
            totalFishSizeSpeciesMap = new HashMap<>();
            summaryTableWithTotals.setSummaryFishSize(totalFishSizeSpeciesMap);
        }

      //  Map<FishSizeClassEnum,Map<String,Long>> fishSizeClassEnumMapMap=summaryTable.getSummaryFishSize();
        Map<FishSizeClassEnum,Object> fishSizeClassEnumMapMap=summaryTable.getSummaryFishSize();
     //   for(Map.Entry<FishSizeClassEnum, Map<String, Long>> entry :fishSizeClassEnumMapMap.entrySet()){
        for(Map.Entry<FishSizeClassEnum, Object> entry :fishSizeClassEnumMapMap.entrySet()){
            FishSizeClassEnum fishSize= entry.getKey(); // key fishSize


           // Map<String, Long> speciesMap=entry.getValue();
            Object value = entry.getValue();
            if(value instanceof Map){
                Map<String, Long> fishSizeMap = (Map<String, Long>) totalFishSizeSpeciesMap.get(fishSize); // check if already present
                fishSizeMap = extractSpeciesCountMAp((Map<String, Long>) value, fishSizeMap);
                totalFishSizeSpeciesMap.put(fishSize, fishSizeMap);

            }else if(value instanceof Long){
                Long totalValue = (Long) totalFishSizeSpeciesMap.get(fishSize); // check if already present
                if(totalValue ==null){
                    totalValue =(Long) value;
                }else {
                    totalValue = totalValue + (Long) value;
                }
                totalFishSizeSpeciesMap.put(fishSize, totalValue);
                //  fishSizeMap = extractSpeciesCountMAp((Map<String, Long>) value, fishSizeMap);
            }

       //     Map<String, Long> fishSizeMap=  totalFishSizeSpeciesMap.get(fishSize); // check if already present
         //   fishSizeMap = extractSpeciesCountMAp(speciesMap, fishSizeMap);

            //totalFishSizeSpeciesMap.put(fishSize,fishSizeMap);
        }
    }

    @NotNull
    private Map<String, Long> extractSpeciesCountMAp(Map<String, Long> speciesMap, Map<String, Long> fishSizeMap) {
        if (MapUtils.isEmpty(fishSizeMap)) {
            fishSizeMap = new HashMap<>();
        }

        for (Map.Entry<String, Long> speciesEntry : speciesMap.entrySet()) {
            String speciesCode = speciesEntry.getKey();
            long speciesCount = speciesEntry.getValue();

            // check in the totals map if the species exist.If yes, add
            if (fishSizeMap.containsKey(speciesCode)) {
                long speciesTotalWeight = fishSizeMap.get(speciesCode);
                speciesTotalWeight = speciesTotalWeight + speciesCount;
                fishSizeMap.put(speciesCode, speciesTotalWeight);
            } else {
                fishSizeMap.put(speciesCode, speciesCount);
            }
        }
        return fishSizeMap;
    }

    public String printJsonstructure(Object obj){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String s = null;
        try {
            s = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.debug("json structure:-------->");
        log.debug(""+s);
        return s;
    }


    @NotNull
    private Map<String, Long> getStringLongMap(FaCatchSummaryCustomEntity customEntity, long speciesCnt) {
        Map<String,Long> speciesCountMap = new HashMap<>();
        if(customEntity.getSpecies() !=null) {
            speciesCountMap.put(customEntity.getSpecies().toUpperCase(), speciesCnt);
        }
        return speciesCountMap;
    }


   /* public List<GroupCriteriaWithValue> populateGroupCriteriaWithValue(FACatchSummaryRecordDTO faCatchSummaryDTO){
        List<GroupCriteriaWithValue> groups = new ArrayList<>();

        if(faCatchSummaryDTO.getDay() !=0 ){
            groups.add( new GroupCriteriaWithValue(GroupCriteria.DATE_DAY,""+faCatchSummaryDTO.getDay()));
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getMonth())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.DATE_MONTH,faCatchSummaryDTO.getMonth()));
        }


        if(faCatchSummaryDTO.getYear() !=0){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.DATE_YEAR,""+faCatchSummaryDTO.getYear()));
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getEffortZone())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.EFFORT_ZONE,faCatchSummaryDTO.getArea().getEffortZone()));
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getFaoArea())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.FAO_AREA,faCatchSummaryDTO.getArea().getFaoArea()));
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getGfcmGsa())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GFCM_GSA,faCatchSummaryDTO.getArea().getGfcmGsa()));
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getGfcmStatRectangle())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GFCM_STAT_RECTANGLE,faCatchSummaryDTO.getArea().getGfcmStatRectangle()));
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getIcesStatRectangle())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.ICES_STAT_RECTANGLE,faCatchSummaryDTO.getArea().getIcesStatRectangle()));
        }
        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getTerritory())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.TERRITORY,faCatchSummaryDTO.getArea().getTerritory()));
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getRfmo())){

            groups.add(new GroupCriteriaWithValue(GroupCriteria.RFMO,faCatchSummaryDTO.getArea().getRfmo()));
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getFlagState())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.FLAG_STATE,faCatchSummaryDTO.getFlagState()));
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getGearType())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.GEAR_TYPE,faCatchSummaryDTO.getGearType()));
        }



        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getPresentation())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.PRESENTATION,faCatchSummaryDTO.getPresentation()));
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getVesselTransportGuid())){
            groups.add(new GroupCriteriaWithValue(GroupCriteria.VESSEL,faCatchSummaryDTO.getVesselTransportGuid()));
        }

        return groups;

    }*/


}
