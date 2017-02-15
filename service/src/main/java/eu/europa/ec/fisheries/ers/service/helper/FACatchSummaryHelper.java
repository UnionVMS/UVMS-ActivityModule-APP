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
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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


    public List<GroupCriteria> mapAreaGroupCriteriaToAllSchemeIdTypes(List<GroupCriteria> groupList){

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
    }

    public void enrichGroupCriteriaWithFishSizeAndSpecies(List<GroupCriteria> groupList){

        if(groupList.indexOf(GroupCriteria.SIZE_CLASS) == -1){
            groupList.add(GroupCriteria.SIZE_CLASS);
        }

        if(groupList.indexOf(GroupCriteria.SPECIES) == -1){
            groupList.add(GroupCriteria.SPECIES);
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

        if(groupList.indexOf(GroupCriteria.SPECIES) == -1){
            groupList.add(GroupCriteria.SPECIES);
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
            catchSummaryDTOList.add(faCatchSummaryDTO);
        }


        return catchSummaryDTOList;

    }

    public  List<FACatchSummaryRecord> buildFACatchSummaryRecordList(List<FACatchSummaryRecordDTO> catchSummaryDTOList){
        List<FACatchSummaryRecord> faCatchSummaryRecords = new ArrayList<>();
        for(FACatchSummaryRecordDTO faCatchSummaryDTO: catchSummaryDTOList){
            FACatchSummaryRecord faCatchSummaryRecord = new FACatchSummaryRecord();
            faCatchSummaryRecord.setGroups(populateGroupCriteriaWithValue(faCatchSummaryDTO));
            faCatchSummaryRecord.setSummary(FACatchSummaryMapper.INSTANCE.mapToSummaryTable(faCatchSummaryDTO.getSummaryTable()));
            faCatchSummaryRecords.add(faCatchSummaryRecord);
        }

        log.debug("FACatchSummaryRecordList ---->");
        printJsonstructure(faCatchSummaryRecords);
        return faCatchSummaryRecords;
    }



    public List<GroupCriteriaWithValue> populateGroupCriteriaWithValue(FACatchSummaryRecordDTO faCatchSummaryDTO){
        List<GroupCriteriaWithValue> groups = new ArrayList<>();

        if(faCatchSummaryDTO.getDay() !=0 ){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.DATE_DAY);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getDay());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getMonth())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.DATE_MONTH);
            groupCriteriaWithValue.setValue(faCatchSummaryDTO.getMonth());
            groups.add(groupCriteriaWithValue);
        }


        if(faCatchSummaryDTO.getYear() !=0){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.DATE_YEAR);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getYear());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getEffortZone())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.EFFORT_ZONE);
            groupCriteriaWithValue.setValue(faCatchSummaryDTO.getArea().getEffortZone());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getFaoArea())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.FAO_AREA);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getArea().getFaoArea());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getGfcmGsa())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.GFCM_GSA);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getArea().getGfcmGsa());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getGfcmStatRectangle())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.GFCM_STAT_RECTANGLE);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getArea().getGfcmStatRectangle());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getIcesStatRectangle())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.ICES_STAT_RECTANGLE);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getArea().getIcesStatRectangle());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getFlagState())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.FLAG_STATE);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getFlagState());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getGearType())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.GEAR_TYPE);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getGearType());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getTerritory())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.TERRITORY);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getArea().getTerritory());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getPresentation())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.PRESENTATION);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getPresentation());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getArea().getRfmo())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.RFMO);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getArea().getRfmo());
            groups.add(groupCriteriaWithValue);
        }

        if(StringUtils.isNotEmpty(faCatchSummaryDTO.getVesselTransportGuid())){
            GroupCriteriaWithValue groupCriteriaWithValue = new GroupCriteriaWithValue();
            groupCriteriaWithValue.setKey(GroupCriteria.VESSEL);
            groupCriteriaWithValue.setValue(""+faCatchSummaryDTO.getVesselTransportGuid());
            groups.add(groupCriteriaWithValue);
        }

        return groups;

    }


    public SummaryTableDTO populateSummaryTableWithTotal( List<FACatchSummaryRecordDTO> catchSummaryDTOList){
        SummaryTableDTO summaryTableWithTotals= new SummaryTableDTO();

        for(FACatchSummaryRecordDTO faCatchSummaryDTO:catchSummaryDTOList){
            SummaryTableDTO summaryTable= faCatchSummaryDTO.getSummaryTable();
            Map<FishSizeClassEnum,Map<String,Long>> fishSizeClassEnumMapMap=summaryTable.getSummaryFishSize();
            Map<FishSizeClassEnum, Map<String, Long>> totalFishSizeSpeciesMap=summaryTableWithTotals.getSummaryFishSize();
            if(MapUtils.isEmpty(totalFishSizeSpeciesMap)){
                totalFishSizeSpeciesMap = new HashMap<>();
                summaryTableWithTotals.setSummaryFishSize(totalFishSizeSpeciesMap);
            }

            for(Map.Entry<FishSizeClassEnum, Map<String, Long>> entry :fishSizeClassEnumMapMap.entrySet()){
                FishSizeClassEnum fishSize= entry.getKey(); // key fishSize
                Map<String, Long> speciesMap=entry.getValue();

                Map<String, Long> fishSizeMap=  totalFishSizeSpeciesMap.get(fishSize); // check if already present
                if(MapUtils.isEmpty(fishSizeMap)){
                    fishSizeMap=new HashMap<>();
                }

                for(Map.Entry<String, Long> speciesEntry :speciesMap.entrySet()){
                    String speciesCode=speciesEntry.getKey();
                    long speciesCount=speciesEntry.getValue();

                    // check in the totals map if the species exist.If yes, add
                    if(fishSizeMap.containsKey(speciesCode)){
                        long speciesTotalWeight=fishSizeMap.get(speciesCode);
                        speciesTotalWeight = speciesTotalWeight+speciesCount;
                        fishSizeMap.put(speciesCode,speciesTotalWeight);
                    }else{
                        fishSizeMap.put(speciesCode,speciesCount);
                    }
                }

                totalFishSizeSpeciesMap.put(fishSize,fishSizeMap);
            }

            //-----------------------------------------


            Map<FaCatchTypeEnum, Map<String, Long>> catchTypeEnumMapMap=summaryTable.getSummaryFaCatchType();
            if(MapUtils.isNotEmpty(catchTypeEnumMapMap)) {
                Map<FaCatchTypeEnum, Map<String, Long>> totalCatchTypeMap = summaryTableWithTotals.getSummaryFaCatchType();
                if (MapUtils.isEmpty(totalCatchTypeMap)) {
                    totalCatchTypeMap = new HashMap<>();
                    summaryTableWithTotals.setSummaryFaCatchType(totalCatchTypeMap);
                }

                for (Map.Entry<FaCatchTypeEnum, Map<String, Long>> entry : catchTypeEnumMapMap.entrySet()) {
                    FaCatchTypeEnum fishSize = entry.getKey(); // key fishSize
                    Map<String, Long> speciesMap = entry.getValue();

                    Map<String, Long> fishSizeMap = totalCatchTypeMap.get(fishSize); // check if already present
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

                    totalCatchTypeMap.put(fishSize, fishSizeMap);
                }
            }

        }

        //---------------------------------------------------
        //print totals
       // log.debug("Totals---->");
      //  printJsonstructure(summaryTableWithTotals);
      return summaryTableWithTotals;
    }

    public void printJsonstructure(Object obj){
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
