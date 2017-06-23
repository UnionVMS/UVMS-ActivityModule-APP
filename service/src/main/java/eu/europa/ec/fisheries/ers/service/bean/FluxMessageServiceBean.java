/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package eu.europa.ec.fisheries.ers.service.bean;

import com.google.common.collect.ImmutableMap;
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
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.ers.fa.utils.MovementTypeComparator;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.MovementModuleService;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMapper;
import eu.europa.ec.fisheries.ers.service.util.DatabaseDialect;
import eu.europa.ec.fisheries.ers.service.util.Oracle;
import eu.europa.ec.fisheries.ers.service.util.PostGres;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.common.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
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

    private FluxFaReportMessageDao fluxReportMessageDao;

    @EJB
    private MovementModuleService movementModule;

    @EJB
    private AssetModuleService assetModule;

    @EJB
    private PropertiesBean properties;

    @EJB
    private FishingTripService fishingTripService;

    private DatabaseDialect dialect;

    @PostConstruct
    public void init() {
        initEntityManager();
        faReportDocumentDao = new FaReportDocumentDao(getEntityManager());
        fluxReportMessageDao = new FluxFaReportMessageDao(getEntityManager());
        dialect = new PostGres();
        if ("oracle".equals(properties.getProperty("database.dialect"))){
            dialect = new Oracle();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void saveFishingActivityReportDocuments(FLUXFAReportMessage faReportMessage, FaReportSourceEnum faReportSourceEnum) throws ServiceException {
        FluxFaReportMessageEntity messageEntity = FluxFaReportMessageMapper.INSTANCE.mapToFluxFaReportMessage(faReportMessage, faReportSourceEnum, new FluxFaReportMessageEntity());
        final Set<FaReportDocumentEntity> faReportDocuments = messageEntity.getFaReportDocuments();
        for (FaReportDocumentEntity faReportDocument : faReportDocuments) {
            updateGeometry(faReportDocument);
            enrichFishingActivityWithGuiID(faReportDocument);
        }
        log.debug("fishing activity records to be saved : "+faReportDocuments.size() );


        fluxReportMessageDao.saveFluxFaReportMessage(messageEntity);
        log.debug("Save partial FluxFaReportMessage before further processing" );
        updateFaReportCorrections(faReportMessage.getFAReportDocuments());
        log.debug("Update FaReport Corrections is complete." );
        updateFishingTripStartAndEndDate(faReportDocuments);
        log.info("FluxFaReportMessage Saved successfully.");
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
                List<FishingActivityEntity> fishingActivityEntityList=fishingTripService.getAllFishingActivitiesForTrip(tripIdentifierEntity.getTripId());
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
    private void updateFaReportCorrections(List<FAReportDocument> faReportDocuments) throws ServiceException {
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            if (faReportDocument.getRelatedFLUXReportDocument().getReferencedID() != null &&
                    faReportDocument.getRelatedFLUXReportDocument().getPurposeCode() != null) {
                FaReportDocumentEntity faReportDocumentEntity = faReportDocumentDao.findFaReportByIdAndScheme(
                        faReportDocument.getRelatedFLUXReportDocument().getReferencedID().getValue(),
                        faReportDocument.getRelatedFLUXReportDocument().getReferencedID().getSchemeID()); // Find the existing report by using reference Id and scheme Id
                if (faReportDocumentEntity != null) { // If found then update the entity with respect to purpose code in the Report
                    FaReportStatusEnum faReportStatusEnum = FaReportStatusEnum.getFaReportStatusEnum(Integer.parseInt(faReportDocument.getRelatedFLUXReportDocument().getPurposeCode().getValue()));
                    faReportDocumentEntity.setStatus(faReportStatusEnum.getStatus());
                    faReportDocumentEntities.add(faReportDocumentEntity);
                }
            }
        }
        faReportDocumentDao.updateAllFaData(faReportDocumentEntities); // Update all the Entities together
    }


    private void updateFishingTripStartAndEndDate(Set<FaReportDocumentEntity> faReportDocuments) throws ServiceException {
        log.debug("Start  update of FishingTrip Start And End Date");
        if(CollectionUtils.isEmpty(faReportDocuments)){
            log.error("faReportDocuments are empty or null in updateFishingTripStartAndEndDate");
            return;
        }
        for (FaReportDocumentEntity faReportDocument : faReportDocuments) {
            calculateFishingTripStartAndEndDate(faReportDocument);
        }
        faReportDocumentDao.updateAllFaData(new ArrayList<>(faReportDocuments)); // Update all the Entities together
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
        List<Geometry> multiPointForFaReport = new ArrayList<>();
        for (FishingActivityEntity fishingActivity : faReportDocumentEntity.getFishingActivities()) {
            List<Geometry> multiPointForFa = new ArrayList<>();
            Date activityDate = fishingActivity.getOccurence() != null ? fishingActivity.getOccurence() : getFirstDateFromDelimitedPeriods(fishingActivity.getDelimitedPeriods());
            Geometry interpolatedPoint = interpolatePointFromMovements(movements, activityDate);
            for (FluxLocationEntity fluxLocation : fishingActivity.getFluxLocations()) {
                Geometry point = null;
                String fluxLocationStr = fluxLocation.getTypeCode();
                if (fluxLocationStr.equalsIgnoreCase(FluxLocationEnum.AREA.name())) { // Interpolate Geometry from movements
                    point = interpolatedPoint;
                    fluxLocation.setGeom(point);
                } else if (fluxLocationStr.equalsIgnoreCase(FluxLocationEnum.LOCATION.name())) { // Create Geometry directly from long/lat
                    point = GeometryUtils.createPoint(fluxLocation.getLongitude(), fluxLocation.getLatitude());
                    fluxLocation.setGeom(point);
                }
                if (point != null) { // Add to the list of Geometry. This will be converted to Multipoint and saved in FaReportDocument
                    multiPointForFa.add(point);
                    multiPointForFaReport.add(point);
                }
            }
            fishingActivity.setGeom(GeometryUtils.createMultipoint(multiPointForFa)); // Add the Multipoint to Fishing Activity
        }
        faReportDocumentEntity.setGeom(GeometryUtils.createMultipoint(multiPointForFaReport)); // Add the Multipoint to FA Report
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

            if (previousMovement == null && nextMovement == null) { // If nothing found return null
                faReportGeom = null;
            } else if (nextMovement == null) { // if no next movement then the last previous movement is the position
                faReportGeom = GeometryMapper.INSTANCE.wktToGeometry(previousMovement.getWkt()).getValue();
                faReportGeom.setSRID(dialect.defaultSRID());
            } else if (previousMovement == null) { // if no previous movement then the first next movement is the position
                faReportGeom = GeometryMapper.INSTANCE.wktToGeometry(nextMovement.getWkt()).getValue();
                faReportGeom.setSRID(dialect.defaultSRID());
            } else { // ideal scenario, find the intersecting position
                faReportGeom = calculateIntermediatePoint(previousMovement, nextMovement, activityDate);
            }
        } catch (ParseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
        return faReportGeom;
    }

    private Geometry calculateIntermediatePoint(MovementType previousMovement, MovementType nextMovement, Date acceptedDate) throws ServiceException { // starting point = A, end point = B, calculated point = C
        Geometry point;

        Long durationAB = nextMovement.getPositionTime().toGregorianCalendar().getTimeInMillis() - previousMovement.getPositionTime().toGregorianCalendar().getTimeInMillis();
        Long durationAC = acceptedDate.getTime() - previousMovement.getPositionTime().toGregorianCalendar().getTimeInMillis();
        Long durationBC = nextMovement.getPositionTime().toGregorianCalendar().getTimeInMillis() - acceptedDate.getTime();

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
                Double index = durationAC * (lengthIndexedLine.getEndIndex() - lengthIndexedLine.getStartIndex()) / durationAB; // Calculate the index to find the intersecting point
                point = GeometryUtils.calculateIntersectingPoint(lengthIndexedLine, index);
            }
        } catch (ParseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
        point.setSRID(dialect.defaultSRID());
        return point;
    }

    private Map<String, MovementType> getPreviousAndNextMovement(List<MovementType> movements, Date inputDate) throws ServiceException {
        XMLGregorianCalendar date = convertDateToXmlGregorianCalendar(inputDate);
        Map<String, MovementType> movementMap = new HashMap<>();
        for (MovementType movement : movements) {
            if (movement.getPositionTime().compare(date) <= 0) { // Find the previous movement
                movementMap.put(PREVIOUS, movement);
            } else if (movement.getPositionTime().compare(date) > 0) { // Find the next movement
                movementMap.put(NEXT, movement);
                break;
            }
        }
        return movementMap;
    }

    private XMLGregorianCalendar convertDateToXmlGregorianCalendar(Date inputDate) throws ServiceException {
        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(inputDate);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }  catch (DatatypeConfigurationException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void setDialect(DatabaseDialect dialect) {
        this.dialect = dialect;
    }

}
