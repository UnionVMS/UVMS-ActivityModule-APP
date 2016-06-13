package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.service.mapper.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.*;

import javax.annotation.PostConstruct;
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
@Transactional
@Slf4j
public class FluxResponseMessageServiceBean implements FluxResponseMessageService {

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private FaReportDocumentDao faReportDocumentDao;

    @PostConstruct
    public void init() {
        faReportDocumentDao = new FaReportDocumentDao(em);
    }

    /**
     * This Service saves Fishing activity report received in the module into Activity Database.
     * It receives a list of FAReportDocuments which is converted into FaReportDocumentEntity and cascade saved into database.
     *
     * @param faReportDocuments
     * @throws ServiceException
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void saveFishingActivityReportDocuments(List<FAReportDocument> faReportDocuments) throws ServiceException {
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            faReportDocumentEntities.add(getFaReportDocumentEntity(faReportDocument));
        }
        faReportDocumentDao.bulkUploadFaData(faReportDocumentEntities);
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
        Set<FishingActivityEntity> fishingActivityEntities = new HashSet<>();
        for (FishingActivity fishingActivity : fishingActivities) {
            FishingActivityEntity fishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(fishingActivity);
            fishingActivityEntity.setFaReportDocument(faReportDocumentEntity);
            Set<FishingActivityIdentifierEntity> fishingActivityIdentifierEntities = new HashSet<>(FishingActivityMapper.INSTANCE.mapToFishingActivityIdentifierEntities(fishingActivity.getIDS()));
            for (FishingActivityIdentifierEntity fishingActivityIdentifierEntity : fishingActivityIdentifierEntities) {
                fishingActivityIdentifierEntity.setFishingActivity(fishingActivityEntity);
            }
            fishingActivityEntity.setFishingActivityIdentifiers(fishingActivityIdentifierEntities);
            fishingActivityEntities.add(fishingActivityEntity);
        }
        faReportDocumentEntity.setFishingActivities(fishingActivityEntities);
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
        if (vesselTransportMeans.getSpecifiedContactParties() != null && !vesselTransportMeans.getSpecifiedContactParties().isEmpty()) {
            updateContactParty(vesselTransportMeans.getSpecifiedContactParties().get(0), vesselTransportMeansEntity);
        }
        faReportDocumentEntity.setVesselTransportMeans(vesselTransportMeansEntity);
    }

    private void updateContactParty(ContactParty contactParty, VesselTransportMeansEntity vesselTransportMeansEntity) {
        ContactPartyEntity contactPartyEntity = ContactPartyMapper.INSTANCE.mapToContactPartyEntity(contactParty);
        contactPartyEntity.setVesselTransportMeans(vesselTransportMeansEntity);
        if (contactParty.getSpecifiedContactPersons() != null && !contactParty.getSpecifiedContactPersons().isEmpty()) {
            updateContactPerson(contactParty.getSpecifiedContactPersons().get(0), contactPartyEntity);
        }
        updateContactPartyStructuredAddress(contactParty.getSpecifiedStructuredAddresses(), contactPartyEntity);
        vesselTransportMeansEntity.setContactParty(contactPartyEntity);
    }

    private void updateContactPartyStructuredAddress(List<StructuredAddress> structuredAddresses, ContactPartyEntity contactPartyEntity) {
        Set<StructuredAddressEntity> structuredAddressEntities = new HashSet<>();
        for (StructuredAddress structuredAddress : structuredAddresses) {
            StructuredAddressEntity structuredAddressEntity = StructuredAddressMapper.INSTANCE.mapToStructuredAddress(structuredAddress);
            structuredAddressEntity.setContactParty(contactPartyEntity);
            structuredAddressEntities.add(structuredAddressEntity);
        }
        contactPartyEntity.setStructuredAddresses(structuredAddressEntities);
    }

    private void updateContactPerson(ContactPerson contactPerson, ContactPartyEntity contactPartyEntity) {
        ContactPersonEntity contactPersonEntity = ContactPersonMapper.INSTANCE.mapToContactPersonEntity(contactPerson);
        contactPersonEntity.setContactParty(contactPartyEntity);
        contactPartyEntity.setContactPerson(contactPersonEntity);
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
