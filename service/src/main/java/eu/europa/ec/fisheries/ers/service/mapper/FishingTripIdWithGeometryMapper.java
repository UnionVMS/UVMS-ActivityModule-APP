/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierType;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by sanera on 02/12/2016.
 */
@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public abstract class FishingTripIdWithGeometryMapper extends BaseMapper  {
    public static final FishingTripIdWithGeometryMapper INSTANCE = Mappers.getMapper(FishingTripIdWithGeometryMapper.class);

    @Mappings({
            @Mapping(target = "tripId", source = "dto.tripId"),
            @Mapping(target = "firstFishingActivity", expression = "java(getFirstFishingActivity(fishingTripList))"),
            @Mapping(target = "firstFishingActivityDateTime", expression = "java(getFirstFishingActivityDateTime(fishingTripList))"),
            @Mapping(target = "lastFishingActivity", expression = "java(getLastFishingActivity(fishingTripList))"),
            @Mapping(target = "lastFishingActivityDateTime", expression = "java(getLastFishingActivityDateTime(fishingTripList))"),
            @Mapping(target = "vesselIdLists", expression = "java(getVesselIdLists(fishingTripList))"),
            @Mapping(target = "flagState", expression = "java(getFlagState(fishingTripList))"),
            @Mapping(target = "noOfCorrections", expression = "java(getNumberOfCorrections(fishingTripList))"),
            @Mapping(target = "tripDuration", expression = "java(getTotalDuration(fishingTripList))"),
            @Mapping(target = "schemeId", source = "dto.schemeID"),
            @Mapping(target = "geometry", source = "geometry"),
            @Mapping(target = "relativeFirstFaDateTime", expression = "java(getRelativeFirstFaDateTime(fishingTripList))"),
            @Mapping(target = "relativeLastFaDateTime", expression = "java(getRelativeLastFaDateTime(fishingTripList))")
    })
    public abstract FishingTripIdWithGeometry mapToFishingTripIdWithGeometry(FishingTripId dto, String geometry,List<FishingTripEntity> fishingTripList);

    protected String getFirstFishingActivity(List<FishingTripEntity> fishingTripList){
       if(CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(0).getFishingActivity() ==null){
           return null;
       }
      return  fishingTripList.get(0).getFishingActivity().getTypeCode();

    }

    protected XMLGregorianCalendar getFirstFishingActivityDateTime(List<FishingTripEntity> fishingTripList){
        if(CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(0).getFishingActivity() ==null
                || fishingTripList.get(0).getFishingActivity().getCalculatedStartTime()==null){
            return null;
        }

        return convertToXMLGregorianCalendar(fishingTripList.get(0).getFishingActivity().getCalculatedStartTime(),false);
    }


    protected XMLGregorianCalendar getRelativeFirstFaDateTime(List<FishingTripEntity> fishingTripList){
        if(CollectionUtils.isEmpty(fishingTripList)){
            return null;
        }
        Date tripStartDate =getFishingTripDateTime(fishingTripList,FishingActivityTypeEnum.DEPARTURE.toString());
        if(tripStartDate ==null)
            return null;

        return convertToXMLGregorianCalendar(tripStartDate,false);
    }


    protected XMLGregorianCalendar getRelativeLastFaDateTime(List<FishingTripEntity> fishingTripList){
        if(CollectionUtils.isEmpty(fishingTripList)){
            return null;
        }
        Date tripEndDate =getFishingTripDateTime(fishingTripList,FishingActivityTypeEnum.ARRIVAL.toString());
        if(tripEndDate ==null)
            return null;

        return convertToXMLGregorianCalendar(tripEndDate,false);
    }

    protected String getLastFishingActivity(List<FishingTripEntity> fishingTripList){
        if(CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(fishingTripList.size()-1).getFishingActivity() ==null){
            return null;
        }
        int totalFishTripEntityCount=fishingTripList.size();
        return  fishingTripList.get(totalFishTripEntityCount -1).getFishingActivity().getTypeCode();
    }

    protected XMLGregorianCalendar getLastFishingActivityDateTime(List<FishingTripEntity> fishingTripList){
        if(CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(fishingTripList.size()-1).getFishingActivity() ==null
                || fishingTripList.get(fishingTripList.size() -1).getFishingActivity().getCalculatedStartTime() ==null){
            return null;
        }
        int totalFishTripEntityCount=fishingTripList.size();
        return  convertToXMLGregorianCalendar(fishingTripList.get(totalFishTripEntityCount -1).getFishingActivity().getCalculatedStartTime(),false);
    }

    protected List<VesselIdentifierType> getVesselIdLists(List<FishingTripEntity> fishingTripList){
        if(CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(fishingTripList.size()-1).getFishingActivity() ==null ||
                fishingTripList.get(fishingTripList.size()-1).getFishingActivity().getFaReportDocument() ==null ||
                fishingTripList.get(fishingTripList.size()-1).getFishingActivity().getFaReportDocument().getVesselTransportMeans() ==null){
            return Collections.emptyList();
        }
        int totalFishTripEntityCount=fishingTripList.size();
        FishingTripEntity fishingTripEntity=fishingTripList.get(totalFishTripEntityCount-1);
        VesselTransportMeansEntity vesselTransportMeansEntity=fishingTripEntity.getFishingActivity().getFaReportDocument().getVesselTransportMeans();
        Set<VesselIdentifierEntity> vesselIdentifierEntities= vesselTransportMeansEntity.getVesselIdentifiers();
        List<VesselIdentifierType> vesselIdentifierTypes =new ArrayList<>();

        if(CollectionUtils.isNotEmpty(vesselIdentifierEntities)){
            for(VesselIdentifierEntity vesselIdentifierEntity:vesselIdentifierEntities){
                VesselIdentifierType vesselIdentifierType = new VesselIdentifierType();
                vesselIdentifierType.setKey(VesselIdentifierSchemeIdEnum.valueOf(vesselIdentifierEntity.getVesselIdentifierSchemeId()));
                vesselIdentifierType.setValue(vesselIdentifierEntity.getVesselIdentifierId());
                vesselIdentifierTypes.add(vesselIdentifierType);
            }
        }

        return  vesselIdentifierTypes;
    }

    protected String getFlagState(List<FishingTripEntity> fishingTripList){
        if(CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(fishingTripList.size()-1).getFishingActivity() ==null ||
                fishingTripList.get(fishingTripList.size()-1).getFishingActivity().getFaReportDocument() ==null ||
                fishingTripList.get(fishingTripList.size()-1).getFishingActivity().getFaReportDocument().getVesselTransportMeans() ==null){
            return null;
        }
        int totalFishTripEntityCount=fishingTripList.size();
        FishingTripEntity fishingTripEntity=fishingTripList.get(totalFishTripEntityCount-1);
        VesselTransportMeansEntity vesselTransportMeansEntity=fishingTripEntity.getFishingActivity().getFaReportDocument().getVesselTransportMeans();
        return vesselTransportMeansEntity.getCountry();
    }

    protected Double getTotalDuration(List<FishingTripEntity> fishingTripList){
        if(CollectionUtils.isEmpty(fishingTripList) ){
            return new Double(0);
        }

        Double duration=new Double(0);
        Date startDate =getFishingTripDateTime(fishingTripList,FishingActivityTypeEnum.DEPARTURE.toString());
        Date endDate = getFishingTripDateTime(fishingTripList,FishingActivityTypeEnum.ARRIVAL.toString());

        if(startDate!=null && endDate!=null){
            duration = Double.valueOf(endDate.getTime() - startDate.getTime());
        }

        return duration;
    }

    protected int getNumberOfCorrections(List<FishingTripEntity> fishingTripList){
        if(CollectionUtils.isEmpty(fishingTripList) ){
            return 0;
        }

        int noOfCorrections=0;
        for(FishingTripEntity fishingTripEntity : fishingTripList){
               if(getCorrection(fishingTripEntity.getFishingActivity())){
                   noOfCorrections++;
               }
        }

        return noOfCorrections;
    }


}
