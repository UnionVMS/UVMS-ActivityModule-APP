/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package eu.europa.ec.fisheries.ers.service.dto.mapper;

import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;

/**
 * Created by sanera on 17/02/2017.
 */
public class FACatchMapper {

    public static FACatchSummaryDTO mapToFACatchSummaryDTO(FACatchSummaryReportResponse faCatchSummaryReportResponse){
        FACatchSummaryDTO faCatchSummaryDTO = new FACatchSummaryDTO();
        List<FACatchSummaryRecordDTO> faCatchSummaryRecordDTOs = new ArrayList<>();
        List<FACatchSummaryRecord> faCatchSummaryRecords= faCatchSummaryReportResponse.getSummaryRecords();
        for(FACatchSummaryRecord faCatchSummaryRecord : faCatchSummaryRecords){
            faCatchSummaryRecordDTOs.add(mapToFACatchSummaryRecordDTO(faCatchSummaryRecord));
        }
        faCatchSummaryDTO.setRecordDTOs(faCatchSummaryRecordDTOs);
        faCatchSummaryDTO.setTotal(mapToSummaryTableDTO(faCatchSummaryReportResponse.getTotal()));

        return  faCatchSummaryDTO;
    }

    public static FACatchSummaryRecordDTO mapToFACatchSummaryRecordDTO(FACatchSummaryRecord faCatchSummaryRecord){
        FACatchSummaryRecordDTO faCatchSummaryRecordDTO = new FACatchSummaryRecordDTO();
        faCatchSummaryRecordDTO.setGroups(faCatchSummaryRecord.getGroups());
        faCatchSummaryRecordDTO.setSummaryTable(mapToSummaryTableDTO(faCatchSummaryRecord.getSummary()));

        return faCatchSummaryRecordDTO;

    }

    public static SummaryTableDTO mapToSummaryTableDTO(SummaryTable summaryTable){
        SummaryTableDTO summaryTableDTO = new SummaryTableDTO();

        Map<FishSizeClassEnum,Object> summaryFishSizeMap = new HashMap();
        List<SummaryFishSize> fishSizeSummaries = summaryTable.getFishSizeSummaries();
        for(SummaryFishSize summaryFishSize:fishSizeSummaries){
            FishSizeClassEnum fishSizeClassEnum=  summaryFishSize.getFishSize();
            if(CollectionUtils.isNotEmpty(summaryFishSize.getSpecies())){
               summaryFishSizeMap.put(fishSizeClassEnum,extractSpeciesMap(summaryFishSize.getSpecies()));

            }else if(summaryFishSize.getFishSizeCount() !=null){
                summaryFishSizeMap.put(fishSizeClassEnum,summaryFishSize.getFishSizeCount());
            }

        }
        summaryTableDTO.setSummaryFishSize(summaryFishSizeMap);


        Map<FaCatchTypeEnum,Object> faCatchTypeEnumObjectMap = new HashMap();
        List<SummaryFACatchtype> faCatchTypeSummaries = summaryTable.getFaCatchTypeSummaries();
        for(SummaryFACatchtype summaryFACatchtype:faCatchTypeSummaries){
            FaCatchTypeEnum faCatchTypeEnum =  summaryFACatchtype.getCatchType();
            if(CollectionUtils.isNotEmpty(summaryFACatchtype.getSpecies())){
                faCatchTypeEnumObjectMap.put(faCatchTypeEnum,extractSpeciesMap(summaryFACatchtype.getSpecies()));
            }else if(summaryFACatchtype.getCatchTypeCount() !=null){
                faCatchTypeEnumObjectMap.put(faCatchTypeEnum,summaryFACatchtype.getCatchTypeCount());
            }

        }
        summaryTableDTO.setSummaryFaCatchType(faCatchTypeEnumObjectMap);


        return  summaryTableDTO;
    }

    private static Map<String, Double> extractSpeciesMap(List<SpeciesCount> speciesCounts) {
        Map<String,Double> speciesMap=new HashMap<>();
        for(SpeciesCount speciesCount : speciesCounts){
            speciesMap.put(speciesCount.getSpaciesName(), speciesCount.getCount());
        }
        return speciesMap;
    }
}
