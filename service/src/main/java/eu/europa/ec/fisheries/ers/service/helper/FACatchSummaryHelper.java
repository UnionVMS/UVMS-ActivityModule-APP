package eu.europa.ec.fisheries.ers.service.helper;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.GroupCriteriaMapper;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FaCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FishSizeClassEnum;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTable;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

            if (GroupCriteria.DATE.equals(criteria))
                parameterType = Date.class;

            GroupCriteriaMapper mapper = groupMappings.get(criteria);
            Method method = cls.getDeclaredMethod(mapper.getMethodName(), parameterType);
            method.invoke(faCatchSummaryCustomEntityObj, catchSummaryArr[i]);
            parameterType = String.class;
        }

        Method method = cls.getDeclaredMethod("setCount", Long.TYPE);
        method.invoke(faCatchSummaryCustomEntityObj, catchSummaryArr[objectArrSize]);

        return (FaCatchSummaryCustomEntity) faCatchSummaryCustomEntityObj;


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

        for (Map.Entry<FaCatchSummaryCustomEntity, List<FaCatchSummaryCustomEntity>> entry : groupedMap.entrySet()) {

            System.out.println("Key:"+entry.getKey()+":::"+entry.getValue().size());
            System.out.println(entry.getValue());
            System.out.println("**********************************************************");
        }
        return groupedMap;
    }


    public void buildFaCatchSummaryTable(Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap){
        List<FACatchSummaryDTO> catchSummaryDTOList = new ArrayList<>();

        for (Map.Entry<FaCatchSummaryCustomEntity, List<FaCatchSummaryCustomEntity>> entry : groupedMap.entrySet()) {
            FaCatchSummaryCustomEntity customEntity= entry.getKey();
            FACatchSummaryDTO faCatchSummaryDTO= FACatchSummaryMapper.INSTANCE.mapToFACatchSummaryDTO(customEntity);
            List<FaCatchSummaryCustomEntity> catchSummaryEntityList=entry.getValue();
            for(FaCatchSummaryCustomEntity entity :catchSummaryEntityList){
                populateSummaryTable(faCatchSummaryDTO.getSummaryTable(),entity);
            }

            catchSummaryDTOList.add(faCatchSummaryDTO);
        }

        System.out.println("Print CatchSummaryDTO");
        for(FACatchSummaryDTO dto:catchSummaryDTOList){
            System.out.println(dto);
            SummaryTable summaryTable= dto.getSummaryTable();
            Map<FishSizeClassEnum,Map<String,Long>> fishSizeMap=summaryTable.getSummaryFishSize();
            for(Map.Entry<FishSizeClassEnum, Map<String, Long>> entry : fishSizeMap.entrySet()){
                System.out.println("----------"+entry.getKey()+"-----------");
                Map<String, Long> speciesMap= entry.getValue();
                for(Map.Entry<String, Long> entrySpecies : speciesMap.entrySet()){
                    System.out.println("*"+entrySpecies.getKey()+"---->"+entrySpecies.getValue());
                }
                System.out.println("------ End ------------");
            }

        }
    }

    private void populateSummaryTable(SummaryTable summaryTable,FaCatchSummaryCustomEntity customEntity){
        long speciesCnt = customEntity.getCount();

        if(customEntity.getFishClass()!=null) {
            Map<FishSizeClassEnum, Map<String, Long>> fishSizeSummaryMap = summaryTable.getSummaryFishSize();

            if (MapUtils.isEmpty(fishSizeSummaryMap)) {
                fishSizeSummaryMap = new HashMap<>();
                fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), getStringLongMap(customEntity, speciesCnt));

            } else {
                Map<String, Long> speciesCountMap = fishSizeSummaryMap.get(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()));
                if (MapUtils.isEmpty(speciesCountMap)) {
                    fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()), getStringLongMap(customEntity, speciesCnt));
                } else if (customEntity.getSpecies() != null) {
                    Long totalCountForSpecies = speciesCountMap.get(customEntity.getSpecies().toUpperCase());
                    if (totalCountForSpecies == null) {
                        speciesCountMap.put(customEntity.getSpecies().toUpperCase(), speciesCnt);
                    } else {
                        totalCountForSpecies = totalCountForSpecies + speciesCnt;
                    }
                }
            }

            summaryTable.setSummaryFishSize(fishSizeSummaryMap);
        }

        if(customEntity.getTypeCode()!=null) {
            Map<FaCatchTypeEnum, Map<String, Long>> faCatchSummaryMap = summaryTable.getSummaryFaCatchType();
            if (MapUtils.isEmpty(faCatchSummaryMap)) {
                faCatchSummaryMap = new HashMap<>();
                faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), getStringLongMap(customEntity, speciesCnt));
            } else {
                Map<String, Long> speciesCountMap = faCatchSummaryMap.get(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()));
                if (MapUtils.isEmpty(speciesCountMap)) {
                    faCatchSummaryMap.put(FaCatchTypeEnum.valueOf(customEntity.getTypeCode().toUpperCase()), getStringLongMap(customEntity, speciesCnt));
                } else if (customEntity.getSpecies() != null) {
                    Long totalCountForSpecies = speciesCountMap.get(customEntity.getSpecies().toUpperCase());
                    if (totalCountForSpecies == null) {
                        speciesCountMap.put(customEntity.getSpecies().toUpperCase(), speciesCnt);
                    } else {
                        totalCountForSpecies = totalCountForSpecies + speciesCnt;
                    }
                }
            }
            summaryTable.setSummaryFaCatchType(faCatchSummaryMap);
        }

        // Map<FaCatchTypeEnum,Map<String,Long>> catchTypeMap= summaryTable.getSummaryFaCatchType(); // do catch type table summation later


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
