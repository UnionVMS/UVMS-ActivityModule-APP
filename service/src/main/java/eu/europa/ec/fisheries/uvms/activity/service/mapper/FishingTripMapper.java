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
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripKey;
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

@Mapper(uses = {DelimitedPeriodMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FishingTripMapper {

    public static final FishingTripMapper INSTANCE = Mappers.getMapper(FishingTripMapper.class);

    @Mappings({
            @Mapping(target = "fishingTripKey", expression = "java(mapToFishingTripKey(fishingTrip))"),
            @Mapping(target = "typeCode", source = "typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "typeCode.listID"),
            @Mapping(target = "delimitedPeriods", ignore = true),
            @Mapping(target = "catchEntities", ignore = true),
            @Mapping(target = "fishingActivities", ignore = true),
            @Mapping(target = "calculatedTripStartDate", ignore = true),
            @Mapping(target = "calculatedTripEndDate", ignore = true)
    })
    public abstract FishingTripEntity mapToFishingTripEntity(FishingTrip fishingTrip);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(source = "delimitedPeriods", target = "specifiedDelimitedPeriods"),
            @Mapping(target = "IDS", expression = "java(mapToIDS(fishingTrip))"),
    })
    public abstract FishingTrip mapToFishingTrip(FishingTripEntity fishingTrip);

    protected FishingTripKey mapToFishingTripKey(FishingTrip fishingTrip) {
        List<IDType> ids = fishingTrip.getIDS();
        IDType idType = ids.get(0);
        if (idType != null) {
            return new FishingTripKey(idType.getValue(), idType.getSchemeID());
        }
        return null;
    }

    protected List<IDType> mapToIDS(FishingTripEntity fishingTripEntity) {
        List<IDType> IDS = new ArrayList<>();
        IDType idType = new IDType();
        FishingTripKey fishingTripKey = fishingTripEntity.getFishingTripKey();
        idType.setValue(fishingTripKey.getTripId());
        idType.setSchemeID(fishingTripKey.getTripSchemeId());
        IDS.add(idType);
        return IDS;
    }

}
