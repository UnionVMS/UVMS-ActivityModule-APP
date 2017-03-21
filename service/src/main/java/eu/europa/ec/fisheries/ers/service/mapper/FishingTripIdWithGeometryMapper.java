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

import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(imports = BaseMapper.class)
public interface FishingTripIdWithGeometryMapper {

    FishingTripIdWithGeometryMapper INSTANCE = Mappers.getMapper(FishingTripIdWithGeometryMapper.class);

    @Mappings({
            @Mapping(target = "tripId", source = "dto.tripId"),
            @Mapping(target = "firstFishingActivity", expression = "java(BaseMapper.getFirstFishingActivity(fishingTripList))"),
            @Mapping(target = "firstFishingActivityDateTime", expression = "java(BaseMapper.getFirstFishingActivityDateTime(fishingTripList))"),
            @Mapping(target = "lastFishingActivity", expression = "java(BaseMapper.getLastFishingActivity(fishingTripList))"),
            @Mapping(target = "lastFishingActivityDateTime", expression = "java(BaseMapper.getLastFishingActivityDateTime(fishingTripList))"),
            @Mapping(target = "vesselIdLists", expression = "java(BaseMapper.getVesselIdLists(fishingTripList))"),
            @Mapping(target = "flagState", expression = "java(BaseMapper.getFlagState(fishingTripList))"),
            @Mapping(target = "noOfCorrections", expression = "java(BaseMapper.getNumberOfCorrections(fishingTripList))"),
            @Mapping(target = "tripDuration", expression = "java(BaseMapper.getTotalDuration(fishingTripList))"),
            @Mapping(target = "schemeId", source = "dto.schemeID"),
    })
    FishingTripIdWithGeometry mapToFishingTripIdWithGeometry(FishingTripId dto, List<FishingTripEntity> fishingTripList);

}