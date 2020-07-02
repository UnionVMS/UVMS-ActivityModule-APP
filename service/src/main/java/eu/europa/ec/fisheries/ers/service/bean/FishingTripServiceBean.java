/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.bean;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import eu.europa.ec.fisheries.ers.fa.dao.*;
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.fa.utils.UsmUtils;
import eu.europa.ec.fisheries.ers.service.*;
import eu.europa.ec.fisheries.ers.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.ers.service.dto.FaReportDTO;
import eu.europa.ec.fisheries.ers.service.dto.FlapDocumentDto;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.*;
import eu.europa.ec.fisheries.ers.service.dto.view.TripIdDto;
import eu.europa.ec.fisheries.ers.service.dto.view.TripOverviewDto;
import eu.europa.ec.fisheries.ers.service.dto.view.TripWidgetDto;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.CatchProgressProcessor;
import eu.europa.ec.fisheries.ers.service.facatch.evolution.TripCatchProgressRegistry;
import eu.europa.ec.fisheries.ers.service.mapper.*;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.ers.service.search.SortKey;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.Nullable;
import org.mockito.internal.util.collections.Sets;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum.*;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Stateless
@Local(FishingTripService.class)
@Transactional
@Slf4j
public class FishingTripServiceBean extends BaseActivityBean implements FishingTripService {

    private static final String DECLARATION ="Declaration";
    private static final String NOTIFICATION ="Notification";
    private static final String PREVIOUS = "PREVIOUS";
    private static final String NEXT = "NEXT";

    @EJB
    private SpatialModuleService spatialModule;

    @EJB
    private ActivityService activityServiceBean;

    @EJB
    private AssetModuleService assetModuleService;

    @EJB
    private MdrModuleService mdrModuleService;

    private FaReportDocumentDao faReportDocumentDao;
    private FishingActivityDao fishingActivityDao;
    private VesselIdentifierDao vesselIdentifierDao;
    private VesselTransportMeansDao vesselTransportMeansDao;
    private FishingTripIdentifierDao fishingTripIdentifierDao;
    private FishingTripDao fishingTripDao;
    private FaCatchDao faCatchDao;
    private ActivityConfigurationDao activityConfigurationDao;

    private static final CatchProgressProcessor catchProgressProcessor =
            new CatchProgressProcessor(new TripCatchProgressRegistry());

    @PostConstruct
    public void init() {
        initEntityManager();
        fishingTripIdentifierDao = new FishingTripIdentifierDao(getEntityManager());
        vesselIdentifierDao = new VesselIdentifierDao(getEntityManager());
        fishingActivityDao = new FishingActivityDao(getEntityManager());
        faReportDocumentDao = new FaReportDocumentDao(getEntityManager());
        faCatchDao = new FaCatchDao(getEntityManager());
        fishingTripDao = new FishingTripDao(getEntityManager());
        vesselTransportMeansDao = new VesselTransportMeansDao(em);
        activityConfigurationDao = new ActivityConfigurationDao(getEntityManager());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChronologyTripDTO getChronologyOfFishingTrip(String tripId, Integer count) throws ServiceException {
        String vesselGuid = vesselIdentifierDao.getLatestVesselIdByTrip(tripId); // Find the latest Vessel for the Trip for finding the trip of that vessel
        ChronologyTripDTO chronologyTripDTO = new ChronologyTripDTO();
        if(vesselGuid!=null){
            Date startDate = fishingTripIdentifierDao.getSelectedTripStartDate(tripId);
            chronologyTripDTO.setSelectedTrip(new ChronologyDTO(tripId, DateUtils.dateToString(startDate)));
            List<ChronologyDTO> previousTrips = getPreviousTrips(vesselGuid, startDate, count);
            List<ChronologyDTO> nextTrips = getNextTrips(vesselGuid, startDate, count);
            previousTrips.addAll(0,getPreviousConcurrentTrips(tripId, vesselGuid, startDate, count));
            nextTrips.addAll(0, getNextConcurrentTrips(tripId, vesselGuid, startDate, count));
            Collections.reverse(nextTrips);

            Map<String, Integer> countMap = calculateTripCounts(count, previousTrips.size(), nextTrips.size());

            chronologyTripDTO.setPreviousTrips(previousTrips.subList(0, countMap.get(PREVIOUS)));
            chronologyTripDTO.setNextTrips(nextTrips.subList(nextTrips.size() - countMap.get(NEXT), nextTrips.size()));
        }
        return chronologyTripDTO;
    }

    private Map<String, Integer> calculateTripCounts(Integer count, Integer previousTripCount, Integer nextTripCount) {

        Integer previous = 0;
        Integer next = 0;

        if (count == 0) {
            log.info("All the previous and next result will be returned");
            previous = previousTripCount;
            next = nextTripCount;
        } else if (count != 1) {
            log.info("Count the number of previous and next result based on count received");
            count = count - 1; // FIXME squid:S1226 intoduce a new variable instead of reusing
            if (count % 2 == 0) {
                previous = count / 2;
                next = count / 2;
            } else if (count % 2 == 1) {
                previous = count / 2 + 1;
                next = count / 2;
            }
            if (previous > previousTripCount) {
                previous = previousTripCount;
                next = count - previous;
            }
            if (next > nextTripCount) {
                next = nextTripCount;
                previous = count - next;
                if (previous > previousTripCount) {
                    previous = previousTripCount;
                }
            }
        }
        return ImmutableMap.<String, Integer>builder().put(PREVIOUS, previous).put(NEXT, next).build();
    }

    private List<ChronologyDTO> getPreviousTrips(String vesselGuid, Date startDate, Integer limit) {
        return fishingTripIdentifierDao.getPreviousTrips(vesselGuid, startDate, limit)
                .map(trip -> new ChronologyDTO(trip.getTripId(), trip.getTripDate()))
                .collect(Collectors.toList());
    }

    private List<ChronologyDTO> getNextTrips(String vesselGuid, Date startDate, Integer limit) {
        return fishingTripIdentifierDao.getNextTrips(vesselGuid, startDate,limit)
                .map(trip -> new ChronologyDTO(trip.getTripId(), trip.getTripDate()))
                .collect(Collectors.toList());
    }

    private List<ChronologyDTO> getPreviousConcurrentTrips(String tripId, String vesselGuid, Date startDate, Integer limit) {
        return fishingTripIdentifierDao.getPreviousConcurrentTrips(tripId, vesselGuid, startDate, limit)
                .map(trip -> new ChronologyDTO(trip.getTripId(), trip.getTripDate()))
                .collect(Collectors.toList());
    }

    private List<ChronologyDTO> getNextConcurrentTrips(String tripId, String vesselGuid, Date startDate, Integer limit) {
        return fishingTripIdentifierDao.getNextConcurrentTrips(tripId, vesselGuid, startDate, limit)
                .map(trip -> new ChronologyDTO(trip.getTripId(), trip.getTripDate()))
                .collect(Collectors.toList());
    }

    @Override
    public VesselDetailsDTO getVesselDetailsForFishingTrip(final String fishingTripId) throws ServiceException {
        if (fishingTripId == null) {
            throw new IllegalArgumentException("PARAMETER CANNOT BE NULL");
        }
        VesselDetailsDTO detailsDTO = null;
        try {
            VesselTransportMeansEntity latestVesselByTripId = vesselTransportMeansDao.findLatestVesselByTripId(fishingTripId);
            if (latestVesselByTripId != null) {
                FishingActivityEntity parent = latestVesselByTripId.getFishingActivity();
                detailsDTO = getVesselDetailsDTO(latestVesselByTripId, parent);
            }
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
        return detailsDTO;
    }

    @Nullable
    private VesselDetailsDTO getVesselDetailsDTO(VesselTransportMeansEntity vesselTransportMeansEntity, FishingActivityEntity fishingActivityEntity) {
        VesselDetailsDTO detailsDTO;
        detailsDTO = VesselTransportMeansMapper.INSTANCE.map(vesselTransportMeansEntity);

        getMdrCodesEnrichWithAssetsModuleDataIfNeeded(detailsDTO);

        if (fishingActivityEntity != null) {
            VesselStorageCharacteristicsEntity sourceVesselCharId = fishingActivityEntity.getSourceVesselCharId();
            if (detailsDTO != null) {
                detailsDTO.setStorageDto(VesselStorageCharacteristicsMapper.INSTANCE.mapToStorageDto(sourceVesselCharId));
            }
        }
        return detailsDTO;
    }


    //To process MDR code list and compare with  database:vesselTransportMeansDao and then enrich with asset module
    private void getMdrCodesEnrichWithAssetsModuleDataIfNeeded(VesselDetailsDTO vesselDetailsDTO) {
        final String ACRONYM = "FLUX_VESSEL_ID_TYPE";
        final String filter = "*";
        final List<String> columnsList = new ArrayList<>();
        Integer nrOfResults = 9999999;
        if (vesselDetailsDTO != null) {
            List<String> codeList;
            try {
                codeList = mdrModuleService.getAcronymFromMdr(ACRONYM, filter, columnsList, nrOfResults, "code").get("code");
                Set<AssetIdentifierDto> vesselIdentifiers = vesselDetailsDTO.getVesselIdentifiers();

                List<AssetListCriteriaPair> assetListCriteriaPairs = BaseMapper.mapMdrCodeListToAssetListCriteriaPairList(vesselIdentifiers, codeList);
                log.info("Asset Criteria Pair List size is :" + assetListCriteriaPairs.size());
                log.info("Got code list of size from mdr:" + codeList.size());
                if (!CollectionUtils.isEmpty(assetListCriteriaPairs)) {
                    AssetListCriteria criteria = new AssetListCriteria();
                    criteria.setIsDynamic(false); // need to set this
                    criteria.getCriterias().addAll(assetListCriteriaPairs);
                    AssetListQuery query = new AssetListQuery();
                    AssetListPagination assetListPagination = new AssetListPagination();
                    assetListPagination.setPage(1);   //need to set this
                    assetListPagination.setListSize(1000);   //need to set this
                    query.setPagination(assetListPagination);
                    query.setAssetSearchCriteria(criteria);
                    List<Asset> assetList = assetModuleService.getAssetListResponse(query);
                    if (null != assetList && !CollectionUtils.isEmpty(assetList)) {
                        vesselDetailsDTO.enrichIdentifiers(assetList.get(0));
                    }
                }
            } catch (ServiceException e) {
                log.error("Error while trying to send message to Assets module.", e);
            }
        }
    }


    @Override
    public FishingTripSummaryViewDTO getFishingTripSummaryAndReports(String fishingTripId, List<Dataset> datasets) throws ServiceException {
        List<ReportDTO> reportDTOList = new ArrayList<>();
        Geometry multipolygon = getRestrictedAreaGeom(datasets);
        Map<String, FishingActivityTypeDTO> summary = populateFishingActivityReportListAndFishingTripSummary(fishingTripId, reportDTOList, multipolygon, false);
        return populateFishingTripSummary(fishingTripId, reportDTOList, summary);
    }

    private Geometry getRestrictedAreaGeom(List<Dataset> datasets) throws ServiceException {
        if (CollectionUtils.isEmpty(datasets)) {
            return null;
        }
        try {
            List<AreaIdentifierType> areaIdentifierTypes = UsmUtils.convertDataSetToAreaId(datasets);
            String areaWkt = spatialModule.getFilteredAreaGeom(areaIdentifierTypes);
            Geometry geometry = GeometryMapper.INSTANCE.wktToGeometry(areaWkt).getValue();
            geometry.setSRID(GeometryUtils.DEFAULT_EPSG_SRID);
            return geometry;
        } catch (ParseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * Populates and return a FishingTripSummaryViewDTO with the inputParameters values.
     *
     * @param fishingTripId
     * @param reportDTOList
     * @param summary
     * @return fishingTripSummaryViewDTO
     */
    private FishingTripSummaryViewDTO populateFishingTripSummary(String fishingTripId, List<ReportDTO> reportDTOList, Map<String, FishingActivityTypeDTO> summary) {
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO = new FishingTripSummaryViewDTO();
        fishingTripSummaryViewDTO.setActivityReports(reportDTOList);
        fishingTripSummaryViewDTO.setSummary(summary);
        fishingTripSummaryViewDTO.setFishingTripId(fishingTripId);
        return fishingTripSummaryViewDTO;
    }

    /**
     * @param fishingTripId     - Fishing trip summary will be collected for this tripID
     * @param reportDTOList     - This DTO will have details about Fishing Activities.Method will process and populate data into this list     *
     * @param multipolygon      - Activities only in this area would be selected
     * @param isOnlyTripSummary - This method you could reuse to only get Fishing trip summary as well
     * @throws ServiceException
     */

    @Override
    public Map<String, FishingActivityTypeDTO> populateFishingActivityReportListAndFishingTripSummary(String fishingTripId, List<ReportDTO> reportDTOList,
                                                                                                      Geometry multipolygon, boolean isOnlyTripSummary) throws ServiceException {
        List<FishingActivityEntity> fishingActivityList = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId, multipolygon);
        fishingActivityList.addAll(getReportsThatWereCancelledOrDeleted(fishingActivityList));
        if (CollectionUtils.isEmpty(fishingActivityList)) {
            return Collections.emptyMap();
        }
        Map<String, FishingActivityTypeDTO> tripSummary = new HashMap<>();
        for (FishingActivityEntity activityEntity : fishingActivityList) {
            if (!isOnlyTripSummary) {
                ReportDTO reportDTO = FishingActivityMapper.INSTANCE.mapToReportDTO(activityEntity);
                reportDTOList.add(reportDTO);
            }
            if (activityEntity != null && activityEntity.getFaReportDocument() != null && DECLARATION.equalsIgnoreCase(activityEntity.getFaReportDocument().getTypeCode())) {
                // FA Report should be of type Declaration. And Fishing Activity type should be Either Departure,Arrival or Landing
                populateFishingTripSummary(activityEntity, tripSummary);
            }
        }
        return tripSummary;
    }

    private List<FishingActivityEntity> getReportsThatWereCancelledOrDeleted(List<FishingActivityEntity> fishingActivityList) {
        Map<FaReportDocumentEntity, FishingActivityEntity> responseMap = new HashMap<>();
        List<Integer> cancelledByDeletedByList = new ArrayList<>();
        List<FishingActivityEntity> cancelledOrDeletedActivities = new ArrayList<>();
        if (CollectionUtils.isEmpty(fishingActivityList)) {
            return new ArrayList<>();
        }
        for (FishingActivityEntity fishingActivityEntity : fishingActivityList) {
            if (fishingActivityEntity.getCanceledBy() != null) {
                cancelledByDeletedByList.add(fishingActivityEntity.getCanceledBy());
                cancelledOrDeletedActivities.add(fishingActivityEntity);
            }
            if (fishingActivityEntity.getDeletedBy() != null) {
                cancelledByDeletedByList.add(fishingActivityEntity.getDeletedBy());
                cancelledOrDeletedActivities.add(fishingActivityEntity);
            }
        }
        if (CollectionUtils.isNotEmpty(cancelledByDeletedByList)) {
            List<FaReportDocumentEntity> reportsByIdsList = faReportDocumentDao.findReportsByIdsList(cancelledByDeletedByList);
            if (CollectionUtils.isNotEmpty(reportsByIdsList)) {
                mapFishActToFaReportCounterpart(responseMap, cancelledOrDeletedActivities, reportsByIdsList);
            }
        }
        return mapToFishingActivitiesList(responseMap);
    }

    private List<FishingActivityEntity> mapToFishingActivitiesList(Map<FaReportDocumentEntity, FishingActivityEntity> fishActFaRepMap) {
        List<FishingActivityEntity> fishingActivityEntities = new ArrayList<>();
        if (MapUtils.isEmpty(fishActFaRepMap)) {
            return fishingActivityEntities;
        }
        for(Map.Entry<FaReportDocumentEntity, FishingActivityEntity> entry : fishActFaRepMap.entrySet()){
            FaReportDocumentEntity faRep = entry.getKey();
            FishingActivityEntity fishAct = entry.getValue();
            FishingActivityEntity cloneActivity = SerializationUtils.clone(fishAct);
            cloneActivity.setFaReportDocument(faRep);
            cloneActivity.setFishingGears(fishAct.getFishingGears());
            cloneActivity.setFaCatchs(fishAct.getFaCatchs());
            cloneActivity.setDelimitedPeriods(fishAct.getDelimitedPeriods());
            cloneActivity.setAllRelatedFishingActivities(fishAct.getAllRelatedFishingActivities());
            cloneActivity.setFisheryTypeCodeListId(fishAct.getFisheryTypeCodeListId());
            cloneActivity.setFluxCharacteristics(fishAct.getFluxCharacteristics());
            fishingActivityEntities.add(cloneActivity);
        }
        return fishingActivityEntities;
    }

    private void mapFishActToFaReportCounterpart(Map<FaReportDocumentEntity, FishingActivityEntity> responseMap, List<FishingActivityEntity> cancelledOrDeletedActivities, List<FaReportDocumentEntity> reportsByIdsList) {
        for (FishingActivityEntity fishingActivityEntity : cancelledOrDeletedActivities) {
            Integer canceledBy = fishingActivityEntity.getCanceledBy();
            Integer deletedBy = fishingActivityEntity.getDeletedBy();
            if (canceledBy != null) {
                for (FaReportDocumentEntity faReportDocumentEntity : reportsByIdsList) {
                    if (canceledBy.equals(faReportDocumentEntity.getId())) {
                        responseMap.put(faReportDocumentEntity, fishingActivityEntity);
                    }
                }
            } else {
                for (FaReportDocumentEntity faReportDocumentEntity : reportsByIdsList) {
                    if (deletedBy.equals(faReportDocumentEntity.getId())) {
                        responseMap.put(faReportDocumentEntity, fishingActivityEntity);
                    }
                }
            }
        }
    }


    public void populateFishingTripSummary(FishingActivityEntity activityEntity, Map<String, FishingActivityTypeDTO> summary) {
        String activityTypeCode = activityEntity.getTypeCode();
        if (DEPARTURE.toString().equalsIgnoreCase(activityTypeCode)
                || ARRIVAL.toString().equalsIgnoreCase(activityTypeCode)
                || LANDING.toString().equalsIgnoreCase(activityTypeCode)) {
            Date occurrence = activityEntity.getOccurence();
            Boolean isCorrection = BaseMapper.getCorrection(activityEntity);
            FishingActivityTypeDTO fishingActivityTypeDTO = summary.get(activityTypeCode);
            if (fishingActivityTypeDTO == null
                    || (isCorrection
                    && fishingActivityTypeDTO.getDate() != null
                    && occurrence != null
                    && occurrence.compareTo(fishingActivityTypeDTO.getDate()) > 0)) {
                fishingActivityTypeDTO = new FishingActivityTypeDTO();
                fishingActivityTypeDTO.setDate(occurrence);
                summary.put(activityTypeCode, fishingActivityTypeDTO);
            }
        }
    }

    /**
     * Gets the counters for the trip with this tripId.
     *
     * @param tripId
     * @return
     */
    @Override
    public MessageCountDTO getMessageCountersForTripId(String tripId) {
        List<FaReportDocumentEntity> reports = faReportDocumentDao.loadReports(tripId, "N");
        if(CollectionUtils.isNotEmpty(reports)){
            List<FaReportDocumentEntity> canceledDeletedReports = faReportDocumentDao.loadCanceledAndDeletedReports(reports);
            if(CollectionUtils.isNotEmpty(canceledDeletedReports)){
                reports.addAll(canceledDeletedReports);
            }
        }
        return createMessageCounter(reports);
    }

    public FileOutputStream getLogBookReport(String tripId,String consolidated,String vesselId,String schemeId,String startDate,String endDate) throws ServiceException {
        List<FaReportDocumentEntity> faReportDocumentEntities = faReportDocumentDao.loadReports(tripId, consolidated, vesselId, schemeId, startDate, endDate);
        List<FaReportDTO> jasperReportData = getJasperReportData(faReportDocumentEntities);
        JRBeanCollectionDataSource  dsCollection = new JRBeanCollectionDataSource(jasperReportData);
        Map<String,Object> params = new HashMap<>();
        params.put("CollectionBeanParam",dsCollection);

        try {
            InputStream input = new FileInputStream(new File("C:\\Users\\inomikos\\eclipse-workspace\\JasperReports\\src\\main\\java\\sample_report.jrxml"));
            JasperDesign jasperDesign = JRXmlLoader.load(input);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
            FileOutputStream outputStream = new FileOutputStream(tripId+".pdf");
            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            outputStream.write(bytes);
            return  outputStream;
        } catch (JRException | IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }


    private List<FaReportDTO> getJasperReportData(List<FaReportDocumentEntity> faReportDocumentEntities){
        List<FaReportDTO> reportDTOList = new ArrayList<>();

        for(FaReportDocumentEntity documentEntity:faReportDocumentEntities){
            FaReportDTO reportDTO = new FaReportDTO();
            reportDTO.setId(documentEntity.getId());
            reportDTO.setFmcMarker(documentEntity.getFmcMarker());
            reportDTO.setSource(documentEntity.getSource());
            reportDTO.setStatus(documentEntity.getStatus());
            reportDTO.setTypeCode(documentEntity.getTypeCode());
            reportDTO.setDateTime(documentEntity.getAcceptedDatetime() == null? null:documentEntity.getAcceptedDatetime().toString());
            reportDTOList.add(reportDTO);
        }
            return reportDTOList;
    }

    private String getJasperFilePath(){
        String internalPath = this.getClass().getName().replace(".", File.separator).replace("FishingTripServiceBean","");
        String externalPath = System.getProperty("user.dir")+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+ "sample_report.jrxml";
//        String workDir = externalPath+File.separator+internalPath.substring(0, internalPath.lastIndexOf(File.separator));
        return  internalPath;
//        return Paths.get("").toAbsolutePath()+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_report.jrxml";
    }

    /**
     * Populates the MessageCounter adding to the right counter 1 unit depending on the type of report (typeCode, purposeCode, size()).
     *
     * @param faReportDocumentList
     */
    public MessageCountDTO createMessageCounter(List<FaReportDocumentEntity> faReportDocumentList) {

        MessageCountDTO messagesCounter = new MessageCountDTO();
        if (CollectionUtils.isEmpty(faReportDocumentList)) {
            return messagesCounter;
        }

        // Reports total
        messagesCounter.setNoOfReports(faReportDocumentList.size());

        for (FaReportDocumentEntity faReport : faReportDocumentList) {
            String faDocumentType = faReport.getTypeCode();
            FaReportStatusType status = FaReportStatusType.valueOf(faReport.getStatus());

            // Declarations / Notifications
            if (DECLARATION.equalsIgnoreCase(faDocumentType)) {
                messagesCounter.setNoOfDeclarations(messagesCounter.getNoOfDeclarations() + 1);
            } else if (NOTIFICATION.equalsIgnoreCase(faDocumentType)) {
                messagesCounter.setNoOfNotifications(messagesCounter.getNoOfNotifications() + 1);
            }

            // Fishing operations
            Set<FishingActivityEntity> faEntitiyList = faReport.getFishingActivities();
            if (isNotEmpty(faEntitiyList)) {
                for (FishingActivityEntity faEntity : faEntitiyList) {
                    if (FishingActivityTypeEnum.FISHING_OPERATION.toString().equalsIgnoreCase(faEntity.getTypeCode())) {
                        messagesCounter.setNoOfFishingOperations(messagesCounter.getNoOfFishingOperations() + 1);
                    }
                }
            }

            // PurposeCode : Deletions / Cancellations / Corrections
            if (FaReportStatusType.DELETED.equals(status)) {
                messagesCounter.setNoOfDeletions(messagesCounter.getNoOfDeletions() + 1);
            } else if (FaReportStatusType.CANCELED.equals(status)) {
                messagesCounter.setNoOfCancellations(messagesCounter.getNoOfCancellations() + 1);
            } else if (FaReportStatusType.UPDATED.equals(status)) {
                messagesCounter.setNoOfCorrections(messagesCounter.getNoOfCorrections() + 1);
            }
        }
        return messagesCounter;
    }

    /**
     * Retrieves all the catches for the given fishing trip;
     *
     * @param fishingTripId
     * @return
     */
    @Override
    public Map<String, CatchSummaryListDTO> retrieveFaCatchesForFishingTrip(String fishingTripId) {
        return FaCatchMapper.INSTANCE.mapCatchesToSummaryDTO(faCatchDao.findFaCatchesByFishingTrip(fishingTripId));
    }

    /**
     * Retrieve GEO data for fishing trip Map for tripID
     *
     * @param tripId
     * @return
     */
    @Override
    public ObjectNode getTripMapDetailsForTripId(String tripId) {
        log.info("Get GEO data for Fishing Trip for tripId:" + tripId);
        List<FaReportDocumentEntity> faReportDocumentEntityList = faReportDocumentDao.loadReports(tripId, "Y");
        List<Geometry> geoList = new ArrayList<>();
        for (FaReportDocumentEntity entity : faReportDocumentEntityList) {
            if (entity.getGeom() != null)
                geoList.add(entity.getGeom());
        }
        return FishingTripToGeoJsonMapper.toJson(geoList);
    }

    /**
     * This method filters fishing Trips for Reporting module
     */
    @Override
    public FishingTripResponse filterFishingTripsForReporting(FishingActivityQuery query) throws ServiceException {
        log.info("getFishingTripResponse For Filter");
        if ((MapUtils.isEmpty(query.getSearchCriteriaMap()) && MapUtils.isEmpty(query.getSearchCriteriaMapMultipleValues()))
                || activityServiceBean.checkAndEnrichIfVesselFiltersArePresent(query)) {
            return new FishingTripResponse();
        }
        // As per business usecase, period_start and period_end date is MUST to filter fishing trip Ids on Reporting.
        Map<SearchFilter, String> searchFilters = query.getSearchCriteriaMap();
        if (searchFilters.get(SearchFilter.PERIOD_START) == null || searchFilters.get(SearchFilter.PERIOD_END) == null) {
            throw new ServiceException("Either PERIOD_START or PERIOD_END not present. Please provide values for both.");
        }
        Set<FishingTripId> fishingTripIds = fishingTripDao.getFishingTripIdsForMatchingFilterCriteria(query,false);
        // checkThresholdForFishingTripList(fishingTripIds); // If size of Ids retrieved is more than threshold, Error will be thrown and then user would need to apply more filters to retrict the data.
        log.debug("Fishing trips received from db:" + fishingTripIds.size());
        // build Fishing trip response from FishingTripEntityList and return
        return buildFishingTripSearchRespose(fishingTripIds, true);
    }


    public void checkThresholdForFishingTripList(Set<FishingTripId> fishingTripIds) throws ServiceException {
        if (CollectionUtils.isNotEmpty(fishingTripIds)) {
            String tresholdTrips = activityConfigurationDao.getPropertyValue(ActivityConfiguration.LIMIT_FISHING_TRIPS);
            if (tresholdTrips != null) {
                int threshold = Integer.parseInt(tresholdTrips);
                log.debug("fishing trip threshold value:" + threshold);
                if (fishingTripIds.size() > threshold)
                    throw new ServiceException("Fishing Trips found for matching criteria exceed threshold value. Please restrict resultset by modifying filters");
                log.info("fishing trip list size is within threshold value:" + fishingTripIds.size());
            }
        }
    }


    /**
     * This method filters fishing Trips for Activity tab
     */
    @Override
    public FishingTripResponse filterFishingTrips(FishingActivityQuery query) throws ServiceException {
        log.info("getFishingTripResponse For Filter");
        if ((MapUtils.isEmpty(query.getSearchCriteriaMap()) && MapUtils.isEmpty(query.getSearchCriteriaMapMultipleValues()))
                || activityServiceBean.checkAndEnrichIfVesselFiltersArePresent(query)) {
            return new FishingTripResponse();
        }
        Set<FishingTripId> fishingTripIds = fishingTripDao.getFishingTripIdsForMatchingFilterCriteria(query,true);
        Integer totalCountOfRecords = fishingTripDao.getCountOfFishingTripsForMatchingFilterCriteria(query);
        log.debug("Total count of records: {} ", totalCountOfRecords);
        FishingTripResponse fishingTripResponse = buildFishingTripSearchRespose(fishingTripIds, false);
        fishingTripResponse.setTotalCountOfRecords(BigInteger.valueOf(totalCountOfRecords));
        return fishingTripResponse;
    }


    /**
     * This method builds FishingTripSerachReponse objectc for FishingTripIds passed to the method
     * collectFishingActivities : If the value is TRUE, all fishing Activities for every fishing Trip would be sent in the response.
     * If the value is FALSE, No fishing activities would be sent in the response.
     */
    FishingTripResponse buildFishingTripSearchRespose(Set<FishingTripId> fishingTripIds, boolean collectFishingActivities) throws ServiceException {
        if (fishingTripIds == null || fishingTripIds.isEmpty()) {
            return new FishingTripResponse();
        }
        List<Integer> uniqueActivityIdList = new ArrayList<>();
        List<FishingActivitySummary> fishingActivitySummaries = new ArrayList<>();

        List<FishingTripIdWithGeometry> fishingTripIdLists = new ArrayList<>();
        for (FishingTripId fishingTripId : fishingTripIds) {

            FishingActivityQuery query = new FishingActivityQuery();
            Map<SearchFilter, String> searchCriteriaMap = new EnumMap<>(SearchFilter.class);
            searchCriteriaMap.put(SearchFilter.TRIP_ID, fishingTripId.getTripId());
            searchCriteriaMap.put(SearchFilter.FISHING_TRIP_SCHEME_ID, fishingTripId.getSchemeID());
            query.setSearchCriteriaMap(searchCriteriaMap);
            SortKey sortKey = new SortKey();
            sortKey.setSortBy(SearchFilter.PERIOD_START); // this is important to find out first and last fishing activity for the Fishing Trip
            sortKey.setReversed(false);
            query.setSorting(sortKey);
            List<FishingActivityEntity> fishingActivityEntityList = fishingActivityDao.getFishingActivityListByQuery(query);
            if (collectFishingActivities) {
                List<FishingActivityEntity> cleanedList = cleanFromDeletionsAndCancelations(fishingActivityEntityList);
                fishingActivitySummaries.addAll(getFishingActivitySummaryList(cleanedList, uniqueActivityIdList));
            }

            FishingTripIdWithGeometry fishingTripIdWithGeometry = new FishingTripIdWithGeometryMapper().mapToFishingTripIdWithDetails(fishingTripId, fishingActivityEntityList);
            fishingTripIdLists.add(fishingTripIdWithGeometry);
        }

        new FishingTripIdWithGeometryMapper().populateGeometryFromAssetModule(fishingTripIdLists,assetModuleService);
        // populate response object
        FishingTripResponse response = new FishingTripResponse();
        response.setFishingActivityLists(fishingActivitySummaries);
        response.setFishingTripIdLists(fishingTripIdLists);
        return response;
    }

    private List<FishingActivityEntity> cleanFromDeletionsAndCancelations(List<FishingActivityEntity> fishingActivityEntityList) {
        List<FishingActivityEntity> cleanList = new ArrayList<>();
        if(CollectionUtils.isEmpty(fishingActivityEntityList)){
            return cleanList;
        }
        String DELETED_STR = FaReportStatusType.DELETED.name();
        String CANCELLED_STR = FaReportStatusType.CANCELED.name();
        for (FishingActivityEntity fishingActivityEntity : fishingActivityEntityList) {
            String status = fishingActivityEntity.getFaReportDocument().getStatus();
            if(!DELETED_STR.equals(status) && !CANCELLED_STR.equals(status) && fishingActivityEntity.getLatest()){
                cleanList.add(fishingActivityEntity);
            }
        }
        return cleanList;
    }


    /**
     * This method creates FishingActivitySummary object from FishingActivityEntity object retrieved from database.
     *
     * @param uniqueActivityIdList      This method helps parent function to collect FishingActivities for all the fishingTrips. In order to avoid duplicate fishing Activities, we need to maintain uniqueActivityIdList
     * @param fishingActivityEntityList
     * @return
     */
    public List<FishingActivitySummary> getFishingActivitySummaryList(List<FishingActivityEntity> fishingActivityEntityList, List<Integer> uniqueActivityIdList) {
        List<FishingActivitySummary> fishingActivitySummaryList = new ArrayList<>();
        if (CollectionUtils.isEmpty(uniqueActivityIdList)) {
            uniqueActivityIdList = new ArrayList<>();
        }
        for (FishingActivityEntity fishingActivityEntity : fishingActivityEntityList) {
            if (fishingActivityEntity != null && uniqueActivityIdList.add(fishingActivityEntity.getId())) {
                FishingActivitySummary fishingActivitySummary = FishingActivityMapper.INSTANCE.mapToFishingActivitySummary(fishingActivityEntity);
                ContactPartyEntity contactParty = getContactParty(fishingActivityEntity);
                if (contactParty != null) {
                    VesselContactPartyType vesselContactParty = FishingActivityMapper.INSTANCE.mapToVesselContactParty(contactParty);
                    fishingActivitySummary.setVesselContactParty(vesselContactParty);
                }
                if (fishingActivitySummary != null) {
                    fishingActivitySummaryList.add(fishingActivitySummary);
                }
            }
        }
        return fishingActivitySummaryList;
    }

    private ContactPartyEntity getContactParty(FishingActivityEntity fishingActivity) {
        if ((fishingActivity.getFaReportDocument() != null)
                && (fishingActivity.getFaReportDocument().getVesselTransportMeans() != null)
                && (!fishingActivity.getFaReportDocument().getVesselTransportMeans().isEmpty())
                && (fishingActivity.getFaReportDocument().getVesselTransportMeans().iterator().next().getContactParty() != null)
                && (!fishingActivity.getFaReportDocument().getVesselTransportMeans().iterator().next().getContactParty().isEmpty())) {
            return fishingActivity.getFaReportDocument().getVesselTransportMeans().iterator().next().getContactParty().iterator().next();
        }
        return null;
    }

    @Override
    /**
     *  Returns TripWidgetDto based on the tripId and activityId
     */
    public TripWidgetDto getTripWidgetDto(FishingActivityEntity activityEntity, String tripId) {
        if (activityEntity == null && tripId == null) {
            return null;
        }
        TripWidgetDto tripWidgetDto = new TripWidgetDto();
        try {
            if (tripId != null) {
                log.debug("Trip Id found for Fishing Activity. Get TripWidget information for tripID :" + tripId);
                TripOverviewDto tripOverviewDto = getTripOverviewDto(activityEntity, tripId);
                List<TripOverviewDto> tripOverviewDtoList = new ArrayList<>();
                tripOverviewDtoList.add(tripOverviewDto);
                tripWidgetDto.setTrips(tripOverviewDtoList);
                if (activityEntity != null && activityEntity.getFaReportDocument() != null && CollectionUtils.isNotEmpty(activityEntity.getFaReportDocument().getVesselTransportMeans())) {
                    Set<VesselTransportMeansEntity> vesselTransportMeansEntities = activityEntity.getFaReportDocument().getVesselTransportMeans();
                    for (VesselTransportMeansEntity vesselTransportMeansEntity : vesselTransportMeansEntities) {
                        if (vesselTransportMeansEntity.getFishingActivity() == null) {
                            tripWidgetDto.setVesselDetails(getVesselDetailsDTO(vesselTransportMeansEntity, activityEntity));
                            break;
                        }
                    }
                }
                // VesselDetailsDTO detailsDTO = getVesselDetailsForFishingTrip(tripId);
                // tripWidgetDto.setVesselDetails(detailsDTO);
                log.debug("tripWidgetDto set for tripID :" + tripId);
            } else {
                log.debug("TripId is not received for the screen. Try to get TripSummary information for all the tripIds specified for FishingActivity:" + activityEntity.getId());
                return createTripWidgetDtoWithFishingActivity(activityEntity);
            }
        } catch (ServiceException e) {
            log.error("Error while creating TripWidgetDto.", e);
        }
        return tripWidgetDto;
    }

    @Override
    public CatchEvolutionDTO retrieveCatchEvolutionForFishingTrip(String fishingTripId) throws ServiceException {
        CatchEvolutionDTO catchEvolution = new CatchEvolutionDTO();
        List<FishingActivityEntity> fishingActivities = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId, null);
        catchEvolution.setTripDetails(getTripWidgetDto(fishingActivities.get(0), fishingTripId));
        catchEvolution.setCatchEvolutionProgress(catchProgressProcessor.process(fishingActivities));
        return catchEvolution;
    }

    private void setFlapDocuments(VesselDetailsDTO detailsDTO, FishingActivityEntity parent, TripWidgetDto tripWidgetDto) {
        FlapDocumentDto flapDocumentDto = FlapDocumentMapper.INSTANCE.mapToFlapDocumentDto(parent.getFirstFlapDocument());
        if (flapDocumentDto != null) {
            tripWidgetDto.setFlapDocuments(Sets.newSet(flapDocumentDto));
        }
        tripWidgetDto.setVesselDetails(detailsDTO);
    }

    public TripWidgetDto createTripWidgetDtoWithFishingActivity(FishingActivityEntity activityEntity) throws ServiceException {
        TripWidgetDto tripWidgetDto = new TripWidgetDto();
        Set<FishingTripEntity> fishingTripEntities = activityEntity.getFishingTrips();

        if (CollectionUtils.isEmpty(fishingTripEntities)) {
            throw new ServiceException(" Could not find fishingTrips associated with FishingActivity id :" + activityEntity.getId());
        }
        List<TripOverviewDto> tripOverviewDtoList = new ArrayList<>();
        // try to find unique tripIds for the fishing Activity
        Set<String> tripIdSet = new HashSet<>();
        for (FishingTripEntity fishingTripEntity : fishingTripEntities) {
            Set<FishingTripIdentifierEntity> identifierEntities = fishingTripEntity.getFishingTripIdentifiers();
            for (FishingTripIdentifierEntity tripIdentifierEntity : identifierEntities) {
                if (!tripIdSet.contains(tripIdentifierEntity.getTripId())) {
                    log.debug("Get Trip summary information for tripID :" + tripIdentifierEntity.getTripId());
                    tripOverviewDtoList.add(getTripOverviewDto(activityEntity, tripIdentifierEntity.getTripId()));
                    tripIdSet.add(tripIdentifierEntity.getTripId());
                }
            }
        }
        tripWidgetDto.setTrips(tripOverviewDtoList);
        //As per new requirement, vessel should always be the one associated with fishing Activity in the trip widget
        if (activityEntity.getFaReportDocument() != null && CollectionUtils.isNotEmpty(activityEntity.getFaReportDocument().getVesselTransportMeans())) {
            Set<VesselTransportMeansEntity> vesselTransportMeansEntities = activityEntity.getFaReportDocument().getVesselTransportMeans();
            for (VesselTransportMeansEntity vesselTransportMeansEntity : vesselTransportMeansEntities) {
                if (vesselTransportMeansEntity.getFishingActivity() == null) {
                    tripWidgetDto.setVesselDetails(getVesselDetailsDTO(vesselTransportMeansEntity, activityEntity));
                    break;
                }
            }

        }
        return tripWidgetDto;
    }

    public List<FishingActivityEntity> getAllFishingActivitiesForTrip(String tripId) throws ServiceException {
        if (tripId == null) {
            throw new ServiceException("tripId is null. Please provide valid tripId");
        }
        return fishingTripDao.getFishingActivitiesForFishingTripId(tripId);

    }

    public TripOverviewDto getTripOverviewDto(FishingActivityEntity activityEntity, String tripId) throws ServiceException {
        Map<String, FishingActivityTypeDTO> typeDTOMap = populateFishingActivityReportListAndFishingTripSummary(tripId, null, null, true);
        TripOverviewDto tripOverviewDto = new TripOverviewDto();
        Set<FishingTripEntity> fishingTripEntities = activityEntity.getFishingTrips(); // Find out fishingTrip schemeId matching to tripId from fishingActivity object.
        if (CollectionUtils.isNotEmpty(fishingTripEntities)) {
            for (FishingTripEntity fishingTripEntity : fishingTripEntities) {
                Set<FishingTripIdentifierEntity> identifierEntities = fishingTripEntity.getFishingTripIdentifiers();
                for (FishingTripIdentifierEntity tripIdentifierEntity : identifierEntities) {
                    if (tripId.equalsIgnoreCase(tripIdentifierEntity.getTripId())) {
                        TripIdDto tripIdDto = new TripIdDto();
                        tripIdDto.setId(tripId);
                        tripIdDto.setSchemeId(tripIdentifierEntity.getTripSchemeId());
                        List<TripIdDto> tripIdList = new ArrayList<>();
                        tripIdList.add(tripIdDto);
                        tripOverviewDto.setTripId(tripIdList);
                        tripOverviewDto.setTypeCode(fishingTripEntity.getTypeCode());
                        break;
                    }
                }
            }
        }

        populateTripOverviewDto(typeDTOMap, tripOverviewDto);
        return tripOverviewDto;
    }

    private void populateTripOverviewDto(Map<String, FishingActivityTypeDTO> typeDTOMap, TripOverviewDto tripOverviewDto) {
        for (Map.Entry<String, FishingActivityTypeDTO> entry : typeDTOMap.entrySet()) {
            String key = entry.getKey();
            FishingActivityTypeDTO fishingActivityTypeDTO = entry.getValue();
            switch (FishingActivityTypeEnum.valueOf(key)) {
                case DEPARTURE:
                    tripOverviewDto.setDepartureTime(fishingActivityTypeDTO.getDate());
                    break;
                case ARRIVAL:
                    tripOverviewDto.setArrivalTime(fishingActivityTypeDTO.getDate());
                    break;
                case LANDING:
                    tripOverviewDto.setLandingTime(fishingActivityTypeDTO.getDate());
                    break;
                default:
                    log.debug("Fishing Activity type found is :" + key);
                    break;
            }
        }
    }

    @Override
    public String getOwnerFluxPartyFromTripId(String tripId) {
        return fishingTripDao.getOwnerFluxPartyFromTripId(tripId);
    }

}
