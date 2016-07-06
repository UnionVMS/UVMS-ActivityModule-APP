/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.AapStockEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.AAPStock;

/**
 * Created by padhyad on 6/15/2016.
 */
@Mapper
public abstract class AapStockMapper extends BaseMapper {

    public static AapStockMapper INSTANCE = Mappers.getMapper(AapStockMapper.class);

    @Mappings({
            @Mapping(target = "stockId", expression = "java(getIdType(aapStock.getID()))"),
            @Mapping(target = "stockSchemeId", expression = "java(getIdTypeSchemaId(aapStock.getID()))"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)")
    })
    public abstract AapStockEntity mapToAapStockEntity(AAPStock aapStock, FaCatchEntity faCatchEntity, @MappingTarget AapStockEntity aapStockEntity);
}