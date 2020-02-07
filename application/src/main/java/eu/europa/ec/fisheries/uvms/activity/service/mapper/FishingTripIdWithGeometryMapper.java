/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierType;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import eu.europa.ec.fisheries.uvms.activity.service.util.GeomUtil;

import static eu.europa.ec.fisheries.uvms.activity.service.util.GeomUtil.createMultipoint;

@Slf4j
@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FishingTripIdWithGeometryMapper extends BaseMapper {

    @Inject
    FishingTripIdWithGeometryMapper fishingTripIdWithGeometryMapper;

    public FishingTripIdWithGeometry mapToFishingTripIdWithDetails(FishingTripId dto, List<FishingActivityEntity> fishingActivities) {
        FishingTripIdWithGeometry fishingTripIdWithGeometry = new FishingTripIdWithGeometry();

        if ( dto != null ) {
            fishingTripIdWithGeometry.setTripId( dto.getTripId() );
            fishingTripIdWithGeometry.setSchemeId( dto.getSchemeID() );
        }
        fishingTripIdWithGeometry.setFirstFishingActivityDateTime( getFirstFishingActivityStartTime(fishingActivities) );
        fishingTripIdWithGeometry.setVesselIdLists( getVesselIdListsForFishingActivity(fishingActivities) );
        fishingTripIdWithGeometry.setNoOfCorrections( getNumberOfCorrectionsForFishingActivities(fishingActivities) );
        fishingTripIdWithGeometry.setRelativeLastFaDateTime( getRelativeLastFishingActivityDateForTrip(fishingActivities) );
        fishingTripIdWithGeometry.setFirstFishingActivity( getFirstFishingActivityType(fishingActivities) );
        fishingTripIdWithGeometry.setFlagState( getFlagStateFromActivityList(fishingActivities) );
        fishingTripIdWithGeometry.setLastFishingActivity( getLastFishingActivityType(fishingActivities) );
        fishingTripIdWithGeometry.setRelativeFirstFaDateTime( getRelativeFirstFishingActivityDateForTrip(fishingActivities) );
        fishingTripIdWithGeometry.setGeometry( getGeometryMultiPointForAllFishingActivities(fishingActivities) );
        fishingTripIdWithGeometry.setTripDuration( getTotalTripDuration(fishingActivities).getSeconds() );
        fishingTripIdWithGeometry.setLastFishingActivityDateTime( getLastFishingActivityStartTime(fishingActivities) );

        return fishingTripIdWithGeometry;
    }

    private String getGeometryMultiPointForAllFishingActivities(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }

        List<Geometry> activityGeomList = new ArrayList<>();
        for (FishingActivityEntity fishingActivityEntity : fishingActivities) {
            if (fishingActivityEntity.getGeom() != null) {
                activityGeomList.add(fishingActivityEntity.getGeom());
            }
        }

        if (CollectionUtils.isNotEmpty(activityGeomList)) {
            Geometry geometry = createMultipoint(activityGeomList);
            return new WKTWriter().write(geometry);
        }

        return null;
    }

    private String getFirstFishingActivityType(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }
        return fishingActivities.get(0).getTypeCode();
    }

    private XMLGregorianCalendar getFirstFishingActivityStartTime(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(0).getCalculatedStartTime() == null) {
            return null;
        }

        return convertToXMLGregorianCalendar(fishingActivities.get(0).getCalculatedStartTime());
    }

    private String getLastFishingActivityType(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }
        int totalFishingActivityCount = fishingActivities.size();
        return fishingActivities.get(totalFishingActivityCount - 1).getTypeCode();
    }

    private XMLGregorianCalendar getLastFishingActivityStartTime(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1).getCalculatedStartTime() == null) {
            return null;
        }
        int totalFishingActivityCount = fishingActivities.size();
        return convertToXMLGregorianCalendar(fishingActivities.get(totalFishingActivityCount - 1).getCalculatedStartTime());
    }

    private List<VesselIdentifierType> getVesselIdListsForFishingActivity(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1).getFaReportDocument() == null || fishingActivities.get(fishingActivities.size() - 1).getFaReportDocument().getVesselTransportMeans() == null) {
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

        for (VesselIdentifierEntity vesselIdentifierEntity : Utils.safeIterable(vesselIdentifierEntities)) {
            VesselIdentifierType vesselIdentifierType = new VesselIdentifierType();
            vesselIdentifierType.setKey(VesselIdentifierSchemeIdEnum.valueOf(vesselIdentifierEntity.getVesselIdentifierSchemeId()));
            vesselIdentifierType.setValue(vesselIdentifierEntity.getVesselIdentifierId());
            vesselIdentifierTypes.add(vesselIdentifierType);
        }

        return vesselIdentifierTypes;
    }

    private String getFlagStateFromActivityList(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1).getFaReportDocument() == null || fishingActivities.get(fishingActivities.size() - 1).getFaReportDocument().getVesselTransportMeans() == null) {
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

    private int getNumberOfCorrectionsForFishingActivities(List<FishingActivityEntity> fishingActivities) {
        int noOfCorrections = 0;
        for (FishingActivityEntity fishingActivityEntity : fishingActivities) {
            if (BaseUtil.getCorrection(fishingActivityEntity)) {
                noOfCorrections++;
            }
        }
        return noOfCorrections;
    }

    /*
        Calculate trip value from all the activities happened during the trip
        if we have only start date received, we will subtract it from current date
     */
    private Duration getTotalTripDuration(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return Duration.ZERO;
        }

        Optional<Instant> optionalStartDate = getCalculatedStartTimeForFishingActivity(fishingActivities, FishingActivityTypeEnum.DEPARTURE);
        Optional<Instant> optionalEndDate = getCalculatedStartTimeForFishingActivity(fishingActivities, FishingActivityTypeEnum.ARRIVAL);

        if (!optionalStartDate.isPresent()) {
            return Duration.ZERO;
        }

        Instant startDate = optionalStartDate.get();

        if (optionalEndDate.isPresent()) {
            return Duration.between(startDate, optionalEndDate.get());
        } else {
            log.info("ARRIVAL is not yet received for the trip");

            // find out date of last activity for the trip
            Instant lastActivity = fishingActivities.get(fishingActivities.size() - 1).getCalculatedStartTime();
            if (lastActivity != null) {
                if (lastActivity.isAfter(startDate)) { // If last activity date is later than start date
                    return Duration.between(startDate, lastActivity);
                }
            }
        }

        if (startDate.isBefore(Instant.now())) {
            return Duration.between(startDate, Instant.now());
        }

        return Duration.ZERO;
    }

    private XMLGregorianCalendar getRelativeFirstFishingActivityDateForTrip(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }

        Optional<Instant> tripStartDate = getCalculatedStartTimeForFishingActivity(fishingActivities, FishingActivityTypeEnum.DEPARTURE);
        return tripStartDate.map(fishingTripIdWithGeometryMapper::convertToXMLGregorianCalendar).orElse(null);

    }

    private XMLGregorianCalendar getRelativeLastFishingActivityDateForTrip(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }

        Optional<Instant> tripEndDate = getCalculatedStartTimeForFishingActivity(fishingActivities, FishingActivityTypeEnum.ARRIVAL);
        return tripEndDate.map(fishingTripIdWithGeometryMapper::convertToXMLGregorianCalendar).orElse(null);

    }

    /**
     * Get first "Calculated Start Date" found for a fishing activity matching the wanted type
     */
    private Optional<Instant> getCalculatedStartTimeForFishingActivity(List<FishingActivityEntity> fishingActivities, FishingActivityTypeEnum wantedType) {
        String activityTypeAsString = wantedType.name();
        for (FishingActivityEntity fishingActivityEntity : fishingActivities) {
            if (fishingActivityEntity == null) {
                continue;
            }

            if (activityTypeAsString.equals(fishingActivityEntity.getTypeCode()) && fishingActivityEntity.getCalculatedStartTime() != null) {
                return Optional.of(fishingActivityEntity.getCalculatedStartTime());
            }
        }

        return Optional.empty();
    }

    private XMLGregorianCalendar convertToXMLGregorianCalendar(Instant dateTime) {
        XMLGregorianCalendar calendar = null;
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(dateTime.toEpochMilli());
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            log.error(e.getMessage(), e);
        }
        return calendar;
    }
}
