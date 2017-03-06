/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.facatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomChildEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.GroupCriteriaMapper;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryRecord;
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
 * This class acts as helper class for FAcatchSummary Report functionality.
 * Created by sanera on 31/01/2017.
 */
@Slf4j
public abstract class FACatchSummaryHelper {
    protected String faCatchSummaryCustomClassName;


    public  FaCatchSummaryCustomEntity mapObjectArrayToFaCatchSummaryCustomEntity(Object[] catchSummaryArr, List<GroupCriteria> groupList,boolean isLanding) throws ServiceException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, IllegalAccessException, InstantiationException {

       if (ArrayUtils.isEmpty(catchSummaryArr))
            return new FaCatchSummaryCustomEntity();
        int objectArrSize = (catchSummaryArr.length - 1);
        if (objectArrSize != groupList.size())  // do not include count field from object array
            throw new ServiceException("selected number of SQL fields do not match with grouping criterias asked by user ");

        Class cls= Class.forName(faCatchSummaryCustomClassName);
    /*    if(isLanding){
            cls = Class.forName("eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomChildEntity");
        }else{
            cls = Class.forName("eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity");
        }*/


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

        Method method = cls.getDeclaredMethod("setCount", Double.TYPE);
        method.invoke(faCatchSummaryCustomEntityObj,  catchSummaryArr[objectArrSize]);

        if(isLanding)
           return (FaCatchSummaryCustomChildEntity) faCatchSummaryCustomEntityObj;
        else
          return (FaCatchSummaryCustomEntity) faCatchSummaryCustomEntityObj;
    }

    // This method parses the date to extract either day, month or year
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

    /**
     *  Add catchType in the list of groups
     * @param groupList
     */
    public void enrichGroupCriteriaWithFACatchType(List<GroupCriteria> groupList){

        if(groupList.indexOf(GroupCriteria.SIZE_CLASS) != -1){
            groupList.remove(GroupCriteria.SIZE_CLASS);
        }

        if(groupList.indexOf(GroupCriteria.CATCH_TYPE) == -1){
            groupList.add(GroupCriteria.CATCH_TYPE);
        }

    }


    /**
     *  This method creates groups for various aggregation criterias and combine records for the group
     * @param customEntities
     * @return Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> key => group value => list of records with that group
     */
    public Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupByFACatchCustomEntities(List<FaCatchSummaryCustomEntity> customEntities){
        Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap= new HashMap<>();
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

    public abstract  List<FACatchSummaryRecordDTO> buildFACatchSummaryRecordDTOList(Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap);

    /**
     *  Post process data received from database to create FACatchSummaryRecordDTO. Every record will have summary calculated for it.
     *
     * @param groupedMap
     * @return List<FACatchSummaryRecordDTO> Processed records having summary data
     */
   /* public   List<FACatchSummaryRecordDTO> buildFACatchSummaryRecordDTOList(Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap){
        List<FACatchSummaryRecordDTO> faCatchSummaryRecordDTOs = new ArrayList<>();

        for (Map.Entry<FaCatchSummaryCustomEntity, List<FaCatchSummaryCustomEntity>> entry : groupedMap.entrySet()) {
            FACatchSummaryRecordDTO faCatchSummaryDTO= FACatchSummaryMapper.INSTANCE.mapToFACatchSummaryRecordDTO(entry.getKey(),entry.getValue());
            if(CollectionUtils.isEmpty(faCatchSummaryDTO.getGroups())){ // Do not add record to the list if no data for grouping factors found
                log.error("No data for the grouping factors found :"+faCatchSummaryDTO);
                continue;
            }
            faCatchSummaryRecordDTOs.add(faCatchSummaryDTO);
        }
        return faCatchSummaryRecordDTOs;

    }*/
    /**
     *  Post process data received from database to create FACatchSummaryRecordDTO. Every record will have summary calculated for it.
     *
  //   * @param groupedMap
     * @return List<FACatchSummaryRecordDTO> Processed records having summary data
     */
  /*  public  List<FACatchSummaryRecordDTO> buildFACatchSummaryRecordDTOListWithPresentation(Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap){
        List<FACatchSummaryRecordDTO> faCatchSummaryRecordDTOs = new ArrayList<>();

        for (Map.Entry<FaCatchSummaryCustomEntity, List<FaCatchSummaryCustomEntity>> entry : groupedMap.entrySet()) {
            FaCatchSummaryCustomEntity customEntity= entry.getKey();
            customEntity.setPresentation(null); // We dont want Presentation to be part of group criteria. We want to display this information in summmary table so, remove it
            FACatchSummaryRecordDTO faCatchSummaryDTO= FACatchSummaryMapper.INSTANCE.mapToFACatchSummaryRecordDTOWithPresentation(entry.getKey(),entry.getValue());
            if(CollectionUtils.isEmpty(faCatchSummaryDTO.getGroups())){ // Do not add record to the list if no data for grouping factors found
                log.error("No data for the grouping factors found :"+faCatchSummaryDTO);
                continue;
            }



            faCatchSummaryRecordDTOs.add(faCatchSummaryDTO);
        }
        return faCatchSummaryRecordDTOs;

    }*/

    public SummaryTableDTO populateSummaryTableWithTotal(List<FACatchSummaryRecordDTO> catchSummaryDTOList){
        SummaryTableDTO summaryTableWithTotals= new SummaryTableDTO();

        for(FACatchSummaryRecordDTO faCatchSummaryDTO:catchSummaryDTOList){
            SummaryTableDTO summaryTable= faCatchSummaryDTO.getSummaryTable();

            populateTotalFishSizeMap(summaryTableWithTotals, summaryTable);
            populateTotalFaCatchMap(summaryTableWithTotals, summaryTable);

        }

        return summaryTableWithTotals;
    }

   // public abstract SummaryTableDTO populateSummaryTableWithTotal( List<FACatchSummaryRecordDTO> catchSummaryDTOList);
    /**
     * Calculate Total for each column after processing all the records
  //   * @param catchSummaryDTOList
     * @return
     */

   /* public SummaryTableDTO populateSummaryTableWithTotal( List<FACatchSummaryRecordDTO> catchSummaryDTOList){
        SummaryTableDTO summaryTableWithTotals= new SummaryTableDTO();

        for(FACatchSummaryRecordDTO faCatchSummaryDTO:catchSummaryDTOList){
            SummaryTableDTO summaryTable= faCatchSummaryDTO.getSummaryTable();

            populateTotalFishSizeMap(summaryTableWithTotals, summaryTable);
            populateTotalFaCatchMap(summaryTableWithTotals, summaryTable);

        }

        return summaryTableWithTotals;
    }


    public SummaryTableDTO populateSummaryTableWithTotalWithPresentation( List<FACatchSummaryRecordDTO> catchSummaryDTOList){
        SummaryTableDTO summaryTableWithTotals= new SummaryTableDTO();

        for(FACatchSummaryRecordDTO faCatchSummaryDTO:catchSummaryDTOList){
            SummaryTableDTO summaryTable= faCatchSummaryDTO.getSummaryTable();

            populateTotalFishSizeMapWithPresentation(summaryTableWithTotals, summaryTable);
            populateTotalFaCatchMapWithPresentation(summaryTableWithTotals, summaryTable);

        }

        return summaryTableWithTotals;
    }*/

    public abstract void populateTotalFishSizeMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable);
    /**
     * This method processes data to calculate weights for different FishSize classes
     * @param summaryTableWithTotals  Add the calculation to this final class
     * @param summaryTable process this object to calculate totals
     */
 /*   protected void populateTotalFishSizeMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable) {

        Map<FishSizeClassEnum,Object> fishSizeClassEnumMap=summaryTable.getSummaryFishSize();
        if(MapUtils.isEmpty(fishSizeClassEnumMap)){
            return;
        }

        Map<FishSizeClassEnum, Object> totalFishSizeSpeciesMap=summaryTableWithTotals.getSummaryFishSize();
        if(MapUtils.isEmpty(totalFishSizeSpeciesMap)){
            totalFishSizeSpeciesMap = new HashMap<>();
            summaryTableWithTotals.setSummaryFishSize(totalFishSizeSpeciesMap);
        }

        // Go through all the Fish classes  and calculate total for each fishclass
        for(Map.Entry<FishSizeClassEnum, Object> entry :fishSizeClassEnumMap.entrySet()){
            FishSizeClassEnum fishSize= entry.getKey(); // key fishSize

            Object value = entry.getValue();
            // Value will be Double if species are not present as grouping criteria Else it will be map of Species and its count
            if(value instanceof Map){
                Map<String, Double> fishSizeMap = (Map<String, Double>) totalFishSizeSpeciesMap.get(fishSize); // check if already present
                fishSizeMap = extractSpeciesCountMAp((Map<String, Double>) value, fishSizeMap);
                totalFishSizeSpeciesMap.put(fishSize, fishSizeMap);

            }else if(value instanceof Double){
                 totalFishSizeSpeciesMap.put(fishSize, calculateTotalValue((Double) value, (Double) totalFishSizeSpeciesMap.get(fishSize)));
            }
        }
    }
*/

    /**
     * This method processes data to calculate weights for different FishSize classes
     * @param summaryTableWithTotals  Add the calculation to this final class
     * @param summaryTable process this object to calculate totals
     */
 /*   protected void populateTotalFishSizeMapWithPresentation(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable) {

        Map<FishSizeClassEnum,Object> fishSizeClassEnumMap=summaryTable.getSummaryFishSize();
        if(MapUtils.isEmpty(fishSizeClassEnumMap)){
            return;
        }

        Map<FishSizeClassEnum, Object> totalFishSizeSpeciesMap=summaryTableWithTotals.getSummaryFishSize();
        if(MapUtils.isEmpty(totalFishSizeSpeciesMap)){
            totalFishSizeSpeciesMap = new HashMap<>();
            summaryTableWithTotals.setSummaryFishSize(totalFishSizeSpeciesMap);
        }

        // Go through all the Fish classes  and calculate total for each fishclass
        for(Map.Entry<FishSizeClassEnum, Object> entry :fishSizeClassEnumMap.entrySet()){
            FishSizeClassEnum fishSize= entry.getKey(); // key fishSize

            Object value = entry.getValue();
            // Value will be Double if species are not present as grouping criteria Else it will be map of Species and its count
            if(value instanceof Map){
                Map<String, Map<String,Double>> fishSizeMap = (Map<String, Map<String,Double>>) totalFishSizeSpeciesMap.get(fishSize); // check if already present
                fishSizeMap = populateSpeciesPresentationMapWithTotal(( Map<String, Map<String,Double>>) value, fishSizeMap);
                totalFishSizeSpeciesMap.put(fishSize, fishSizeMap);
            }
        }
    }*/


    public abstract void populateTotalFaCatchMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable);

    /**
     * This method processes data to calculate weights for different Catch types
     * @param summaryTableWithTotals  Add the calculation to this final class
     * @param summaryTable process this object to calculate totals
     */
  /*  protected void populateTotalFaCatchMapWithPresentation(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable) {

        Map<FaCatchTypeEnum, Object> catchTypeEnumMapMap=summaryTable.getSummaryFaCatchType();
        if(MapUtils.isNotEmpty(catchTypeEnumMapMap)) {

            Map<FaCatchTypeEnum,Object> totalCatchTypeMap = summaryTableWithTotals.getSummaryFaCatchType();
            if (MapUtils.isEmpty(totalCatchTypeMap)) {
                totalCatchTypeMap = new HashMap<>();
                summaryTableWithTotals.setSummaryFaCatchType(totalCatchTypeMap);
            }

            // Go through all the catch types and calculate total for each type
            for (Map.Entry<FaCatchTypeEnum,Object> entry : catchTypeEnumMapMap.entrySet()) {
                FaCatchTypeEnum catchType = entry.getKey(); // key fishSize
                Object value = entry.getValue();
                if(value instanceof Map){
                    Map<String, Map<String,Double>> fishSizeMap = (Map<String, Map<String,Double>>) totalCatchTypeMap.get(catchType); // check if already present
                  //  fishSizeMap = extractSpeciesCountMAp((Map<String, Double>) value, fishSizeMap);
                    fishSizeMap = populateSpeciesPresentationMapWithTotal(( Map<String, Map<String,Double>>) value, fishSizeMap);
                    totalCatchTypeMap.put(catchType, fishSizeMap);

                }
            }
        }
    }

*/

    /**
     * This method processes data to calculate weights for different Catch types
  //   * @param summaryTableWithTotals  Add the calculation to this final class
    // * @param summaryTable process this object to calculate totals

    protected void populateTotalFaCatchMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable) {

        Map<FaCatchTypeEnum, Object> catchTypeEnumMapMap=summaryTable.getSummaryFaCatchType();
        if(MapUtils.isNotEmpty(catchTypeEnumMapMap)) {

            Map<FaCatchTypeEnum,Object> totalCatchTypeMap = summaryTableWithTotals.getSummaryFaCatchType();
            if (MapUtils.isEmpty(totalCatchTypeMap)) {
                totalCatchTypeMap = new HashMap<>();
                summaryTableWithTotals.setSummaryFaCatchType(totalCatchTypeMap);
            }

            // Go through all the catch types and calculate total for each type
            for (Map.Entry<FaCatchTypeEnum,Object> entry : catchTypeEnumMapMap.entrySet()) {
                FaCatchTypeEnum catchType = entry.getKey(); // key fishSize
                Object value = entry.getValue();
                if(value instanceof Map){
                    Map<String, Double> fishSizeMap = (Map<String, Double>) totalCatchTypeMap.get(catchType); // check if already present
                    fishSizeMap = extractSpeciesCountMAp((Map<String, Double>) value, fishSizeMap);
                    totalCatchTypeMap.put(catchType, fishSizeMap);

                }else if(value instanceof Double){
                      totalCatchTypeMap.put(catchType, calculateTotalValue((Double) value, (Double) totalCatchTypeMap.get(catchType)));
                }
            }
        }
    }*/

    protected Double calculateTotalValue(Double value, Double totalValue) {
        if(totalValue ==null){
            totalValue = value;
        }else {
            totalValue = totalValue + value;
        }
        return totalValue;
    }


    public  List<FACatchSummaryRecord> buildFACatchSummaryRecordList(List<FACatchSummaryRecordDTO> catchSummaryDTOList){
        List<FACatchSummaryRecord> faCatchSummaryRecords = new ArrayList<>();
        for(FACatchSummaryRecordDTO faCatchSummaryDTO: catchSummaryDTOList){
            FACatchSummaryRecord faCatchSummaryRecord = new FACatchSummaryRecord();
            faCatchSummaryRecord.setGroups(faCatchSummaryDTO.getGroups());
            faCatchSummaryRecord.setSummary(FACatchSummaryMapper.INSTANCE.mapToSummaryTable(faCatchSummaryDTO.getSummaryTable()));
            faCatchSummaryRecords.add(faCatchSummaryRecord);
        }
        log.debug("List of FACatchSummaryRecord objects created" );
        return faCatchSummaryRecords;
    }




    @NotNull
    protected Map<String, Double> extractSpeciesCountMAp(Map<String, Double> speciesMap, Map<String, Double> fishSizeMap) {
        if (MapUtils.isEmpty(fishSizeMap)) {
            fishSizeMap = new HashMap<>();
        }

        for (Map.Entry<String, Double> speciesEntry : speciesMap.entrySet()) {
            String speciesCode = speciesEntry.getKey();
            Double speciesCount = speciesEntry.getValue();

            // check in the totals map if the species exist.If yes, add
            if (fishSizeMap.containsKey(speciesCode)) {
                Double speciesTotalWeight = fishSizeMap.get(speciesCode);
                speciesTotalWeight = speciesTotalWeight + speciesCount;
                fishSizeMap.put(speciesCode, speciesTotalWeight);
            } else {
                fishSizeMap.put(speciesCode, speciesCount);
            }
        }
        return fishSizeMap;
    }

    public static String printJsonstructure(Object obj){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String s = null;
        try {
            s = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Exception while parsing JSON",e) ;
        }
        log.debug("json structure:-------->");
        log.debug(""+s);
        return s;
    }


}
