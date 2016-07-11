/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.SizeDistribution;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.CodeType;

import java.util.List;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class SizeDistributionMapper extends BaseMapper {

    public static SizeDistributionMapper INSTANCE = Mappers.getMapper(SizeDistributionMapper.class);

    @Mappings({
            @Mapping(target = "classCode", expression = "java(getCodeTypeFromList(sizeDistribution.getClassCodes()))"),
            @Mapping(target = "classCodeListId", expression = "java(getCodeTypeListIdFromList(sizeDistribution.getClassCodes()))"),
            @Mapping(target = "categoryCode", expression = "java(getCodeType(sizeDistribution.getCategoryCode()))"),
            @Mapping(target = "categoryCodeListId", expression = "java(getCodeTypeListId(sizeDistribution.getCategoryCode()))"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)")
    })
    public abstract SizeDistributionEntity mapToSizeDistributionEntity(SizeDistribution sizeDistribution, FaCatchEntity faCatchEntity, @MappingTarget SizeDistributionEntity sizeDistributionEntity);
}