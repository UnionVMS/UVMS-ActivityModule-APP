package eu.europa.ec.fisheries.ers.service.facatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
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
import org.apache.commons.collections.CollectionUtils;
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

    public  FaCatchSummaryCustomEntity mapObjectArrayToFaCatchSummaryCustomEntity(Object[] catchSummaryArr, List<GroupCriteria> groupList) throws ServiceException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, IllegalAccessException, InstantiationException {

       if (ArrayUtils.isEmpty(catchSummaryArr))
            return new FaCatchSummaryCustomEntity();
        int objectArrSize = (catchSummaryArr.length - 1);
        if (objectArrSize != groupList.size())  // do not include count field from object array
            throw new ServiceException("selected number of SQL fields do not match with grouping criterias asked by user ");


        Class cls = Class.forName("eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity");
        Object faCatchSummaryCustomEntityObj = cls.newInstance();
        Class parameterType = String.class;

        Map<GroupCriteria, GroupCriteriaMapper> groupMappings = FilterMap.getGroupByMapping();
        for (int i = 0; i < objectArrSize; i++) {
            GroupCriteria criteria = groupList.get(i);
            Object value=catchSummaryArr[i];

            if(GroupCriteria.DATE_DAY.equals(criteria) || GroupCriteria.DATE_MONTH.equals(criteria) ||
                    GroupCriteria.DATE_YEAR.equals(criteria) ){
                value= extractValueFromDate((Date) value,criteria);
            }

            GroupCriteriaMapper mapper = groupMappings.get(criteria);
            Method method = cls.getDeclaredMethod(mapper.getMethodName(), parameterType);
            method.invoke(faCatchSummaryCustomEntityObj,value );
        }

        Method method = cls.getDeclaredMethod("setCount", Long.TYPE);
        method.invoke(faCatchSummaryCustomEntityObj, catchSummaryArr[objectArrSize]);

        return (FaCatchSummaryCustomEntity) faCatchSummaryCustomEntityObj;
    }

    private Object extractValueFromDate(Date date,GroupCriteria criteria){

        if (GroupCriteria.DATE_DAY.equals(criteria)){
            SimpleDateFormat day = new SimpleDateFormat("dd");
            return day.format(date);
        }else  if (GroupCriteria.DATE_MONTH.equals(criteria)){
            SimpleDateFormat day = new SimpleDateFormat("MMM");
            return day.format(date);
        }else  if (GroupCriteria.DATE_YEAR.equals(criteria)){
            SimpleDateFormat day = new SimpleDateFormat("YYYY");
            return day.format(date);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
       return sdf.format(date);
    }



    /**
     *  Add FishClass to the grouping factor and remove FACatch if already present.
     *  Method adds default grouping criterias which are required to build summary table.
     *
     * @param groupList
     */
    public void enrichGroupCriteriaWithFishSizeAndSpecies(List<GroupCriteria> groupList){

        if(groupList.indexOf(GroupCriteria.SIZE_CLASS) == -1){
            groupList.add(GroupCriteria.SIZE_CLASS);
        }

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
            if(CollectionUtils.isEmpty(faCatchSummaryDTO.getGroups())){
                log.error("No data for the grouping factors found :"+faCatchSummaryDTO);
                continue;
            }
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
        if(MapUtils.isEmpty(fishSizeClassEnumMapMap)){
           return;
        }
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

}
