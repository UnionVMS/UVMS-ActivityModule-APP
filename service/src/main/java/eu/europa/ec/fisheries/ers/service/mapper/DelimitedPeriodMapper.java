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
import eu.europa.ec.fisheries.ers.service.util.CustomBigDecimal;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;

@Mapper(uses = {XMLDateUtils.class, CustomBigDecimal.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DelimitedPeriodMapper extends BaseMapper {

    public static DelimitedPeriodMapper INSTANCE = Mappers.getMapper(DelimitedPeriodMapper.class);

    @Mappings({
            @Mapping(target = "startDate", source = "startDateTime", qualifiedByName = "dateTimeTypeToInstant"),
            @Mapping(target = "endDate", source = "endDateTime", qualifiedByName = "dateTimeTypeToInstant"),
            @Mapping(target = "durationMeasure", source = "durationMeasure")
    })
    public abstract DelimitedPeriodEntity mapToDelimitedPeriodEntity(DelimitedPeriod delimitedPeriod);

    @Mappings({
            @Mapping(target = "startDateTime", source = "startDate", qualifiedByName = "instantToDateTimeTypeUTC"),
            @Mapping(target = "endDateTime", source = "endDate", qualifiedByName = "instantToDateTimeTypeUTC"),
            @Mapping(target = "durationMeasure", source = "durationMeasure")
    })
    public abstract DelimitedPeriod mapToDelimitedPeriod(DelimitedPeriodEntity delimitedPeriod);

    public abstract List<DelimitedPeriod> mapToDelimitedPeriodList(Set<DelimitedPeriodEntity> delimitedPeriod);

    @Mappings({
            @Mapping(target = "startDate", source = "startDate", qualifiedByName = "dateTimeTypeToInstant"),
            @Mapping(target = "endDate", source = "endDate", qualifiedByName = "dateTimeTypeToInstant"),
    })
    public abstract DelimitedPeriodDTO mapToDelimitedPeriodDTO(DelimitedPeriodEntity delimitedPeriodEntity);
}