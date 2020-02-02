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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collection;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierType;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.model.StringWrapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.*;

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

    public final void populateGeometryFromAssetModule( List<FishingTripIdWithGeometry> fishingTripIdLists , AssetModuleService assetModuleService) {
        Set<String> cfrSet = fishingTripIdLists.stream()
                .map(FishingTripIdWithGeometry::getVesselIdLists)
                .flatMap(List::stream)
                .filter(k -> isCFR(k.getKey().name()))
                .map(VesselIdentifierType::getValue)
                .collect(Collectors.toSet());
        Set<Asset> assets = retrieveVesselIdentifiersFromAssetModule(cfrSet,assetModuleService);

        fishingTripIdLists.forEach(t -> {
            Optional<VesselIdentifierType> cfrIdentifier = t.getVesselIdLists().stream().filter(r -> isCFR(r.getKey().name())).findFirst();
            if(!cfrIdentifier.isPresent()){
                return;
            }

            List<VesselIdentifierType> vesselIdLists = t.getVesselIdLists();
            Set<VesselIdentifierSchemeIdEnum> presentIdentifierEnums = vesselIdLists.stream().map(VesselIdentifierType::getKey).collect(Collectors.toSet());

            Stream.of(VesselIdentifierSchemeIdEnum.values())
                    .filter(s -> !presentIdentifierEnums.contains(s))
                    .forEach(s -> {
                        assets.stream().filter(a -> cfrIdentifier.get().getValue().equals(a.getCfr())).findFirst().ifPresent(asset -> {
                            switch (s) {
                                case UVI:
                                    addVesselIdentifierType(vesselIdLists,asset.getUvi(),UVI);
                                    break;
                                case GFCM:
                                    addVesselIdentifierType(vesselIdLists,asset.getGfcm(),GFCM);
                                    break;
                                case EXT_MARK:
                                    addVesselIdentifierType(vesselIdLists,asset.getExternalMarking(),EXT_MARK);
                                    break;
                                case IRCS:
                                    addVesselIdentifierType(vesselIdLists,asset.getIrcs(),IRCS);
                                    break;
                                case ICCAT:
                                    addVesselIdentifierType(vesselIdLists,asset.getIccat(),ICCAT);
                                    break;
                            }
                        });
                    });
            t.setVesselIdLists(vesselIdLists);
        });
    }

    private void addVesselIdentifierType(List<VesselIdentifierType> vesselIdLists,String identifier,VesselIdentifierSchemeIdEnum schemeIdEnum) {
        if(identifier !=null) {
            vesselIdLists.add(new VesselIdentifierType(schemeIdEnum, identifier));
        }
    }

    public final Set<Asset> retrieveVesselIdentifiersFromAssetModule(Set<String> cfrIdentifier, AssetModuleService assetModuleService )  {
        List<AssetListQuery> cfrIdentifierSet = cfrIdentifier.stream().map(this::prepareAssetModuleQuery).distinct().collect(Collectors.toList());

        try {
            List<BatchAssetListResponseElement> assetListResponseBatch = assetModuleService.getAssetListResponseBatch(cfrIdentifierSet);
            List<Asset> assetList = assetListResponseBatch
                    .stream()
                    .map(BatchAssetListResponseElement::getAsset)
                    .filter(e -> e != null)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            return Collections.unmodifiableSet(new HashSet<>(assetList));
        } catch (Exception ex) {
            log.info("Could not reach asset module",ex);
            return Collections.emptySet();
        }
    }

    private AssetListQuery prepareAssetModuleQuery(String cfrIdentifier){
        AssetListQuery query = new AssetListQuery();

        AssetListPagination listPagination = new AssetListPagination();
        listPagination.setListSize(25);
        listPagination.setPage(1);
        query.setPagination(listPagination);

        AssetListCriteria listCriteria = new AssetListCriteria();
        listCriteria.setIsDynamic(false);

        List<AssetListCriteriaPair>  assetCriteriaPairList = new ArrayList<>();
        AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
        assetListCriteriaPair.setKey(ConfigSearchField.CFR);
        assetListCriteriaPair.setValue(cfrIdentifier);
        assetCriteriaPairList.add(assetListCriteriaPair);
        listCriteria.getCriterias().addAll(assetCriteriaPairList);
        query.setAssetSearchCriteria(listCriteria);
        return query;
    }

    public boolean isCFR(String param){
        return VesselIdentifierSchemeIdEnum.CFR.equals( VesselIdentifierSchemeIdEnum.valueOf(param));
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