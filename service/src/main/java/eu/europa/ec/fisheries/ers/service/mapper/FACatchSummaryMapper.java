package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.Area;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTable;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by sanera on 30/01/2017.
 */
@Mapper
public abstract class FACatchSummaryMapper extends BaseMapper {

    public static final FACatchSummaryMapper INSTANCE = Mappers.getMapper(FACatchSummaryMapper.class);

    @Mappings({
        //    @Mapping(target = "date", source = "date"),
            @Mapping(target = "day", source = "day"),
            @Mapping(target = "month", source = "month"),
            @Mapping(target = "year", source = "year"),
            @Mapping(target = "area", expression = "java(getArea(customEntity))"),
            @Mapping(target = "flagState", source = "flagState"),
            @Mapping(target = "gearType", source = "gearType"),
            @Mapping(target = "presentation", source = "presentation"),
            @Mapping(target = "summaryTable", expression = "java(getSummaryTable(customEntity))")
    })
    public abstract FACatchSummaryDTO mapToFACatchSummaryDTO(FaCatchSummaryCustomEntity customEntity);

    protected Area getArea(FaCatchSummaryCustomEntity customEntity) {
        return new Area(customEntity.getTerritory(), customEntity.getFaoArea(), customEntity.getIcesStatRectangle(), customEntity.getEffortZone()
                , customEntity.getRfmo(), customEntity.getGfcmGsa(), customEntity.getGfcmStatRectangle());
    }

    protected SummaryTable getSummaryTable(FaCatchSummaryCustomEntity customEntity) {
        SummaryTable summaryTable = new SummaryTable();

       /* Map<FishSizeClassEnum,Map<String,Long>> fishSizeSummaryMap=summaryTable.getSummaryFishSize();
        long speciesCnt = customEntity.getCount();
        if(MapUtils.isEmpty(fishSizeSummaryMap)){
            fishSizeSummaryMap = new HashMap<>();
            fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()),getStringLongMap(customEntity, speciesCnt));

        }else{
            Map<String,Long> speciesCountMap= fishSizeSummaryMap.get(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()));
            if(MapUtils.isEmpty(speciesCountMap)){
                 fishSizeSummaryMap.put(FishSizeClassEnum.valueOf(customEntity.getFishClass().toUpperCase()),getStringLongMap(customEntity, speciesCnt));
            }else if(customEntity.getSpecies() !=null) {
                   Long totalCountForSpecies= speciesCountMap.get(customEntity.getSpecies().toUpperCase());
                   if(totalCountForSpecies ==null) {
                       speciesCountMap.put(customEntity.getSpecies().toUpperCase(),speciesCnt);
                   }else{
                       totalCountForSpecies = totalCountForSpecies+ speciesCnt;
                   }
                }
           }

        Map<FaCatchTypeEnum,Map<String,Long>> catchTypeSummaryMap= summaryTable.getSummaryFaCatchType();*/
        return summaryTable;
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
