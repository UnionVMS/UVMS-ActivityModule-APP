/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.facatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.ers.fa.dao.proxy.FaCatchSummaryCustomChildProxy;
import eu.europa.ec.fisheries.ers.fa.dao.proxy.FaCatchSummaryCustomProxy;
import eu.europa.ec.fisheries.ers.service.dto.fareport.summary.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.summary.SummaryTableDTO;
import eu.europa.ec.fisheries.ers.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.GroupCriteriaMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryRecord;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

/**
 * This class acts as helper class for FAcatchSummary Report functionality.
 * Created by sanera on 31/01/2017.
 */
@Slf4j
public abstract class FACatchSummaryHelper {
    protected String faCatchSummaryCustomClassName;

    public static String printJsonstructure(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String s = null;
        try {
            s = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Exception while parsing JSON", e);
        }
        log.debug("json structure:-------->");
        log.debug("" + s);
        return s;
    }

    /**
     * This method maps raw data fetched from database to customEntity.
     * @param catchSummaryArr
     * @param groupList
     * @param isLanding
     * @return
     * @throws ServiceException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public FaCatchSummaryCustomProxy mapObjectArrayToFaCatchSummaryCustomEntity(Object[] catchSummaryArr, List<GroupCriteria> groupList, boolean isLanding) throws ServiceException {

        if (ArrayUtils.isEmpty(catchSummaryArr))
            return new FaCatchSummaryCustomProxy();
        int objectArrSize = catchSummaryArr.length - 1;
        if (objectArrSize != groupList.size())  // do not include count field from object array
            throw new ServiceException("selected number of SQL fields do not match with grouping criterias asked by user ");

        Class cls = null;
        try {
            cls = Class.forName(faCatchSummaryCustomClassName);


        Object faCatchSummaryCustomEntityObj = cls.newInstance();
        Class parameterType = String.class;

        Map<GroupCriteria, GroupCriteriaMapper> groupMappings = FilterMap.getGroupByMapping();
        for (int i = 0; i < objectArrSize; i++) {
            GroupCriteria criteria = groupList.get(i);
            Object value = catchSummaryArr[i];

            if (GroupCriteria.DATE_DAY.equals(criteria) || GroupCriteria.DATE_MONTH.equals(criteria) ||
                    GroupCriteria.DATE_YEAR.equals(criteria)) {
                value = extractValueFromDate((Date) value, criteria);
            }

            GroupCriteriaMapper mapper = groupMappings.get(criteria);
            Method method = cls.getDeclaredMethod(mapper.getMethodName(), parameterType);
            method.invoke(faCatchSummaryCustomEntityObj, value);
        }

        Method method = cls.getDeclaredMethod("setCount", Double.TYPE);
        method.invoke(faCatchSummaryCustomEntityObj, catchSummaryArr[objectArrSize]);

        if (isLanding)
            return (FaCatchSummaryCustomChildProxy) faCatchSummaryCustomEntityObj;
        else
            return (FaCatchSummaryCustomProxy) faCatchSummaryCustomEntityObj;
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.debug("Error while trying to map FaCatchSummaryCustomEntity. ",e);
        }
        return new FaCatchSummaryCustomProxy();
    }

    // This method parses the date to extract either day, month or year
    private Object extractValueFromDate(Date date, GroupCriteria criteria) {

        if (GroupCriteria.DATE_DAY.equals(criteria)) {
            SimpleDateFormat day = new SimpleDateFormat("dd");
            return day.format(date);
        } else if (GroupCriteria.DATE_MONTH.equals(criteria)) {
            SimpleDateFormat day = new SimpleDateFormat("MMM");
            return day.format(date);
        } else if (GroupCriteria.DATE_YEAR.equals(criteria)) {
            SimpleDateFormat day = new SimpleDateFormat("YYYY");
            return day.format(date);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        return sdf.format(date);
    }

    /**
     * Add FishClass to the grouping factor and remove FACatch if already present.
     * Method adds default grouping criterias which are required to build summary table.
     *
     * @param groupList
     */
    public void enrichGroupCriteriaWithFishSizeAndSpecies(List<GroupCriteria> groupList) {

        if (groupList.indexOf(GroupCriteria.SIZE_CLASS) == -1) {
            groupList.add(GroupCriteria.SIZE_CLASS);
        }

        if (groupList.indexOf(GroupCriteria.CATCH_TYPE) != -1) {
            groupList.remove(GroupCriteria.CATCH_TYPE);
        }

    }

    /**
     * Add catchType in the list of groups
     *
     * @param groupList
     */
    public void enrichGroupCriteriaWithFACatchType(List<GroupCriteria> groupList) {

        if (groupList.indexOf(GroupCriteria.SIZE_CLASS) != -1) {
            groupList.remove(GroupCriteria.SIZE_CLASS);
        }

        if (groupList.indexOf(GroupCriteria.CATCH_TYPE) == -1) {
            groupList.add(GroupCriteria.CATCH_TYPE);
        }

    }

    /**
     * This method creates groups for various aggregation criterias and combine records for the group
     *
     * @param customEntities
     * @return Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> key => group value => list of records with that group
     */
    public Map<FaCatchSummaryCustomProxy, List<FaCatchSummaryCustomProxy>> groupByFACatchCustomEntities(List<FaCatchSummaryCustomProxy> customEntities) {
        Map<FaCatchSummaryCustomProxy, List<FaCatchSummaryCustomProxy>> groupedMap = new HashMap<>();
        for (FaCatchSummaryCustomProxy summaryObj : customEntities) {
            List<FaCatchSummaryCustomProxy> tempList = groupedMap.get(summaryObj);
            if (Collections.isEmpty(tempList)) {
                tempList = new ArrayList<>();
                tempList.add(summaryObj);
                groupedMap.put(summaryObj, tempList);
            } else {
                tempList.add(summaryObj);
            }
        }

        return groupedMap;
    }

    /**
     * Post process data received from database to create FACatchSummaryRecordDTO. Every record will have summary calculated for it.
     *
     * @param groupedMap
     * @return List<FACatchSummaryRecordDTO> Processed records having summary data
     */
    public abstract List<FACatchSummaryRecordDTO> buildFACatchSummaryRecordDTOList(Map<FaCatchSummaryCustomProxy, List<FaCatchSummaryCustomProxy>> groupedMap);

    /**
     * Creates Summary table structure for provided DTO list
     * @param catchSummaryDTOList list of records to be processed to create summary structure
     * @return
     */
    public SummaryTableDTO populateSummaryTableWithTotal(List<FACatchSummaryRecordDTO> catchSummaryDTOList) {
        SummaryTableDTO summaryTableWithTotals = new SummaryTableDTO();

        for (FACatchSummaryRecordDTO faCatchSummaryDTO : catchSummaryDTOList) {
            SummaryTableDTO summaryTable = faCatchSummaryDTO.getSummaryTable();

            populateTotalFishSizeMap(summaryTableWithTotals, summaryTable);
            populateTotalFaCatchMap(summaryTableWithTotals, summaryTable);

        }
        return summaryTableWithTotals;
    }

    /**
     * This method processes data to calculate weights for different FishSize classes
     *
     * @param summaryTableWithTotals Add the calculation to this final class
     * @param summaryTable           process this object to calculate totals
     */
    public abstract void populateTotalFishSizeMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable);

    /**
     * This method processes data to calculate weights for different Catch types
     *
     * @param summaryTableWithTotals Add the calculation to this final class
     * @param summaryTable           process this object to calculate totals
     */
    public abstract void populateTotalFaCatchMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable);

    protected Double calculateTotalValue(Double value, Double totalValue) {
        if (totalValue == null) {
            return value;
        } else {
            return totalValue + value;
        }
    }

    public List<FACatchSummaryRecord> buildFACatchSummaryRecordList(List<FACatchSummaryRecordDTO> catchSummaryDTOList) {
        List<FACatchSummaryRecord> faCatchSummaryRecords = new ArrayList<>();
        for (FACatchSummaryRecordDTO faCatchSummaryDTO : catchSummaryDTOList) {
            FACatchSummaryRecord faCatchSummaryRecord = new FACatchSummaryRecord();
            faCatchSummaryRecord.setGroups(faCatchSummaryDTO.getGroups());
            faCatchSummaryRecord.setSummary(FACatchSummaryMapper.INSTANCE.mapToSummaryTable(faCatchSummaryDTO.getSummaryTable()));
            faCatchSummaryRecords.add(faCatchSummaryRecord);
        }
        log.debug("List of FACatchSummaryRecord objects created");
        return faCatchSummaryRecords;
    }

    /**
     *  This method tries to add the species into resultMap
     * @param valueSpeciesMap This map will be processed and the values will be added into result map
     * @param resultTotalspeciesMap values will be added to this map
     * @return
     */
    @NotNull
    protected Map<String, Double> extractSpeciesCountMap(Map<String, Double> valueSpeciesMap, Map<String, Double> resultTotalspeciesMap) {
        if (MapUtils.isEmpty(resultTotalspeciesMap)) {
            resultTotalspeciesMap = new HashMap<>(); // FIXME squid:S1226 introduce a new variable instead of reusing
        }

        for (Map.Entry<String, Double> speciesEntry : valueSpeciesMap.entrySet()) {
            String speciesCode = speciesEntry.getKey();
            Double speciesCount = speciesEntry.getValue();

            // check in the totals map if the species exist.If yes, add
            if (resultTotalspeciesMap.containsKey(speciesCode)) {
                resultTotalspeciesMap.put(speciesCode, resultTotalspeciesMap.get(speciesCode) + speciesCount);
            } else {
                resultTotalspeciesMap.put(speciesCode, speciesCount);
            }
        }
        return resultTotalspeciesMap;
    }


}
