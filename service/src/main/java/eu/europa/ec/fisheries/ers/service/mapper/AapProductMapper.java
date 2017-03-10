/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.ProcessingProductsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProduct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class AapProductMapper extends BaseMapper {

    public static final AapProductMapper INSTANCE = Mappers.getMapper(AapProductMapper.class);

    public static final String FISH_PRESENTATION = "FISH_PRESENTATION";

    public static final String FISH_PRESERVATION = "FISH_PRESERVATION";

    public static final String FISH_FRESHNESS = "FISH_FRESHNESS";

    @Mappings({
            @Mapping(target = "packagingTypeCode", expression = "java(getCodeType(aapProduct.getPackagingTypeCode()))"),
            @Mapping(target = "packagingTypeCodeListId", expression = "java(getCodeTypeListId(aapProduct.getPackagingTypeCode()))"),
            @Mapping(target = "packagingUnitAvarageWeight", expression = "java(getMeasure(aapProduct.getPackagingUnitAverageWeightMeasure()))"),
            @Mapping(target = "packagingWeightUnitCode", expression = "java(getMeasureUnitCode(aapProduct.getPackagingUnitAverageWeightMeasure()))"),
            @Mapping(target = "calculatedPackagingWeight", expression = "java(getCalculatedMeasure(aapProduct.getPackagingUnitAverageWeightMeasure()))"),
            @Mapping(target = "packagingUnitCount", expression = "java(getQuantity(aapProduct.getPackagingUnitQuantity()))"),
            @Mapping(target = "packagingUnitCountCode", expression = "java(getQuantityUnitCode(aapProduct.getPackagingUnitQuantity()))"),
            @Mapping(target = "calculatedPackagingUnitCount", expression = "java(getCalculatedQuantity(aapProduct.getPackagingUnitQuantity()))"),
            @Mapping(target = "speciesCode", expression = "java(getCodeType(aapProduct.getSpeciesCode()))"),
            @Mapping(target = "speciesCodeListId", expression = "java(getCodeTypeListId(aapProduct.getSpeciesCode()))"),
            @Mapping(target = "unitQuantity", expression = "java(getQuantity(aapProduct.getUnitQuantity()))"),
            @Mapping(target = "unitQuantityCode", expression = "java(getQuantityUnitCode(aapProduct.getUnitQuantity()))"),
            @Mapping(target = "calculatedUnitQuantity", expression = "java(getCalculatedQuantity(aapProduct.getUnitQuantity()))"),
            @Mapping(target = "weightMeasure", expression = "java(getMeasure(aapProduct.getWeightMeasure()))"),
            @Mapping(target = "weightMeasureUnitCode", expression = "java(getMeasureUnitCode(aapProduct.getWeightMeasure()))"),
            @Mapping(target = "calculatedWeightMeasure", expression = "java(getCalculatedMeasure(aapProduct.getWeightMeasure()))"),
            @Mapping(target = "weighingMeansCode", expression = "java(getCodeType(aapProduct.getWeighingMeansCode()))"),
            @Mapping(target = "weighingMeansCodeListId", expression = "java(getCodeTypeListId(aapProduct.getWeighingMeansCode()))"),
            @Mapping(target = "usageCode", expression = "java(getCodeType(aapProduct.getUsageCode()))"),
            @Mapping(target = "usageCodeListId", expression = "java(getCodeTypeListId(aapProduct.getUsageCode()))"),
            @Mapping(target = "aapProcess", expression = "java(aapProcessEntity)")
    })
    public abstract AapProductEntity mapToAapProductEntity(AAPProduct aapProduct, AapProcessEntity aapProcessEntity, @MappingTarget AapProductEntity aapProductEntity);

    @Mappings({
            @Mapping(target = "type", source = "aapProcess.faCatch.typeCode"),
            @Mapping(target = "locations", expression = "java(getDenormalizedLocations(aapProduct))"),
            @Mapping(target = "species", source = "aapProcess.faCatch.speciesCode"),
            @Mapping(target = "gear", source = "aapProcess.faCatch.gearTypeCode"),
            @Mapping(target = "presentation", expression = "java(getAapProcessTypeByCode(aapProduct.getAapProcess(), FISH_PRESENTATION))"),
            @Mapping(target = "preservation", expression = "java(getAapProcessTypeByCode(aapProduct.getAapProcess(), FISH_PRESERVATION))"),
            @Mapping(target = "freshness", expression = "java(getAapProcessTypeByCode(aapProduct.getAapProcess(), FISH_FRESHNESS))"),
            @Mapping(target = "conversionFactor", source = "aapProcess.conversionFactor"),
            @Mapping(target = "weight", source = "calculatedWeightMeasure"),
            @Mapping(target = "quantity", source = "unitQuantity"),
            @Mapping(target = "packageWeight", source = "calculatedPackagingWeight"),
            @Mapping(target = "packageQuantity", source = "packagingUnitCount"),
            @Mapping(target = "packagingType", source = "packagingTypeCode")
    })
    public abstract ProcessingProductsDto mapToProcessingProduct(AapProductEntity aapProduct);

    protected Map<String, String> getDenormalizedLocations(AapProductEntity aapProduct) {
        Map<String, String> locations = new HashMap<>();
        locations.put("ices_stat_rectangle", aapProduct.getAapProcess().getFaCatch().getIcesStatRectangle());
        locations.put("fao_area", aapProduct.getAapProcess().getFaCatch().getFaoArea());
        locations.put("gfcm_gsa", aapProduct.getAapProcess().getFaCatch().getGfcmGsa());
        locations.put("effort_zone", aapProduct.getAapProcess().getFaCatch().getEffortZone());
        locations.put("territory", aapProduct.getAapProcess().getFaCatch().getTerritory());
        return locations;
    }

    protected String getAapProcessTypeByCode(AapProcessEntity aapProcess, String typeCode) {
        String fishPresentation = null;
        for(AapProcessCodeEntity aapProcessCode : aapProcess.getAapProcessCode()) {
            if (aapProcessCode.getTypeCodeListId().equalsIgnoreCase(typeCode)) {
                fishPresentation = aapProcessCode.getTypeCode();
            }
        }
        return fishPresentation;
    }
}