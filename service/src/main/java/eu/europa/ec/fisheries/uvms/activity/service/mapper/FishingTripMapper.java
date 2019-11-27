/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripIdentifierEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {FishingTripIdentifierMapper.class, DelimitedPeriodMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FishingTripMapper {

    public static final FishingTripMapper INSTANCE = Mappers.getMapper(FishingTripMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "delimitedPeriods", ignore = true),
            @Mapping(target = "fishingTripIdentifier", expression = "java(FishingTripIdentifierMapper.INSTANCE.mapToFishingTripIdentifier(fishingTrip.getIDS().get(0)))"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "catchEntities", ignore = true),
            @Mapping(target = "fishingActivities", ignore = true),
    })
    public abstract FishingTripEntity mapToFishingTripEntity(FishingTrip fishingTrip);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(source = "delimitedPeriods", target = "specifiedDelimitedPeriods"),
            @Mapping(target = "IDS", expression = "java(mapToIDS(fishingTrip.getFishingTripIdentifier()))"),
    })
    public abstract FishingTrip mapToFishingTrip(FishingTripEntity fishingTrip);

    protected List<IDType> mapToIDS(FishingTripIdentifierEntity fishingTripIdentifierEntity) {
        List<IDType> IDS = new ArrayList<>();
        IDS.add(FishingTripIdentifierMapper.INSTANCE.mapToIDType(fishingTripIdentifierEntity));
        return IDS;
    }

}
