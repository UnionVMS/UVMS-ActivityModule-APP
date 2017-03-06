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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 06/03/2017.
 */
@Slf4j
public class FACatchSummaryReportHelper extends FACatchSummaryHelper {

    public FACatchSummaryReportHelper(){
        super();
        this.faCatchSummaryCustomClassName="eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity";
    }

    /**
     *  Post process data received from database to create FACatchSummaryRecordDTO. Every record will have summary calculated for it.
     *
     * @param groupedMap
     * @return List<FACatchSummaryRecordDTO> Processed records having summary data
     */
    public List<FACatchSummaryRecordDTO> buildFACatchSummaryRecordDTOList(Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedMap){
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

    }

   /* public SummaryTableDTO populateSummaryTableWithTotal(List<FACatchSummaryRecordDTO> catchSummaryDTOList){
        SummaryTableDTO summaryTableWithTotals= new SummaryTableDTO();

        for(FACatchSummaryRecordDTO faCatchSummaryDTO:catchSummaryDTOList){
            SummaryTableDTO summaryTable= faCatchSummaryDTO.getSummaryTable();

            populateTotalFishSizeMap(summaryTableWithTotals, summaryTable);
            populateTotalFaCatchMap(summaryTableWithTotals, summaryTable);

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
                Map<String, Double> fishSizeMap = (Map<String, Double>) totalFishSizeSpeciesMap.get(fishSize); // check if already present
                fishSizeMap = extractSpeciesCountMAp((Map<String, Double>) value, fishSizeMap);
                totalFishSizeSpeciesMap.put(fishSize, fishSizeMap);

            }else if(value instanceof Double){
                totalFishSizeSpeciesMap.put(fishSize, calculateTotalValue((Double) value, (Double) totalFishSizeSpeciesMap.get(fishSize)));
            }
        }
    }

    /**
     * This method processes data to calculate weights for different Catch types
     * @param summaryTableWithTotals  Add the calculation to this final class
     * @param summaryTable process this object to calculate totals
     */
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
                    Map<String, Double> fishSizeMap = (Map<String, Double>) totalCatchTypeMap.get(catchType); // check if already present
                    fishSizeMap = extractSpeciesCountMAp((Map<String, Double>) value, fishSizeMap);
                    totalCatchTypeMap.put(catchType, fishSizeMap);

                }else if(value instanceof Double){
                    totalCatchTypeMap.put(catchType, calculateTotalValue((Double) value, (Double) totalCatchTypeMap.get(catchType)));
                }
            }
        }
    }
}
