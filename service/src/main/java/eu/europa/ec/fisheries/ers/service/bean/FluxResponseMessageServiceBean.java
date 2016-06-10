package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.service.mapper.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.*;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 5/13/2016.
 */
@Stateless
@Local(value = FluxResponseMessageService.class)
@Transactional
@Slf4j
public class FluxResponseMessageServiceBean implements FluxResponseMessageService {

    final static Logger LOG = LoggerFactory.getLogger(FluxResponseMessageServiceBean.class);
    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private FaReportDocumentDao faReportDocumentDao;

    @PostConstruct
    public void init() {
        faReportDocumentDao = new FaReportDocumentDao(em);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void saveFishingActivityReportDocuments(List<FAReportDocument> faReportDocuments) throws ServiceException {
        LOG.info("saveFishingActivityReportDocuments starts");
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            faReportDocumentEntities.add(getFaReportDocumentEntity(faReportDocument));
        }
        LOG.info("mapping entities is complete");
        faReportDocumentDao.bulkUploadFaData(faReportDocumentEntities);
        LOG.info("bulkUploadFaData is complete");
    }

    private FaReportDocumentEntity getFaReportDocumentEntity(FAReportDocument faReportDocument) {
        FaReportDocumentEntity entity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument);
        updateVesselTransportMeans(faReportDocument.getSpecifiedVesselTransportMeans(), entity); // Update Vessel Transport means
        updateFishingActivity(faReportDocument.getSpecifiedFishingActivities(), entity); // Update Fishing activities
        FluxReportDocumentEntity fluxReportDocumentEntity = FluxReportDocumentMapper.INSTANCE.mapToFluxReportDocumentEntity(faReportDocument.getRelatedFLUXReportDocument());
        entity.setFluxReportDocument(fluxReportDocumentEntity);
        Set<FaReportIdentifierEntity> faReportIdentifiers = new HashSet<>(FaReportDocumentMapper.INSTANCE.mapToFAReportIdentifierEntities(faReportDocument.getRelatedReportIDs()));
        for (FaReportIdentifierEntity faReportIdentifierEntity : faReportIdentifiers) {
            faReportIdentifierEntity.setFaReportDocument(entity);
        }
        entity.setFaReportIdentifiers(faReportIdentifiers);
        return entity;
    }

    private void updateFishingActivity(List<FishingActivity> fishingActivities, FaReportDocumentEntity faReportDocumentEntity) {
        for (FishingActivity fishingActivity : fishingActivities) {
            FishingActivityEntity fishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(fishingActivity);
            fishingActivityEntity.setFaReportDocument(faReportDocumentEntity);
            Set<FishingActivityIdentifierEntity> fishingActivityIdentifierEntities = new HashSet<>(FishingActivityMapper.INSTANCE.mapToFishingActivityIdentifierEntities(fishingActivity.getIDS()));
            for (FishingActivityIdentifierEntity fishingActivityIdentifierEntity : fishingActivityIdentifierEntities) {
                fishingActivityIdentifierEntity.setFishingActivity(fishingActivityEntity);
            }
            fishingActivityEntity.setFishingActivityIdentifiers(fishingActivityIdentifierEntities);
        }
    }

    private void updateVesselTransportMeans(VesselTransportMeans vesselTransportMeans, FaReportDocumentEntity faReportDocumentEntity) {
        VesselTransportMeansEntity vesselTransportMeansEntity = VesselTransportMeansMapper.INSTANCE.mapToVesselTransportMeansEntity(vesselTransportMeans);
        vesselTransportMeansEntity.setFaReportDocument(faReportDocumentEntity);
        Set<VesselIdentifierEntity> vesselIdentifierEntities = new HashSet<>(VesselTransportMeansMapper.INSTANCE.mapToVesselIdentifierEntities(vesselTransportMeans.getIDS()));
        for (VesselIdentifierEntity vesselIdentifierEntity : vesselIdentifierEntities) {
            vesselIdentifierEntity.setVesselTransportMeans(vesselTransportMeansEntity);
        }
        vesselTransportMeansEntity.setVesselIdentifiers(vesselIdentifierEntities);
        if (vesselTransportMeans.getSpecifiedRegistrationEvents() != null && !vesselTransportMeans.getSpecifiedRegistrationEvents().isEmpty()) {
            updateRegistrationEvent(vesselTransportMeans.getSpecifiedRegistrationEvents().get(0), vesselTransportMeansEntity);
        }
        faReportDocumentEntity.setVesselTransportMeans(vesselTransportMeansEntity);
    }

    private void updateRegistrationEvent(RegistrationEvent registrationEvent, VesselTransportMeansEntity vesselTransportMeansEntity) {
        RegistrationEventEntity registrationEventEntity = RegistrationEventMapper.INSTANCE.mapToRegistrationEventEntity(registrationEvent);
        registrationEventEntity.setVesselTransportMeanses(vesselTransportMeansEntity);
        updateRegistrationLocation(registrationEvent.getRelatedRegistrationLocation(), registrationEventEntity);
        vesselTransportMeansEntity.setRegistrationEvent(registrationEventEntity);
    }

    private void updateRegistrationLocation(RegistrationLocation registrationLocation, RegistrationEventEntity registrationEventEntity) {
        RegistrationLocationEntity registrationLocationEntity = RegistrationLocationMapper.INSTANCE.mapToRegistrationLocationEntity(registrationLocation);
        registrationLocationEntity.setRegistrationEvent(registrationEventEntity);
        registrationEventEntity.setRegistrationLocation(registrationLocationEntity);
    }

}
