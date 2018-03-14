/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.HashMap;
import java.util.Map;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.ProcessingProductsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProduct;

@Mapper
public abstract class AapProductMapper extends BaseMapper {

    public static final AapProductMapper INSTANCE = Mappers.getMapper(AapProductMapper.class);

    public static final String FISH_PRESENTATION = "FISH_PRESENTATION";

    public static final String FISH_PRESERVATION = "FISH_PRESERVATION";

    public static final String FISH_FRESHNESS = "FISH_FRESHNESS";

    @Mappings({
            @Mapping(target = "packagingTypeCode", source = "packagingTypeCode.value"),
            @Mapping(target = "packagingTypeCodeListId", source = "packagingTypeCode.listID"),
            @Mapping(target = "packagingUnitAvarageWeight", source = "packagingUnitAverageWeightMeasure.value"),
            @Mapping(target = "packagingWeightUnitCode", source = "packagingUnitAverageWeightMeasure.unitCode"),
            @Mapping(target = "calculatedPackagingWeight", expression = "java(getCalculatedMeasure(aapProduct.getPackagingUnitAverageWeightMeasure()))"),
            @Mapping(target = "packagingUnitCount", source = "packagingUnitQuantity.value"),
            @Mapping(target = "packagingUnitCountCode", source = "packagingUnitQuantity.unitCode"),
            @Mapping(target = "calculatedPackagingUnitCount", expression = "java(getCalculatedQuantity(aapProduct.getPackagingUnitQuantity()))"),
            @Mapping(target = "speciesCode", source = "speciesCode.value"),
            @Mapping(target = "speciesCodeListId", source = "speciesCode.listID"),
            @Mapping(target = "unitQuantity", source = "unitQuantity.value"),
            @Mapping(target = "unitQuantityCode", source = "unitQuantity.unitCode"),
            @Mapping(target = "calculatedUnitQuantity", expression = "java(getCalculatedQuantity(aapProduct.getUnitQuantity()))"),
            @Mapping(target = "weightMeasure", source = "weightMeasure.value"),
            @Mapping(target = "weightMeasureUnitCode", source = "weightMeasure.unitCode"),
            @Mapping(target = "calculatedWeightMeasure", expression = "java(getCalculatedMeasure(aapProduct.getWeightMeasure()))"),
            @Mapping(target = "weighingMeansCode", source = "weighingMeansCode.value"),
            @Mapping(target = "weighingMeansCodeListId", source = "weighingMeansCode.listID"),
            @Mapping(target = "usageCode", source = "usageCode.value"),
            @Mapping(target = "usageCodeListId", source = "usageCode.listID"),
    })
    public abstract AapProductEntity mapToAapProductEntity(AAPProduct aapProduct);

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
        if (aapProduct.getAapProcess().getFaCatch().getIcesStatRectangle() != null) {
            locations.put("ices_stat_rectangle", aapProduct.getAapProcess().getFaCatch().getIcesStatRectangle());
        }

        if (aapProduct.getAapProcess().getFaCatch().getFaoArea() != null) {
            locations.put("fao_area", aapProduct.getAapProcess().getFaCatch().getFaoArea());
        }

        if (aapProduct.getAapProcess().getFaCatch().getGfcmGsa() != null) {
            locations.put("gfcm_gsa", aapProduct.getAapProcess().getFaCatch().getGfcmGsa());
        }

        if (aapProduct.getAapProcess().getFaCatch().getEffortZone() != null) {
            locations.put("effort_zone", aapProduct.getAapProcess().getFaCatch().getEffortZone());
        }

        if (aapProduct.getAapProcess().getFaCatch().getTerritory() != null) {
            locations.put("territory", aapProduct.getAapProcess().getFaCatch().getTerritory());
        }
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