package eu.europa.ec.fisheries.ers.service.facatch;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishSizeClassEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 06/03/2017.
 */
@Slf4j
public class FACatchSummaryPresentationHelper extends FACatchSummaryHelper {

    public FACatchSummaryPresentationHelper(){
        super();
        this.faCatchSummaryCustomClassName="eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomChildEntity";
    }

/**
 *  Post process data received from database to create FACatchSummaryRecordDTO. Every record will have summary calculated for it.
 *
 * @param groupedMap
 * @return List<FACatchSummaryRecordDTO> Processed records having summary data
 */
  public  List<FACatchSummaryRecordDTO> buildFACatchSummaryRecordDTOList(Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap){
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

    }

 /*   public SummaryTableDTO populateSummaryTableWithTotal(List<FACatchSummaryRecordDTO> catchSummaryDTOList){
        SummaryTableDTO summaryTableWithTotals= new SummaryTableDTO();

        for(FACatchSummaryRecordDTO faCatchSummaryDTO:catchSummaryDTOList){
            SummaryTableDTO summaryTable= faCatchSummaryDTO.getSummaryTable();

            populateTotalFishSizeMap(summaryTableWithTotals, summaryTable);
            populateTotalFaCatchMapWithPresentation(summaryTableWithTotals, summaryTable);

        }

        return summaryTableWithTotals;
    }*/


    public void populateTotalFishSizeMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable) {

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
    }



    public void populateTotalFaCatchMap(SummaryTableDTO summaryTableWithTotals, SummaryTableDTO summaryTable) {

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

    @NotNull
    protected Map<String, Map<String,Double>> populateSpeciesPresentationMapWithTotal(Map<String, Map<String,Double>> speciesMap, Map<String, Map<String,Double>> resultTotalfishSizeMap) {
        if (MapUtils.isEmpty(resultTotalfishSizeMap)) {
            resultTotalfishSizeMap = new HashMap<>();
        }

        for (Map.Entry<String, Map<String,Double>> speciesEntry : speciesMap.entrySet()) {
            String speciesCode = speciesEntry.getKey();
            Map<String,Double> valuePresentationCountMap = speciesEntry.getValue();

            // check in the totals map if the species exist.If yes, add
            if (resultTotalfishSizeMap.containsKey(speciesCode)) {
                Map<String,Double> resultPresentationCount = resultTotalfishSizeMap.get(speciesCode);
                resultPresentationCount= extractSpeciesCountMAp(valuePresentationCountMap,resultPresentationCount);
                resultTotalfishSizeMap.put(speciesCode,resultPresentationCount);
            } else {

                resultTotalfishSizeMap.put(speciesCode,valuePresentationCountMap );
            }
        }
        return resultTotalfishSizeMap;
    }

}
