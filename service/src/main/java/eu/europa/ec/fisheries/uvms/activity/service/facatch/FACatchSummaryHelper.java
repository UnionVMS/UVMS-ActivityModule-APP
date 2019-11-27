/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.service.facatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.proxy.FaCatchSummaryCustomChildProxy;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.proxy.FaCatchSummaryCustomProxy;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.uvms.activity.service.search.FilterMap;
import eu.europa.ec.fisheries.uvms.activity.service.search.GroupCriteriaMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryRecord;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class acts as helper class for FAcatchSummary Report functionality.
 * Helper class with basic utility methods for FACatch Summary table
 * Created by sanera on 31/01/2017.
 */
@Slf4j
public abstract class FACatchSummaryHelper {
    protected String faCatchSummaryCustomClassName;

    private DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd", Locale.ROOT).withZone(ZoneOffset.UTC.normalized());
    private DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM", Locale.ROOT).withZone(ZoneOffset.UTC.normalized());
    private DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ROOT).withZone(ZoneOffset.UTC.normalized());
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT).withZone(ZoneOffset.UTC.normalized());
    private DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT).withZone(ZoneOffset.UTC.normalized());

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
     * @throws ServiceException
     */
    public FaCatchSummaryCustomProxy mapObjectArrayToFaCatchSummaryCustomEntity(Object[] catchSummaryArr, List<GroupCriteria> groupList, boolean isLanding) throws ServiceException {

        if (ArrayUtils.isEmpty(catchSummaryArr))
            return new FaCatchSummaryCustomProxy();
        int objectArrSize = catchSummaryArr.length - 1;
        if (objectArrSize != groupList.size())  // do not include count field from object array
            throw new ServiceException("selected number of SQL fields do not match with grouping criterias asked by user ");

        try {
            Class cls = Class.forName(faCatchSummaryCustomClassName);


        Object faCatchSummaryCustomEntityObj = cls.newInstance();
        Class parameterType = String.class;

        Map<GroupCriteria, GroupCriteriaMapper> groupMappings = FilterMap.getGroupByMapping();
        for (int i = 0; i < objectArrSize; i++) {
            GroupCriteria criteria = groupList.get(i);
            Object value = catchSummaryArr[i];
            if (value == null) {
                continue;
            }

            if (GroupCriteria.DATE_DAY.equals(criteria) || GroupCriteria.DATE_MONTH.equals(criteria) ||
                    GroupCriteria.DATE_YEAR.equals(criteria) || GroupCriteria.DATE.equals(criteria)) {

                Instant instant;
                if (value instanceof Instant) {
                    instant = (Instant)value;
                } else if (value instanceof Date) {
                    instant = ((Date)value).toInstant();
                } else {
                    throw new ServiceException("Object is not a supported time/date class");
                }

                value = instantToString(instant, criteria);
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
            log.debug("Error while trying to map FaCatchSummaryCustomProxy. ",e);
        }
        return null;
    }

    private String instantToString(Instant instant, GroupCriteria criteria) {
        switch(criteria) {
            case DATE_DAY:
                return dayFormatter.format(instant);
            case DATE_MONTH:
                return monthFormatter.format(instant);
            case DATE_YEAR:
                return yearFormatter.format(instant);
            case DATE:
                return dateFormatter.format(instant);
            default:
                return defaultFormatter.format(instant);
        }
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
     * @return key =&gt; group value =&gt; list of records with that group
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
     * @return Processed records having summary data
     */
    public abstract List<FACatchSummaryRecordDTO> buildFACatchSummaryRecordDTOList(Map<FaCatchSummaryCustomProxy, List<FaCatchSummaryCustomProxy>> groupedMap);

    /**
     * Creates Summary table structure for provided DTO list
     * @param catchSummaryDTOList list of records to be processed to create summary structure
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
     */
    @NotNull
    protected Map<String, Double> extractSpeciesCountMap(Map<String, Double> valueSpeciesMap, Map<String, Double> resultTotalspeciesMap) {
        if (MapUtils.isEmpty(resultTotalspeciesMap)) {
            resultTotalspeciesMap = new HashMap<>(); // do not introduce a new variable instead of reusing.Reusing is required here.
        }

        for (Map.Entry<String, Double> speciesEntry : valueSpeciesMap.entrySet()) {
            String speciesCode = speciesEntry.getKey();
            Double speciesCount = speciesEntry.getValue();

            // check in the totals map if the species exist.If yes, add
            if (resultTotalspeciesMap.containsKey(speciesCode)) {
                Double total = resultTotalspeciesMap.get(speciesCode) +speciesCount;
                resultTotalspeciesMap.put(speciesCode, total);
            } else {
                resultTotalspeciesMap.put(speciesCode, speciesCount);
            }
        }
        return resultTotalspeciesMap;
    }


}
