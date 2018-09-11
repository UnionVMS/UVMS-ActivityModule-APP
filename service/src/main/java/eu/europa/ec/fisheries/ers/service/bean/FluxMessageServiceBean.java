/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package eu.europa.ec.fisheries.ers.service.bean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FluxFaReportMessageDao;
import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.ers.fa.utils.MovementTypeComparator;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.MdrModuleService;
import eu.europa.ec.fisheries.ers.service.MovementModuleService;
import eu.europa.ec.fisheries.ers.service.SpatialModuleService;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMapper;
import eu.europa.ec.fisheries.ers.service.util.DatabaseDialect;
import eu.europa.ec.fisheries.ers.service.util.Oracle;
import eu.europa.ec.fisheries.ers.service.util.Postgres;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;


@Stateless
@Transactional
@Slf4j
public class FluxMessageServiceBean extends BaseActivityBean implements FluxMessageService {

    private static final String PREVIOUS = "PREVIOUS";
    private static final String NEXT = "NEXT";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private FaReportDocumentDao faReportDocumentDao;

    private FluxFaReportMessageDao fluxReportMessageDao;

    @EJB
    private MovementModuleService movementModule;

    @EJB
    private AssetModuleService assetModule;

    @EJB
    private PropertiesBean properties;

    @EJB
    private FishingTripService fishingTripService;

    @EJB
    private MdrModuleService mdrModuleServiceBean;

    @EJB
    private SpatialModuleService spatialModuleService;

    private DatabaseDialect dialect;

    @PostConstruct
    public void init() {
        initEntityManager();
        faReportDocumentDao = new FaReportDocumentDao(getEntityManager());
        fluxReportMessageDao = new FluxFaReportMessageDao(getEntityManager());
        dialect = new Postgres();
        if ("oracle".equals(properties.getProperty("database.dialect"))){
            dialect = new Oracle();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public FluxFaReportMessageEntity saveFishingActivityReportDocuments(FLUXFAReportMessage faReportMessage, FaReportSourceEnum faReportSourceEnum) throws ServiceException {
        log.info("[START] Going to save [ " + faReportMessage.getFAReportDocuments().size() + " ] FaReportDocuments..");
        FluxFaReportMessageEntity messageEntity = new FluxFaReportMessageMapper().mapToFluxFaReportMessage(faReportMessage, faReportSourceEnum,
                new FluxFaReportMessageEntity());
        final Set<FaReportDocumentEntity> faReportDocuments = messageEntity.getFaReportDocuments();
        for (FaReportDocumentEntity faReportDocument : faReportDocuments) {
            try {
                updateGeometry(faReportDocument);
                enrichFishingActivityWithGuiID(faReportDocument);
            } catch (Exception e){
                log.error("Could not update Geometry OR enrichActivities for faReportDocument:"+faReportDocument.getId());
            }
        }
        log.debug("[INFO] Fishing activity records to be saved : "+faReportDocuments.size() );
        FluxFaReportMessageEntity entity = fluxReportMessageDao.saveFluxFaReportMessage(messageEntity);
        log.debug("[INFO] Saved partial FluxFaReportMessage before further processing" );
        updateFaReportCorrectionsOrCancellations(faReportMessage.getFAReportDocuments());
        log.debug("[INFO] Updating FaReport Corrections is complete." );
        updateFishingTripStartAndEndDate(faReportDocuments);
        log.info("[END] FluxFaReportMessage Saved successfully.");
        return entity;
    }

    /**
     * This method will traverse through all the FishingTrips mentioned in the FaReportDocument and
     * update start and end date for the trip based on the fishing activities reported for the trip
     * @param faReportDocument
     */
    public void calculateFishingTripStartAndEndDate(FaReportDocumentEntity faReportDocument){
        Set<FishingActivityEntity> fishingActivities=faReportDocument.getFishingActivities();
        if(CollectionUtils.isEmpty(fishingActivities)) {
            log.error("Could not find FishingActivities for faReportDocument.");
            return;
        }
        for(FishingActivityEntity fishingActivityEntity:fishingActivities){
            Set<FishingTripEntity> fishingTripEntities=fishingActivityEntity.getFishingTrips();
            if(CollectionUtils.isEmpty(fishingTripEntities)) {
                continue;
            }
            for(FishingTripEntity fishingTripEntity :fishingTripEntities){
                setTripStartAndEndDateForFishingTrip(fishingTripEntity);
            }
        }
    }

    private void setTripStartAndEndDateForFishingTrip(FishingTripEntity fishingTripEntity) {
        Set<FishingTripIdentifierEntity> identifierEntities= fishingTripEntity.getFishingTripIdentifiers();
        if(CollectionUtils.isEmpty(identifierEntities))
            return;
        for(FishingTripIdentifierEntity tripIdentifierEntity : identifierEntities){
            try {
                List<FishingActivityEntity> fishingActivityEntityList = fishingTripService.getAllFishingActivitiesForTrip(tripIdentifierEntity.getTripId());
                if(CollectionUtils.isNotEmpty(fishingActivityEntityList)){
                    //Calculate trip start date
                    FishingActivityEntity firstFishingActivity= fishingActivityEntityList.get(0);
                    tripIdentifierEntity.setCalculatedTripStartDate(firstFishingActivity.getCalculatedStartTime());

                    // calculate trip end date
                    Date calculatedTripEndDate;
                    int totalActivities=fishingActivityEntityList.size();
                    if(totalActivities>1){
                        calculatedTripEndDate=fishingActivityEntityList.get(totalActivities-1).getCalculatedStartTime();
                    }else{
                        calculatedTripEndDate=firstFishingActivity.getCalculatedStartTime();
                    }
                    tripIdentifierEntity.setCalculatedTripEndDate(calculatedTripEndDate);
                }

            } catch (Exception e) {
                log.error("Error while trying to calculate FishingTrip start and end Date",e);
            }
        }
    }

    private void enrichFishingActivityWithGuiID(FaReportDocumentEntity faReportDocument){
        if(CollectionUtils.isNotEmpty(faReportDocument.getVesselTransportMeans())) {
            for(VesselTransportMeansEntity vesselTransportMeansEntity : faReportDocument.getVesselTransportMeans()) {
                enrichWithGuidFromAssets(vesselTransportMeansEntity);
                vesselTransportMeansEntity.setFaReportDocument(faReportDocument);
            }
        }
        Set<FishingActivityEntity> fishingActivities=faReportDocument.getFishingActivities();
        if(CollectionUtils.isEmpty(fishingActivities))
            return;

        for(FishingActivityEntity fishingActivityEntity:fishingActivities){
            enrichFishingActivityVesselWithGuiId(fishingActivityEntity);
            if(fishingActivityEntity.getRelatedFishingActivity() !=null)
                enrichFishingActivityVesselWithGuiId(fishingActivityEntity.getRelatedFishingActivity());
        }
    }

    private void enrichFishingActivityVesselWithGuiId(FishingActivityEntity fishingActivityEntity) {
        Set<VesselTransportMeansEntity> vesselTransportMeansEntityList=  fishingActivityEntity.getVesselTransportMeans();
        if(CollectionUtils.isEmpty(vesselTransportMeansEntityList)){
           return;
        }

        for(VesselTransportMeansEntity entity : vesselTransportMeansEntityList) {
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
            List<String> guids = assetModule.getAssetGuids(vesselTransport.getVesselIdentifiers());
            if(CollectionUtils.isNotEmpty(guids)){
                vesselTransport.setGuid(guids.get(0));
            }
        } catch (ServiceException e) {
            log.error("Error while trying to get guids from Assets Module {}", e);
        }
    }


    /**
     * If there is a reference Id exist for any of the FaReport Document, than it means this is an update to an existing report.
     */
    private void updateFaReportCorrectionsOrCancellations(List<FAReportDocument> faReportDocuments) throws ServiceException {
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            FLUXReportDocument relatedFLUXReportDocument = faReportDocument.getRelatedFLUXReportDocument();
            IDType receivedRefId = relatedFLUXReportDocument.getReferencedID();
            if (receivedRefId != null && relatedFLUXReportDocument.getPurposeCode() != null) {
                FaReportDocumentEntity faReportDocumentEntity = faReportDocumentDao.findFaReportByIdAndScheme(receivedRefId.getValue(),
                        receivedRefId.getSchemeID());
                if (faReportDocumentEntity != null) {
                    FaReportStatusType faReportStatusEnum = FaReportStatusType.getFaReportStatusEnum(Integer.parseInt(relatedFLUXReportDocument.getPurposeCode().getValue()));
                    log.debug("[INFO] Found FaReportDocument with id [" +receivedRefId+ "] (same as the received message ReferencedID)! Going to modify its " +
                            "purpose code from ["+ faReportDocumentEntity.getStatus() +"] to [" +relatedFLUXReportDocument.getPurposeCode()+ "] (aka "+faReportStatusEnum+")...");
                    faReportDocumentEntity.setStatus(faReportStatusEnum);
                    faReportDocumentEntities.add(faReportDocumentEntity);
                    // Correction (purposecode == 5) => set 'latest' to false (for each activitiy related to this report)
                    checkAndUpdateActivitiesForCorrection(receivedRefId, faReportDocumentEntity.getFishingActivities(), faReportStatusEnum);
                }
            }
        }
        faReportDocumentDao.bulkUploadFaData(faReportDocumentEntities);
    }

    private void checkAndUpdateActivitiesForCorrection(IDType receivedRefId, Set<FishingActivityEntity> fishingActivities, FaReportStatusType faReportStatusEnum) {
        if(FaReportStatusType.UPDATED.equals(faReportStatusEnum)){
            if(CollectionUtils.isNotEmpty(fishingActivities)){
                for (FishingActivityEntity fishingActivity : fishingActivities) {
                    fishingActivity.setLatest(false);
                }
            } else {
                log.warn("[WARN] Didn't find any activities to correct related to FaReportDocument with ID [" +receivedRefId+ "] ..");
            }
        }
    }

    private void updateFishingTripStartAndEndDate(Set<FaReportDocumentEntity> faReportDocuments) {
        log.debug("Start  update of FishingTrip Start And End Date");
        if(CollectionUtils.isEmpty(faReportDocuments)){
            log.error("FaReportDocuments List is EMPTY or NULL in updateFishingTripStartAndEndDate");
            return;
        }
        for (FaReportDocumentEntity faReportDocument : faReportDocuments) {
            calculateFishingTripStartAndEndDate(faReportDocument);
        }
        faReportDocumentDao.bulkUploadFaData(new ArrayList<>(faReportDocuments));
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
        List<MovementType> movements = getInterpolatedGeomForArea(faReportDocumentEntity);
        Set<FishingActivityEntity> fishingActivityEntities = faReportDocumentEntity.getFishingActivities();
        List<Geometry> multiPointForFaReport =populateGeometriesForFishingActivities(movements,  fishingActivityEntities);
        faReportDocumentEntity.setGeom(GeometryUtils.createMultipoint(multiPointForFaReport));
    }

    private List<Geometry> populateGeometriesForFishingActivities(List<MovementType> movements, Set<FishingActivityEntity> fishingActivityEntities) throws ServiceException {
        List<Geometry> multiPointForFaReport = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(fishingActivityEntities)) {
            for (FishingActivityEntity fishingActivity : fishingActivityEntities) {
                List<Geometry> multiPointForFa = new ArrayList<>();
                Date activityDate = fishingActivity.getOccurence() != null ? fishingActivity.getOccurence() : getFirstDateFromDelimitedPeriods(fishingActivity.getDelimitedPeriods());
                Geometry interpolatedPoint = interpolatePointFromMovements(movements, activityDate);
                for (FluxLocationEntity fluxLocation : fishingActivity.getFluxLocations()) {
                    Geometry point = null;
                    String fluxLocationStr = fluxLocation.getTypeCode();
                    if (fluxLocationStr.equalsIgnoreCase(FluxLocationEnum.AREA.name())) {
                        point = interpolatedPoint;
                        fluxLocation.setGeom(point);
                    } else if (fluxLocationStr.equalsIgnoreCase(FluxLocationEnum.LOCATION.name())) {
                        point = getGeometryForLocation(fluxLocation);
                        log.debug("[INFO] Geometry calculated for location is : "+point);
                        fluxLocation.setGeom(point);
                    } else if (fluxLocationStr.equalsIgnoreCase(FluxLocationEnum.POSITION.name())) {
                        point = GeometryUtils.createPoint(fluxLocation.getLongitude(), fluxLocation.getLatitude());
                        fluxLocation.setGeom(point);
                    }
                    if (point != null) {
                        multiPointForFa.add(point);
                        multiPointForFaReport.add(point);
                    }
                }
                fishingActivity.setGeom(GeometryUtils.createMultipoint(multiPointForFa));
            }
        }
        return multiPointForFaReport;
    }

    private Geometry getGeometryForLocation(FluxLocationEntity fluxLocation) {
        Geometry point;
        if(fluxLocation.getLongitude()!=null && fluxLocation.getLatitude()!=null){
            point = GeometryUtils.createPoint(fluxLocation.getLongitude(), fluxLocation.getLatitude());
        }else{
            point= getGeometryFromMdr(fluxLocation.getFluxLocationIdentifier());
            if(point ==null){
                point= getGeometryFromSpatial(fluxLocation.getFluxLocationIdentifier());
            }
        }
        return point;
    }

    /**
     * Find geometry for fluxLocation code in MDR
     * @param fluxLocationIdentifier
     * @return
     */
    private Geometry getGeometryFromMdr(String fluxLocationIdentifier){
        log.debug("[INFO] Get Geometry from MDR for : "+fluxLocationIdentifier);
        if(fluxLocationIdentifier ==null){
            return null;
        }
        Geometry geometry=null;
        final List<String> columnsList = new ArrayList<>(Collections.singletonList("code"));

        try {
            Map<String, List<String>> portValuesFromMdr= mdrModuleServiceBean.getAcronymFromMdr("LOCATION",fluxLocationIdentifier,columnsList,1,"latitude","longitude");
            List<String> latitudeValues = portValuesFromMdr.get("latitude");
            List<String> longitudeValues = portValuesFromMdr.get("longitude");
            Double latitude=null;
            Double longitude=null;
            if(CollectionUtils.isNotEmpty(latitudeValues)){
                String latitudeStr = latitudeValues.get(0);
                if(latitudeStr!=null){
                    latitude= Double.parseDouble(latitudeStr);
                }
            }

            if(CollectionUtils.isNotEmpty(longitudeValues)){
                String longitudeStr = longitudeValues.get(0);
                if(longitudeStr!=null){
                    longitude= Double.parseDouble(longitudeStr);
                }
            }

            geometry =GeometryUtils.createPoint(longitude, latitude);
        } catch (ServiceException e) {
            log.error("Error while retriving values from MDR.",e);
        }
        return geometry;

    }

    /**
     * Get Geometry information from spatial for FLUXLocation code
     * @param fluxLocationIdentifier
     * @return
     */
    private Geometry getGeometryFromSpatial(String fluxLocationIdentifier){
        log.info("Get Geometry from Spatial for:"+fluxLocationIdentifier);
        if(fluxLocationIdentifier ==null){
            return null;
        }
        Geometry geometry = null;
        try {
           String geometryWkt= spatialModuleService.getGeometryForPortCode(fluxLocationIdentifier);
            if(geometryWkt !=null){
                Geometry value = GeometryMapper.INSTANCE.wktToGeometry(geometryWkt).getValue();
                Coordinate[] coordinates = value.getCoordinates();
                if (coordinates.length > 0){
                    Coordinate coordinate = coordinates[0];
                    double x = coordinate.x;
                    double y = coordinate.y;
                    geometry =  GeometryUtils.createPoint(x, y);
                }
            }

            log.debug(" Geometry received from Spatial for:"+fluxLocationIdentifier+"  :"+geometryWkt);
        } catch (ServiceException  |ParseException e) {
            log.error("Exception while trying to get geometry from spatial",e);
        }
        return geometry;
    }

    private List<MovementType> getInterpolatedGeomForArea(FaReportDocumentEntity faReportDocumentEntity) throws ServiceException {
        if(CollectionUtils.isEmpty(faReportDocumentEntity.getVesselTransportMeans())){
           return Collections.emptyList();
        }
        Set<VesselIdentifierEntity> vesselIdentifiers = faReportDocumentEntity.getVesselTransportMeans().iterator().next().getVesselIdentifiers();
        Map<String, Date> dateMap = findStartAndEndDate(faReportDocumentEntity);
        return getAllMovementsForDateRange(vesselIdentifiers, dateMap.get(START_DATE), dateMap.get(END_DATE));
    }

    private Map<String, Date> findStartAndEndDate(FaReportDocumentEntity faReportDocumentEntity) {
        TreeSet<Date> dates = new TreeSet<>();
        for (FishingActivityEntity fishingActivity : faReportDocumentEntity.getFishingActivities()) {
            if (fishingActivity.getOccurence() != null) {
                dates.add(fishingActivity.getOccurence());
            } else if (CollectionUtils.isNotEmpty(fishingActivity.getDelimitedPeriods())) {
                Date firstDate= getFirstDateFromDelimitedPeriods(fishingActivity.getDelimitedPeriods());
                if(firstDate!=null) {
                    dates.add(firstDate);
                }
            }
        }
        return ImmutableMap.<String, Date>builder().put(START_DATE, dates.first()).put(END_DATE, dates.last()).build();
    }

    private Date getFirstDateFromDelimitedPeriods(Collection<DelimitedPeriodEntity> delimitedPeriods) {
        TreeSet<Date> set = new TreeSet<>();
        for (DelimitedPeriodEntity delimitedPeriodEntity : delimitedPeriods) {
            if(delimitedPeriodEntity.getStartDate() !=null)
                set.add(delimitedPeriodEntity.getStartDate());
        }
        if(CollectionUtils.isEmpty(set))
           return null;
        return set.first();
    }

    private List<MovementType> getAllMovementsForDateRange(Set<VesselIdentifierEntity> vesselIdentifiers, Date startDate, Date endDate) throws ServiceException {
        List<String> assetGuids = assetModule.getAssetGuids(vesselIdentifiers); // Call asset to get Vessel Guids
        return movementModule.getMovement(assetGuids, startDate, endDate); // Send Vessel Guids to movements
    }

    private Geometry interpolatePointFromMovements(List<MovementType> movements, Date activityDate) throws ServiceException {

        if (movements == null || movements.isEmpty()) {
            return null;
        }

        Geometry faReportGeom;
        Collections.sort(movements, new MovementTypeComparator());
        Map<String, MovementType> movementTypeMap = getPreviousAndNextMovement(movements, activityDate);
        MovementType nextMovement = movementTypeMap.get(NEXT);
        MovementType previousMovement = movementTypeMap.get(PREVIOUS);

        try {

            if (previousMovement == null && nextMovement == null) {
                faReportGeom = null;
            } else if (nextMovement == null) {
                faReportGeom = GeometryMapper.INSTANCE.wktToGeometry(previousMovement.getWkt()).getValue();
                faReportGeom.setSRID(dialect.defaultSRID());
            } else if (previousMovement == null) {
                faReportGeom = GeometryMapper.INSTANCE.wktToGeometry(nextMovement.getWkt()).getValue();
                faReportGeom.setSRID(dialect.defaultSRID());
            } else {
                faReportGeom = calculateIntermediatePoint(previousMovement, nextMovement, activityDate);
            }
        } catch (ParseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
        return faReportGeom;
    }

    private Geometry calculateIntermediatePoint(MovementType previousMovement, MovementType nextMovement, Date acceptedDate) throws ServiceException {
        Geometry point;

        Long durationAB = nextMovement.getPositionTime().getTime() - previousMovement.getPositionTime().getTime();
        Long durationAC = acceptedDate.getTime() - previousMovement.getPositionTime().getTime();
        Long durationBC = nextMovement.getPositionTime().getTime() - acceptedDate.getTime();

        try {

            if (durationAC == 0) {
                log.info("The point is same as the start point");
                point =  GeometryMapper.INSTANCE.wktToGeometry(previousMovement.getWkt()).getValue();
            } else if (durationBC == 0) {
                log.info("The point is the same as end point");
                point = GeometryMapper.INSTANCE.wktToGeometry(nextMovement.getWkt()).getValue();
            } else {
                log.info("The point is between start and end point");
                LengthIndexedLine lengthIndexedLine = GeometryUtils.createLengthIndexedLine(previousMovement.getWkt(), nextMovement.getWkt());
                Double index = durationAC * (lengthIndexedLine.getEndIndex() - lengthIndexedLine.getStartIndex()) / durationAB;
                point = GeometryUtils.calculateIntersectingPoint(lengthIndexedLine, index);
            }
        } catch (ParseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
        point.setSRID(dialect.defaultSRID());
        return point;
    }

    private Map<String, MovementType> getPreviousAndNextMovement(List<MovementType> movements, Date inputDate) {
        Map<String, MovementType> movementMap = new HashMap<>();
        for (MovementType movement : movements) {
            if (movement.getPositionTime().compareTo(inputDate) <= 0) {
                movementMap.put(PREVIOUS, movement);
            } else if (movement.getPositionTime().compareTo(inputDate) > 0) {
                movementMap.put(NEXT, movement);
                break;
            }
        }
        return movementMap;
    }

    public void setDialect(DatabaseDialect dialect) {
        this.dialect = dialect;
    }

}
