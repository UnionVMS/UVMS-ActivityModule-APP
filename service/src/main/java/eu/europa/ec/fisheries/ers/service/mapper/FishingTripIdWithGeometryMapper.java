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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
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
            if(SearchFilter.FLAG_STATE.equals(dto.getSearchFilter()) && dto.getVessel() != null) {
                fishingTripIdWithGeometry.setFlagState(dto.getVessel());
            } else {
                fishingTripIdWithGeometry.setFlagState( getFlagStateFromActivityList(fishingActivities) );
            }
        }
        fishingTripIdWithGeometry.setFirstFishingActivityDateTime( getFirstFishingActivityStartTime(fishingActivities) );
        fishingTripIdWithGeometry.setVesselIdLists( getVesselIdListsForFishingActivity(fishingActivities, dto) );
        fishingTripIdWithGeometry.setNoOfCorrections( getNumberOfCorrectionsForFishingActivities(fishingActivities) );
        fishingTripIdWithGeometry.setRelativeLastFaDateTime( getRelativeLastFishingActivityDateForTrip(fishingActivities) );
        fishingTripIdWithGeometry.setFirstFishingActivity( getFirstFishingActivityType(fishingActivities) );
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

        return null;
    }

    private String getFirstFishingActivityType(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(0) == null) {
            return null;
        }
        Optional<FishingActivityEntity> fishingActivityEntity = fishingActivities.stream().filter(s -> s.getCalculatedStartTime() != null).findFirst();

        if(!fishingActivityEntity.isPresent()){
            return fishingActivities.get(0).getTypeCode();
        }

        return fishingActivityEntity.get().getTypeCode();
    }

    private XMLGregorianCalendar getFirstFishingActivityStartTime(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(0) == null ) {
            return null;
        }
        Optional<FishingActivityEntity> fishingActivityEntity = fishingActivities.stream().filter(s -> s.getCalculatedStartTime() != null).findFirst();

        if(fishingActivityEntity.isPresent()){
            return convertToXMLGregorianCalendar(fishingActivityEntity.get().getCalculatedStartTime(), false);
        }


        return null;
    }

    private String getLastFishingActivityType(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1) == null) {
            return null;
        }


        List<FishingActivityEntity> fishingActivityEntities = fishingActivities.stream().filter(s -> s.getCalculatedStartTime() != null).collect(Collectors.toList());

        if(!fishingActivityEntities.isEmpty())
            return  fishingActivityEntities.get(fishingActivityEntities.size() - 1).getTypeCode();
        else {
            return fishingActivities.get(fishingActivities.size() - 1).getTypeCode();
        }
    }

    private XMLGregorianCalendar getLastFishingActivityStartTime(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1) == null ) {
            return null;
        }

        List<FishingActivityEntity> fishingActivityEntities = fishingActivities.stream().filter(s -> s.getCalculatedStartTime() != null).collect(Collectors.toList());

        if(!fishingActivityEntities.isEmpty()) {
            return convertToXMLGregorianCalendar(fishingActivityEntities.get(fishingActivityEntities.size() - 1).getCalculatedStartTime(), false);
        }
        return null;
    }

    private List<VesselIdentifierType> getVesselIdListsForFishingActivity(List<FishingActivityEntity> fishingActivities, FishingTripId dto) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.get(fishingActivities.size() - 1) == null || fishingActivities.get(fishingActivities.size() - 1).getFaReportDocument() == null || fishingActivities.get(fishingActivities.size() - 1).getFaReportDocument().getVesselTransportMeans() == null) {
            return Collections.emptyList();
        }

        FishingActivityEntity fishingActivityEntity;

        if (dto.getSearchFilter() != null && dto.getVessel() != null) {
            fishingActivityEntity = findActivityFromVessel(fishingActivities, dto);
        } else {
            fishingActivityEntity = fishingActivities.get(fishingActivities.size() - 1);
        }

        Set<VesselTransportMeansEntity> vesselTransportMeansEntityList = fishingActivityEntity.getFaReportDocument().getVesselTransportMeans();
        if (CollectionUtils.isEmpty(vesselTransportMeansEntityList) || CollectionUtils.isEmpty(vesselTransportMeansEntityList.iterator().next().getVesselIdentifiers())) {
            return Collections.emptyList();
        }

        List<VesselIdentifierType> vesselIdentifierTypes = new ArrayList<>();

        if (SearchFilter.CFR.equals(dto.getSearchFilter())) {
            VesselIdentifierType vesselIdentifierType = new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("CFR"), dto.getVessel());
            vesselIdentifierTypes.add(vesselIdentifierType);
        } else {
            Optional.ofNullable(fishingActivityEntity.getCfrAlt())
                    .map(cfr -> new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("CFR"), cfr))
                    .ifPresent(vesselIdentifierTypes::add);
        }

        if (SearchFilter.EXT_MARK.equals(dto.getSearchFilter())) {
            VesselIdentifierType vesselIdentifierType = new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("EXT_MARK"), dto.getVessel());
            vesselIdentifierTypes.add(vesselIdentifierType);
        } else {
            Optional.ofNullable(fishingActivityEntity.getExtMarkAlt())
                    .map(extMark -> new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("EXT_MARK"), extMark))
                    .ifPresent(vesselIdentifierTypes::add);
        }

        if (SearchFilter.ICCAT.equals(dto.getSearchFilter())) {
            VesselIdentifierType vesselIdentifierType = new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("ICCAT"), dto.getVessel());
            vesselIdentifierTypes.add(vesselIdentifierType);
        } else {
            Optional.ofNullable(fishingActivityEntity.getIccatAlt())
                    .map(iccat -> new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("ICCAT"), iccat))
                    .ifPresent(vesselIdentifierTypes::add);
        }

        if (SearchFilter.IRCS.equals(dto.getSearchFilter())) {
            VesselIdentifierType vesselIdentifierType = new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("IRCS"), dto.getVessel());
            vesselIdentifierTypes.add(vesselIdentifierType);
        } else {
            Optional.ofNullable(fishingActivityEntity.getIrcsAlt())
                    .map(ircs -> new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("IRCS"), ircs))
                    .ifPresent(vesselIdentifierTypes::add);
        }

        if (SearchFilter.UVI.equals(dto.getSearchFilter())) {
            VesselIdentifierType vesselIdentifierType = new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("UVI"), dto.getVessel());
            vesselIdentifierTypes.add(vesselIdentifierType);
        } else {
            Optional.ofNullable(fishingActivityEntity.getUviAlt())
                    .map(uvi -> new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("UVI"), uvi))
                    .ifPresent(vesselIdentifierTypes::add);
        }

        if (SearchFilter.GFCM.equals(dto.getSearchFilter())) {
            VesselIdentifierType vesselIdentifierType = new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("GFCM"), dto.getVessel());
            vesselIdentifierTypes.add(vesselIdentifierType);
        } else {
            Optional.ofNullable(fishingActivityEntity.getGfcmAlt())
                    .map(gfcm -> new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf("GFCM"), gfcm))
                    .ifPresent(vesselIdentifierTypes::add);
        }

        return vesselIdentifierTypes;
    }


    private FishingActivityEntity findActivityFromVessel(List<FishingActivityEntity> fishingActivities, FishingTripId dto) {
        FishingActivityEntity fishingActivityEntity;
        switch (dto.getSearchFilter()) {
            case CFR:
                fishingActivityEntity = fishingActivities.stream().filter(fa -> Objects.equals(dto.getVessel(),fa.getCfr())).findFirst().orElse(fishingActivities.get(fishingActivities.size() - 1));
                break;
            case IRCS:
                fishingActivityEntity = fishingActivities.stream().filter(fa -> Objects.equals(dto.getVessel(),fa.getIrcs())).findFirst().orElse(fishingActivities.get(fishingActivities.size() - 1));
                break;
            case UVI:
                fishingActivityEntity = fishingActivities.stream().filter(fa -> Objects.equals(dto.getVessel(),fa.getUvi())).findFirst().orElse(fishingActivities.get(fishingActivities.size() - 1));
                break;
            case EXT_MARK:
                fishingActivityEntity = fishingActivities.stream().filter(fa -> Objects.equals(dto.getVessel(),fa.getExtMark())).findFirst().orElse(fishingActivities.get(fishingActivities.size() - 1));
                break;
            case GFCM:
                fishingActivityEntity = fishingActivities.stream().filter(fa -> Objects.equals(dto.getVessel(),fa.getGfcm())).findFirst().orElse(fishingActivities.get(fishingActivities.size() - 1));
                break;
            case ICCAT:
                fishingActivityEntity = fishingActivities.stream().filter(fa -> Objects.equals(dto.getVessel(),fa.getIccat())).findFirst().orElse(fishingActivities.get(fishingActivities.size() - 1));
                break;
            default:
                fishingActivityEntity = fishingActivities.get(fishingActivities.size() - 1);
        }
        return fishingActivityEntity;
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
    public static Double getTotalTripDuration(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivities.size() == 1) {
            return 0d;
        }

        Date startDate = findFirstActivityDateForTrip(fishingActivities);
        Date endDate = findLastActivityDateForTrip(fishingActivities);


        if (startDate != null && endDate != null) {
           return (double) (endDate.getTime() - startDate.getTime());
        }

        return 0d;
    }



    private static Date findFirstActivityDateForTrip(List<FishingActivityEntity> fishingActivities){

        FishingActivityEntity fishingActivityEntity = getFirstDepartureForTrip(fishingActivities);
        if(fishingActivityEntity != null){
            return fishingActivityEntity.getCalculatedStartTime();
        }

        fishingActivityEntity = fishingActivities.stream()
                .filter(f -> f.getCalculatedStartTime() != null)
                .min(Comparator.comparing(FishingActivityEntity::getCalculatedStartTime))
                .orElse(null);

        return fishingActivityEntity == null ? null:fishingActivityEntity.getCalculatedStartTime();
    }

    private static Date findLastActivityDateForTrip(List<FishingActivityEntity> fishingActivities){
        FishingActivityEntity fishingActivityEntity = getLastArrivalForTrip(fishingActivities);

        if(fishingActivityEntity != null){
            return fishingActivityEntity.getCalculatedStartTime();
        }

        fishingActivityEntity = fishingActivities.stream()
                .filter(f -> f.getCalculatedStartTime() != null)
                .max(Comparator.comparing(FishingActivityEntity::getCalculatedStartTime))
                .orElse(null);

        return fishingActivityEntity == null ? null: fishingActivityEntity.getCalculatedStartTime();
    }

    public static FishingActivityEntity getFirstDepartureForTrip(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }
        return fishingActivities.stream()
                .filter(fa -> FishingActivityTypeEnum.DEPARTURE.toString().equals(fa.getTypeCode()))
                .filter(f -> f.getCalculatedStartTime() != null)
                .min(Comparator.comparing(FishingActivityEntity::getCalculatedStartTime))
                .orElse(null);
    }

    public static List<FishingActivityEntity> getTranshipmentsForTrip(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return new ArrayList<>();
        }
        return fishingActivities.stream()
                .filter(fa -> FishingActivityTypeEnum.TRANSHIPMENT.toString().equals(fa.getTypeCode()))
                .filter(f -> f.getCalculatedStartTime() != null)
                .collect(Collectors.toList());
    }

    public static List<FishingActivityEntity> getLandingsForTrip(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return new ArrayList<>();
        }
        return fishingActivities.stream()
                .filter(fa -> FishingActivityTypeEnum.LANDING.toString().equals(fa.getTypeCode()))
                .filter(f -> f.getCalculatedStartTime() != null)
                .collect(Collectors.toList());
    }

    private static FishingActivityEntity getLastArrivalForTrip(List<FishingActivityEntity> fishingActivities) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return null;
        }

        return fishingActivities.stream()
                .filter(fa -> FishingActivityTypeEnum.ARRIVAL.toString().equals(fa.getTypeCode()))
                .filter(fa -> fa.getFaReportDocument()!= null && fa.getFaReportDocument().getTypeCode() != null)
                .filter(fa -> "DECLARATION".equals(fa.getFaReportDocument().getTypeCode()))
                .filter(f -> f.getCalculatedStartTime() != null)
                .max(Comparator.comparing(FishingActivityEntity::getCalculatedStartTime))
                .orElse(null);
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