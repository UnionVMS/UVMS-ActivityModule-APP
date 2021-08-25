/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMapper;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Transactional;
import java.util.*;

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
    private PropertiesBean properties;

    @EJB
    private FishingTripService fishingTripService;

    @EJB
    private FaMessageSaverBean faMessageSaverBean;

    @EJB
    private FishingActivityEnricherBean activityEnricher;

    @PostConstruct
    public void init() {
        initEntityManager();
        faReportDocumentDao = new FaReportDocumentDao(getEntityManager());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public FluxFaReportMessageEntity saveFishingActivityReportDocuments(FLUXFAReportMessage faReportMessage, FaReportSourceEnum faReportSourceEnum) throws ServiceException {
        log.info("[START-SAVING] Going to save [ " + faReportMessage.getFAReportDocuments().size() + " ] FaReportDocuments..");
        FluxFaReportMessageEntity messageEntity = new FluxFaReportMessageMapper().mapToFluxFaReportMessage(null, faReportMessage, faReportSourceEnum, new FluxFaReportMessageEntity());
        Set<FaReportDocumentEntity> faReportDocuments = messageEntity.getFaReportDocuments();
        FluxFaReportMessageEntity entity = faMessageSaverBean.saveReportMessageNow(messageEntity);//trySavingFaReport(messageEntity, 0);
        updateFaReportCorrectionsOrCancellations(entity.getFaReportDocuments());
        log.info("[INFO] Updating FaReport Corrections is complete.");
        updateFishingTripStartAndEndDate(faReportDocuments);
        log.info("[END-SAVING] FluxFaReportMessage Saved successfully.");
        return entity;
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public FluxFaReportMessageEntity saveFishingActivityReportDocuments(FluxFaReportMessageEntity messageEntity) throws ServiceException {
        log.info("[START-SAVING] Going to save [ " + messageEntity.getFaReportDocuments().size() + " ] FaReportDocuments..");
        Set<FaReportDocumentEntity> faReportDocuments = messageEntity.getFaReportDocuments();
        FluxFaReportMessageEntity entity = faMessageSaverBean.saveReportMessageNow(messageEntity);
        log.info("[INFO] Updating FaReport Corrections/Cancellations and Trip start and end dates.");
        updateFaReportCorrectionsOrCancellations(entity.getFaReportDocuments());
        log.info("[END-UPDATING] Updating FaReport Corrections/Cancellations COMPLETE.");
        updateFishingTripStartAndEndDate(faReportDocuments);
        log.info("[END-SAVING] FluxFaReportMessage Saved successfully.");
        return entity;
    }

    /**
     * This method will traverse through all the FishingTrips mentioned in the FaReportDocument and
     * update start and end date for the trip based on the fishing activities reported for the trip
     *
     * @param faReportDocument
     */
    public void calculateFishingTripStartAndEndDate(FaReportDocumentEntity faReportDocument) throws ServiceException {
        Set<FishingActivityEntity> fishingActivities = faReportDocument.getFishingActivities();
        if (CollectionUtils.isEmpty(fishingActivities)) {
            log.error("Could not find FishingActivities for faReportDocument.");
            return;
        }
        for (FishingActivityEntity fishingActivityEntity : fishingActivities) {
            Set<FishingTripEntity> fishingTripEntities = fishingActivityEntity.getFishingTrips();
            if (CollectionUtils.isEmpty(fishingTripEntities)) {
                continue;
            }
            for (FishingTripEntity fishingTripEntity : fishingTripEntities) {
                setTripStartAndEndDateForFishingTrip(fishingTripEntity);
            }
        }
    }

    private void setTripStartAndEndDateForFishingTrip(FishingTripEntity fishingTripEntity) {
        Set<FishingTripIdentifierEntity> identifierEntities = fishingTripEntity.getFishingTripIdentifiers();
        if (CollectionUtils.isEmpty(identifierEntities)) {
            return;
        }
        for (FishingTripIdentifierEntity tripIdentifierEntity : identifierEntities) {
            try {
                List<FishingActivityEntity> fishingActivityEntityList = fishingTripService.getAllFishingActivitiesForTrip(tripIdentifierEntity.getTripId());
                if (CollectionUtils.isNotEmpty(fishingActivityEntityList)) {
                    //Calculate trip start date
                    FishingActivityEntity firstFishingActivity = fishingActivityEntityList.get(0);
                    tripIdentifierEntity.setCalculatedTripStartDate(firstFishingActivity.getCalculatedStartTime());
                    // calculate trip end date
                    Date calculatedTripEndDate;
                    int totalActivities = fishingActivityEntityList.size();
                    if (totalActivities > 1) {
                        calculatedTripEndDate = fishingActivityEntityList.get(totalActivities - 1).getCalculatedStartTime();
                    } else {
                        calculatedTripEndDate = firstFishingActivity.getCalculatedStartTime();
                    }
                    tripIdentifierEntity.setCalculatedTripEndDate(calculatedTripEndDate);
                }
            } catch (Exception e) {
                log.error("Error while trying to calculate FishingTrip start and end Date", e);
            }
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
                List<FaReportDocumentEntity> relatedDocumentsList =
                        faReportDocumentDao.findFaReportByRefIdAndRefScheme(faReportIdentifier.getFluxReportIdentifierId(), faReportIdentifier.getFluxReportIdentifierSchemeId());
                FaReportDocumentEntity foundRelatedFaReportCorrOrDelOrCanc = null;
                if (CollectionUtils.isNotEmpty(relatedDocumentsList)) {
                    Collections.sort(relatedDocumentsList, Comparator.comparing(FaReportDocumentEntity::getAcceptedDatetime));
                    foundRelatedFaReportCorrOrDelOrCanc = relatedDocumentsList.get(0);
                }
                if (foundRelatedFaReportCorrOrDelOrCanc != null) {
                    String purposeCodeFromDb = foundRelatedFaReportCorrOrDelOrCanc.getFluxReportDocument().getPurposeCode();
                    FaReportStatusType faReportStatusEnumFromDb = FaReportStatusType.getFaReportStatusEnum(Integer.parseInt(purposeCodeFromDb));
                    FaReportDocumentEntity persistentFaDoc;
                    try {
                        persistentFaDoc = faReportDocumentDao.findEntityById(FaReportDocumentEntity.class, justSavedReport.getId());
                    } catch (ServiceException e) {
                        log.error("Error while trying to get FaRepDoc from db.");
                        continue;
                    }
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

    private void updateFishingTripStartAndEndDate(Set<FaReportDocumentEntity> faReportDocuments) throws ServiceException {
        log.debug("Start  update of FishingTrip Start And End Date");
        if (CollectionUtils.isEmpty(faReportDocuments)) {
            log.error("FaReportDocuments List is EMPTY or NULL in updateFishingTripStartAndEndDate");
            return;
        }
        for (FaReportDocumentEntity faReportDocument : faReportDocuments) {
            calculateFishingTripStartAndEndDate(faReportDocument);
        }
        log.debug("Update of Start And End Date for all fishingTrips is complete");
    }

}
