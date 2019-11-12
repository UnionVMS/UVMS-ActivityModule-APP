/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.activity.service.mapper;

import eu.europa.ec.fisheries.ers.activity.fa.entities.AapProcessEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProcess;

@Mapper(uses = {AapProcessCodeMapper.class, AapProductMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AapProcessMapper {

    AapProcessMapper INSTANCE = Mappers.getMapper(AapProcessMapper.class);

    @Mappings({
            @Mapping(target = "conversionFactor", source = "conversionFactorNumeric.value"),
            @Mapping(target = "aapProcessCode", ignore = true),
            @Mapping(target = "aapProducts", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "faCatch", ignore = true)
    })
    AapProcessEntity mapToAapProcessEntity(AAPProcess aapProcess);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(source = "aapProducts", target = "resultAAPProducts"),
            @Mapping(source = "aapProcessCode", target = "typeCodes"),
            @Mapping(target = "usedFACatches", ignore = true)
    })
    AAPProcess mapToAapProcess(AapProcessEntity aapProcess);

}
