/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.activity.service.mapper;

import eu.europa.ec.fisheries.ers.activity.fa.entities.SizeDistributionClassCodeEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.SizeDistributionEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SizeDistribution;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

@Mapper(imports = BaseMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SizeDistributionMapper {

    SizeDistributionMapper INSTANCE = Mappers.getMapper(SizeDistributionMapper.class);

    @Mappings({
            @Mapping(target = "categoryCode", source = "categoryCode.value"),
            @Mapping(target = "categoryCodeListId", source = "categoryCode.listID"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "faCatch", ignore = true),
            @Mapping(target = "sizeDistributionClassCodeEntities", ignore = true)
    })
    SizeDistributionEntity mapToSizeDistributionEntity(SizeDistribution sizeDistribution);

    @Mappings({
            @Mapping(target = "classCode", source = "value"),
            @Mapping(target = "classCodeListId", source = "listID"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "sizeDistribution", ignore = true),
    })
    SizeDistributionClassCodeEntity mapToSizeDistributionClassCodeEntity(CodeType codeType);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "listAgencyID", ignore = true),
            @Mapping(target = "listAgencyName", ignore = true),
            @Mapping(target = "listName", ignore = true),
            @Mapping(target = "listVersionID", ignore = true),
            @Mapping(target = "name", ignore = true),
            @Mapping(target = "languageID", ignore = true),
            @Mapping(target = "listURI", ignore = true),
            @Mapping(target = "listSchemeURI", ignore = true)
    })
    CodeType mapToSizeDistributionClassCode(SizeDistributionClassCodeEntity classCodeEntity);

}
