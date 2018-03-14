/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierType;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.model.StringWrapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Created by sanera on 02/12/2016.
 */
@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
@Slf4j
public abstract class FishingTripIdWithGeometryMapper extends BaseMapper {
    public static final FishingTripIdWithGeometryMapper INSTANCE = Mappers.getMapper(FishingTripIdWithGeometryMapper.class);

    @Mappings({
            @Mapping(target = "tripId", source = "dto.tripId"),
            @Mapping(target = "firstFishingActivity", expression = "java(getFirstFishingActivityType(fishingActivities))"),
            @Mapping(target = "firstFishingActivityDateTime", expression = "java(getFirstFishingActivityStartTime(fishingActivities))"),
            @Mapping(target = "lastFishingActivity", expression = "java(getLastFishingActivityType(fishingActivities))"),
            @Mapping(target = "lastFishingActivityDateTime", expression = "java(getLastFishingActivityStartTime(fishingActivities))"),
            @Mapping(target = "vesselIdLists", expression = "java(getVesselIdListsForFishingActivity(fishingActivities))"),
            @Mapping(target = "flagState", expression = "java(getFlagStateFromActivityList(fishingActivities))"),
            @Mapping(target = "noOfCorrections", expression = "java(getNumberOfCorrectionsForFishingActivities(fishingActivities))"),
            @Mapping(target = "tripDuration", expression = "java(getTotalTripDuration(fishingActivities))"),
            @Mapping(target = "schemeId", source = "dto.schemeID"),
            @Mapping(target = "relativeFirstFaDateTime", expression = "java(getRelativeFirstFishingActivityDateForTrip(fishingActivities))"),
            @Mapping(target = "relativeLastFaDateTime", expression = "java(getRelativeLastFishingActivityDateForTrip(fishingActivities))"),
            @Mapping(target = "geometry", expression = "java(getGeometryMultiPointForAllFishingActivities(fishingActivities))")})
    public abstract FishingTripIdWithGeometry mapToFishingTripIdWithDetails(FishingTripId dto, List<FishingActivityEntity> fishingActivities);

    protected String getGeometryMultiPointForAllFishingActivities(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(0) == null) {
            return null;
        }

        String GeometryWkt = null;
        List<Geometry> activityGeomList = new ArrayList<>();
        for (FishingActivityEntity fishingActivityEntity : fishingActivities) {
            if (fishingActivityEntity.getGeom() != null) {
                activityGeomList.add(fishingActivityEntity.getGeom());
            }
        }

        if (CollectionUtils.isNotEmpty(activityGeomList)) {
            Geometry geometry = GeometryUtils.createMultipoint(activityGeomList);
            StringWrapper stringWrapper = GeometryMapper.INSTANCE.geometryToWkt(geometry);
            if (stringWrapper != null) {
                return stringWrapper.getValue();
            }
        }

        return GeometryWkt;
    }

    protected String getFirstFishingActivityType(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(0) == null) {
            return null;
        }
        return fishingActivities.get(0).getTypeCode();
    }

    protected XMLGregorianCalendar getFirstFishingActivityStartTime(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(0) == null || fishingActivities.get(0).getCalculatedStartTime() == null) {
            return null;
        }

        return convertToXMLGregorianCalendar(fishingActivities.get(0).getCalculatedStartTime(), false);
    }

    protected String getLastFishingActivityType(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1) == null) {
            return null;
        }
        int totalFishingActivityCount = fishingActivities.size();
        return fishingActivities.get(totalFishingActivityCount - 1).getTypeCode();
    }

    protected XMLGregorianCalendar getLastFishingActivityStartTime(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1) == null || fishingActivities.get(fishingActivities.size() - 1).getCalculatedStartTime() == null) {
            return null;
        }
        int totalFishingActivityCount = fishingActivities.size();
        return convertToXMLGregorianCalendar(fishingActivities.get(totalFishingActivityCount - 1).getCalculatedStartTime(), false);
    }

    protected List<VesselIdentifierType> getVesselIdListsForFishingActivity(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1) == null || fishingActivities.get(fishingActivities.size() - 1).getFaReportDocument() == null || fishingActivities.get(fishingActivities.size() - 1).getFaReportDocument().getVesselTransportMeans() == null) {
            return Collections.emptyList();
        }
        int totalFishingActivityCount = fishingActivities.size();
        FishingActivityEntity fishingActivityEntity = fishingActivities.get(totalFishingActivityCount - 1);
        Set<VesselTransportMeansEntity> vesselTransportMeansEntityList = fishingActivityEntity.getFaReportDocument().getVesselTransportMeans();
        if (CollectionUtils.isEmpty(vesselTransportMeansEntityList) || CollectionUtils.isEmpty(vesselTransportMeansEntityList.iterator().next().getVesselIdentifiers())) {
            return Collections.emptyList();
        }
        Set<VesselIdentifierEntity> vesselIdentifierEntities = vesselTransportMeansEntityList.iterator().next().getVesselIdentifiers();
        List<VesselIdentifierType> vesselIdentifierTypes = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(vesselIdentifierEntities)) {
            for (VesselIdentifierEntity vesselIdentifierEntity : vesselIdentifierEntities) {
                VesselIdentifierType vesselIdentifierType = new VesselIdentifierType();
                vesselIdentifierType.setKey(VesselIdentifierSchemeIdEnum.valueOf(vesselIdentifierEntity.getVesselIdentifierSchemeId()));
                vesselIdentifierType.setValue(vesselIdentifierEntity.getVesselIdentifierId());
                vesselIdentifierTypes.add(vesselIdentifierType);
            }
        }

        return vesselIdentifierTypes;
    }

    protected String getFlagStateFromActivityList(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1) == null || fishingActivities.get(fishingActivities.size() - 1).getFaReportDocument() == null || fishingActivities.get(fishingActivities.size() - 1).getFaReportDocument().getVesselTransportMeans() == null) {
            return null;
        }
        int totalFishingActivityCount = fishingActivities.size();
        FishingActivityEntity fishingActivityEntity = fishingActivities.get(totalFishingActivityCount - 1);
        Set<VesselTransportMeansEntity> vesselTransportMeansEntityList = fishingActivityEntity.getFaReportDocument().getVesselTransportMeans();
        if (CollectionUtils.isEmpty(vesselTransportMeansEntityList)) {
            return null;
        }
        return vesselTransportMeansEntityList.iterator().next().getCountry();
    }

    protected int getNumberOfCorrectionsForFishingActivities(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return 0;
        }
        int noOfCorrections = 0;
        for (FishingActivityEntity fishingActivityEntity : fishingActivities) {
            if (getCorrection(fishingActivityEntity)) {
                noOfCorrections++;
            }
        }
        return noOfCorrections;
    }

    /*
        Calculate trip duration from all the activities happened during the trip
        if we have only start date received, we will subtract it from current date
     */
    protected Double getTotalTripDuration(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return new Double(0);
        }

        Double duration = new Double(0);
        Date startDate = getFishingTripDateTimeFromFishingActivities(fishingActivities, FishingActivityTypeEnum.DEPARTURE.toString());
        Date endDate = getFishingTripDateTimeFromFishingActivities(fishingActivities, FishingActivityTypeEnum.ARRIVAL.toString());

        Date currentDate = new Date();
        if (startDate != null && endDate != null) {
            duration = Double.valueOf(endDate.getTime() - startDate.getTime());
        } else if (endDate == null && startDate != null) { // received null means no ARRIVAL yet received for the trip

            log.info("ARRIVAL is not yet received for the trip");

            // find out date of last activity for the trip
            int fishingActivityCount = fishingActivities.size();
            Date lastActivityDate = fishingActivities.get(fishingActivityCount - 1).getCalculatedStartTime();
            if (lastActivityDate != null && lastActivityDate.compareTo(startDate) > 0) { // If last activity date is later than start date
                duration = Double.valueOf(lastActivityDate.getTime() - startDate.getTime());
            } else if (currentDate.compareTo(startDate) > 0) {// if not, then compare with current date
                duration = Double.valueOf(currentDate.getTime() - startDate.getTime());
            }
        }

        return duration; // duration returned is in miliseconds
    }

    protected XMLGregorianCalendar getRelativeFirstFishingActivityDateForTrip(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }
        Date tripStartDate = getFishingTripDateTimeFromFishingActivities(fishingActivities, FishingActivityTypeEnum.DEPARTURE.toString());
        if (tripStartDate == null) return null;

        return convertToXMLGregorianCalendar(tripStartDate, false);
    }

    protected XMLGregorianCalendar getRelativeLastFishingActivityDateForTrip(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }
        Date tripEndDate = getFishingTripDateTimeFromFishingActivities(fishingActivities, FishingActivityTypeEnum.ARRIVAL.toString());
        if (tripEndDate == null) return null;

        return convertToXMLGregorianCalendar(tripEndDate, false);
    }
}