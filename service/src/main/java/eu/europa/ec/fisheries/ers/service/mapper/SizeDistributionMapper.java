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
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionClassCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SizeDistribution;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class SizeDistributionMapper extends BaseMapper {

    public static final SizeDistributionMapper INSTANCE = Mappers.getMapper(SizeDistributionMapper.class);

    @Mappings({
            @Mapping(target = "categoryCode", expression = "java(getCodeType(sizeDistribution.getCategoryCode()))"),
            @Mapping(target = "categoryCodeListId", expression = "java(getCodeTypeListId(sizeDistribution.getCategoryCode()))"),
            @Mapping(target = "sizeDistributionClassCode", expression = "java(mapToSizeDistributionClassCodes(sizeDistribution.getClassCodes(), sizeDistributionEntity))"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)")
    })
    public abstract SizeDistributionEntity mapToSizeDistributionEntity(SizeDistribution sizeDistribution, FaCatchEntity faCatchEntity, @MappingTarget SizeDistributionEntity sizeDistributionEntity);

    @Mappings({
            @Mapping(target = "classCode", expression = "java(getCodeType(codeType))"),
            @Mapping(target = "classCodeListId", expression = "java(getCodeTypeListId(codeType))")
    })
    public abstract SizeDistributionClassCodeEntity mapToSizeDistributionClassCodeEntity(CodeType codeType);

    protected Set<SizeDistributionClassCodeEntity> mapToSizeDistributionClassCodes(List<CodeType> codeTypes, SizeDistributionEntity sizeDistributionEntity) {
        if (codeTypes == null || codeTypes.isEmpty()) {
            Collections.emptySet();
        }
        Set<SizeDistributionClassCodeEntity> classCodes = new HashSet<>();
        for (CodeType codeType : codeTypes) {
            SizeDistributionClassCodeEntity entity = SizeDistributionMapper.INSTANCE.mapToSizeDistributionClassCodeEntity(codeType);
            entity.setSizeDistribution(sizeDistributionEntity);
            classCodes.add(entity);
        }
        return classCodes;
    }
}