/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.activity.message.producer.EventProducer;
import eu.europa.ec.fisheries.uvms.activity.service.AssetModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.FluxMessageService;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FluxFaReportMessageMapper;
import eu.europa.ec.fisheries.uvms.activity.service.util.GeomUtil;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;

@Stateless
@Transactional
@Slf4j
public class FluxMessageServiceBean extends BaseActivityBean implements FluxMessageService {

    private FaReportDocumentDao faReportDocumentDao;

    @EJB
    private AssetModuleService assetService;

    @Inject
    FluxFaReportMessageMapper fluxFaReportMessageMapper;

    @Inject
    EventProducer eventProducer;

    @PostConstruct
    public void init() {
        faReportDocumentDao = new FaReportDocumentDao(entityManager);
    }

    @Override
    public FluxFaReportMessageEntity saveFishingActivityReportDocuments(FLUXFAReportMessage faReportMessage, FaReportSourceEnum faReportSourceEnum) {
        log.info("[START] Going to save [{}] FaReportDocuments.", faReportMessage.getFAReportDocuments().size());

        FluxFaReportMessageEntity messageEntity = fluxFaReportMessageMapper.mapToFluxFaReportMessage(faReportMessage, faReportSourceEnum);

        persistAndUpdateFishingActivityReport(messageEntity);

        eventProducer.sendActivityEvent(messageEntity);

        return messageEntity;
    }

    @Override
    public void saveFishingActivityReportDocuments(FluxFaReportMessageEntity messageEntity) {
        log.info("[START] Going to save [{}] FaReportDocuments.", messageEntity.getFaReportDocuments().size());
        persistAndUpdateFishingActivityReport(messageEntity);
    }

    private void persistAndUpdateFishingActivityReport(FluxFaReportMessageEntity messageEntity) {
        entityManager.merge(messageEntity);

        for (FaReportDocumentEntity faReportDocument : messageEntity.getFaReportDocuments()) {
            updateGeometry(faReportDocument);
            enrichFishingActivityWithGuiID(faReportDocument);
        }
        log.debug("Saved partial FluxFaReportMessage before further processing");

        updateFaReportCorrectionsOrCancellations(messageEntity.getFaReportDocuments());
        log.debug("Updating FaReport Corrections is complete.");

        updateFishingTripStartAndEndDate(messageEntity.getFaReportDocuments());
        log.info("[END] FluxFaReportMessage Saved successfully.");
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
        List<String> guids = assetService.getAssetGuids(vesselTransport.getVesselIdentifiers());
        if (CollectionUtils.isNotEmpty(guids)) {
            vesselTransport.setGuid(guids.get(0));
        }
    }


    /**
     * If the reference Id (of the received report) exist for any of the FaReport Document, than it means that this is an update to an existing report.
     */
    private void updateFaReportCorrectionsOrCancellations(Set<FaReportDocumentEntity> justReceivedAndSavedFaReports) {
        for (FaReportDocumentEntity justSavedReport : justReceivedAndSavedFaReports) {
            String receivedReferencedFaReportDocumentId = justSavedReport.getFluxReportDocument_ReferencedFaReportDocumentId();
            String receivedReferencedFaReportDocumentSchemeId = justSavedReport.getFluxReportDocument_ReferencedFaReportDocumentSchemeId();
            String justSavedPurposeCode = StringUtils.isNotEmpty(justSavedReport.getFluxReportDocument_PurposeCode()) ?
                    justSavedReport.getFluxReportDocument_PurposeCode() : StringUtils.EMPTY;

            // If we received an original report we have to check if we have previously received a correction/deletion/cancellation related to it.
            if (FaReportStatusType.NEW.getPurposeCode().toString().equals(justSavedPurposeCode)) {
                List<FaReportDocumentEntity> foundRelatedFaReportsCorrOrDelOrCanc = faReportDocumentDao.findFaReportsThatReferTo(justSavedReport.getFluxReportDocument_Id(), justSavedReport.getFluxReportDocument_IdSchemeId());

                if (!foundRelatedFaReportsCorrOrDelOrCanc.isEmpty()) {
                    FaReportDocumentEntity persistentFaDoc = entityManager.find(FaReportDocumentEntity.class, justSavedReport.getId());

                    for (FaReportDocumentEntity foundRelatedFaReportCorrOrDelOrCanc : foundRelatedFaReportsCorrOrDelOrCanc) {
                        String purposeCodeFromDb = foundRelatedFaReportCorrOrDelOrCanc.getFluxReportDocument_PurposeCode();
                        FaReportStatusType faReportStatusEnumFromDb = FaReportStatusType.getFaReportStatusEnum(Integer.parseInt(purposeCodeFromDb));
                        persistentFaDoc.setStatus(faReportStatusEnumFromDb.name());
                        checkAndUpdateActivitiesForCorrectionsAndCancellationsAndDeletions(persistentFaDoc, faReportStatusEnumFromDb, foundRelatedFaReportCorrOrDelOrCanc.getId());
                    }
                }
            }

            // If it has a refId it means that it is a correction/deletion or cancellation message, so we should update the referred entity STATUS (FaReportDocument)..
            if (StringUtils.isNotEmpty(receivedReferencedFaReportDocumentId) && !StringUtils.EMPTY.equals(justSavedPurposeCode)) {
                // Get the document(s) that have the same id as the just received msg's ReferenceId.
                FaReportDocumentEntity foundReferencedFaReport = faReportDocumentDao.findFaReportByIdAndScheme(receivedReferencedFaReportDocumentId, receivedReferencedFaReportDocumentSchemeId);
                if (foundReferencedFaReport != null) { // Means that the report we just received refers to an exising one (Correcting it/Deleting it/Cancelling it)
                    FaReportStatusType faReportStatusEnum = FaReportStatusType.getFaReportStatusEnum(Integer.parseInt(justSavedPurposeCode));
                    // Change status with the new reports status (new report = report that refers to this one = the newly saved one)
                    foundReferencedFaReport.setStatus(faReportStatusEnum.name());
                    // Correction (purposecode == 5) => set 'latest' to false (for each activitiy related to this report)
                    checkAndUpdateActivitiesForCorrectionsAndCancellationsAndDeletions(foundReferencedFaReport, faReportStatusEnum, justSavedReport.getId());
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

    private void updateGeometry(FaReportDocumentEntity faReportDocumentEntity) {
        Set<FishingActivityEntity> fishingActivityEntities = faReportDocumentEntity.getFishingActivities();
        for(FishingActivityEntity each : fishingActivityEntities) {
            if(each.getLatitude() != null && each.getLongitude() != null) {
                each.setGeom(GeomUtil.createPoint(each.getLongitude(), each.getLatitude()));
            }
        }
    }
}
