/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;

@Mapper(imports = BaseMapper.class, uses = {XMLDateUtils.class})
public interface DelimitedPeriodMapper {

    DelimitedPeriodMapper INSTANCE = Mappers.getMapper(DelimitedPeriodMapper.class);

    @Mappings({
            @Mapping(target = "startDate", source = "startDateTime.dateTime"),
            @Mapping(target = "endDate", source = "endDateTime.dateTime"),
            @Mapping(target = "durationMeasure", source = "durationMeasure"),
            @Mapping(target = "calculatedDuration", expression = "java(BaseMapper.getCalculatedMeasure(delimitedPeriod.getDurationMeasure()))"),
    })
    DelimitedPeriodEntity mapToDelimitedPeriodEntity(DelimitedPeriod delimitedPeriod);

    @InheritInverseConfiguration
    DelimitedPeriod mapToDelimitedPeriod(DelimitedPeriodEntity delimitedPeriod);

    List<DelimitedPeriod> mapToDelimitedPeriodList(Set<DelimitedPeriodEntity> delimitedPeriod);

    DelimitedPeriodDTO mapToDelimitedPeriodDTO(DelimitedPeriodEntity delimitedPeriodEntity);

}