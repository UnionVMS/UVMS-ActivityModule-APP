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
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.ers.fa.utils.MovementTypeComparator;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;


@Stateless
@Transactional
@Slf4j
public class FluxMessageServiceBean implements FluxMessageService {

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private FaReportDocumentDao faReportDocumentDao;
    private FluxFaReportMessageDao fluxReportMessageDao;

    @EJB
    private MovementModuleService movementModule;

    @EJB
    private AssetModuleService assetModule;

    @EJB
    private PropertiesBean properties;

    private static final String PREVIOUS   = "PREVIOUS";
    private static final String NEXT       = "NEXT";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE   = "END_DATE";

    private DatabaseDialect dialect;

    @PostConstruct
    public void init() {
        faReportDocumentDao = new FaReportDocumentDao(em);
        fluxReportMessageDao = new FluxFaReportMessageDao(em);
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
            log.debug("fishing activity records to be saved : " + faReportDocument.getFluxReportDocument().getId());
        }
        log.info("Insert fishing activity records into DB");

        fluxReportMessageDao.saveFluxFaReportMessage(messageEntity);
        updateFaReportCorrections(faReportMessage.getFAReportDocuments());
    }

    private void enrichFishingActivityWithGuiID(FaReportDocumentEntity faReportDocument){
        enrichWithGuidFromAssets(faReportDocument.getVesselTransportMeans());
        Set<FishingActivityEntity> fishingActivities=faReportDocument.getFishingActivities();
        for(FishingActivityEntity fishingActivityEntity:fishingActivities){
            enrichFishingActivityVesselWithGuiId(fishingActivityEntity);
            if(fishingActivityEntity.getRelatedFishingActivity() !=null)
                enrichFishingActivityVesselWithGuiId(fishingActivityEntity.getRelatedFishingActivity());
        }
    }

    private void enrichFishingActivityVesselWithGuiId(FishingActivityEntity fishingActivityEntity) {
        VesselTransportMeansEntity vesselTransportMeansEntity=  fishingActivityEntity.getVesselTransportMeans();
        if(vesselTransportMeansEntity!=null) {
            enrichWithGuidFromAssets(vesselTransportMeansEntity);
            fishingActivityEntity.setVesselTransportGuid(vesselTransportMeansEntity.getGuid());
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
        Set<VesselIdentifierEntity> vesselIdentifiers = faReportDocumentEntity.getVesselTransportMeans().getVesselIdentifiers();
        Map<String, Date> dateMap = findStartAndEndDate(faReportDocumentEntity);
        return getAllMovementsForDateRange(vesselIdentifiers, dateMap.get(START_DATE), dateMap.get(END_DATE));
    }

    private Map<String, Date> findStartAndEndDate(FaReportDocumentEntity faReportDocumentEntity) {
        TreeSet<Date> dates = new TreeSet<>();
        for (FishingActivityEntity fishingActivity : faReportDocumentEntity.getFishingActivities()) {
            if (fishingActivity.getOccurence() != null) {
                dates.add(fishingActivity.getOccurence());
            } else if (CollectionUtils.isNotEmpty(fishingActivity.getDelimitedPeriods())) {
                dates.add(getFirstDateFromDelimitedPeriods(fishingActivity.getDelimitedPeriods()));
            }
        }
        return ImmutableMap.<String, Date>builder().put(START_DATE, dates.first()).put(END_DATE, dates.last()).build();
    }

    private Date getFirstDateFromDelimitedPeriods(Collection<DelimitedPeriodEntity> delimitedPeriods) {
        TreeSet<Date> set = new TreeSet<>();
        for (DelimitedPeriodEntity delimitedPeriodEntity : delimitedPeriods) {
            set.add(delimitedPeriodEntity.getStartDate());
        }
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

}
