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
import eu.europa.ec.fisheries.ers.service.util.Utils;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierType;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.model.StringWrapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

@Slf4j
public class FishingTripIdWithGeometryMapper extends BaseMapper {

    public FishingTripIdWithGeometry mapToFishingTripIdWithDetails(FishingTripId dto, List<FishingActivityEntity> fishingActivities) {
        if ( dto == null && fishingActivities == null ) {
            return null;
        }

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
        fishingTripIdWithGeometry.setTripDuration( getTotalTripDuration(fishingActivities) );
        fishingTripIdWithGeometry.setLastFishingActivityDateTime( getLastFishingActivityStartTime(fishingActivities) );

        return fishingTripIdWithGeometry;
    }

    private String getGeometryMultiPointForAllFishingActivities(List<FishingActivityEntity> fishingActivities) {
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

    private String getFirstFishingActivityType(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(0) == null) {
            return null;
        }
        return fishingActivities.get(0).getTypeCode();
    }

    private XMLGregorianCalendar getFirstFishingActivityStartTime(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(0) == null || fishingActivities.get(0).getCalculatedStartTime() == null) {
            return null;
        }

        return convertToXMLGregorianCalendar(fishingActivities.get(0).getCalculatedStartTime(), false);
    }

    private String getLastFishingActivityType(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1) == null) {
            return null;
        }
        int totalFishingActivityCount = fishingActivities.size();
        return fishingActivities.get(totalFishingActivityCount - 1).getTypeCode();
    }

    private XMLGregorianCalendar getLastFishingActivityStartTime(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1) == null || fishingActivities.get(fishingActivities.size() - 1).getCalculatedStartTime() == null) {
            return null;
        }
        int totalFishingActivityCount = fishingActivities.size();
        return convertToXMLGregorianCalendar(fishingActivities.get(totalFishingActivityCount - 1).getCalculatedStartTime(), false);
    }

    private List<VesselIdentifierType> getVesselIdListsForFishingActivity(List<FishingActivityEntity> fishingActivities) {
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

        for (VesselIdentifierEntity vesselIdentifierEntity : Utils.safeIterable(vesselIdentifierEntities)) {
            VesselIdentifierType vesselIdentifierType = new VesselIdentifierType();
            vesselIdentifierType.setKey(VesselIdentifierSchemeIdEnum.valueOf(vesselIdentifierEntity.getVesselIdentifierSchemeId()));
            vesselIdentifierType.setValue(vesselIdentifierEntity.getVesselIdentifierId());
            vesselIdentifierTypes.add(vesselIdentifierType);
        }

        return vesselIdentifierTypes;
    }

    private String getFlagStateFromActivityList(List<FishingActivityEntity> fishingActivities) {
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

    private int getNumberOfCorrectionsForFishingActivities(List<FishingActivityEntity> fishingActivities) {
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
        Calculate trip value from all the activities happened during the trip
        if we have only start date received, we will subtract it from current date
     */
    private Double getTotalTripDuration(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return 0d;
        }

        Double duration = 0d;
        Date startDate = getFishingTripDateTimeFromFishingActivities(fishingActivities, FishingActivityTypeEnum.DEPARTURE.toString());
        Date endDate = getFishingTripDateTimeFromFishingActivities(fishingActivities, FishingActivityTypeEnum.ARRIVAL.toString());

        Date currentDate = new Date();
        if (startDate != null && endDate != null) {
            duration = (double) (endDate.getTime() - startDate.getTime());
        } else if (endDate == null && startDate != null) { // received null means no ARRIVAL yet received for the trip

            log.info("ARRIVAL is not yet received for the trip");

            // find out date of last activity for the trip
            int fishingActivityCount = fishingActivities.size();
            Date lastActivityDate = fishingActivities.get(fishingActivityCount - 1).getCalculatedStartTime();
            if (lastActivityDate != null && lastActivityDate.compareTo(startDate) > 0) { // If last activity date is later than start date
                duration = (double) (lastActivityDate.getTime() - startDate.getTime());
            } else if (currentDate.compareTo(startDate) > 0) {// if not, then compare with current date
                duration = (double) (currentDate.getTime() - startDate.getTime());
            }
        }

        return duration; // value returned is in miliseconds
    }

    private XMLGregorianCalendar getRelativeFirstFishingActivityDateForTrip(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }
        Date tripStartDate = getFishingTripDateTimeFromFishingActivities(fishingActivities, FishingActivityTypeEnum.DEPARTURE.toString());
        if (tripStartDate == null) return null;

        return convertToXMLGregorianCalendar(tripStartDate, false);
    }

    private XMLGregorianCalendar getRelativeLastFishingActivityDateForTrip(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }
        Date tripEndDate = getFishingTripDateTimeFromFishingActivities(fishingActivities, FishingActivityTypeEnum.ARRIVAL.toString());
        if (tripEndDate == null) return null;

        return convertToXMLGregorianCalendar(tripEndDate, false);
    }
}