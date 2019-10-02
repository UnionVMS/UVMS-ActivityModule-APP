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

import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;

@Mapper(uses = {FishingTripIdentifierMapper.class, DelimitedPeriodMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FishingTripMapper {

    FishingTripMapper INSTANCE = Mappers.getMapper(FishingTripMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "delimitedPeriods", ignore = true),
            @Mapping(target = "fishingTripIdentifiers", ignore = true),
    })
    FishingTripEntity mapToFishingTripEntity(FishingTrip fishingTrip);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(source = "delimitedPeriods", target = "specifiedDelimitedPeriods"),
            @Mapping(source = "fishingTripIdentifiers", target = "IDS"),
    })
    FishingTrip mapToFishingTrip(FishingTripEntity fishingTrip);

    List<FishingTrip> mapToFishingTripList(Set<FishingTripEntity> fishingTrip);

}