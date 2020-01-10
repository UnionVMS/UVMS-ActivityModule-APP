/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.bean;

import com.google.common.collect.ImmutableMap;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.MicroMovementComparator;
import eu.europa.ec.fisheries.uvms.activity.service.AssetModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.FluxMessageService;
import eu.europa.ec.fisheries.uvms.activity.service.MdrModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.MovementModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.SpatialModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FluxFaReportMessageMapper;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.movement.client.model.MicroMovement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.linearref.LengthIndexedLine;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Stateless
@Transactional
@Slf4j
public class FluxMessageServiceBean extends BaseActivityBean implements FluxMessageService {

    private static final String PREVIOUS = "PREVIOUS";
    private static final String NEXT = "NEXT";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";

    private FaReportDocumentDao faReportDocumentDao;

    @EJB
    private MovementModuleService movementModule;

    @EJB
    private AssetModuleService assetService;

    @EJB
    private MdrModuleService mdrModuleServiceBean;

    @EJB
    private SpatialModuleService spatialModuleService;

    @EJB
    private FaMessageSaverBean faMessageSaverBean;

    private GeometryFactory geometryFactory = new GeometryFactory();

    private FluxFaReportMessageMapper fluxFaReportMessageMapper = new FluxFaReportMessageMapper();

    @PostConstruct
    public void init() {
        faReportDocumentDao = new FaReportDocumentDao(entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FluxFaReportMessageEntity saveFishingActivityReportDocuments(FLUXFAReportMessage faReportMessage, FaReportSourceEnum faReportSourceEnum) throws ServiceException {
        log.info("[START] Going to save [{}] FaReportDocuments.", faReportMessage.getFAReportDocuments().size());
        FluxFaReportMessageEntity messageEntity = fluxFaReportMessageMapper.mapToFluxFaReportMessage(faReportMessage, faReportSourceEnum);
        final Set<FaReportDocumentEntity> faReportDocuments = messageEntity.getFaReportDocuments();
        for (FaReportDocumentEntity faReportDocument : faReportDocuments) {
            try {
                updateGeometry(faReportDocument);
                enrichFishingActivityWithGuiID(faReportDocument);
            } catch (Exception e) {
                log.error("Could not update Geometry OR enrichActivities for faReportDocument: {}", faReportDocument.getId());
            }
        }
        FluxFaReportMessageEntity entity = faMessageSaverBean.saveReportMessageNow(messageEntity);
        log.debug("Saved partial FluxFaReportMessage before further processing");
        updateFaReportCorrectionsOrCancellations(entity.getFaReportDocuments());
        log.debug("Updating FaReport Corrections is complete.");
        updateFishingTripStartAndEndDate(entity.getFaReportDocuments());
        log.info("[END] FluxFaReportMessage Saved successfully.");
        return entity;
    }

    /**
     * This method will traverse through all the FishingTrips mentioned in the FaReportDocument and
     * update start and end date for the trip based on the fishing activities reported for the trip
     *
     * @param faReportDocument
     */
    private void calculateFishingTripStartAndEndDate(FaReportDocumentEntity faReportDocument) {
        Set<FishingActivityEntity> fishingActivities = faReportDocument.getFishingActivities();
        if (CollectionUtils.isEmpty(fishingActivities)) {
            log.error("Could not find FishingActivities for faReportDocument.");
            return;
        }

        for (FishingActivityEntity fishingActivityEntity : fishingActivities) {
            setTripStartAndEndDateForFishingTrip(fishingActivityEntity);
        }
    }

    private void setTripStartAndEndDateForFishingTrip(FishingActivityEntity fishingActivityEntity) {
        FishingTripEntity fishingTripEntity = fishingActivityEntity.getFishingTrip();

        Instant calculatedTripStartDate = fishingTripEntity.getCalculatedTripStartDate();
        Instant calculatedTripEndDate = fishingTripEntity.getCalculatedTripEndDate();

        Instant activityTime = fishingActivityEntity.getCalculatedStartTime();

        if (calculatedTripStartDate == null) {
            fishingTripEntity.setCalculatedTripStartDate(activityTime);
        }

        if (calculatedTripEndDate == null) {
            fishingTripEntity.setCalculatedTripEndDate(activityTime);
        }

        if (calculatedTripStartDate != null && activityTime != null && activityTime.isBefore(calculatedTripStartDate)) {
            fishingTripEntity.setCalculatedTripStartDate(activityTime);
        }

        if (calculatedTripEndDate != null && activityTime != null && activityTime.isAfter(calculatedTripEndDate)) {
            fishingTripEntity.setCalculatedTripEndDate(activityTime);
        }
    }

    private void enrichFishingActivityWithGuiID(FaReportDocumentEntity faReportDocument) {
        for (VesselTransportMeansEntity vesselTransportMeansEntity : Utils.safeIterable(faReportDocument.getVesselTransportMeans())) {
            enrichWithGuidFromAssets(vesselTransportMeansEntity);
            vesselTransportMeansEntity.setFaReportDocument(faReportDocument);
        }

        Set<FishingActivityEntity> fishingActivities = faReportDocument.getFishingActivities();
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return;
        }
        for (FishingActivityEntity fishingActivityEntity : fishingActivities) {
            enrichFishingActivityVesselWithGuiId(fishingActivityEntity);
            if (fishingActivityEntity.getRelatedFishingActivity() != null)
                enrichFishingActivityVesselWithGuiId(fishingActivityEntity.getRelatedFishingActivity());
        }
    }

    private void enrichFishingActivityVesselWithGuiId(FishingActivityEntity fishingActivityEntity) {
        Set<VesselTransportMeansEntity> vesselTransportMeansEntityList = fishingActivityEntity.getVesselTransportMeans();
        if (CollectionUtils.isEmpty(vesselTransportMeansEntityList)) {
            return;
        }

        for (VesselTransportMeansEntity entity : vesselTransportMeansEntityList) {
            enrichWithGuidFromAssets(entity);
            fishingActivityEntity.setVesselTransportGuid(entity.getGuid());
        }
    }

    /**
     * This method enriches the VesselTransportMeansEntity we got from FLUX with the related GUIDs.
     *
     * @param
     */
    private void enrichWithGuidFromAssets(VesselTransportMeansEntity vesselTransport) {
        try {
            List<String> guids = assetService.getAssetGuids(vesselTransport.getVesselIdentifiers());
            if (CollectionUtils.isNotEmpty(guids)) {
                vesselTransport.setGuid(guids.get(0));
            }
        } catch (ServiceException e) {
            log.error("Error while trying to get guids from Assets Module", e);
        }
    }


    /**
     * If the reference Id (of the received report) exist for any of the FaReport Document, than it means that this is an update to an existing report.
     */
    private void updateFaReportCorrectionsOrCancellations(Set<FaReportDocumentEntity> justReceivedAndSavedFaReports) {
        for (FaReportDocumentEntity justSavedReport : justReceivedAndSavedFaReports) {
            FluxReportDocumentEntity justSavedFluxReport = justSavedReport.getFluxReportDocument();
            String receivedRefId = justSavedFluxReport.getReferenceId();
            String receivedRefSchemeId = justSavedFluxReport.getReferenceSchemeId();
            String justSavedPurposeCode = StringUtils.isNotEmpty(justSavedFluxReport.getPurposeCode()) ? justSavedFluxReport.getPurposeCode() : StringUtils.EMPTY;

            // If we received an original report we have to check if we have previously received a correction/deletion/cancellation related to it.
            if (FaReportStatusType.NEW.getPurposeCode().toString().equals(justSavedPurposeCode)) {
                FluxReportIdentifierEntity faReportIdentifier = justSavedReport.getFluxReportDocument().getFluxReportIdentifiers().iterator().next();
                FaReportDocumentEntity foundRelatedFaReportCorrOrDelOrCanc = faReportDocumentDao.findFaReportByRefIdAndRefScheme(faReportIdentifier.getFluxReportIdentifierId(), faReportIdentifier.getFluxReportIdentifierSchemeId());

                if (foundRelatedFaReportCorrOrDelOrCanc != null) {
                    String purposeCodeFromDb = foundRelatedFaReportCorrOrDelOrCanc.getFluxReportDocument().getPurposeCode();
                    FaReportStatusType faReportStatusEnumFromDb = FaReportStatusType.getFaReportStatusEnum(Integer.parseInt(purposeCodeFromDb));
                    FaReportDocumentEntity persistentFaDoc = faReportDocumentDao.findEntityById(FaReportDocumentEntity.class, justSavedReport.getId());
                    persistentFaDoc.setStatus(faReportStatusEnumFromDb.name());
                    checkAndUpdateActivitiesForCorrectionsAndCancellationsAndDeletions(persistentFaDoc, faReportStatusEnumFromDb, foundRelatedFaReportCorrOrDelOrCanc.getId());
                }
            }

            // If it has a refId it means that it is a correction/deletion or cancellation message, so we should update the referred entity STATUS (FaReportDocument)..
            if (StringUtils.isNotEmpty(receivedRefId) && !StringUtils.EMPTY.equals(justSavedPurposeCode)) {
                // Get the document(s) that have the same id as the just received msg's ReferenceId.
                FaReportDocumentEntity foundRelatedFaReport = faReportDocumentDao.findFaReportByIdAndScheme(receivedRefId, receivedRefSchemeId);
                if (foundRelatedFaReport != null) { // Means that the report we just received refers to an exising one (Correcting it/Deleting it/Cancelling it)
                    FaReportStatusType faReportStatusEnum = FaReportStatusType.getFaReportStatusEnum(Integer.parseInt(justSavedPurposeCode));
                    // Change status with the new reports status (new report = report that refers to this one = the newly saved one)
                    foundRelatedFaReport.setStatus(faReportStatusEnum.name());
                    // Correction (purposecode == 5) => set 'latest' to false (for each activitiy related to this report)
                    checkAndUpdateActivitiesForCorrectionsAndCancellationsAndDeletions(foundRelatedFaReport, faReportStatusEnum, justSavedReport.getId());
                } else {
                    log.warn("Received and saved a correction/cancellation or deletion message but couldn't find a message to apply it to!");
                }
            }
        }
    }


    private void checkAndUpdateActivitiesForCorrectionsAndCancellationsAndDeletions(FaReportDocumentEntity faReportDocumentEntity, FaReportStatusType faReportStatusEnum,
                                                                                    int idOfCfPotentiallyCancellingOrDeletingReport) {
        Set<FishingActivityEntity> fishingActivities = faReportDocumentEntity.getFishingActivities();
        if (CollectionUtils.isNotEmpty(fishingActivities)) {
            switch (faReportStatusEnum) {
                case UPDATED:
                    for (FishingActivityEntity fishingActivity : fishingActivities) {
                        fishingActivity.setLatest(false);
                    }
                    break;
                case CANCELED:
                    for (FishingActivityEntity fishingActivity : fishingActivities) {
                        fishingActivity.setCanceledBy(idOfCfPotentiallyCancellingOrDeletingReport);
                    }
                    break;
                case DELETED:
                    for (FishingActivityEntity fishingActivity : fishingActivities) {
                        fishingActivity.setDeletedBy(idOfCfPotentiallyCancellingOrDeletingReport);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void updateFishingTripStartAndEndDate(Set<FaReportDocumentEntity> faReportDocuments) {
        log.debug("Start update of FishingTrip Start And End Date");
        if (CollectionUtils.isEmpty(faReportDocuments)) {
            log.error("FaReportDocuments List is EMPTY or NULL in updateFishingTripStartAndEndDate");
            return;
        }
        for (FaReportDocumentEntity faReportDocument : faReportDocuments) {
            calculateFishingTripStartAndEndDate(faReportDocument);
        }
        log.debug("Update of Start And End Date for all fishingTrips is complete");
    }

    /**
     * Create Geometry for FaReportDocument and FluxLocation. In Flux location we save each reported location as a point geometry.
     * In Fa Report document, all the points are converted to Multipoint and saved as a single geometry.
     * In Fishing activity we save all the points those are reported in the corresponding flux location.
     *
     * @param faReportDocumentEntity
     */
    private void updateGeometry(FaReportDocumentEntity faReportDocumentEntity) throws ServiceException {
        List<MicroMovement> movements = getInterpolatedGeomForArea(faReportDocumentEntity);
        Set<FishingActivityEntity> fishingActivityEntities = faReportDocumentEntity.getFishingActivities();
        List<Geometry> multiPointForFaReport = populateGeometriesForFishingActivitiesAndRelatedActivities(movements, fishingActivityEntities);
        faReportDocumentEntity.setGeom(GeometryUtils.createMultipoint(multiPointForFaReport));
    }

    private List<Geometry> populateGeometriesForFishingActivitiesAndRelatedActivities(List<MicroMovement> movements, Set<FishingActivityEntity> fishingActivityEntities) throws ServiceException {
        List<Geometry> multiPointForFaReport = new ArrayList<>();
        for (FishingActivityEntity fishingActivityEntity : Utils.safeIterable(fishingActivityEntities)) {
            multiPointForFaReport = deriveMultipointsFromActivitiesAndMovements(multiPointForFaReport, fishingActivityEntity, movements);
            for (FishingActivityEntity relatedFishingActivityEntity : Utils.safeIterable(fishingActivityEntity.getAllRelatedFishingActivities())) {
                multiPointForFaReport = deriveMultipointsFromActivitiesAndMovements(multiPointForFaReport, relatedFishingActivityEntity, movements);
            }
        }
        return multiPointForFaReport;
    }

    private List<Geometry> deriveMultipointsFromActivitiesAndMovements(List<Geometry> multiPointsToAddTo, FishingActivityEntity fishingActivityEntity, List<MicroMovement> movements) throws ServiceException {
        List<Geometry> multiPointForFa = new ArrayList<>();
        Instant activityDate = fishingActivityEntity.getOccurence() != null ? fishingActivityEntity.getOccurence() : getFirstDateFromDelimitedPeriods(fishingActivityEntity.getDelimitedPeriods());
        Geometry interpolatedPoint = interpolatePointFromMovements(movements, activityDate);
        for (FluxLocationEntity fluxLocation : fishingActivityEntity.getFluxLocations()) {
            Geometry point = null;
            String fluxLocationStr = fluxLocation.getTypeCode();
            if (fluxLocationStr.equalsIgnoreCase(FluxLocationEnum.AREA.name())) {
                point = interpolatedPoint;
                fluxLocation.setGeom(point);
            } else if (fluxLocationStr.equalsIgnoreCase(FluxLocationEnum.LOCATION.name())) {
                point = getGeometryForLocation(fluxLocation);
                log.debug("Geometry calculated for location is: {}", point);
                fluxLocation.setGeom(point);
            } else if (fluxLocationStr.equalsIgnoreCase(FluxLocationEnum.POSITION.name())) {
                point = GeometryUtils.createPoint(fluxLocation.getLongitude(), fluxLocation.getLatitude());
                fluxLocation.setGeom(point);
            }
            if (point != null) {
                multiPointForFa.add(point);
                multiPointsToAddTo.add(point);
            }
        }
        fishingActivityEntity.setGeom(GeometryUtils.createMultipoint(multiPointForFa));
        return multiPointsToAddTo;
    }

    private Geometry getGeometryForLocation(FluxLocationEntity fluxLocation) throws ServiceException {
        Geometry point;
        if (fluxLocation.getLongitude() != null && fluxLocation.getLatitude() != null) {
            point = GeometryUtils.createPoint(fluxLocation.getLongitude(), fluxLocation.getLatitude());
        } else {
            point = getGeometryFromMdr(fluxLocation.getFluxLocationIdentifier());
            if (point == null) {
                point = getGeometryFromSpatial(fluxLocation.getFluxLocationIdentifier());
            }
        }
        return point;
    }

    /**
     * Find geometry for fluxLocation code in MDR
     *
     * @param fluxLocationIdentifier
     */
    private Geometry getGeometryFromMdr(String fluxLocationIdentifier) {
        log.debug("Get Geometry from MDR for : " + fluxLocationIdentifier);
        if (fluxLocationIdentifier == null) {
            return null;
        }

        final List<String> columnsList = new ArrayList<>(Collections.singletonList("code"));
        Map<String, List<String>> portValuesFromMdr = mdrModuleServiceBean.getAcronymFromMdr("LOCATION", fluxLocationIdentifier, columnsList, 1, "latitude", "longitude");
        List<String> latitudeValues = portValuesFromMdr.get("latitude");
        List<String> longitudeValues = portValuesFromMdr.get("longitude");

        Double latitude = null;
        Double longitude = null;
        if (CollectionUtils.isNotEmpty(latitudeValues)) {
            String latitudeStr = latitudeValues.get(0);
            if (latitudeStr != null) {
                latitude = Double.parseDouble(latitudeStr);
            }
        }

        if (CollectionUtils.isNotEmpty(longitudeValues)) {
            String longitudeStr = longitudeValues.get(0);
            if (longitudeStr != null) {
                longitude = Double.parseDouble(longitudeStr);
            }
        }
        return GeometryUtils.createPoint(longitude, latitude);
    }

    /**
     * Get Geometry information from spatial for FLUXLocation code
     *
     * @param fluxLocationIdentifier
     */
    private Geometry getGeometryFromSpatial(String fluxLocationIdentifier) throws ServiceException {
        log.info("Get Geometry from Spatial for: {}", fluxLocationIdentifier);
        if (fluxLocationIdentifier == null) {
            return null;
        }

        Geometry geometry = null;
        try {
            String geometryWkt = spatialModuleService.getGeometryForPortCode(fluxLocationIdentifier);
            if (geometryWkt != null) {
                Geometry value = GeometryMapper.INSTANCE.wktToGeometry(geometryWkt).getValue();
                Coordinate[] coordinates = value.getCoordinates();
                if (coordinates.length > 0) {
                    Coordinate coordinate = coordinates[0];
                    double x = coordinate.x;
                    double y = coordinate.y;
                    geometry = GeometryUtils.createPoint(x, y);
                }
            }
            log.debug("Geometry received from Spatial for: {} : {}", fluxLocationIdentifier, geometryWkt);
        } catch (ParseException e) {
            log.error("Exception while trying to get geometry from spatial", e);
            throw new ServiceException(e.getMessage(), e);
        }
        return geometry;
    }

    private List<MicroMovement> getInterpolatedGeomForArea(FaReportDocumentEntity faReportDocumentEntity) throws ServiceException {
        if (CollectionUtils.isEmpty(faReportDocumentEntity.getVesselTransportMeans())) {
            return Collections.emptyList();
        }

        Set<VesselIdentifierEntity> vesselIdentifiers = faReportDocumentEntity.getVesselTransportMeans().iterator().next().getVesselIdentifiers();
        Map<String, Instant> dateMap = findStartAndEndDate(faReportDocumentEntity);
        return getAllMovementsForDateRange(vesselIdentifiers, dateMap.get(START_DATE), dateMap.get(END_DATE));
    }

    private Map<String, Instant> findStartAndEndDate(FaReportDocumentEntity faReportDocumentEntity) {
        TreeSet<Instant> dates = new TreeSet<>();
        for (FishingActivityEntity fishingActivity : faReportDocumentEntity.getFishingActivities()) {
            if (fishingActivity.getOccurence() != null) {
                dates.add(fishingActivity.getOccurence());
            } else if (CollectionUtils.isNotEmpty(fishingActivity.getDelimitedPeriods())) {
                Instant firstDate = getFirstDateFromDelimitedPeriods(fishingActivity.getDelimitedPeriods());
                if (firstDate != null) {
                    dates.add(firstDate);
                }
            }
        }
        return ImmutableMap.<String, Instant>builder().put(START_DATE, dates.first()).put(END_DATE, dates.last()).build();
    }

    private Instant getFirstDateFromDelimitedPeriods(Collection<DelimitedPeriodEntity> delimitedPeriods) {
        TreeSet<Instant> set = new TreeSet<>();
        for (DelimitedPeriodEntity delimitedPeriodEntity : delimitedPeriods) {
            if (delimitedPeriodEntity.getStartDate() != null)
                set.add(delimitedPeriodEntity.getStartDate());
        }
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        return set.first();
    }

    private List<MicroMovement> getAllMovementsForDateRange(Set<VesselIdentifierEntity> vesselIdentifiers,
                                                            Instant startDate, Instant endDate) throws ServiceException {
        List<String> assetGuids = assetService.getAssetGuids(vesselIdentifiers); // Call asset to get Vessel Guids
        return movementModule.getMovement(assetGuids, startDate, endDate); // Send Vessel Guids to movements
    }

    private Geometry interpolatePointFromMovements(List<MicroMovement> movements, Instant activityDate) throws ServiceException {
        if (movements == null || movements.isEmpty()) {
            return null;
        }

        movements.sort(new MicroMovementComparator());
        Map<String, MicroMovement> movementTypeMap = getPreviousAndNextMovement(movements, activityDate);
        MicroMovement nextMovement = movementTypeMap.get(NEXT);
        MicroMovement previousMovement = movementTypeMap.get(PREVIOUS);

        Geometry faReportGeom;
        if (previousMovement == null && nextMovement == null) {
            return null;
        } else if (nextMovement == null) {
            faReportGeom = convertToPoint(previousMovement);
            faReportGeom.setSRID(DEFAULT_WILDFLY_SRID);
        } else if (previousMovement == null) {
            faReportGeom = convertToPoint(nextMovement);
            faReportGeom.setSRID(DEFAULT_WILDFLY_SRID);
        } else {
            faReportGeom = calculateIntermediatePoint(previousMovement, nextMovement, activityDate);
        }
        return faReportGeom;
    }

    private Point convertToPoint(MicroMovement microMovement) {
        MovementPoint location = microMovement.getLocation();
        Coordinate coordinate = new Coordinate(location.getLongitude(), location.getLatitude());
        return geometryFactory.createPoint(coordinate);
    }

    private Geometry calculateIntermediatePoint(MicroMovement previousMovement, MicroMovement nextMovement, Instant acceptedDate) throws ServiceException {
        long durationAB = nextMovement.getTimestamp().toEpochMilli() - previousMovement.getTimestamp().toEpochMilli();
        long durationAC = acceptedDate.toEpochMilli() - previousMovement.getTimestamp().toEpochMilli();
        long durationBC = nextMovement.getTimestamp().toEpochMilli() - acceptedDate.toEpochMilli();

        Point previousPoint = convertToPoint(previousMovement);
        Point nextPoint = convertToPoint(nextMovement);

        Geometry point;
        if (durationAC == 0) {
            log.info("The point is same as the start point");
            point = previousPoint;
        } else if (durationBC == 0) {
            log.info("The point is the same as end point");
            point = nextPoint;
        } else {
            log.info("The point is between start and end point");
            String nextWkt = WKTWriter.toPoint(nextPoint.getCoordinate());
            String previousWkt = WKTWriter.toPoint(previousPoint.getCoordinate());

            LengthIndexedLine lengthIndexedLine = GeometryUtils.createLengthIndexedLine(previousWkt, nextWkt);
            Double index = durationAC * (lengthIndexedLine.getEndIndex() - lengthIndexedLine.getStartIndex()) / durationAB;
            point = GeometryUtils.calculateIntersectingPoint(lengthIndexedLine, index);
        }
        point.setSRID(DEFAULT_WILDFLY_SRID);
        return point;
    }

    private Map<String, MicroMovement> getPreviousAndNextMovement(List<MicroMovement> movements, Instant inputDate) {
        Map<String, MicroMovement> movementMap = new HashMap<>();
        for (MicroMovement movement : movements) {
            if (movement.getTimestamp().compareTo(inputDate) <= 0) {
                movementMap.put(PREVIOUS, movement);
            } else if (movement.getTimestamp().compareTo(inputDate) > 0) {
                movementMap.put(NEXT, movement);
                break;
            }
        }
        return movementMap;
    }
}
