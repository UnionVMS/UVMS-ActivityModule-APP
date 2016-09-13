/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.AapProductDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.AAPProduct;

import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class  AapProductMapper extends BaseMapper {

    public static final AapProductMapper INSTANCE = Mappers.getMapper(AapProductMapper.class);

    @Mappings({
            @Mapping(target = "packagingTypeCode", expression = "java(getCodeType(aapProduct.getPackagingTypeCode()))"),
            @Mapping(target = "packagingTypeCodeListId", expression = "java(getCodeTypeListId(aapProduct.getPackagingTypeCode()))"),
            @Mapping(target = "packagingUnitAvarageWeight", expression = "java(getMeasure(aapProduct.getPackagingUnitAverageWeightMeasure()))"),
            @Mapping(target = "packagingUnitCount", expression = "java(getQuantity(aapProduct.getPackagingUnitQuantity()))"),
            @Mapping(target = "speciesCode", expression = "java(getCodeType(aapProduct.getSpeciesCode()))"),
            @Mapping(target = "speciesCodeListId", expression = "java(getCodeTypeListId(aapProduct.getSpeciesCode()))"),
            @Mapping(target = "unitQuantity", expression = "java(getQuantity(aapProduct.getUnitQuantity()))"),
            @Mapping(target = "weightMeasure", expression = "java(getMeasure(aapProduct.getWeightMeasure()))"),
            @Mapping(target = "weighingMeansCode", expression = "java(getCodeType(aapProduct.getWeighingMeansCode()))"),
            @Mapping(target = "weighingMeansCodeListId", expression = "java(getCodeTypeListId(aapProduct.getWeighingMeansCode()))"),
            @Mapping(target = "usageCode", expression = "java(getCodeType(aapProduct.getUsageCode()))"),
            @Mapping(target = "usageCodeListId", expression = "java(getCodeTypeListId(aapProduct.getUsageCode()))"),
            @Mapping(target = "aapProcess", expression = "java(aapProcessEntity)")
    })
    public abstract AapProductEntity mapToAapProductEntity(AAPProduct aapProduct, AapProcessEntity aapProcessEntity, @MappingTarget AapProductEntity aapProductEntity);

    @Mappings({
            @Mapping(target = "packagingTypeCode", source = "packagingTypeCode"),
            @Mapping(target = "packagingUnitAvarageWeight", source = "packagingUnitAvarageWeight"),
            @Mapping(target = "packagingUnitCount", source = "packagingUnitCount")
    })
    public abstract AapProductDetailsDTO mapToAapProductDetailsDTO(AapProductEntity aapProductEntity);

    public abstract List<AapProductDetailsDTO> mapToAapProductDetailsDTOList(Set<AapProductEntity> aapProductEntities);
}