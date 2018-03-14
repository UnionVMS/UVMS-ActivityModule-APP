/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProcess;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProduct;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

@Mapper(uses = {AapProductMapper.class})
public abstract class AapProcessMapper extends BaseMapper {

    public static final AapProcessMapper INSTANCE = Mappers.getMapper(AapProcessMapper.class);

    @Mappings({
            @Mapping(target = "conversionFactor", source = "aapProcess.conversionFactorNumeric.value"),
            @Mapping(target = "aapProducts", expression = "java(getAapProductEntities(aapProcess.getResultAAPProducts(), aapProcessEntity))"),
            @Mapping(target = "aapProcessCode", expression = "java(getAapProcessCodes(aapProcess.getTypeCodes(), aapProcessEntity))")
    })
    public abstract AapProcessEntity mapToAapProcessEntity(AAPProcess aapProcess);

    @Mappings({
            @Mapping(target = "typeCode", source = "value"),
            @Mapping(target = "typeCodeListId", source = "listID")
    })
    public abstract AapProcessCodeEntity mapToAapProcessCodeEntity(CodeType codeType);

    protected Set<AapProcessCodeEntity> getAapProcessCodes(List<CodeType> codeTypes, AapProcessEntity aapProcessEntity) {
        if (codeTypes == null || codeTypes.isEmpty()) {
            return Collections.emptySet();
        }
        Set<AapProcessCodeEntity> aapProcessCodeEntities = new HashSet<>();
        for (CodeType codeType : codeTypes) {
            AapProcessCodeEntity entity = AapProcessMapper.INSTANCE.mapToAapProcessCodeEntity(codeType);
            entity.setAapProcess(aapProcessEntity);
            aapProcessCodeEntities.add(entity);
        }
        return aapProcessCodeEntities;
    }

    protected Set<AapProductEntity> getAapProductEntities(List<AAPProduct> aapProducts, AapProcessEntity aapProcessEntity) {
        if (aapProducts == null || aapProducts.isEmpty()) {
            return Collections.emptySet();
        }
        Set<AapProductEntity> aapProductEntities = new HashSet<>();
        for (AAPProduct aapProduct : aapProducts) {
            AapProductEntity aapProductEntity = AapProductMapper.INSTANCE.mapToAapProductEntity(aapProduct);
            aapProductEntity.setAapProcess(aapProcessEntity);
            aapProductEntities.add(aapProductEntity);
        }
        return aapProductEntities;
    }
}