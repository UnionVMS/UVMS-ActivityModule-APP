/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.facatch;

import eu.europa.ec.fisheries.ers.fa.dao.proxy.FaCatchSummaryCustomProxy;
import eu.europa.ec.fisheries.ers.service.dto.fareport.summary.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.summary.SummaryTableDTO;
import eu.europa.ec.fisheries.ers.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishSizeClassEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 06/03/2017.
 * These classes help creating table structure for FACatch summary reports.
 * Every class is handling specific table structure formatting requirements .
 * If new type of table structure is required. Just extend FACatchSummaryHelper and modify methods suitable to your needs
 */
@Slf4j
public class FACatchSummaryPresentationHelper extends FACatchSummaryHelper {

    public FACatchSummaryPresentationHelper(){
        super();
        this.faCatchSummaryCustomClassName = "eu.europa.ec.fisheries.ers.fa.dao.proxy.FaCatchSummaryCustomChildProxy";
    }

/**
 *  Post process data received from database to create FACatchSummaryRecordDTO. Every record will have summary calculated for it.
 *
 * @param groupedMap
 * @return List<FACatchSummaryRecordDTO> Processed records having summary data
 */
  @Override
  public List<FACatchSummaryRecordDTO> buildFACatchSummaryRecordDTOList(Map<FaCatchSummaryCustomProxy, List<FaCatchSummaryCustomProxy>> groupedMap) {
        List<FACatchSummaryRecordDTO> faCatchSummaryRecordDTOs = new ArrayList<>();

      for (Map.Entry<FaCatchSummaryCustomProxy, List<FaCatchSummaryCustomProxy>> entry : groupedMap.entrySet()) {
          FaCatchSummaryCustomProxy customEntity = entry.getKey();
            customEntity.setPresentation(null); // We dont want Presentation to be part of group criteria. We want to display this information in summmary table so, remove it
            FACatchSummaryRecordDTO faCatchSummaryDTO= FACatchSummaryMapper.INSTANCE.mapToFACatchSummaryRecordDTOWithPresentation(entry.getKey(),entry.getValue());
            if(CollectionUtils.isEmpty(faCatchSummaryDTO.getGroups())){ // Do not add record to the list if no data for grouping factors found
                log.error("No data for the grouping factors found :"+faCatchSummaryDTO);
                continue;
            }

            // If there is Group but no data for Summary table for the group, do not send it to frontend
            if(faCatchSummaryDTO.getSummaryTable() == null || (faCatchSummaryDTO.getSummaryTable().getSummaryFaCatchType()==null &&
                    faCatchSummaryDTO.getSummaryTable().getSummaryFishSize()==null)){
                log.error("No data for the summary found :"+faCatchSummaryDTO);
                continue;
            }
            faCatchSummaryRecordDTOs.add(faCatchSummaryDTO);
        }
        return faCatchSummaryRecordDTOs;

    }

    @Override
    public void populateTotalFishSizeMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable) {

        Map<FishSizeClassEnum,Object> fishSizeClassEnumMap=summaryTable.getSummaryFishSize();
        if(MapUtils.isEmpty(fishSizeClassEnumMap)){
            return;
        }

        Map<FishSizeClassEnum, Object> totalFishSizeSpeciesMap=summaryTableWithTotals.getSummaryFishSize();
        if(MapUtils.isEmpty(totalFishSizeSpeciesMap)){
            totalFishSizeSpeciesMap = new EnumMap<>(FishSizeClassEnum.class);
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
    }


    @Override
    public void populateTotalFaCatchMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable) {

        Map<FaCatchTypeEnum, Object> catchTypeEnumMapMap=summaryTable.getSummaryFaCatchType();
        if(MapUtils.isNotEmpty(catchTypeEnumMapMap)) {

            Map<FaCatchTypeEnum,Object> totalCatchTypeMap = summaryTableWithTotals.getSummaryFaCatchType();
            if (MapUtils.isEmpty(totalCatchTypeMap)) {
                totalCatchTypeMap = new EnumMap<>(FaCatchTypeEnum.class);
                summaryTableWithTotals.setSummaryFaCatchType(totalCatchTypeMap);
            }

            // Go through all the catch types and calculate total for each type
            for (Map.Entry<FaCatchTypeEnum,Object> entry : catchTypeEnumMapMap.entrySet()) {
                FaCatchTypeEnum catchType = entry.getKey(); // key fishSize
                Object value = entry.getValue();
                if(value instanceof Map){
                    Map<String, Map<String,Double>> fishSizeMap = (Map<String, Map<String,Double>>) totalCatchTypeMap.get(catchType); // check if already present
                    fishSizeMap = populateSpeciesPresentationMapWithTotal(( Map<String, Map<String,Double>>) value, fishSizeMap);
                    totalCatchTypeMap.put(catchType, fishSizeMap);

                }
            }
        }
    }

    @NotNull
    protected Map<String, Map<String,Double>> populateSpeciesPresentationMapWithTotal(Map<String, Map<String,Double>> speciesMap, Map<String, Map<String,Double>> resultTotalPresentationMap) {
        if (MapUtils.isEmpty(resultTotalPresentationMap)) {
            resultTotalPresentationMap = new HashMap<>(); // do not introduce a new variable instead of reusing. Here reusing is required.
        }

        for (Map.Entry<String, Map<String,Double>> speciesEntry : speciesMap.entrySet()) {
            String speciesCode = speciesEntry.getKey();
            Map<String,Double> valuePresentationCountMap = speciesEntry.getValue();

            // check in the totals map if the species exist.If yes, add
            if (resultTotalPresentationMap.containsKey(speciesCode)) {
                Map<String,Double> resultPresentationCount = resultTotalPresentationMap.get(speciesCode);
                resultPresentationCount= extractSpeciesCountMap(valuePresentationCountMap,resultPresentationCount);
                resultTotalPresentationMap.put(speciesCode,resultPresentationCount);
            } else {
                 Map totalValueMap = new HashMap(valuePresentationCountMap);
                resultTotalPresentationMap.put(speciesCode,totalValueMap );
            }
        }
        return resultTotalPresentationMap;
    }

}
